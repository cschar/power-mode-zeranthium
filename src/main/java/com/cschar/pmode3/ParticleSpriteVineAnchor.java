
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

public class ParticleSpriteVineAnchor extends Particle{

    private static int EYE_SPRITE_SCALE = 2;


    private int anchorX;
    private int anchorY;
    private int initialX;
    private int initialY;

    private int maxLife;


    int dir2anchorX;
    int dir2anchorY;

    private Point midPoint;

    private BufferedImage sprite;
    private BufferedImage sprite2;

    double radius;


    double initAnchorAngle;
    double curAngle;
    double adjacent;

    private boolean useSprite;

    private boolean spawnsFromBelow = false;
    private boolean spawnsFromRight = false;

    private Color topVineColor;
    private Color bottomVineColor;
    public ParticleSpriteVineAnchor(int x, int y, int dx, int dy,
                                    int anchorX, int anchorY, int size, int life, Color topVine, Color botVine,
                                    boolean useSprite) {
        super(x,y,dx,dy,size,life,topVine);
        this.useSprite = useSprite;
        this.topVineColor = topVine;
        this.bottomVineColor = botVine;

        sprite = ParticleUtils.loadSprite(String.format("/blender/vine/000.png"));
        sprite2 = ParticleUtils.loadSprite(String.format("/blender/vine/0002.png"));

        int scale2 = 5;
        sprite  =  Scalr.resize(sprite, Scalr.Method.BALANCED,
                sprite.getWidth()/EYE_SPRITE_SCALE, sprite.getHeight()/EYE_SPRITE_SCALE);
        sprite2  =  Scalr.resize(sprite2, Scalr.Method.BALANCED,
                sprite2.getWidth()/scale2, sprite2.getHeight()/scale2);

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




    private double randYSquiggleOffset;
    private double randXSquiggleOffset;

    ConcurrentLinkedQueue<VinePoint> prevPoints;

    private boolean hasFoundEnd = false;
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

                float angleIncr = -0.05f;

                float squiggleBlendX = 1;

                float squiggleBlendY = 1;

                float rampUpLife = 15;
                if(life + rampUpLife > maxLife){
                    squiggleBlendX = (maxLife - life) / rampUpLife;
                    squiggleBlendY = (maxLife - life) / rampUpLife;
                }
                //add squiggle movement
                x += squiggleBlendX * ((radius / 10) + 5 * randXSquiggleOffset) * Math.sin((5 * randXSquiggleOffset) * curAngle);
                y += squiggleBlendY * (radius / 10) * Math.sin((5 * randYSquiggleOffset) * curAngle);


                curAngle += angleIncr;  // clockwise  --  on bottom
//            prevPoints.add(new Point(x,y));
                prevPoints.add(new VinePoint(x,y, curAngle, spawnsFromBelow));
            }




//            if(spawnsFromBelow) {
//                angleIncr = -0.05f;
//            }else {
//                angleIncr = -0.05f;
//            }

            //so particles dont go infront of text, less Looping aesthetic though
//                if(spawnsFromRight && !spawnsFromBelow){
//                    angleIncr *= -1;
//                }
//                if(spawnsFromRight && spawnsFromBelow){
//                    angleIncr *= -1;
//                }


        } //if life % 2 == 0


        life--;
        return life <= 0;
    }

    boolean isNearEnd(int x, int y){
        int dist = 20;
        if(spawnsFromBelow){ //add height for caretline
            return (Math.abs(x - initialX) < dist &&

                    (Math.abs(y - (initialY + 20)) < dist ||     //incase bottom 1 comes from right then loops top
                     Math.abs(y - (initialY - 5)) < dist));
        }else{
            return (Math.abs(x - initialX) < dist && Math.abs(y - initialY) < dist);
        }
    }


    public void render(Graphics g) {

        if (life > 0) {
            Graphics2D g2d = (Graphics2D) g.create();

            //set alpha based on lifetime
            float PARTICLE_ALPHA_MAX = 0.8f;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, PARTICLE_ALPHA_MAX *(life / (float) maxLife)));
            if(anchorY > initialY) {
                g2d.setColor(this.bottomVineColor);
            }else{
                g2d.setColor(this.topVineColor);
            }


            if(!useSprite) {
                g2d.fillRect(x - (8 / 2), y - (8 / 2), 8, 8);
                for (VinePoint p : prevPoints) {
                    g2d.fillRect((int) p.p.x - (4 / 2), (int) p.p.y - (4 / 2), 4, 4);
                }
            }

            //debug
//            g2d.drawString(String.format("anchorAngle %.3f (cur: %.3f) ", initAnchorAngle, curAngle), anchorX - 20, anchorY-30);
//            g2d.setColor(Color.RED);  g2d.fillRect(midPoint.x - (8 / 2), midPoint.y - (8 / 2), 8, 8);
//            g2d.setColor(Color.ORANGE);
//            g2d.fillRect(anchorX - (8 / 2), anchorY - (8 / 2), 8, 8);


            if(useSprite) {
                //Draw sprites
                renderSprites(g2d);
            }

            g2d.dispose();
        }


    }

    private static boolean doDeathAnimation = false;
    private int deathFrame = 1;
    private float VINE_ALPHA = 0.7f;
    private float HEAD_ALPHA = 0.9f;
    private void renderSprites(Graphics2D g2d){


        AffineTransform at;

        if(life < 30){
            HEAD_ALPHA -= 0.01f;
            if(HEAD_ALPHA < 0){
                HEAD_ALPHA = 0.0f;
            }
        }
        if(hasFoundEnd){
            VINE_ALPHA -= 0.02f;
            if(VINE_ALPHA < 0){
                VINE_ALPHA = 0.0f;
            }

        }
        if(VINE_ALPHA > 0.01f) {
            Iterator<VinePoint> iter = prevPoints.iterator();
            int maxSize = prevPoints.size();
            int count = 0;
            while (iter.hasNext()) {
                count += 1;
                if (count + 2 > maxSize) {
                    break;
                }
                VinePoint p = iter.next();
                if (iter.hasNext()) { //skip every 2nd point
                    p = iter.next();
                }

                at = new AffineTransform();


                if (p.spawnsFromBelow) {
                    at.translate(p.p.x, p.p.y + 30);
                    at.rotate(Math.PI / 2);
                    at.rotate(p.angle);
                } else {
                    at.translate(p.p.x, p.p.y - 10);
                    at.rotate(-1 * (Math.PI / 2));
                    at.rotate(-1 * p.angle);
                }

                at.translate(-sprite2.getWidth() / 2,
                        -sprite2.getHeight() / 2); // around bracket height


                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, VINE_ALPHA));
                g2d.drawImage(sprite2, at, null);

            }
        }
        at = new AffineTransform();
        at.translate(x, y);
        at.translate(-sprite.getWidth() / 2,
                -sprite.getHeight() / 2); // around bracket height

        if(spawnsFromBelow) {
            at.translate(0, 30);
        }else{
            at.translate(0, -10);
        }
//                if(y > initialY){
//                    at.scale(1.0f, -1.0f);
//                    at.translate(0.0f, -sprite.getHeight() - 50);
//                }


        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, HEAD_ALPHA));


            g2d.drawImage(sprite, at, null);


            g2d.setColor(Color.BLACK);
            int offsetX = (int) (0.1f * (initialX - x));
            int offsetY = (int) (0.1f * (initialY - y));
            int d = 7; //limit eye shifts
            if (offsetX < 0) {
                offsetX = Math.max(offsetX, -d);
            } else {
                offsetX = Math.min(offsetX, d);
            }
            if (offsetY < 0) {
                offsetY = -d;
//                offsetY = Math.max(offsetY, -d);
            } else {
                offsetY = d;
//                offsetY = Math.min(offsetY, d);
            }

            int pupilSize = 4;
            if(spawnsFromBelow) {
                g2d.fillOval(x + 7 + offsetX, y - 5 + offsetY + 30, pupilSize, pupilSize);
            }else{
                g2d.fillOval(x + 7 + offsetX, y - 5 + offsetY - 10, pupilSize, pupilSize);
            }

    }

}
