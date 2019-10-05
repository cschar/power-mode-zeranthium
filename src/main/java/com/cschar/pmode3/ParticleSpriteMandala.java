
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


import com.cschar.pmode3.config.Mandala2Config;
import com.cschar.pmode3.config.common.SpriteDataAnimated;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


public class ParticleSpriteMandala extends Particle{


    static {

    }

    public static ArrayList<SpriteDataAnimated> mandalaRingData;
    private BufferedImage sprite;


    public static int targetX;
    public static int targetY;

    private int frame = 0;


    private float maxAlpha;


    private float spriteScale = 1.0f;


    public static int[] CUR_RINGS = new int[4];

    public static boolean settingEnabled = true;
    public static float moveSpeed = 0.1f;

    private int frameSpeed = 2;
    private int frameLife;
    private int ringIndex;
    private String spritePath;
    private ArrayList<BufferedImage> ringSprites;

    public ParticleSpriteMandala(int x, int y, int dx, int dy,
                                 int size, int life, int ringIndex) {
        super(x,y,dx,dy,size,life,Color.GREEN);
//        if(mandalaRingData.get(ringIndex).isCyclic){
        this.renderZIndex = 2;
//        }

        this.ringIndex = ringIndex;
        this.maxAlpha = 1.0f;
        this.frameLife = 10000;

        targetX = x;
        targetY = y;


        CUR_RINGS[ringIndex] += 1;

        this.frameSpeed = mandalaRingData.get(ringIndex).speedRate;
        this.spriteScale = mandalaRingData.get(ringIndex).scale;
//        this.spritePath = mandalaRingData.get(ringIndex).customPath;
        this.spritePath = mandalaRingData.get(ringIndex).customPath;

        ringSprites = mandalaRingData.get(ringIndex).images;
        sprite = ringSprites.get(0);


    }

    public boolean update() {
        
        frameLife--;
        //every X updates, increment frame, this controls how fast it animates
        if( this.frameLife % this.frameSpeed == 0){
            frame += 1;
            if (frame >= ringSprites.size()){
                frame = 0;

            }
            if(this.frameLife < 100){
                this.frameLife = 10000;
            }
        }


        if (!settings.isEnabled()) { // performance hit?
            CUR_RINGS[ringIndex] -= 1;
            return true;
        }

        if(!settingEnabled){ //main config toggle
            CUR_RINGS[ringIndex] -= 1;
            return true;
        }


        if( life % 2 == 0){
            //TODO fix bug here where small moveSpeed values turn dx into virutally 0
            //make it 1,2or3px min movement speed if targetX - x != 0
            int dx = targetX - x;
            this.x = (int) (this.x + dx*moveSpeed);
//            this.x = (int) (this.x + dx*0.03);

            int dy = targetY - y;
            this.y = (int) (this.y + dy*moveSpeed);
//            this.y = (int) (this.y + dy*0.03);
        }



        life--;
        boolean lifeOver = (life <= 0);

        if(lifeOver) { //ready to reset?
            if (mandalaRingData.get(ringIndex).isCyclic) {

                //If entire plugin is turned off
//                if(!PowerMode3.getInstance().isEnabled()){
//                    CUR_RINGS[ringIndex] -= 1;
//                    return true;
//                }

                if(!mandalaRingData.get(ringIndex).enabled){
                    CUR_RINGS[ringIndex] -= 1;
                    return true;
                }

                if(!mandalaRingData.get(ringIndex).customPath.equals(this.spritePath)){
                    //we've changed the sprites being cycled, so kill this particle
                    CUR_RINGS[ringIndex] -= 1;
                    return true;
                }


                //num particles changed?
                if( CUR_RINGS[ringIndex] > mandalaRingData.get(ringIndex).val2){
                    CUR_RINGS[ringIndex] -= 1;
                    return true;
                }

                //cyclic: reset next cycle , if changed in config
                //TODO, just use data in array, see Droste Particle
                this.frameSpeed = mandalaRingData.get(ringIndex).speedRate;
                this.spriteScale = mandalaRingData.get(ringIndex).scale;

                //TODO STICK THIS IN THE DAMN TABLE
                moveSpeed = Mandala2Config.CARET_MOVE_SPEED(settings); //god forgive me lol
                this.life = 99;
                return false;
            } else {
                CUR_RINGS[ringIndex] -= 1;
            }
        }
        return lifeOver;
    }







    @Override
    public void render(Graphics g) {


        if (life > 0) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(c);


            AffineTransform at = new AffineTransform();
            at.scale(this.spriteScale, this.spriteScale);
//            at.translate((int) cursorX * (1/this.spriteScale), (int) cursorY * (1/this.spriteScale));
            at.translate((int) x * (1/this.spriteScale), (int) y * (1/this.spriteScale));

            at.translate(-sprite.getWidth()/2,
                    -sprite.getHeight()/2);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.maxAlpha));



            g2d.drawImage(ringSprites.get(frame), at, null);

//            g2d.setColor(Color.ORANGE);
//            g2d.drawString(String.format("%d-RINGS %d",ringIndex, CUR_RINGS[ringIndex]), x - 20, y - 30*(ringIndex+1));



            g2d.dispose();
        }
    }

}
