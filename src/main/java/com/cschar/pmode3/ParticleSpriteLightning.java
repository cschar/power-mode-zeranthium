
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


import com.cschar.pmode3.config.LightningConfig;
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


public class ParticleSpriteLightning extends Particle{
    private static final Logger LOGGER = Logger.getLogger( ParticleSpriteLightning.class.getName() );


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

    public static void reloadSpritesWithColors(Color colorInner, Color colorOuter) {


        spritesInner = new ArrayList<BufferedImage>();
        spritesOuter = new ArrayList<BufferedImage>();
        spritesBoth = new ArrayList<BufferedImage>();


        for (int i = 1; i <= 20; i++) {
            BufferedImage tmp = ParticleUtils.loadSprite(String.format("/blender/lightning2/inner/Image0%03d.png", i));
            BufferedImage resized_image = Scalr.resize(tmp, Scalr.Method.BALANCED,
                    tmp.getWidth() / 2, tmp.getHeight() / 2);
            BufferedImage colored = colorImage(resized_image, colorInner);

            spritesInner.add(colored);
        }

        for (int i = 1; i <= 20; i++) {
            BufferedImage tmp = ParticleUtils.loadSprite(String.format("/blender/lightning2/outer2/Image0%03d.png", i));
            BufferedImage resized_image = Scalr.resize(tmp, Scalr.Method.BALANCED,
                    tmp.getWidth() / 2, tmp.getHeight() / 2);
            BufferedImage colored = colorImage(resized_image, colorOuter);

            spritesOuter.add(colored);
        }


        //This is so inefficient lol but i can't figure out another way to remove 'outline' created when
//        //drawing both inner and outer beam sprites over one another
        for( int i= 0; i< 20; i++){
            BufferedImage inner = spritesInner.get(i);
            BufferedImage outer = ParticleUtils.loadSprite(String.format("/blender/lightning2/outer2/Image0%03d.png", i+1));
            BufferedImage resized_image = Scalr.resize(outer, Scalr.Method.BALANCED,
                    outer.getWidth() / 2, outer.getHeight() / 2);
            BufferedImage colored = colorImage(resized_image, colorOuter);

            Graphics g = colored.getGraphics();
            for(int j=0; j<15; j++) {
                g.drawImage(inner, 0, 0, null);
            }
            g.dispose();
            spritesBoth.add(colored);

            g.dispose();


        }
    }



//    static ArrayList<BufferedImage> sprites;
    static ArrayList<BufferedImage> spritesInner;
    static ArrayList<BufferedImage> spritesOuter;
    static ArrayList<BufferedImage> spritesBoth;

    static {

        spritesInner = new ArrayList<BufferedImage>();
        spritesOuter = new ArrayList<BufferedImage>();
        spritesBoth = new ArrayList<BufferedImage>();
        PowerMode3 settings = PowerMode3.getInstance();


        reloadSpritesWithColors(LightningConfig.INNER_BEAM_COLOR(settings), LightningConfig.OUTER_BEAM_COLOR(settings));


        LOGGER.info("LightningSpritesTwo initialized");


    }
    private BufferedImage sprite;


    private int frame = 0;

    private int origX,origY;

    private boolean makeLightningBeam = false;
    private boolean innerBeam = false;
    private boolean outerBeam = false;

    private Color innerBeamColor;
    private Color outerBeamColor;

    public ParticleSpriteLightning(int x, int y, int dx, int dy, int size, int life,
                                   int lightningChance, boolean innerBeam, boolean outerBeam) {
        super(x,y,dx,dy,size,life,Color.ORANGE);

        sprite = spritesInner.get(0);

        innerBeamColor = LightningConfig.INNER_BEAM_COLOR(settings);
        outerBeamColor = LightningConfig.OUTER_BEAM_COLOR(settings);

        this.innerBeam = innerBeam;
        this.outerBeam = outerBeam;
        origX = x;
        origY = y;

        int randomNum = ThreadLocalRandom.current().nextInt(1, 100 +1);

        if(randomNum < lightningChance){
            makeLightningBeam = true;
            this.life = 1000;
        }
    }

    public boolean update() {

        //every X updates, increment frame, this controls how fast it animates
        if( this.life % 2 == 0){
            frame += 1;
            if (frame >= ParticleSpriteLightning.spritesInner.size()){
                frame = 0;
                life = 0;
            }
        }

        x += dx;
        y += dy;
        life--;
        return life <= 0;
    }




    @Override
    public void render(Graphics g) {


        if (life > 0) {
            Graphics2D g2d = (Graphics2D) g.create();


            if (makeLightningBeam) {
                AffineTransform at = new AffineTransform();

                double scale = origY/(double)sprite.getHeight();
                if(scale < 1.0) scale = 1.0; //only scale if we need to stretch it
                at.scale(1.0, scale);
                at.translate((int)origX * (1/1.0) ,(int)origY *(1/scale));
                at.translate(-sprite.getWidth()/2,
                                 -sprite.getHeight()); //move image 100% up so lightning lands at bottom




                if(innerBeam && !outerBeam) {
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.innerBeamColor.getAlpha()/255f * 0.7f));
                    g2d.drawImage(ParticleSpriteLightning.spritesInner.get(frame), at, null);
                }else if(outerBeam && !innerBeam){
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.outerBeamColor.getAlpha()/255f * 0.7f));
                    g2d.drawImage(ParticleSpriteLightning.spritesOuter.get(frame), at, null);
                }
                else if(innerBeam && outerBeam){
//                    float alpha = this.outerBeamColor.getAlpha()/255f + this.innerBeamColor.getAlpha()/255f;
//                    alpha *= 0.7f;
//                    alpha = Math.min(alpha, 0.7f);
                    float alpha = Math.max(this.outerBeamColor.getAlpha()/255f, this.innerBeamColor.getAlpha()/255f);
                    alpha *= 0.7f;

                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                    g2d.drawImage(ParticleSpriteLightning.spritesBoth.get(frame), at, null);
                }
            }

            g2d.dispose();
        }
    }
}
