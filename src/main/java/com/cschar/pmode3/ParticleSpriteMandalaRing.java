
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


import com.cschar.pmode3.config.SparkData;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


public class ParticleSpriteMandalaRing extends Particle{


    static {

    }

    public static SparkData[] mandalaRingData;
    private BufferedImage sprite;



    private int frame = 0;


    private int origX,origY;

    private boolean makeLightningBeam = false;

    private boolean makeSparks;
    private float maxAlpha;

    private int randomNum;

    private int maxLife;
    private int sparkLife;
    private float SPARK_ALPHA = 0.99f;
    private float spriteScale = 1.0f;

    private BufferedImage tmpSprite;

    public ParticleSpriteMandalaRing(int x, int y, int dx, int dy,
                                     int size, int life, Color c, int chanceOfSpawn, float maxAlpha, boolean sparksEnabled) {
        super(x,y,dx,dy,size,life,c);



        //arhghrgh my eyes!!!
        this.makeSparks = sparksEnabled;

        int enabled = 0;
        for(SparkData d : mandalaRingData){
            if(d.enabled){
                enabled++;
            }
        }

        if(enabled == 0){
            this.makeSparks = false;
        }else { //get winning sprite to render based on weights
            tmpSprite = mandalaRingData[0].image;
        }

        this.maxAlpha = maxAlpha;
        this.maxLife = life;
        this.sparkLife = life;

        origX = x;
        origY = y;

        randomNum = ThreadLocalRandom.current().nextInt(1, 100 +1);

        if(randomNum <= chanceOfSpawn){
            makeLightningBeam = true;
            this.life = 1000;
        }

    }

    public boolean update() {
        x += dx;
        y += dy;
        life--;
        return life <= 0;
    }







    @Override
    public void render(Graphics g) {

        if (life > 0) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(c);


            if(makeSparks){
                if(life > 50) {

                    if(SPARK_ALPHA > 0.9){
                        SPARK_ALPHA -= 0.01f;
//                        SPARK_ALPHA -= 0.02f;
                    }else{
//                        SPARK_ALPHA -= 0.02f;
                        SPARK_ALPHA -= 0.04f;
                    }

                    if(SPARK_ALPHA < 0){
                        SPARK_ALPHA = 0.0f;
                    }
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, SPARK_ALPHA));

                    // define spread CC (+'ve) then reverse -'ve so its centered over caret
                    AffineTransform at;  // -'ve ---> CC
                    double arcSpace = (randomNum / 100.0)*(Math.PI*0.5) - (Math.PI/2)*0.6;



                    at = new AffineTransform();
//                  at.scale(0.5, 0.5);
//                  at.translate((int) origX * 2, (int) origY * 2);
                    at.scale(this.spriteScale, this.spriteScale);
                    at.translate((int) origX * (1/this.spriteScale), (int) origY * (1/this.spriteScale));

                    // -'ve radians ---> rotate counter clockwise ...
                    at.rotate((arcSpace));
                    at.translate((-tmpSprite.getWidth() / 2.0),
                            (-tmpSprite.getHeight())); //move image 100% up so lightning lands at bottom

                    g2d.drawImage(tmpSprite, at, null);



//                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
//                    g2d.setColor(Color.YELLOW);
////                    g2d.drawString(String.format("r %d ", randomNum), origX - 200, origY-30 + randomNum);
//                    g2d.fillRect(x - (size / 2), y - (size / 2), size, size);

                }
            }



            if (makeLightningBeam) {
                AffineTransform at = new AffineTransform();
                at.translate((int)origX ,(int)origY );
                at.translate(-sprite.getWidth()/2,
                                 -sprite.getHeight()); //move image 100% up so lightning lands at bottom

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.maxAlpha));


                //every X updates, increment frame, this controls how fast it animates
                if( this.life % 2 == 0){
                    frame += 1;
                    if (frame >= ParticleSpriteMandalaRing.sprites.size()){
                        frame = 0;
                        life = 0;
                    }
                }
                g2d.drawImage(ParticleSpriteMandalaRing.sprites.get(frame), at, null);
            }

            g2d.dispose();
        }
    }

}
