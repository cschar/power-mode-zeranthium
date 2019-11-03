
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


public class ParticleSpriteMultiLayerChance extends Particle{


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


    public static float moveSpeed = 0.1f;

    private int frameSpeed = 2;
    private int frameLife;
    private int layerIndex;
    private String spritePath;
    private ArrayList<BufferedImage> layerSprites;
    private SpriteDataAnimated d;
    public static int MAX_LAYERS = 4;

    public ParticleSpriteMultiLayerChance(int x, int y, int dx, int dy,
                                          int size, int life, int layerIndex, Editor editor) {
        super(x,y,dx,dy,size,life,Color.GREEN);
        this.editor = editor;

        this.renderZIndex = 2;
//        }

        this.layerIndex = layerIndex;
        this.frameLife = 10000;

        targetX = x;
        targetY = y;


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
                life = 0;

            }
        }


        if (!settings.isEnabled()) { // performance hit?
            cleanupSingle(this);
            return true;
        }

        if(!settings.getSpriteTypeEnabled(PowerMode3.ConfigType.MULTI_LAYER_CHANCE)){ //main config toggle
            cleanupSingle(this);
            return true;
        }


        life--;
        boolean lifeOver = (life <= 0);

        return lifeOver;
    }


    private static void cleanupSingle(ParticleSpriteMultiLayerChance e){
//        if(spawnMap.get(e.editor) != null){
//            int[] state = spawnMap.get(e.editor);
//            if(state[e.layerIndex] >= 1){
//                state[e.layerIndex] = state[e.layerIndex] - 1;
////                spawnMap.put(e.editor, state);
//            }else{
//
//                boolean doDelete = true;
//                //scan other ring indexes and only remove if all others are empty
//                for(int i = 0; i < MAX_LAYERS; i++){
//                    if( i == e.layerIndex) continue;
//                    if(state[i] > 1) doDelete = false;
//                }
//                if(doDelete) spawnMap.remove(e.editor);
//
//                //just let it exist, cleanup when editor closes
//            }
//        }
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

//            at.translate(-sprite.getWidth()/2,
//                    -sprite.getHeight()/2);

            //from bottom
            if(d.isCyclic){
                at.translate(-sprite.getWidth()/2,
                        editor.getLineHeight() + 10); //move image below caret
            }else{
                at.translate(-sprite.getWidth()/2,
                        -sprite.getHeight()); //move image 100% up so end lands at caret
            }


            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, d.alpha));



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
