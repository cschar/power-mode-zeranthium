
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


import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class ParticleSpritePasteSingle extends Particle{

    private static ArrayList<BufferedImage> sprites;

    static {
        sprites = new ArrayList<BufferedImage>();
        for(int i=1; i <= 10; i++){
            sprites.add(ParticleUtils.loadSprite(String.format("/blender/font1/0%03d.png", i)));
        }

    }

    private BufferedImage sprite;



    private int frame = 0;


    private int origX,origY;


    private float maxAlpha;

    private int randomNum;

    private int maxLife;
    private int sparkLife;
    private float SPARK_ALPHA = 0.99f;
    private float spriteScale = 1.0f;

    private BufferedImage sparkSprite;

    private int charHeight;
    private int charWidth;

    public ParticleSpritePasteSingle(int x, int y,
                                     int size, int charHeight, int charWidth, int life, Color c) {
        super(x,y,0,0,size,life,c);
        sprite = sprites.get(0);

        this.charHeight = charHeight;
        this.charWidth = charWidth;

        this.maxAlpha = 0.9f;
        this.maxLife = life;
        this.sparkLife = life;

        origX = x;
        origY = y;

        double requiredScale[] = new double[2];
        requiredScale[0] = sprite.getWidth() / (double) charWidth;


    }

    public boolean update() {
        //every X updates, increment frame, this controls how fast it animates
        if( this.life % 2 == 0){
            frame += 1;
            if (frame >= ParticleSpritePasteSingle.sprites.size()){
                frame = 0;
            }
            this.maxAlpha -= 0.05f;
            if(this.maxAlpha < 0){
                this.maxAlpha = 0.0f;
                this.life = 0;
            }
        }


        life--;
        return life <= 0;
    }







    @Override
    public void render(Graphics g) {

        if (life > 0) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(c);



            AffineTransform at = new AffineTransform();
            double scale = 0.5;
            at.scale(scale, scale);
            at.translate((int)origX *(1/scale),(int)origY *(1/scale));
            at.translate(-sprite.getWidth()/2,
                             -sprite.getHeight()/2);



            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.maxAlpha));
//            g2d.drawRect(x-charWidth,y,charWidth,charHeight);
            g2d.fillRect(x-charWidth,y,charWidth,charHeight);

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.maxAlpha));
//            g2d.drawImage(ParticleSpriteFont.sprites.get(frame), at, null);


            g2d.dispose();
        }
    }

}
