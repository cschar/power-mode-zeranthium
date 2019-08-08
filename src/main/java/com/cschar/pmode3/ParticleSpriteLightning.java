
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
import dk.lost_world.MyTypedHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Baptiste Mesta
 */
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
        for(int i=1; i <= 25; i++){
            sprites.add(loadSprite(String.format("/blender/cube/00%02d.png", i)));
        }
        System.out.println("LightningSprites initialized");
    }
    private BufferedImage sprite;


    private int frame = 0;

    public ParticleSpriteLightning(int x, int y, int dx, int dy, int size, int life, Color c) {
        super(x,y,dx,dy,size,life,c);
        sprite = sprites.get(0);
    }

    public boolean update() {
        x += dx;
        y += dy;
        life--;
        return life <= 0;
    }

    private static int gap=10, width=60, offset=20,
            deltaX=gap+width+offset;

    private AlphaComposite makeComposite(float alpha) {
        int type = AlphaComposite.SRC_OVER;
        return(AlphaComposite.getInstance(type, alpha));
    }

    private Rectangle
            blueSquare = new Rectangle(gap+offset, gap+offset, width,
            width),
            redSquare = new Rectangle(gap, gap, width, width);

    @Override
    public void render(Graphics g) {

        if (life > 0) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(c);
            g2d.fillRect(x - (size / 2), y - (size / 2), size, size);


            AffineTransform at = new AffineTransform();
            at.translate((int)x ,(int)y );

            Composite originalComposite = g2d.getComposite();
            g2d.setComposite(makeComposite(0.5f));

            at.translate(-sprite.getWidth()/2 - 20, -sprite.getHeight()/2 - 20);
//            g2d.drawImage(sprite, at, null);

            //every X updates, increment frame, this controls how fast it animates
            if( this.life % 3 == 0){
                frame += 1;
                if (frame >= ParticleSpriteLightning.sprites.size()){
                    frame = 0;
                }
            }

            g2d.drawImage(ParticleSpriteLightning.sprites.get(frame), at, null);


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