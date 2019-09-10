
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

    private int maxLife;
    private int sparkLife;
    private float SPARK_ALPHA = 0.99f;
    private float spriteScale = 1.0f;

    private static int MAX_RINGS = 20;
    private static int CUR_RINGS = 0;

    private int frameSpeed = 2;

    private ArrayList<BufferedImage> ringSprites;

    public ParticleSpriteMandalaRing(int x, int y, int dx, int dy,
                                     int size, int life, int ringIndex) {
        super(x,y,dx,dy,size,life,Color.GREEN);

        this.maxAlpha = 1.0f;
        this.maxLife = life;
        this.sparkLife = life;
        cursorX = x;
        cursorY = y;
        randomNum = ThreadLocalRandom.current().nextInt(1, 100 +1);
        CUR_RINGS += 1;
        if(CUR_RINGS > MAX_RINGS){
            this.life = 0;
        }
        this.frameSpeed = ParticleSpriteMandalaRing.mandalaRingData.get(ringIndex).speedRate;
        this.spriteScale = ParticleSpriteMandalaRing.mandalaRingData.get(ringIndex).scale;

        int enabled = 0;
        for(int i = 0; i < mandalaRingData.size(); i++){
            //TODO: Kill outside before constructing object with static ring data
            SpriteDataAnimated d = mandalaRingData.get(i);

            if(!d.enabled && ringIndex == i){
                this.life = 0;
            }
            if(d.enabled){
                enabled++;
            }
        }

        ringSprites = mandalaRingData.get(ringIndex).images;
        sprite = ringSprites.get(0);

    }

    public boolean update() {
        x += dx;
        y += dy;
        life--;
        if(life <= 0) CUR_RINGS -= 1;

        return life <= 0;
    }







    @Override
    public void render(Graphics g) {

        if (life > 0) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(c);


//            if(makeSparks){
//                if(life > 50) {
//
//                    if(SPARK_ALPHA > 0.9){
//                        SPARK_ALPHA -= 0.01f;
////                        SPARK_ALPHA -= 0.02f;
//                    }else{
////                        SPARK_ALPHA -= 0.02f;
//                        SPARK_ALPHA -= 0.04f;
//                    }
//
//                    if(SPARK_ALPHA < 0){
//                        SPARK_ALPHA = 0.0f;
//                    }
//                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, SPARK_ALPHA));
//
//                    // define spread CC (+'ve) then reverse -'ve so its centered over caret
//                    AffineTransform at;  // -'ve ---> CC
//                    double arcSpace = (randomNum / 100.0)*(Math.PI*0.5) - (Math.PI/2)*0.6;
//
//
//
//                    at = new AffineTransform();
////                  at.scale(0.5, 0.5);
////                  at.translate((int) origX * 2, (int) origY * 2);
//                    at.scale(this.spriteScale, this.spriteScale);
//                    at.translate((int) origX * (1/this.spriteScale), (int) origY * (1/this.spriteScale));
//
//                    // -'ve radians ---> rotate counter clockwise ...
//                    at.rotate((arcSpace));
//                    at.translate((-tmpSprite.getWidth() / 2.0),
//                            (-tmpSprite.getHeight())); //move image 100% up so lightning lands at bottom
//
//                    g2d.drawImage(tmpSprite, at, null);
//
//
//

//
//                }
//            }



            AffineTransform at = new AffineTransform();

//            at.translate((int)cursorX ,(int)cursorY );

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

            g2d.setColor(Color.ORANGE);
            g2d.drawString(String.format("RINGS %d", CUR_RINGS), cursorX - 20, cursorY - 30);



            g2d.dispose();
        }
    }

}
