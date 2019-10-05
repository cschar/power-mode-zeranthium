
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

import com.cschar.pmode3.config.Mandala2Config;
import com.cschar.pmode3.config.common.SpriteDataAnimated;
import com.intellij.openapi.editor.Editor;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ParticleSpriteLinkerAnchor extends Particle{





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
    private int distanceFromCenter;
    private int maxLinks;
    private int wobbleAmount=0;
    private boolean tracerEnabled;
    private int curve1Amount;
    private boolean isSingleCyclicEnabled;

    private int frameLife;

    int[][] repeats_offsets;
    boolean[][] validOnPosIndex;
    private int[] pointsToStartFrom;


    public static int MAX_CYCLE_PARTICLES = 3;


    public static Map<Editor, Integer> spawnMap = new HashMap<>();
    private Editor editor;



    public ParticleSpriteLinkerAnchor(int x, int y, int dx, int dy, int size, int life, Color c,
                                      ArrayList<Anchor> anchors, int distanceFromCenter, int maxLinks, int wobbleAmount,
                                      boolean tracerEnabled, int curve1Amount, boolean isCyclic, Editor editor) {
        super(x,y,dx,dy,size,life,c);
        this.editor = editor;

        this.anchors = anchors;

        this.typeX = x;
        this.typeY = y;
        this.prevX = x;
        this.prevY= y;
        this.distanceFromCenter = distanceFromCenter;
        this.maxLinks = maxLinks;
        this.wobbleAmount = wobbleAmount;
        this.tracerEnabled = tracerEnabled;
        this.curve1Amount = curve1Amount;
        this.isSingleCyclicEnabled = isCyclic;


        if(spawnMap.get(this.editor) != null) {
            spawnMap.put(this.editor, spawnMap.get(this.editor) + 1);
        }else {
            spawnMap.put(this.editor, 1);
        }


        this.frameLife = 1000000;


        this.frames = new int[spriteDataAnimated.size()];


        this.pointsToStartFrom = calcPointsToStartFrom();

        repeats_offsets = new int[spriteDataAnimated.size()][2];
        for(int i = 0; i < repeats_offsets.length; i++){
            repeats_offsets[i][0] = spriteDataAnimated.get(i).val2; //repeat
            repeats_offsets[i][1] = spriteDataAnimated.get(i).val1; //offset

        }



        //TODO: add  repeats_for ....
        //  repeat N times

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


    // If we calculate once at the start instead of each time in loop, it prevents stuttering from
    // length changes due to sin/cos wiggle adds
    private int[] calcPointsToStartFrom(){
        int[] points = new int[this.anchors.size()];

        int MAX_QUAD_POINTS = maxLinks;
        for(int j = 0; j < anchors.size(); j++) {
            Anchor a = anchors.get(j);
            Point[] quadPoints = new Point[MAX_QUAD_POINTS];

            int midPointX = (this.x + a.p.x) / 2;
            int midPointY = (this.y + a.p.y) / 2;
            midPointX += curve1Amount;
            if(a.p.y > this.y) {
                midPointY += curve1Amount;
            }else{
                midPointY -= curve1Amount;
            }
            Point midPoint = new Point(midPointX, midPointY);
            Point startPoint = new Point(this.x, this.y);


            double t = 0.0;
            for (int i = 0; i < MAX_QUAD_POINTS; i++) {
                t += (1.0/MAX_QUAD_POINTS);
                Point p = this.quadTo(startPoint, midPoint, a.p, t);
                quadPoints[i] = p;
            }

            for(int i = 0; i <quadPoints.length; i++){
                if(quadPoints[i].distance(startPoint) < distanceFromCenter){
                        points[j] += 1;
                }
            }
        } // anchors
        return points;
    }



    public boolean update() {

        //every X updates, increment frame, this controls how fast it animates
        for(int i = 0; i < frames.length; i++) {
            if (this.frameLife % spriteDataAnimated.get(i).speedRate == 0) {
                frames[i] += 1;
                if (frames[i] >= spriteDataAnimated.get(i).images.size()) {
                    frames[i] = 0;
                }
            }
        }

        if (!settings.isEnabled()) {
            if(this.isSingleCyclicEnabled) {
                cleanupSingle(this.editor);
            }
            return true;
        }

        //TODO FIX THIS GARBAGE LOL
        if(this.isSingleCyclicEnabled){
            if(MAX_CYCLE_PARTICLES < spawnMap.get(editor)){ // if changed in settings
                cleanupSingle(this.editor);
                return true;
            }
            if(!cyclicToggleEnabled){ //checkbox toggle in config
                cleanupSingle(this.editor);
                return true;
            }
            //added to kill any lingering particles
            if(!settingEnabled){ //checkbox of entire LINKER type
                cleanupSingle(this.editor);
                return true;
            }

            //only update move position of cycle chains, let non-cyclic chains stay at original spawn spot
//            this.x = typeX;
//            this.y = typeY;
        }
        if(Mandala2Config.MOVE_WITH_CARET(settings)) { //otherwise linkers will stay where intial typeAction spawned them
            if( life % 2 == 0){
                //TODO fix bug here where small moveSpeed values turn dx into virutally 0
                //make it 1,2or3px min movement speed if targetX - x != 0
                int dx = targetX - x;
                this.x = (int) (this.x + dx*moveSpeed);
//            this.x = (int) (this.x + dx*0.03);

                int dy = targetY - y;
                this.y = (int) (this.y + dy*moveSpeed);
//            this.y = (int) (this.y + dy*0.03);
            }
        }
        else{
            this.x = typeX;
            this.y = typeY;
        }





        life--;
        frameLife--;


        boolean lifeOver = (life <= 0);
////
        if(lifeOver) { //ready to reset?
            if (this.isSingleCyclicEnabled) {

                this.life = 99; //wont last long if cycle switch off
                if(frameLife < 1000){
                    frameLife = 1000000;
                }
                return false;
            }
        }

        return lifeOver;
    }

    private static void cleanupSingle(Editor e){
        Integer CUR_PARTICLES = spawnMap.get(e);
        if(CUR_PARTICLES != null){
            if(CUR_PARTICLES > 1){
                spawnMap.put(e, CUR_PARTICLES -1);
            }else {
                spawnMap.remove(e);
            }
        }
    }

    public static void cleanup(Editor e){

        if(spawnMap.get(e) != null){
            spawnMap.remove(e);
        }
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

            if(this.isSingleCyclicEnabled){
                if(this.prevX != this.x || this.prevY != this.y) {
                    this.pointsToStartFrom = calcPointsToStartFrom();
                    this.prevX = this.x;
                    this.prevY = this.y;
                }
            }






//            int MAX_QUAD_POINTS = 10;
            int MAX_QUAD_POINTS = maxLinks;

            int idx = 0;
            for(Anchor a: this.anchors) {
                int pointToStartFrom = this.pointsToStartFrom[idx];
                idx++;


                Point[] quadPoints = new Point[MAX_QUAD_POINTS];


                int midPointX = (this.x + a.p.x) / 2;
                int midPointY = (this.y + a.p.y) / 2;
                int incr = this.frameLife;


                midPointX += curve1Amount;
                if(a.p.y > this.y) {
                    midPointY += curve1Amount;
                }else{
                    midPointY -= curve1Amount;
                }

                int waveAmplitude = this.wobbleAmount;
                midPointX += waveAmplitude * Math.sin(0.1 * incr );
                midPointY += waveAmplitude * Math.sin(0.1 * incr + 50 + a.cursorOffset);

                Point midPoint = new Point(midPointX, midPointY);
                Point startPoint = new Point(this.x, this.y);


                double t = 0.0;
                for (int i = 0; i < MAX_QUAD_POINTS; i++) {
                    t += (1.0/MAX_QUAD_POINTS);
                    Point p = this.quadTo(startPoint, midPoint, a.p, t);
                    quadPoints[i] = p;
                }

                Path2D path = new Path2D.Double();


                if(tracerEnabled){
                     boolean pathInitialized = false;
                     for(int i = 0; i <quadPoints.length; i++){
                         if (i >= pointToStartFrom){
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
                }


                int frame = 0;
                //draw sprite pointing from p_i to p_i+n
                int n = 1;

                for (int i = MAX_QUAD_POINTS-1; i > pointToStartFrom+n; i--) {
                    Point p1 = quadPoints[i];
                    Point p0 = quadPoints[i-n];
                    int pos_index = (MAX_QUAD_POINTS-1) - i; // 0,1,2,3....

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
        if( !pData.enabled) return;

        AffineTransform at = new AffineTransform();
        at.scale(pData.scale, pData.scale);
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
