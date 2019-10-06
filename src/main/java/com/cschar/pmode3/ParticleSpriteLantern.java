
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

import com.cschar.pmode3.config.common.SpriteDataAnimated;
import com.intellij.openapi.editor.Editor;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class ParticleSpriteLantern extends Particle{





    public static ArrayList<SpriteDataAnimated> spriteDataAnimated;
    public static boolean settingEnabled = true;

    public static boolean cyclicToggleEnabled = false;

    static {}


    public static int typeX;
    public static int typeY;

    public static int targetX;
    public static int targetY;
    public static float moveSpeed;

    private int prevX;
    private int prevY;


    //    private Anchor a;
    private ArrayList<Anchor> anchors;


    private int frameLife;





    public static int MAX_CYCLE_PARTICLES = 3;


    public static Map<Editor, Integer> spawnMap = new HashMap<>();
    private Editor editor;



    public ParticleSpriteLantern(int x, int y, int dx, int dy, int size, int life, Color c,
                                 Editor editor) {
        super(x,y,dx,dy,size,life,c);
        this.editor = editor;

        this.life = life*2;

        this.typeX = x;
        this.typeY = y;
        this.prevX = x;
        this.prevY= y;


        if(spawnMap.get(this.editor) != null) {
            spawnMap.put(this.editor, spawnMap.get(this.editor) + 1);
        }else {
            spawnMap.put(this.editor, 1);
        }


        this.frameLife = 1000000;

    }




    int stemPointsToDraw = 0;
    public boolean update() {

        if(this.frameLife % 3 == 0){
            stemPointsToDraw += 1;
        }
        stemPointsToDraw = Math.min(stemPointsToDraw, MAX_QUAD_POINTS+300);


        life--;
        frameLife--;
        boolean lifeOver = (life <= 0);
        return lifeOver;
    }




    public Point quadTo(Point from, Point mid, Point destination, double t){
        //https://stackoverflow.com/a/246628/5198805
        //P0 = A * t + (1 - t) * B
        //P1 = B * t + (1 - t) * C
        //This interpolates between two edges we've created, edge AB and edge BC. The only thing we now have to do to calculate the point we have to draw is interpolate between P0 and P1 using the same t like so:
        //
        //Pfinal = P0 * t + (1 - t) * P1

        //https://stackoverflow.com/questions/5634460/quadratic-b%C3%A9zier-curve-calculate-points
        Point[] p = new Point[]{from,mid,destination};
        double x = (1 - t) * (1 - t) * p[0].x + 2 * (1 - t) * t * p[1].x + t * t * p[2].x;
        double y = (1 - t) * (1 - t) * p[0].y + 2 * (1 - t) * t * p[1].y + t * t * p[2].y;

        return new Point((int)x,(int)y);
    }


    int MAX_LOOPS; //4 segments in 1 loop
    int MAX_SEGMENTS = 4;
    int MAX_QUAD_POINTS = 20;
    int frames[];
    public void render(Graphics g) {

        if (life > 0) {
            Graphics2D g2d = (Graphics2D) g.create();

            g2d.setColor(this.c);
//            g2d.drawRect(this.x, this.y, 5 ,5);

            g2d.setStroke(new BasicStroke(2.0f));


            Point[] controlPoints0 = new Point[]{
                    new Point(this.x, this.y - 20),
                    new Point(this.x, this.y - 200),
                    new Point(this.x + 300, this.y - 200)
            };
            drawSegment(g2d, 0, 10, controlPoints0);

            Point[] controlPoints = new Point[]{
                    new Point(x + 300,y - 200),
                    new Point(x + 350,y - 200),
                    new Point(x + 350,y - 100)
            };

            drawSegment(g2d, 10, 10, controlPoints);


            Point[] controlPoints2 = new Point[]{
                    new Point(x + 350,y - 100),
                    new Point(x + 350,y - 50),
                    new Point(x + 300,y - 50)
            };
            drawSegment(g2d, 20, 10, controlPoints2);

            Point[] controlPoints3 = new Point[]{
                    new Point(x + 300,y - 50),
                    new Point(x + 275,y - 50),
                    new Point(x + 275,y - 100)
            };
            drawSegment(g2d, 30, 10, controlPoints3);


            Point[] controlPoints4 = new Point[]{
                    new Point(x + 275,y - 100),
                    new Point(x + 275,y - 150),
                    new Point(x + 400,y - 150)
            };
            drawSegment(g2d, 40, 10, controlPoints4);

            Point[] controlPoints5 = new Point[]{
                    new Point(x + 400,y - 150),
                    new Point(x + 500,y - 150),
                    new Point(x + 500,y - 100)
            };
            drawSegment(g2d, 50, 10, controlPoints5);

            drawLanternSegment(g2d, 60, 10, new Point(x + 500,y - 100));




            Point[] quadPoints = new Point[MAX_QUAD_POINTS];


            Point midPoint = new Point(this.x, this.y - 200);
            Point startPoint = new Point(this.x, this.y - 20);
            Point endPoint = new Point(this.x + 300, this.y - 200);

            double t = 0.0;
            for (int i = 0; i < MAX_QUAD_POINTS; i++) {
                t += (1.0/MAX_QUAD_POINTS);
                Point p = this.quadTo(startPoint, midPoint, endPoint, t);
                quadPoints[i] = p;
            }

            Path2D path = new Path2D.Double();



             boolean pathInitialized = false;
             for(int i = 0; i <quadPoints.length; i++){
                 if (i <= stemPointsToDraw){
                     if(!pathInitialized){
                         pathInitialized = true;
                         path.moveTo(quadPoints[i].x, quadPoints[i].y);
                     }else{
                         path.lineTo(quadPoints[i].x, quadPoints[i].y);
                     }
                 }
            }

            g2d.setPaint(this.c);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.c.getAlpha()/255f));
            g2d.draw(path);



            g2d.dispose();
        } //life > 0
    }

    private void drawLanternSegment(Graphics2D g2d, int start, int SEGMENT_POINTS, Point controlPoint) {
        if (stemPointsToDraw < start) return;

        int randomNum = ThreadLocalRandom.current().nextInt(1, editor.getContentComponent().getWidth()/3 +1);

        Path2D path = new Path2D.Double();

        path.moveTo(controlPoint.x, controlPoint.y);
        path.lineTo(controlPoint.x + 100 + randomNum, editor.getContentComponent().getHeight());
        path.lineTo(controlPoint.x - 100 + randomNum, editor.getContentComponent().getHeight());
        path.lineTo(controlPoint.x, controlPoint.y);

        g2d.setPaint(Color.YELLOW);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        g2d.draw(path);


        g2d.fill(path);

//        g2d.setClip(path);

    }

    private void drawSegment(Graphics2D g2d, int start, int SEGMENT_POINTS, Point[] controlPoints){
        if(stemPointsToDraw < start) return;

        Point[] quadPoints = new Point[SEGMENT_POINTS];



        double t = 0.0;
        for (int i = 0; i < SEGMENT_POINTS; i++) {
            t += (1.0/SEGMENT_POINTS);
            Point p = this.quadTo(controlPoints[0], controlPoints[1], controlPoints[2], t);
            quadPoints[i] = p;
        }

        Path2D path = new Path2D.Double();



        boolean pathInitialized = false;
        for(int i = 0;  i <SEGMENT_POINTS; i++){


            if(!pathInitialized){
                pathInitialized = true;
                path.moveTo(quadPoints[i].x, quadPoints[i].y);
            }else{
                path.lineTo(quadPoints[i].x, quadPoints[i].y);
            }

            if(start + i > stemPointsToDraw){
                break;
            }

        }

        g2d.setPaint(this.c);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.c.getAlpha()/255f));
        g2d.draw(path);
    }




//    private void drawJavaQuad(){
//        Java quadTo doesnt give us intermediary quad points
//            for( Anchor a : this.anchors) {
//                Path2D path = new Path2D.Double();
//
//                path.moveTo(this.initialX, this.initialY);
//
//                double midPointX = (this.initialX + a.p.x)/2;
//                double midPointY = (this.initialY + a.p.y)/2;
//
////                int incr = this.life % 2;
//                int incr = this.life;
//                midPointX += 100 * Math.sin(0.1 * incr );
//                midPointY += 100 * Math.sin(0.1 * incr + 50 + a.cursorOffset);
//
//                path.quadTo(midPointX, midPointY, a.p.x, a.p.y );
//
//                g2d.draw(path);
//            }
//    }

}
