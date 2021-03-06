
/*
 * Copyright 2015 Baptiste Mesta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cschar.pmode3;


import com.cschar.pmode3.config.MultiLayerConfig;
import com.cschar.pmode3.config.common.SpriteDataAnimated;
import com.intellij.openapi.editor.Editor;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ParticleSpriteMultiLayer extends Particle{


    static {

    }

    public static ArrayList<SpriteDataAnimated> spriteDataAnimated;
    private BufferedImage sprite;


    public static int targetX;
    public static int targetY;

    private int frame = 0;






    //editor --> ring_num, # of particles
    public static Map<Editor, int[]> spawnMap = new HashMap<>();
    private Editor editor;

    public static boolean settingEnabled = true;
    public static float moveSpeed = 0.1f;

    private int frameSpeed = 2;
    private int frameLife;
    private int layerIndex;
    private String spritePath;
    private ArrayList<BufferedImage> layerSprites;
    private SpriteDataAnimated d;
    public static int MAX_LAYERS = 4;

    public ParticleSpriteMultiLayer(int x, int y, int dx, int dy,
                                    int size, int life, int layerIndex, Editor editor) {
        super(x,y,dx,dy,size,life,Color.GREEN);
        this.editor = editor;

        this.renderZIndex = 2;
//        }

        this.layerIndex = layerIndex;
        this.frameLife = 10000;

        targetX = x;
        targetY = y;

        if(spawnMap.get(this.editor) == null){
            int[] state = new int[MAX_LAYERS];

            state[layerIndex] += 1;
            spawnMap.put(this.editor, state);

        }else{
            int[] state = spawnMap.get(this.editor);
            state[layerIndex] = state[layerIndex] + 1;
//            spawnMap.put(this.editor, state);
        }

        d = spriteDataAnimated.get(layerIndex);
        this.spritePath = d.customPath;

        layerSprites = spriteDataAnimated.get(layerIndex).images;
        sprite = layerSprites.get(0);


    }

    public boolean update() {

        frameLife--;
        //every X updates, increment frame, this controls how fast it animates
        if( this.frameLife % d.speedRate == 0){
            frame += 1;
            if (frame >= layerSprites.size()){
                frame = 0;

            }
            if(this.frameLife < 100){
                this.frameLife = 10000;
            }
        }


        if (!settings.isEnabled()) { // performance hit?
            cleanupSingle(this);
            return true;
        }

        if(!settingEnabled){ //main config toggle
            cleanupSingle(this);
            return true;
        }


        if( life % 2 == 0){
            if(moveSpeed == 1.0){
                this.x = targetX;
                this.y = targetY;
            }else {
                //TODO fix bug here where small moveSpeed values turn dx into virutally 0
                //make it 1,2or3px min movement speed if targetX - x != 0
                int dx = targetX - x;
                this.x = (int) (this.x + dx * moveSpeed);

                int dy = targetY - y;
                this.y = (int) (this.y + dy * moveSpeed);
            }
        }



        life--;
        boolean lifeOver = (life <= 0);

        if(lifeOver) { //ready to reset?
            if (d.isCyclic) {

                if(!d.enabled){
                    cleanupSingle(this);
                    return true;
                }

                if(!d.customPath.equals(this.spritePath)){
                    //we've changed the sprites being cycled, so kill this particle
                    cleanupSingle(this);
                    return true;
                }



                //num particles changed?
                if( spawnMap.get(this.editor)[layerIndex] > d.val2){
                    cleanupSingle(this);
                    return true;
                }


                moveSpeed = MultiLayerConfig.CARET_MOVE_SPEED(settings);
                this.life = 99;
                return false;
            } else {
                cleanupSingle(this);
            }
        }
        return lifeOver;
    }


    private static void cleanupSingle(ParticleSpriteMultiLayer e){
        if(spawnMap.get(e.editor) != null){
            int[] state = spawnMap.get(e.editor);
            if(state[e.layerIndex] >= 1){
                state[e.layerIndex] = state[e.layerIndex] - 1;
//                spawnMap.put(e.editor, state);
            }else{

                boolean doDelete = true;
                //scan other ring indexes and only remove if all others are empty
                for(int i = 0; i < MAX_LAYERS; i++){
                    if( i == e.layerIndex) continue;
                    if(state[i] > 1) doDelete = false;
                }
                if(doDelete) spawnMap.remove(e.editor);

                //just let it exist, cleanup when editor closes
            }
        }
    }

    public static void cleanup(Editor e){
        if(spawnMap.get(e) != null){
            spawnMap.remove(e);
        }
    }




    @Override
    public void render(Graphics g) {


        if (life > 0) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(c);


            AffineTransform at = new AffineTransform();
            at.scale(d.scale, d.scale);
//            at.translate((int) cursorX * (1/this.spriteScale), (int) cursorY * (1/this.spriteScale));
            at.translate((int) x * (1/d.scale), (int) y * (1/d.scale));

            at.translate(-sprite.getWidth()/2,
                    -sprite.getHeight()/2);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));



            g2d.drawImage(layerSprites.get(frame), at, null);

//            try {
//                g2d.setColor(Color.ORANGE);
//                g2d.drawString(String.format("%d-RINGS %d", ringIndex, spawnMap.get(this.editor)[ringIndex]), x - 20, y - 30 * (ringIndex + 1));
//            }catch(NullPointerException e){
//
//            }



            g2d.dispose();
        }
    }

}
