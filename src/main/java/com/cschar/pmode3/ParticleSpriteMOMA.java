
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
import java.util.concurrent.ThreadLocalRandom;


public class ParticleSpriteMOMA extends Particle{

    private BufferedImage sprite;

    Color oneSquareColor;
    Color twoSquareColor;
    Color threeSquareColor;

    private boolean[] squaresEnabled;

    double threeSquareExtender;
    int maxLife;

    int growRandom;
    public ParticleSpriteMOMA(int x, int y, int dx, int dy, int size, int life,
                              Color[] colors, boolean[] enabled) {
        super(x,y,dx,dy,size,life,colors[0]);
        this.maxLife = life;
        this.oneSquareColor = colors[0];
        this.twoSquareColor = colors[1];
        this.threeSquareColor = colors[2];
        this.squaresEnabled = enabled;

        threeSquareExtender = ThreadLocalRandom.current().nextDouble(0.0f, 1.0f);
        growRandom = ThreadLocalRandom.current().nextInt(0,100);
    }

    public boolean update() {
        x += dx;
        y += dy;
        life--;
        return life <= 0;
    }




    private static int gap=10, width=60, offset=20;

    private Rectangle
            oneSquare = new Rectangle(gap+offset, gap+offset, width,
            width),
            twoSquare = new Rectangle(gap, gap, width, width);



    @Override
    public void render(Graphics g) {
        oneSquare = new Rectangle(x - (size / 2) - 50, y - (size / 2) - 55, width,   width);

        twoSquare = new Rectangle(x - (size / 2), y - (size / 2), width*2, width*2);

        if (life > 0) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(c);
            g2d.fillRect(x - (size / 2), y - (size / 2), size, size);


            AffineTransform at = new AffineTransform();
            at.translate((int)x ,(int)y );
//            at.rotate(Math.PI/2 + angle);


            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
            if(squaresEnabled[0]) {
                g2d.setPaint(oneSquareColor);
                g2d.fill(oneSquare);
            }
            if(squaresEnabled[1]) {
                g2d.setPaint(twoSquareColor);
                g2d.fill(twoSquare);
            }

            if(squaresEnabled[2]) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
                g2d.setPaint(threeSquareColor);
                //Barnett Newman !?

//            int newWidth = width*2 + (int) (threeSquareExtender * 2000 * incr);
                int newWidth = 10 * (maxLife - life);
                int rectHeight = 30; //
                g2d.fillRect(x - (size / 2), y - 2 * (size),
                        newWidth, rectHeight);

                int bifurcationThreshold = 100; // less than X life, and grow
                int growthSpot = 150 + growRandom;
                if (life < bifurcationThreshold) {
                    int newHeight = 10 * (bifurcationThreshold - life);
                    if (dy > 0) { //moving down

                        rectHeight = 0; //cover line
                        g2d.setPaint(twoSquareColor);
                        g2d.fillRect(x - (size / 2) + growthSpot, y - 2 * (size) + rectHeight,
                                30, newHeight);


                        g2d.fillRect(x - (size / 2) + growthSpot, y - 2 * (size) + rectHeight,
                                newWidth - growthSpot, newHeight);
                    } else {

                        g2d.setPaint(twoSquareColor);
                        g2d.fillRect(x - (size / 2) + growthSpot, y - 2 * (size) - newHeight,
                                30, newHeight);
                        g2d.fillRect(x - (size / 2) + growthSpot, y - 2 * (size) - newHeight,
                                newWidth - growthSpot, newHeight);
                    }
                }
            }

            //recursive approach


            g2d.dispose();
        }
    }


}
