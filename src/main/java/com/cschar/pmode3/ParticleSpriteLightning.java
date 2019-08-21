
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


public class ParticleSpriteLightning extends Particle{
    private static BufferedImage loadSprite(String name){
        try {
            return ImageIO.read(ParticleSpriteLightning.class.getResource(name));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static ArrayList<BufferedImage> sprites;

    static {


        sprites = new ArrayList<BufferedImage>();
//        for(int i=1; i <= 25; i++){
//            sprites.add(loadSprite(String.format("/blender/cube/00%02d.png", i)));
//        }
        for(int i=1; i <= 150; i++){
            sprites.add(loadSprite(String.format("/blender/lightning/lightning10%03d.png", i)));
        }
        System.out.println("LightningSprites initialized");
    }
    private BufferedImage sprite;


    private int frame = 0;

    private int origX,origY;

    private boolean makeLightningBeam = false;

    public ParticleSpriteLightning(int x, int y, int dx, int dy, int size, int life, Color c, int chanceOfSpawn) {
        super(x,y,dx,dy,size,life,c);
        sprite = sprites.get(0);

        origX = x;
        origY = y;

        int randomNum = ThreadLocalRandom.current().nextInt(1, 100 +1);
//        System.out.println(randomNum);
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



    private AlphaComposite makeComposite(float alpha) {
        int type = AlphaComposite.SRC_OVER;
        return(AlphaComposite.getInstance(type, alpha));
    }


    @Override
    public void render(Graphics g) {

        if (life > 0) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(c);

            if (makeLightningBeam) {
                AffineTransform at = new AffineTransform();
                at.translate((int)origX ,(int)origY );
                at.translate(-sprite.getWidth()/2,
                                 -sprite.getHeight()); //move image 100% up so lightning lands at bottom

//            Composite originalComposite = g2d.getComposite();
                g2d.setComposite(makeComposite(0.5f));

                //every X updates, increment frame, this controls how fast it animates
                if( this.life % 2 == 0){
                    frame += 1;
                    if (frame >= ParticleSpriteLightning.sprites.size()){
                        frame = 0;
                        life = 0;
                    }
                    System.out.println(String.format("frame %d - life %d", frame, life));
                }
                g2d.drawImage(ParticleSpriteLightning.sprites.get(frame), at, null);
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
