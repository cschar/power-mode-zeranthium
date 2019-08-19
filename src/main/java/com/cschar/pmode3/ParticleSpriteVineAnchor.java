
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

import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

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

    float initAngle;
    float initAnchorAngle;
    float curAngle;

    double K_initAnchorAngle;

    public ParticleSpriteVineAnchor(int x, int y, int dx, int dy,
                                    int anchorX, int anchorY, int size, int life, Color c) {
        super(x,y,dx,dy,size,life,c);



        this.maxLife = life;
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        this.initialX = x;
        this.initialY = y;

        this.dir2anchorX = ((anchorX - initialX)/70);
        this.dir2anchorY = (anchorY - initialY)/70;

        midPoint = new Point((anchorX + initialX)/2,
                             (anchorY + initialY)/2);

        radius =  Math.sqrt(Math.pow((double) (midPoint.x - anchorX), 2.0f) +
                           Math.pow((double)  (midPoint.y - anchorY),2.0f));

        float adjacent = (x - midPoint.x);

        initAngle = (float) Math.cosh(adjacent/radius);
        curAngle = initAngle;


        float anchorAdjacent = (anchorX - midPoint.x);
        initAnchorAngle = (float) Math.cosh(anchorAdjacent/radius);
//        if(anchorAdjacent > 0){  // anchor is on Right side of caret
            initAnchorAngle -= Math.PI;
//        }

//        if(anchorX > initialX) {
//            K_initAnchorAngle = Math.cosh((anchorX - midPoint.x) / radius);
//        }else if(anchorX < initialX){
//            K_initAnchorAngle = Math.cosh(())
//        }

        prevPoints = new ConcurrentLinkedQueue<Point>();
    }


    ConcurrentLinkedQueue<Point> prevPoints;

    double angleDiff = 0.0f;
    public boolean update() {


        //wtf? why does sin work for "x"
        float circleX = radius * (float) Math.sin(curAngle);
        float circleY = radius * (float) Math.cos(curAngle);



//        angleDiff = (float) Math.abs(Math.abs(curAngle % Math.PI) - Math.abs(initAnchorAngle));
//        double degDiff = Math.abs(Math.toDegrees(curAngle) - Math.toDegrees(initAnchorAngle));
        double tmpAnchorAngle = (initAnchorAngle + 2*Math.PI) % (2*Math.PI);
        double tmpcurAngle = curAngle % (2*Math.PI);


            angleDiff = (tmpAnchorAngle - tmpcurAngle);
//        double radDiff = Math.abs((curAngle % (Math.PI)) - (initAnchorAngle % (Math.PI)));
//
//        if(life % 2 == 0 && radDiff > 0.1f) {
//            curAngle += 0.05;
//        }

//        if(life % 2 == 0 && degDiff > 1) {
//            curAngle += 0.05;
//        }

        // -'ve radian --> clockwise

        if(anchorY > initialY) {
            if(life % 2 == 0 && (angleDiff % (Math.PI*2)) > 0.06f) {
                curAngle -= 0.05;  // clockwise  --  on bottom
            }
        }else{
            if(life % 2 == 0 && (angleDiff) > 0.06f) {
                curAngle += 0.05;  //counterclockwise -- on top
            }
        }

        if(!(anchorX < initialX)) {// only kill them if they are on left side
            if (Math.abs(x - anchorX) < 7 && Math.abs(y - anchorY) < 7) {
                life = 0;
            }
        }


//        if( angleDiff < 0.3f){
//            life = 0;
//        }

        x = (int) circleX + midPoint.x;
        y = (int) circleY + midPoint.y;

        prevPoints.add(new Point(x,y));


        life--;
        return life <= 0;
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
//            g2d.drawString(String.format("cur: %.3f",curAngle), midPoint.x - 10, midPoint.y);
            g2d.drawString(String.format("anglediff %f ", angleDiff), midPoint.x - 20, midPoint.y-30);
            g2d.drawString(String.format("initAnchorAngle %.3f ", (initAnchorAngle + 2*Math.PI) % (2*Math.PI)), midPoint.x - 20, midPoint.y-50);

//
//            g2d.drawString(String.format("initAnchor: %.3f", initAnchorAngle), anchorX, anchorY);
//            g2d.fillRect(x - (size / 2), y - (size / 2), size, size);
            g2d.fillRect(x - (8 / 2), y - (8 / 2), 8, 8);

            for( Point p: prevPoints){
                g2d.fillRect(p.x - (4 / 2), p.y - (4 / 2), 4, 4);
            }

            g2d.setColor(Color.RED);
            g2d.fillRect(midPoint.x - (8 / 2), midPoint.y - (8 / 2), 8, 8);

            g2d.dispose();
        }


    }

}
