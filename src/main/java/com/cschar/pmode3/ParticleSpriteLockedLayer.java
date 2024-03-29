
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


import com.cschar.pmode3.config.common.SpriteDataAnimated;
import com.intellij.openapi.editor.Editor;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ParticleSpriteLockedLayer extends Particle{
    public static ArrayList<SpriteDataAnimated> spriteDataAnimated;
    private BufferedImage sprite;
    private ArrayList<BufferedImage> sprites = new ArrayList<BufferedImage>();
    private int frame = 0;
    private int frameLife;

    private SpriteDataAnimated d;
    private String spritePath;


    /** target editor this object is rendering in */
    private Editor editor;

    //each spot is a SpriteDataAnimated instance counter
    public static Map<Editor, int[]> spawnMap = new HashMap<>();


    /** max # of particles per editor */
    public static int MAX_PARTICLES = 5;

    /** determines which sprite collection this object is rendering (one of the rows in the UI config) */
    private int spriteRowIndex = 0;

    public ParticleSpriteLockedLayer(int x, int y, int dx, int dy,
                                     int size, int life, int spriteDataIndex,
                                     Editor editor) {
        super(x,y,dx,dy,size,life,Color.GREEN);
        this.editor = editor;
        this.spriteRowIndex = spriteDataIndex;
        this.frameLife = 10000;

        // If its the first time an editor is rendering this type of particle...
        // initialize the state
        if(spawnMap.get(editor) == null){
            //initialize the tracker for 5 spawn points (5 rows in ui config)
            int[] state = new int[MAX_PARTICLES];
            state[spriteRowIndex] = 1;
            spawnMap.put(editor, state);
        }else{
            int[] state = spawnMap.get(editor);
            state[spriteRowIndex] = 1;
            spawnMap.put(editor, state);
        }

        this.sprites = spriteDataAnimated.get(spriteDataIndex).images;
        this.sprite = spriteDataAnimated.get(spriteDataIndex).image;
        this.d = spriteDataAnimated.get(spriteDataIndex);
        this.spritePath = d.customPath;
    }


    public boolean update() {
//        If entire plugin is turned off

        if(!settings.isEnabled()){
            cleanup(this.editor);
            return true;
        }

        if(!settings.getSpriteTypeEnabled(PowerMode3.ConfigType.LOCKED_LAYER)){
            cleanup(this.editor);
            return true;
        }

        life--;
        frameLife--;

        if( this.frameLife % d.speedRate == 0){
            frame += 1;
            if (frame >= sprites.size()){
                frame = 0;
            }
            if(frameLife < 100){ //TODO can use greatest common denominator to sync up here
                frameLife = 10000;
            }
        }


        boolean lifeOver = (life <= 0);
        if(lifeOver) { //ready to reset?
            if (d.isCyclic) {
                if(!d.enabled){
                    cleanupSingle(this.editor);
                    return true;
                }

                if(!d.customPath.equals(this.spritePath)){
                    //we've changed the sprites being cycled, so kill this particle
                    cleanupSingle(this.editor);
                    return true;
                }




                //cyclic: reset next cycle
                this.life = 99;
                return false;
            } else {
                cleanupSingle(this.editor);
            }
        }

        return lifeOver;
    }

    /** cleanup single lockedlayer item, either stretch,topr,topl,botr,bot */
    private void cleanupSingle(Editor editor){
        if(spawnMap.get(editor) != null){
            int[] state = spawnMap.get(editor);
            if(state[this.spriteRowIndex] >= 1){
                state[this.spriteRowIndex] = 0;
                spawnMap.put(editor, state);
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
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, d.alpha));

            AffineTransform at = new AffineTransform();

//            Component ec = editor.getComponent();
            Rectangle bounds = editor.getComponent().getBounds();
            int gutterWidthOffset = d.val1;
            bounds.setBounds(bounds.x, bounds.y, bounds.width - gutterWidthOffset, bounds.height);


            //stretch
            if(d.val2 == 0) {
                double widthScale = (bounds.width) / (double) sprite.getWidth();
                double heightScale = bounds.height / (double) sprite.getHeight();
                Point midPoint = new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
                at.scale(widthScale, heightScale);
                at.translate((int) midPoint.x * (1 / widthScale),
                             (int) midPoint.y * (1 / heightScale));
                at.translate(-sprite.getWidth() / 2.0,
                             -sprite.getHeight() / 2.0);

            }else if(d.val2 == 1){ //top/right
                Point drawPoint = new Point(bounds.x + bounds.width - sprite.getWidth(), bounds.y);
                at.translate(drawPoint.x,   drawPoint.y);
            }else if(d.val2 == 2) { //top left
                Point drawPoint = new Point(bounds.x, bounds.y);
                at.translate(drawPoint.x,   drawPoint.y);
            }else if(d.val2 == 3) { //bot/right
                Point drawPoint = new Point(bounds.x + bounds.width - sprite.getWidth(), bounds.y + bounds.height - sprite.getHeight());
                at.translate(drawPoint.x,   drawPoint.y);
            }else if(d.val2 == 4) { //bot/left
                Point drawPoint = new Point(bounds.x, bounds.y + bounds.height - sprite.getHeight());
                at.translate(drawPoint.x,   drawPoint.y);
            }

            g2d.drawImage(sprites.get(frame), at, null);


            g2d.dispose();
        }
    }

}
