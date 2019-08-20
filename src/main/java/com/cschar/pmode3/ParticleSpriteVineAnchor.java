
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

import javafx.geometry.Point3D;
import org.imgscalr.Scalr;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Iterator;
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


    double initAnchorAngle;
    double curAngle;
    double adjacent;

    private boolean useSprite;
    private boolean spawnsFromBelow = false;
    private boolean spawnsFromRight = false;
    public ParticleSpriteVineAnchor(int x, int y, int dx, int dy,
                                    int anchorX, int anchorY, int size, int life, Color c, boolean useSprite) {
        super(x,y,dx,dy,size,life,c);
        this.useSprite = useSprite;
        sprite = ParticleUtils.loadSprite(String.format("/blender/vine/0001.png"));
        int scale = 4;
        sprite  =  Scalr.resize(sprite, Scalr.Method.BALANCED,
                sprite.getWidth()/scale, sprite.getHeight()/scale);

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

        prevPoints = new ConcurrentLinkedQueue<VinePoint>();


        randYSquiggleOffset = ThreadLocalRandom.current().nextDouble(0.0f, 1.0f);
        randXSquiggleOffset = ThreadLocalRandom.current().nextDouble(0.0f, 1.0f);




    }

    class VinePoint{
        public VinePoint(int x, int y, double angle, boolean spawnsFromBelow){
            this.p = new Point(x,y);
            this.angle = angle;
            this.spawnsFromBelow = spawnsFromBelow;
        }
        Point p;
        double angle;
        boolean spawnsFromBelow;
    }




    double randYSquiggleOffset;
    double randXSquiggleOffset;

    ConcurrentLinkedQueue<VinePoint> prevPoints;

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

            //add squiggle movement
            x += ((radius / 10) + 5 * randXSquiggleOffset) * Math.sin((5 * randXSquiggleOffset) * curAngle);
            y += (radius / 10) * Math.sin((5 * randYSquiggleOffset) * curAngle);

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
//            prevPoints.add(new Point(x,y));
            prevPoints.add(new VinePoint(x,y, curAngle, spawnsFromBelow));
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

            g2d.setColor(c);
            if(anchorY > initialY) {
                g2d.setColor(Color.CYAN);
            }

            g2d.drawString(String.format("anchorAngle %.3f (cur: %.3f) ", initAnchorAngle, curAngle), anchorX - 20, anchorY-30);
            g2d.fillRect(x - (8 / 2), y - (8 / 2), 8, 8);

            for( VinePoint p: prevPoints){
                g2d.fillRect((int) p.p.x - (4 / 2), (int) p.p.y - (4 / 2), 4, 4);
            }
//            g2d.setColor(Color.RED);  g2d.fillRect(midPoint.x - (8 / 2), midPoint.y - (8 / 2), 8, 8);

            g2d.setColor(Color.ORANGE);
            g2d.fillRect(anchorX - (8 / 2), anchorY - (8 / 2), 8, 8);


            if(useSprite) {
                //Draw sprites

                AffineTransform at = new AffineTransform();

                at.translate(x, y);
                at.translate(-sprite.getWidth() / 2,
                        -sprite.getHeight() / 2 - 15); // around bracket height


//            if(this.anchorX < this.initialX){ //flip image
//                at.scale(-1.0f, 1.0f);
//                at.translate(-1*sprite.getWidth(), 0);  //* -1 now actually sends it RIght on screen
//            }

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));
//            g2d.drawImage(sprite, at, null);


                Iterator<VinePoint> iter = prevPoints.iterator();
                while (iter.hasNext()) {
                    VinePoint p = iter.next();
                    if (iter.hasNext()) { //skip every 2nd point
                        p = iter.next();
                    }

                    at = new AffineTransform();

//                at.rotate(curAngle);
                    at.translate(p.p.x, p.p.y);
                    at.translate(-sprite.getWidth() / 2,
                            -sprite.getHeight() / 2 - 15); // around bracket height
                    if (p.spawnsFromBelow) {
                        at.rotate(Math.PI / 2);
                        at.rotate(p.angle);
                    } else {
                        at.rotate(-1 * (Math.PI / 2));
                        at.rotate(-1 * p.angle);
                    }


//                if(this.anchorX < this.initialX){ //flip image
//                    at.scale(-1.0f, 1.0f);
//                    at.translate(-1*sprite.getWidth(), 0);  //* -1 now actually sends it RIght on screen
//                }
                    g2d.drawImage(sprite, at, null);
                }
            }

            g2d.dispose();
        }


    }

}
