
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


public class ParticleSpriteTapAnim extends Particle{

    private static ArrayList<BufferedImage> sprites = new ArrayList<BufferedImage>();

    public static ArrayList<SpriteDataAnimated> spriteDataAnimated;



    private static BufferedImage sprite;

    private SpriteDataAnimated d;
    private String spritePath;


    private int frameLife;
    private Editor editor;

    // editor --> frame, maxFrame, cursorX, cursorY, speedRate
    public static Map<Editor, int[]> spawnMap = new HashMap<>();

    public ParticleSpriteTapAnim(int x, int y, int dx, int dy,
                                 int size, int life, int spriteDataIndex, Color c,
                                 Editor editor) {
        super(x,y,dx,dy,size,life,c);
        this.editor = editor;
        this.frameLife = 10000;


        sprites = spriteDataAnimated.get(spriteDataIndex).images;

        this.d = spriteDataAnimated.get(spriteDataIndex);
        this.spritePath = d.customPath;
        sprite = spriteDataAnimated.get(spriteDataIndex).image;

        spawnMap.put(editor, new int[]{0, sprites.size(),x,y,d.speedRate});
    }


    public static void updateCursor(Editor e, int cursorX, int cursorY){
        int[] val = ParticleSpriteTapAnim.spawnMap.get(e);
        if(val != null){
            val[2] = cursorX;
            val[3] = cursorY;
        }
    }


    public static void incrementFrame(Editor e){
        int[] val = ParticleSpriteTapAnim.spawnMap.get(e);
        if(val != null){

            val[0] = Math.min(val[0] + val[4], 20); //don't buffer more than 20 frames


            //only used if this is controlling frames directly
//            val[0] = val[0] + val[4];
//            if( val[0] % val[1] == 0){
//                val[0] = 0;
//            }
        }
    }


    int frame = 0;
    public boolean update() {
//        If entire plugin is turned off
        if(this.frameLife % 2 == 0){
            if(spawnMap.get(this.editor)[0] > 0) {
                spawnMap.get(this.editor)[0] -= 1;
                frame += 1;
                if (frame >= sprites.size()) {
                    frame = 0;
                }
            }
        }

        if(!settings.isEnabled()){
            ParticleSpriteTapAnim.spawnMap.remove(this.editor);
            return true;
        }

        if(!settings.getSpriteTypeEnabled(PowerMode3.ConfigType.TAP_ANIM)){
            ParticleSpriteTapAnim.spawnMap.remove(this.editor);
            return true;
        }

        frameLife--;
        life--;

        boolean lifeOver = (life <= 0);
        if(lifeOver) { //ready to reset?
            if (d.isCyclic) {
                if(!d.enabled){
                    ParticleSpriteTapAnim.spawnMap.remove(this.editor);
                    return true;
                }

                if(!d.customPath.equals(this.spritePath)){
                    //we've changed the sprites being cycled, so kill this particle
                    ParticleSpriteTapAnim.spawnMap.remove(this.editor);
                    return true;
                }




                //cyclic: reset next cycle
                this.life = 50;
                if(frameLife < 1000){
                    frameLife = 10000;
                }
                spawnMap.get(this.editor)[4] = d.speedRate; //update speed
                return false;
            } else {
                ParticleSpriteTapAnim.spawnMap.remove(this.editor);
            }
        }

        return lifeOver;
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

            int[] state = spawnMap.get(this.editor);

            AffineTransform at = new AffineTransform();
            at.scale(d.scale, d.scale);
            at.translate(state[2] * (1/d.scale), state[3] * (1/d.scale));

            at.translate(-sprite.getWidth()/2 + (d.val2 / d.scale),
                    -sprite.getHeight() + (d.val1 /d.scale));   //val1 --> y offset

//            g2d.drawImage(sprites.get(state[0]), at, null);
            g2d.drawImage(sprites.get(frame), at, null);


            g2d.dispose();
        }
    }

}
