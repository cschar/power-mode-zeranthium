
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
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ParticleSpriteLinkerAnchor extends Particle{





    public static ArrayList<SpriteDataAnimated> spriteDataAnimated;

    static {}


    private float scale=1.0f;

    private SpriteDataAnimated spriteData;
    private ArrayList<BufferedImage> sprites;
    private float spriteScale = 1.0f;


    public int cursorX;
    public int cursorY;
    int initialX;
    int initialY;

    //    private Anchor a;
    private Anchor[] anchors;
    private int distanceFromCenter;
    private int maxLinks;
    int[][] repeats_offsets;
    boolean[][] validOnPosIndex;

    public ParticleSpriteLinkerAnchor(int x, int y, int dx, int dy, int size, int life, Color c,
                                      Anchor[] anchors, int distanceFromCenter, int maxLinks) {
        super(x,y,dx,dy,size,life,c);


        this.anchors = anchors;

        this.initialX = x;
        this.initialY = y;

        this.cursorX = x;
        this.cursorY = y;
        this.distanceFromCenter = distanceFromCenter;
        this.maxLinks = maxLinks;

        this.sprites = spriteDataAnimated.get(0).images;
        this.spriteData = spriteDataAnimated.get(0);
        spriteScale = spriteData.scale;

        this.frames = new int[spriteDataAnimated.size()];


        repeats_offsets = new int[spriteDataAnimated.size()][2];
        for(int i = 0; i < repeats_offsets.length; i++){
            repeats_offsets[i][0] = spriteDataAnimated.get(i).val2; //repeat
            repeats_offsets[i][1] = spriteDataAnimated.get(i).val1; //offset

        }

        //Compute which spriteData's are valid on which index along curve
        validOnPosIndex = new boolean[spriteDataAnimated.size()][maxLinks];

        for(int j =0; j < validOnPosIndex.length; j++){
            int offset = repeats_offsets[j][1];
            for(int pos_index=0; pos_index < maxLinks; pos_index++) {
                if (pos_index < offset) {
                    validOnPosIndex[j][pos_index] = false;
                } else {
                    boolean repeatsOnThisIndex = ((pos_index - offset) % repeats_offsets[j][0] == 0);
                    validOnPosIndex[j][pos_index] = repeatsOnThisIndex;
                }
            }
        }


    }


//    private boolean valid_on_index(int pos_index, int spriteDataIndex){
//        return validOnPosIndex[spriteDataIndex][pos_index];
//    }




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


    int frames[];
    public void render(Graphics g) {

        if (life > 0) {
            Graphics2D g2d = (Graphics2D) g.create();

            g2d.setColor(this.c);
//            g2d.drawRect(this.x, this.y, 5 ,5);

            g2d.setStroke(new BasicStroke(2.0f));



            //every X updates, increment frame, this controls how fast it animates
            //TODO: this is duplicate work in each particle, extrac
            for(int i = 0; i < frames.length; i++) {
                if (this.life % spriteDataAnimated.get(i).speedRate == 0) {
                    frames[i] += 1;
                    if (frames[i] >= spriteDataAnimated.get(i).images.size()) {
                        frames[i] = 0;
                    }
                }
            }



//            int MAX_QUAD_POINTS = 10;
            int MAX_QUAD_POINTS = maxLinks;

            for(Anchor a: this.anchors) {
                Path2D path = new Path2D.Double();
                path.moveTo(this.initialX, this.initialY);


                Point[] quadPoints = new Point[MAX_QUAD_POINTS];
                Point prevPoint = new Point(this.initialX, this.initialY);

                int midPointX = (this.initialX + a.p.x) / 2;
                int midPointY = (this.initialY + a.p.y) / 2;
                int incr = this.life;
//                int waveAmplitude = 100;
//                int waveAmplitude = 20;
//                midPointX += waveAmplitude * Math.sin(0.1 * incr );
//                midPointY += waveAmplitude * Math.sin(0.1 * incr + 50 + a.cursorOffset);
                Point midPoint = new Point(midPointX, midPointY);
                Point startPoint = new Point(midPointX, midPointY);


                double t = 0.0;
                for (int i = 0; i < MAX_QUAD_POINTS; i++) {
                    t += (1.0/MAX_QUAD_POINTS);
                    Point p = this.quadTo(prevPoint, midPoint, a.p, t);
                    quadPoints[i] = p;
                    path.lineTo(p.x, p.y);
                }

                g2d.setPaint(Color.WHITE);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
                g2d.draw(path);

                int pointToStartFrom = 0;
                for(int i = 0; i <quadPoints.length; i++){
                    if(quadPoints[i].distance(startPoint) < distanceFromCenter){
                        pointToStartFrom += 1;
                    }
                }



                int frame = 0;
                //draw sprite pointing from p_i to p_i+n
                int n = 1;
//                for (int i = pointToStartFrom; i < MAX_QUAD_POINTS-n; i += 2) {
                for (int i = MAX_QUAD_POINTS-1; i > pointToStartFrom+n; i--) {

                    Point p1 = quadPoints[i];
                    Point p0 = quadPoints[i-n];


                    int pos_index = (MAX_QUAD_POINTS-1) - i; // 0,1,2,3....


                    //TODO: we only need to calculate this once

                    //TODO: add  repeats_for ....
                    //  repeat N times

                    //check if were repeating on this index
//                    for(int j =0; j < repeats_offsets.length; j++){
//                        int offset = repeats_offsets[j][1];
//                        if(pos_index < offset){ continue;}
//                        else{
//                            SpriteDataAnimated pData = null;
//                            boolean repeatsOnThisIndex = ((pos_index-offset) % repeats_offsets[j][0] == 0);
//
//                            if(repeatsOnThisIndex){
//                                pData = spriteDataAnimated.get(j);
//                                frame = frames[j];
//
//                                drawSprite(g2d, p0, p1, pData, frame);
//                            }
//                        }
//                    }
                   for(int spriteDataIndex =0; spriteDataIndex < repeats_offsets.length; spriteDataIndex++){
                       if(validOnPosIndex[spriteDataIndex][pos_index]){
                            frame = frames[spriteDataIndex];
                            drawSprite(g2d, p0, p1, spriteDataAnimated.get(spriteDataIndex), frame);
                        }
                    }



                }
            } // anchors




            g2d.dispose();
        } //life > 0
    }

    private void drawSprite(Graphics2D g2d, Point p0, Point p1, SpriteDataAnimated pData, int frame){
        AffineTransform at = new AffineTransform();
        at.scale(pData.scale, pData.scale);
//                    at.translate((int) cursorX * (1 / this.spriteScale), (int) cursorY * (1 / this.spriteScale));
        at.translate((int) p0.x * (1 / pData.scale), (int) p0.y * (1 / pData.scale));


        double radius = Point.distance(p0.x, p0.y, p1.x, p1.y);
        int adjacent = (p0.x - p1.x);
        double initAnchorAngle = Math.acos(adjacent / radius);

        if (p0.y - p1.y < 0) {
            initAnchorAngle *= -1;
        }

        at.rotate(initAnchorAngle);

        at.translate(-pData.image.getWidth() / 2,
                -pData.image.getHeight() / 2);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pData.alpha));


        g2d.drawImage(pData.images.get(frame), at, null);
    }


    private void drawJavaQuad(){
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
    }

}
