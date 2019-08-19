
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


import com.intellij.util.ui.UIUtil;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;


public class ParticleSpriteLightningAlt extends Particle{
    private static BufferedImage loadSprite(String name){
        try {
            return ImageIO.read(ParticleSpriteLightningAlt.class.getResource(name));
        } catch (IOException e) {
            Logger logger  = Logger.getLogger(ParticleSpriteLightningAlt.class.getName());
            logger.severe("error loading image file: " + name);
//            System.out.println("error loading image");
            e.printStackTrace();
        }
        return null;
    }

    //https://stackoverflow.com/a/16054956/403403
    private static BufferedImage colorImage(BufferedImage image, Color newColor) {
        int width = image.getWidth();
        int height = image.getHeight();
        WritableRaster raster = image.getRaster();

        for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                int[] pixels = raster.getPixel(xx, yy, (int[]) null);
                pixels[0] = newColor.getRed();
                pixels[1] = newColor.getGreen();
                pixels[2] = newColor.getBlue();
                raster.setPixel(xx, yy, pixels);
            }
        }
        return image;
    }

    public static void reloadSpritesWithColors(Color colorInner, Color colorOuter){


        spritesInner = new ArrayList<BufferedImage>();
        spritesOuter = new ArrayList<BufferedImage>();

        for(int i=1; i <= 20; i++){
            BufferedImage tmp = loadSprite(String.format("/blender/lightning2/inner/Image0%03d.png", i));
            BufferedImage resized_image =  Scalr.resize(tmp, Scalr.Method.BALANCED,
                    tmp.getWidth()/2, tmp.getHeight()/2);
            BufferedImage colored = colorImage(resized_image, colorInner);

            spritesInner.add(colored);
        }

        for(int i=1; i <= 20; i++){
            BufferedImage tmp = loadSprite(String.format("/blender/lightning2/outer/Image0%03d.png", i));
            BufferedImage resized_image =  Scalr.resize(tmp, Scalr.Method.BALANCED,
                    tmp.getWidth()/2, tmp.getHeight()/2);
            BufferedImage colored = colorImage(resized_image, colorOuter);

            spritesOuter.add(colored);
        }


        sprites = new ArrayList<BufferedImage>();

        for(int i=1; i <= 20; i++){
            BufferedImage tmp = loadSprite(String.format("/blender/lightning2/0%03d.png", i));
            BufferedImage resized_image =  Scalr.resize(tmp, Scalr.Method.BALANCED,
                    tmp.getWidth()/2, tmp.getHeight()/2);

            BufferedImage colored = colorImage(resized_image, colorInner);
            sprites.add(colored);

//            sprites.add(resized_image);
        }
    }



    static ArrayList<BufferedImage> sprites;

    static ArrayList<BufferedImage> spritesInner;
    static ArrayList<BufferedImage> spritesOuter;

    static {

        spritesInner = new ArrayList<BufferedImage>();
        spritesOuter = new ArrayList<BufferedImage>();

        for(int i=1; i <= 20; i++){
            BufferedImage tmp = loadSprite(String.format("/blender/lightning2/inner/Image0%03d.png", i));
            BufferedImage resized_image =  Scalr.resize(tmp, Scalr.Method.BALANCED,
                    tmp.getWidth()/2, tmp.getHeight()/2);
            spritesInner.add(resized_image);
        }

        for(int i=1; i <= 20; i++){
            BufferedImage tmp = loadSprite(String.format("/blender/lightning2/outer/Image0%03d.png", i));
            BufferedImage resized_image =  Scalr.resize(tmp, Scalr.Method.BALANCED,
                    tmp.getWidth()/2, tmp.getHeight()/2);
            spritesOuter.add(resized_image);
        }

        sprites = new ArrayList<BufferedImage>();
        for(int i=1; i <= 20; i++){
            BufferedImage tmp = loadSprite(String.format("/blender/lightning2/0%03d.png", i));
            BufferedImage resized_image =  Scalr.resize(tmp, Scalr.Method.BALANCED,
                    tmp.getWidth()/2, tmp.getHeight()/2);
            sprites.add(resized_image);

        }
        System.out.println("LightningSpritesTwo initialized");


    }
    private BufferedImage sprite;


    private int frame = 0;

    private int origX,origY;

    private boolean makeLightningBeam = false;

    public ParticleSpriteLightningAlt(int x, int y, int dx, int dy, int size, int life, Color c, int lightningChance) {
        super(x,y,dx,dy,size,life,c);
        sprite = sprites.get(0);

        origX = x;
        origY = y;

        int randomNum = ThreadLocalRandom.current().nextInt(1, 100 +1);
////        System.out.println(randomNum);
        if(randomNum < lightningChance){
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
            //g2d.setColor(c);
            //g2d.fillRect(origX - (size / 2), origY - (size / 2), size, size);

            if (makeLightningBeam) {
                AffineTransform at = new AffineTransform();

                at.translate((int)origX ,(int)origY );
                at.translate(-sprite.getWidth()/2,
                                 -sprite.getHeight()); //move image 100% up so lightning lands at bottom

//            Composite originalComposite = g2d.getComposite();


                //every X updates, increment frame, this controls how fast it animates
                if( this.life % 2 == 0){
                    frame += 1;
                    if (frame >= ParticleSpriteLightningAlt.sprites.size()){
                        frame = 0;
                        life = 0;
                    }
//                    System.out.println(String.format("frame %d - life %d", frame, life));
                }

                g2d.setComposite(makeComposite(0.5f));
                g2d.setComposite(makeComposite(0.7f));
//                g2d.drawImage(ParticleSpriteLightningAlt.sprites.get(frame), at, null);
                g2d.drawImage(ParticleSpriteLightningAlt.spritesOuter.get(frame), at, null);
                g2d.drawImage(ParticleSpriteLightningAlt.spritesInner.get(frame), at, null);
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
