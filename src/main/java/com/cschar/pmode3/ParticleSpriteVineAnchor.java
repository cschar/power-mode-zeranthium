
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
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

public class ParticleSpriteVineAnchor extends Particle{


    int anchorX;
    int anchorY;
    int initialX;
    int initialY;

    int maxLife;


    int dir2anchorX;
    int dir2anchorY;

    Point midPoint;

    private BufferedImage sprite;

    double radius;

    double initAngle;
    double initAnchorAngle;
    double curAngle;
    double adjacent;

    double K_initAnchorAngle;

    boolean spawnsFromBelow = false;
    boolean spawnsFromRight = false;
    public ParticleSpriteVineAnchor(int x, int y, int dx, int dy,
                                    int anchorX, int anchorY, int size, int life, Color c) {
        super(x,y,dx,dy,size,life,c);



        this.maxLife = life;
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        this.initialX = x;
        this.initialY = y;

        if(anchorY > initialY) {
            spawnsFromBelow = true;
        }
        if(anchorX > initialX) {
            spawnsFromRight = true;
        }

        this.dir2anchorX = ((anchorX - initialX)/70);
        this.dir2anchorY = (anchorY - initialY)/70;



        midPoint = new Point((anchorX + initialX)/2,
                             (anchorY + initialY)/2);



        radius =  Math.sqrt(Math.pow((double) (midPoint.x - anchorX), 2.0f) +
                           Math.pow((double)  (midPoint.y - anchorY),2.0f));

         adjacent = (anchorX - midPoint.x);
        initAnchorAngle = Math.acos(adjacent/radius);


        curAngle = initAnchorAngle;

        prevPoints = new ConcurrentLinkedQueue<Point>();


        randYSquiggleOffset = ThreadLocalRandom.current().nextDouble(0.0f, 1.0f);
        randXSquiggleOffset = ThreadLocalRandom.current().nextDouble(0.0f, 1.0f);
    }


    double randYSquiggleOffset;
    double randXSquiggleOffset;
    ConcurrentLinkedQueue<Point> prevPoints;

    double angleDiff = 0.0f;
    boolean hasFoundEnd = false;
    public boolean update() {


        if(life % 2 == 0 && !hasFoundEnd    ) {
            double circleX = radius * (float) Math.cos(curAngle);
            double circleY = radius * (float) Math.sin(curAngle);
            if(!spawnsFromBelow){
                circleY = radius * -1 * (float) Math.sin(curAngle); //Why?
            }

//            circleY *= 0.5f; //make general path more oval

            int tmpx = (int) circleX + midPoint.x;
            int tmpy = (int) circleY + midPoint.y;
            if(isNearEnd(tmpx,tmpy)) {
                hasFoundEnd = true;
            }else{
                x = tmpx;
                y = tmpy;
            }
            float angleIncr = 0;
            if(life < maxLife - 10) {
                //add squiggle movement
                x += ((radius / 10) + 5 * randXSquiggleOffset) * Math.sin((5 * randXSquiggleOffset) * curAngle);
                y += (radius / 10) * Math.sin((5 * randYSquiggleOffset) * curAngle);
            }
            if(spawnsFromBelow) {
                angleIncr = -0.05f;
            }else {
                angleIncr = -0.05f;
            }

            //so particles dont go infront of text, less Looping aesthetic though
//                if(spawnsFromRight && !spawnsFromBelow){
//                    angleIncr = 0.05f;
//                }
//                if(spawnsFromRight && spawnsFromBelow){
//                    angleIncr = 0.05f;
//                }


            curAngle += angleIncr;  // clockwise  --  on bottom
            prevPoints.add(new Point(x,y));
        } //if life % 2 == 0


        life--;
        return life <= 0;
    }

    boolean isNearEnd(int x, int y){
        if(spawnsFromBelow){ //add height for caretline
            return (Math.abs(x - initialX) < 15 && Math.abs(y - (initialY + 20)) < 15);
        }else{
            return (Math.abs(x - initialX) < 15 && Math.abs(y - initialY) < 15);
        }
    }


    public void render(Graphics g) {

        if (life > 0) {
            Graphics2D g2d = (Graphics2D) g.create();
//            final AffineTransform identity = new AffineTransform();
//            g2d.setTransform(identity);


            g2d.setColor(c);
            if(anchorY > initialY) {
                g2d.setColor(Color.CYAN);
            }

            g2d.drawString(String.format("anchorAngle %.3f (cur: %.3f) ", initAnchorAngle, curAngle), anchorX - 20, anchorY-30);

            g2d.fillRect(x - (8 / 2), y - (8 / 2), 8, 8);

            for( Point p: prevPoints){
                g2d.fillRect(p.x - (4 / 2), p.y - (4 / 2), 4, 4);
            }

            g2d.setColor(Color.RED);
            g2d.fillRect(midPoint.x - (8 / 2), midPoint.y - (8 / 2), 8, 8);

            g2d.setColor(Color.ORANGE);
            g2d.fillRect(anchorX - (8 / 2), anchorY - (8 / 2), 8, 8);


            g2d.dispose();
        }


    }

}
