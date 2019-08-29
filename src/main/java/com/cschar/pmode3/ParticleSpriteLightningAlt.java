
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


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


public class ParticleSpriteLightningAlt extends Particle{
    private static BufferedImage loadSprite(String name){
        try {
            return ImageIO.read(ParticleSpriteLightningAlt.class.getResource(name));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static ArrayList<BufferedImage> sprites;
    static ArrayList<BufferedImage> sparkSprites;

    static {

        sparkSprites = new ArrayList<BufferedImage>();
        sprites = new ArrayList<BufferedImage>();
        for(int i=152; i <= 159; i++){
            sparkSprites.add(loadSprite(String.format("/blender/lightningAlt/spark1/0%03d.png", i)));
        }
        for(int i=1; i <= 150; i++){
            sprites.add(loadSprite(String.format("/blender/lightningAlt/main/lightning10%03d.png", i)));
        }


        sparkSpriteArray = new BufferedImage[]{
                loadSprite(String.format("/blender/lightningAlt/spark4/0150.png")),
                loadSprite(String.format("/blender/lightningAlt/spark5/0150.png")),
                loadSprite(String.format("/blender/lightningAlt/spark6/0150.png"))
        };

        System.out.println("LightningSprites initialized");
    }
    private BufferedImage sprite;
    private BufferedImage spark1Sprite;

    private BufferedImage spark3Sprite;

    private static BufferedImage[] sparkSpriteArray;

    private int frame = 0;
    private int spark1Frame = 0;

    private int origX,origY;

    private boolean makeLightningBeam = false;
    private boolean makeSpark1 = false;
    private boolean makespark3 = false;
    private boolean makeSparks = true;

    private float maxAlpha;

    int randomNum;

    int maxLife;
    int sparkLife;
    private float SPARK_ALPHA = 0.99f;

    BufferedImage tmpSprite;

    public ParticleSpriteLightningAlt(int x, int y, int dx, int dy,
                                      int size, int life, Color c, int chanceOfSpawn, float maxAlpha) {
        super(x,y,dx,dy,size,life,c);
        sprite = sprites.get(0);
        spark1Sprite = sparkSprites.get(0);
        spark3Sprite = loadSprite(String.format("/blender/lightningAlt/spark4/0150.png"));



//        int r = ThreadLocalRandom.current().nextInt(0, sparkSpriteArray.length);
        int r = 1;
        tmpSprite = sparkSpriteArray[r];

        this.maxAlpha = maxAlpha;
        this.maxLife = life;
        this.sparkLife = life;

        origX = x;
        origY = y;

        randomNum = ThreadLocalRandom.current().nextInt(1, 100 +1);
//        System.out.println(randomNum);
        if(randomNum <= chanceOfSpawn){
            makeLightningBeam = true;
            this.life = 1000;
        }

        randomNum = ThreadLocalRandom.current().nextInt(1, 100 +1);
//        if(randomNum > 50){
//            makeSpark1 = true;
//        }
    }

    public boolean update() {
        x += dx;
        y += dy;
        life--;
        return life <= 0;
    }



    private AlphaComposite makeComposite(float alpha) {
        int type = AlphaComposite.SRC_OVER;
        return(AlphaComposite.getInstance(type, alpha));
    }




    @Override
    public void render(Graphics g) {

        if (life > 0) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(c);

            if(makeSpark1){
                AffineTransform at = new AffineTransform();
                at.translate((int)origX ,(int)origY );
                at.translate(-spark1Sprite.getWidth()/2,
                        -spark1Sprite.getHeight()); //move image 100% up so lightning lands at bottom


                g2d.setComposite(makeComposite(this.maxAlpha));

                //every X updates, increment frame, this controls how fast it animates
                if( this.life % 15 == 0){
                    spark1Frame += 1;
                    if (spark1Frame >= ParticleSpriteLightningAlt.sparkSprites.size()){
                        spark1Frame = ParticleSpriteLightningAlt.sparkSprites.size() - 1;
                        makeSpark1 = false;
                    }

                }
                g2d.drawImage(ParticleSpriteLightningAlt.sparkSprites.get(spark1Frame), at, null);
            }


            if(makeSparks){
                if(life > 50) {
                    //TODO: config
                    // - (min/max scale size)
                    // - select which sprites to use
                    // - round robin (with amt before next sprite) / random?

                    if(SPARK_ALPHA > 0.9){
                        SPARK_ALPHA -= 0.01f;
                    }else{
                        SPARK_ALPHA -= 0.02f;
                    }

                    if(SPARK_ALPHA < 0){
                        SPARK_ALPHA = 0.0f;
                    }
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, SPARK_ALPHA));

                    // define spread CC (+'ve) then reverse -'ve so its centered over caret
                    AffineTransform at;  // -'ve ---> CC
                    double arcSpace = (randomNum / 100.0)*(Math.PI*0.5) - (Math.PI/2)*0.6;


                    if(randomNum > 10) {
                        at = new AffineTransform();
                        at.scale(0.5, 0.5);
                        at.translate((int) origX * 2, (int) origY * 2);
                        // -'ve radians ---> rotate counter clockwise wtf?
                        at.rotate((arcSpace));
                        at.translate((-tmpSprite.getWidth() / 2.0),
                                (-tmpSprite.getHeight())); //move image 100% up so lightning lands at bottom

                        g2d.drawImage(tmpSprite, at, null);
//                    }else{
                    }
//                    at = new AffineTransform();
//                    at.translate((int) origX, (int) origY);
//                    // -'ve radians ---> rotate counter clockwise wtf?
//                    at.rotate(arcSpace);
//                    at.translate(-tmpSprite.getWidth() / 2,
//                            -tmpSprite.getHeight()); //move image 100% up so lightning lands at bottom
//
//                    g2d.drawImage(tmpSprite, at, null);


                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

                    g2d.setColor(Color.YELLOW);
//                    g2d.drawString(String.format("r %d ", randomNum), origX - 200, origY-30 + randomNum);
                    g2d.fillRect(x - (size / 2), y - (size / 2), size, size);

                }
            }

            if(makespark3){
                //TODO make a spark lifespan
                if(life > 50) {
                    SPARK_ALPHA -= 0.01f;
                    if(SPARK_ALPHA < 0){
                        SPARK_ALPHA = 0.0f;
                    }
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, SPARK_ALPHA));

                    AffineTransform at;
                    double arcSpace = (randomNum / 100.0)*(Math.PI*0.5) - (Math.PI/2)*0.8;


                    if(randomNum > 10) {
                        at = new AffineTransform();
                        at.scale(0.5, 0.5);
                        at.translate((int) origX * 2, (int) origY * 2);
                        // -'ve radians ---> rotate counter clockwise wtf?
                        at.rotate((arcSpace));
                        at.translate((-spark3Sprite.getWidth() / 2.0),
                                (-spark3Sprite.getHeight())); //move image 100% up so lightning lands at bottom

                        g2d.drawImage(spark3Sprite, at, null);
//                    }else{
                    }
                        at = new AffineTransform();
                        at.translate((int) origX, (int) origY);
                        // -'ve radians ---> rotate counter clockwise wtf?
                        at.rotate(arcSpace);
                        at.translate(-spark3Sprite.getWidth() / 2,
                                -spark3Sprite.getHeight()); //move image 100% up so lightning lands at bottom

                        g2d.drawImage(spark3Sprite, at, null);
//                    }






                    //every X updates, increment frame, this controls how fast it animates
//                if( this.life % 2 == 0){
//                    spark1Frame += 1;
//                    if (spark1Frame >= ParticleSpriteLightningAlt.sparkSprites.size()){
//                        spark1Frame = ParticleSpriteLightningAlt.sparkSprites.size() - 1;
//                        makeSpark1 = false;
//                    }
//
//                }

                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));

                    g2d.setColor(Color.YELLOW);
                    g2d.drawString(String.format("r %d ", randomNum), origX - 200, origY-30);
                    g2d.fillRect(x - (size / 2), y - (size / 2), size, size);

                }
            }



            if (makeLightningBeam) {
                AffineTransform at = new AffineTransform();
                at.translate((int)origX ,(int)origY );
                at.translate(-sprite.getWidth()/2,
                                 -sprite.getHeight()); //move image 100% up so lightning lands at bottom

//            Composite originalComposite = g2d.getComposite();
                g2d.setComposite(makeComposite(this.maxAlpha));
//                g2d.setComposite(makeComposite(0.5f));

                //every X updates, increment frame, this controls how fast it animates
                if( this.life % 2 == 0){
                    frame += 1;
                    if (frame >= ParticleSpriteLightningAlt.sprites.size()){
                        frame = 0;
                        life = 0;
                    }

                }
                g2d.drawImage(ParticleSpriteLightningAlt.sprites.get(frame), at, null);
            }

            g2d.dispose();
        }
    }


    @Override
    public String toString() {
        return "Particle{" +
                "x=" + x +
                ", y=" + y +
                ", dx=" + dx +
                ", dy=" + dy +
                ", size=" + size +
                ", life=" + life +
                ", c=" + c +
                '}';
    }
}
