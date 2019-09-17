
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

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;

public class ParticleSpriteLinkerAnchor extends Particle{





    public static ArrayList<SpriteDataAnimated> spriteDataAnimated;

    static {}


    int anchorX;
    int anchorY;
    int initialX;
    int initialY;

    int maxLife;


    int dir2anchorX;
    int dir2anchorY;

    private Anchor[] anchors;


    private float scale=1.0f;
    private float alpha=1.0f;

    public ParticleSpriteLinkerAnchor(int x, int y, int dx, int dy, int anchorIndex, int size, int life, Color c,
                                      Anchor[] anchors) {
        super(x,y,dx,dy,size,life,c);


        this.anchors = anchors;

        this.maxLife = life;
        this.anchorX = anchors[anchorIndex].p.x;
        this.anchorY = anchors[anchorIndex].p.y;

        this.initialX = x;
        this.initialY = y;

        this.dir2anchorX = ((anchorX - initialX)/70);
        this.dir2anchorY = (anchorY - initialY)/70;



    }



    private int frameDir = 1;
    private int frame = 0;

    public boolean update() {

        life--;
        return life <= 0;
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

    //TODO Rename LINKER to WEB?
    public void render(Graphics g) {

        if (life > 0) {
            Graphics2D g2d = (Graphics2D) g.create();

            g2d.setColor(this.c);
//            g2d.drawRect(this.x, this.y, 5 ,5);

            g2d.setStroke(new BasicStroke(2.0f));
            g2d.setPaint(Color.WHITE);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));

            for( Anchor a : this.anchors) {
                Path2D path = new Path2D.Double();


                path.moveTo(this.initialX, this.initialY);

                Point[] quadPoints = new Point[10];


                Point prevPoint = new Point(this.initialX, this.initialY);

                int midPointX = (this.initialX + a.p.x)/2;
                int midPointY = (this.initialY + a.p.y)/2;
                int incr = this.life;
                midPointX += 100 * Math.sin(0.1 * incr );
                midPointY += 100 * Math.sin(0.1 * incr + 50 + a.cursorOffset);
                Point midPoint = new Point(midPointX, midPointY);

                double t = 0.1;
                for(int i = 0; i <9; i++){
                    t += 0.1;

                    Point p = this.quadTo(prevPoint, midPoint, a.p, t);
                    path.lineTo(p.x,p.y);
                }

//                path.lineTo(a.p.x, a.p.y);

//                int incr = this.life % 2;


                g2d.draw(path);
            }


            //Java quadTo doesnt give us intermediary quad points
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

            g2d.dispose();
        }
    }

//
//      for(int i = 1; i<sections+1; i++){
//        midPointX = ((this.x - a.p.x) / sections) * i;
//        midPointY = ((this.y - a.p.y) / sections) * i + 100;
//
//        sectionXY[i-1][0] = this.initialX - midPointX;
//        sectionXY[i-1][1] = this.initialY - midPointY;
//    }
//
//    double lastX = this.initialX;
//    double lastY = this.initialY;
//    int amt = 0;
//                for(int[] s : sectionXY){
//        amt += 1;
//        int incr = this.life / 2;
////
//        double midX = (lastX + s[0]) / 2;
//        double midY = (lastY + s[1]) / 2;
//
//        midX += 100 * Math.sin(0.5 * incr + Math.PI*());
//        midY += 100 * Math.sin(0.5 * incr + 50);
//
//        path.quadTo(midX, midY, s[0], s[1]);
//
//        lastX = s[0];
//        lastY = s[1];
//    }
//
//                path.lineTo(a.p.x, a.p.y);
////                path.quadTo(, midPointY, a.p.x, a.p.y);


}
