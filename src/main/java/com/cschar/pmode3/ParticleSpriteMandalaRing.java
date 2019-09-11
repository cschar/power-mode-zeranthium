
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


import com.cschar.pmode3.config.SpriteData;
import com.cschar.pmode3.config.SpriteDataAnimated;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


public class ParticleSpriteMandalaRing extends Particle{


    static {

    }

    public static ArrayList<SpriteDataAnimated> mandalaRingData;
    private BufferedImage sprite;

    public static int cursorX;
    public static int cursorY;

    private int frame = 0;


    private float maxAlpha;

    private int randomNum;

    private float spriteScale = 1.0f;


    public static int[] CUR_RINGS = new int[4];

    public static boolean settingEnabled = true;

    private int frameSpeed = 2;
    private int ringIndex;
    private String spritePath;
    private ArrayList<BufferedImage> ringSprites;

    public ParticleSpriteMandalaRing(int x, int y, int dx, int dy,
                                     int size, int life, int ringIndex) {
        super(x,y,dx,dy,size,life,Color.GREEN);

        this.ringIndex = ringIndex;
        this.maxAlpha = 1.0f;
        cursorX = x;
        cursorY = y;
        randomNum = ThreadLocalRandom.current().nextInt(1, 100 +1);

        CUR_RINGS[ringIndex] += 1;

        this.frameSpeed = mandalaRingData.get(ringIndex).speedRate;
        this.spriteScale = mandalaRingData.get(ringIndex).scale;
        this.spritePath = mandalaRingData.get(ringIndex).customPath;

        ringSprites = mandalaRingData.get(ringIndex).images;
        sprite = ringSprites.get(0);


    }

    public boolean update() {
        if(!settingEnabled){ //added to kill any lingering particles
            CUR_RINGS[ringIndex] -= 1;
            return true;
        }
        x += dx;
        y += dy;
        life--;
        boolean check = (life <= 0);

        if(check) { //ready to reset?
            if (mandalaRingData.get(ringIndex).isCyclic) {

                //If entire plugin is turned off
                if(!PowerMode3.getInstance().isEnabled()){
                    CUR_RINGS[ringIndex] -= 1;
                    return true;
                }

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
                if( CUR_RINGS[ringIndex] > mandalaRingData.get(ringIndex).maxNumParticles){
                    CUR_RINGS[ringIndex] -= 1;
                    return true;
                }

                //cyclic: reset next cycle , if changed in config
                this.frameSpeed = mandalaRingData.get(ringIndex).speedRate;
                this.spriteScale = mandalaRingData.get(ringIndex).scale;
                this.life = 99;
                return false;
            } else {
                CUR_RINGS[ringIndex] -= 1;
            }
        }
        return check;
    }







    @Override
    public void render(Graphics g) {


        if (life > 0) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(c);


            AffineTransform at = new AffineTransform();
            at.scale(this.spriteScale, this.spriteScale);
            at.translate((int) cursorX * (1/this.spriteScale), (int) cursorY * (1/this.spriteScale));

            at.translate(-sprite.getWidth()/2,
                    -sprite.getHeight()/2);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.maxAlpha));


            //every X updates, increment frame, this controls how fast it animates
            if( this.life % this.frameSpeed == 0){
                frame += 1;
                if (frame >= ringSprites.size()){
                    frame = 0;

                }
            }
            g2d.drawImage(ringSprites.get(frame), at, null);

//            g2d.setColor(Color.ORANGE);
//            g2d.drawString(String.format("%d-RINGS %d",ringIndex, CUR_RINGS[ringIndex]), cursorX - 20, cursorY - 30*(ringIndex+1));



            g2d.dispose();
        }
    }

}
