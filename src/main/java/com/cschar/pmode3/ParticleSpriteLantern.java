
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

import com.cschar.pmode3.config.LanternConfig;
import com.cschar.pmode3.config.common.SpriteData;
import com.cschar.pmode3.config.common.SpriteDataAnimated;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class ParticleSpriteLantern extends Particle{

    public static ArrayList<SpriteDataAnimated> spriteDataAnimated;

    public static int typeX;
    public static int typeY;
    public static int targetX;
    public static int targetY;


    private int frames[];
    private int frameLife;

    int[][] repeats_offsets;
    boolean[][] validOnPosIndex;


    public static Map<Editor, Integer> spawnMap = new HashMap<>();
    private Editor editor;


    int randomNum;
    int randomNum50;
    int randomNum100;
    int randomNum200;

    /** max global links, can override maxLinks set in spriteData */
    private int maxLinks = 0;
    private boolean tracerEnabled = true;
    private boolean moveWithCaret = false;
    private double moveSpeed = 1.0;
    private boolean isCylic = false;
    private int lineHeight = 10;

    private boolean makeLoop = false;


    //TODO add trigger key?
    public ParticleSpriteLantern(int x, int y, int dx, int dy, int size, int life, Color c,
                                 Editor editor,  boolean tracerEnabled, boolean moveWithCaret,
                                 double moveSpeed, boolean isCyclic, boolean makeLoop) {
        super(x,y,dx,dy,size,life,c);
        this.editor = editor;


        //cheap hack to resolve off by 1 code below
        this.maxLinks = 100;
        this.tracerEnabled = tracerEnabled;
        this.moveWithCaret = moveWithCaret;
        this.moveSpeed = moveSpeed;
        this.isCylic = isCyclic;
        this.lineHeight = editor.getLineHeight();
        this.makeLoop = makeLoop;

        this.typeX = x;
        this.typeY = y;
        this.targetX = x;
        this.targetY = y;

        this.frames = new int[spriteDataAnimated.size()];


        int bound = editor.getContentComponent().getWidth()/40;
        randomNum = ThreadLocalRandom.current().nextInt(-bound, bound +1);
        randomNum50 = ThreadLocalRandom.current().nextInt(1, 50 +1);
        randomNum100 = ThreadLocalRandom.current().nextInt(1, 100 +1);
        randomNum200 = ThreadLocalRandom.current().nextInt(1, 200 +1);

        if(spawnMap.get(this.editor) != null) {
            spawnMap.put(this.editor, spawnMap.get(this.editor) + 1);
        }else {
            spawnMap.put(this.editor, 1);
        }


        this.frameLife = 1000000;



        Rectangle visibleArea = this.editor.getScrollingModel().getVisibleArea();
        this.y = this.y + visibleArea.y;
        this.x = this.x + visibleArea.x;

        editorOffsets[0] = -1*visibleArea.x;
        editorOffsets[1] = -1*visibleArea.y;


        repeats_offsets = new int[spriteDataAnimated.size()][3];
        for(int i = 0; i < repeats_offsets.length; i++){
            repeats_offsets[i][0] = spriteDataAnimated.get(i).val2; //repeat
            repeats_offsets[i][1] = spriteDataAnimated.get(i).val1; //offset
            //cheap hack to resolve off by 1 code below
            repeats_offsets[i][2] = spriteDataAnimated.get(i).val3 + 1; //local max length
        }



        //TODO: add  repeats_for ....
        //  repeat N times

        //Compute which spriteData's get drawn on which point along curve
        validOnPosIndex = new boolean[spriteDataAnimated.size()][this.maxLinks];

        //For each SpriteDataAnimated Row
        for(int j =0; j < spriteDataAnimated.size(); j++){
            SpriteDataAnimated row = spriteDataAnimated.get(j);
//            int offset = repeats_offsets[j][1];
            int offset = row.val1;
            int maxLocalLinks = Math.min(this.maxLinks, row.val3+1);

            for(int pos_index=offset; pos_index < maxLocalLinks; pos_index++) {

//                if (pos_index < offset) {
//                    validOnPosIndex[j][pos_index] = false;
//                } else {
                boolean repeatsOnThisIndex = ((pos_index - offset) % repeats_offsets[j][0] == 0);
                validOnPosIndex[j][pos_index] = repeatsOnThisIndex;
//                }
            }
        }

        //Populate pathPoints, jointPoints
        calculateLanternPath();


    }

    private void calculateLanternPath(){

        //40 pixels above caret line
        int yStart = this.y - 20;
        pathPoints.add(new Point(x, yStart));

        int offsetX0 = (int) (100 + 200*(randomNum100/100.0));
        int offsetY0 = (int) (150 + 200*(randomNum200/200.0));

        //if we go really high up, bring us back a bit in the left direction
//        if(offsetY0 > 300) {
//            offsetX0 = Math.min(offsetX0 / 2, 100);
//        }

        Point[] controlPoints0 = new Point[]{
                new Point(this.x, yStart),
                new Point(this.x, this.y - offsetY0),
                new Point(this.x + offsetX0, this.y - offsetY0)
        };

        int distance = (int) Point2D.distance(controlPoints0[0].x, controlPoints0[0].y,
                                              controlPoints0[2].x, controlPoints0[2].y);
        int segmentPoints = distance / 20;

        int next0 = drawSegment(0, segmentPoints, controlPoints0);
//        int next0 = drawSegment(0, 15, controlPoints0);


        if(this.makeLoop) {
            int xOffset = randomNum50 + 30;
            if(offsetY0 < 150) xOffset = Math.min(40, xOffset);
            int yOffset = xOffset;
            Point startPoint = controlPoints0[2];
            Point endPoint = new Point(x + offsetX0 + 100 + randomNum50,y - offsetY0/2);

            drawLoopSegment(next0, 40,
                    xOffset,yOffset,
                    startPoint,
                    endPoint
            );
        }

//        drawLanternSegment(g2d, next, 10, endPoint);
    }




    private int editorOffsets[] = new int[2];
    private int stemPointsToDraw = 0;
    private int MAX_POINTS_TO_DRAW= 300;
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

        if( life % 2 == 0){
            //TODO fix bug here where small moveSpeed values turn dx into virutally 0
            //make it 1,2or3px min movement speed if targetX - x != 0
            int dx = (targetX - editorOffsets[0]) - x;
            this.x = (int) (this.x + dx*moveSpeed);

            int dy = (targetY - editorOffsets[1]) - y;
            this.y = (int) (this.y + dy*moveSpeed);
        }


        if(this.frameLife % 2 == 0){
//            stemPointsToDraw += MAX_POINTS_TO_DRAW;
            stemPointsToDraw += 1;
        }
        stemPointsToDraw = Math.min(stemPointsToDraw, MAX_POINTS_TO_DRAW);


        if (!settings.isEnabled()) {
            if(this.isCylic) {
                cleanupSingle(this.editor);
            }
            return true;
        }

        //TODO FIX THIS GARBAGE LOL
        if(this.isCylic){
            if(LanternConfig.MAX_PARTICLES(this.settings) < spawnMap.get(editor)){ // if changed in settings
                cleanupSingle(this.editor);
                return true;
            }
            if(!LanternConfig.IS_CYCLIC_ENABLED(this.settings)){ //checkbox toggle in config
                cleanupSingle(this.editor);
                return true;
            }
            //added to kill any lingering particles
            if(!settings.getSpriteTypeEnabled(PowerMode3.ConfigType.LANTERN)){ //checkbox of entire LANTERN type
                cleanupSingle(this.editor);
                return true;
            }
        }



        life--;
        frameLife--;
        boolean lifeOver = (life <= 0);
        if(lifeOver){
            if (this.isCylic) {

                this.life = 99; //wont last long if cycle switch off
                if(frameLife < 1000){
                    frameLife = 1000000;
                }
                return false;
            }else{
                cleanupSingle(this.editor);
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

    ArrayList<Point> pathPoints = new ArrayList<>();
    ArrayList<Point> jointPoints = new ArrayList<>();
    public void render(Graphics g) {

        int curPointsToDraw = stemPointsToDraw;

        if (life > 0) {
            Graphics2D g2d = (Graphics2D) g.create();

//            pathPoints = new ArrayList<>();

            //On a different thread, cant put in update()
            Rectangle visibleArea = this.editor.getScrollingModel().getVisibleArea();
            editorOffsets[0] = -1*visibleArea.x;
            editorOffsets[1] = -1*visibleArea.y;
            g2d.translate(editorOffsets[0],  editorOffsets[1]);

            //draw tracer
            if(tracerEnabled) {
                g2d.setColor(this.c);
                g2d.setStroke(new BasicStroke(2.0f));
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.c.getAlpha()/255f));

                Path2D path = new Path2D.Double();
                path.moveTo(pathPoints.get(0).x, pathPoints.get(0).y);
                for (int i = 1; i < pathPoints.size() && i < curPointsToDraw; i++) {
                    Point p = pathPoints.get(i);
                    path.lineTo(p.x, p.y);
                }
                g2d.draw(path);

                for (int i = 0; i < jointPoints.size() && i < curPointsToDraw / 10; i++) {
                    Point p = jointPoints.get(i);
                    g2d.drawRect(p.x - 10, p.y - 10, 20, 20);
                }

                int n = pathPoints.size();
                drawLanternSegment(g2d, n, pathPoints.get(n - 1));
            }

            //draw sprites
            for (int pos_index = 1; pos_index < pathPoints.size() && pos_index < curPointsToDraw; pos_index++) {


                Point p0 = pathPoints.get(pos_index-1);
                Point p1 = pathPoints.get(pos_index);

                boolean isLessThanGlobalMaxLinks = (pos_index < this.maxLinks);

                if(pos_index < this.maxLinks) {
                    for (int spriteDataIndex = 0; spriteDataIndex < repeats_offsets.length; spriteDataIndex++) {
                        if (validOnPosIndex[spriteDataIndex][pos_index]) {
                            SpriteDataAnimated.drawSprite(g2d, p0, p1,
                                                          spriteDataAnimated.get(spriteDataIndex),
                                                          frames[spriteDataIndex]);
                        }
                    }
                }
            }


            g2d.dispose();
        } //life > 0
    }

    private int drawLoopSegment(int start, int SEGMENT_POINTS, int xOffset, int yOffset,
                                @NotNull Point entryPoint, Point exitPoint){

        int SEGMENT_PER_QUARTER = SEGMENT_POINTS/4;


        Point[] controlPoints2 = new Point[]{
                entryPoint,
                new Point(entryPoint.x + xOffset, entryPoint.y),
                new Point(entryPoint.x + xOffset, entryPoint.y + yOffset),
        };
        drawSegment(start, SEGMENT_PER_QUARTER, controlPoints2);

        controlPoints2 = new Point[]{
                new Point(entryPoint.x + xOffset, entryPoint.y + yOffset),
                new Point(entryPoint.x + xOffset, entryPoint.y + yOffset*2),
                new Point(entryPoint.x, entryPoint.y + yOffset*2),
        };
        drawSegment(start+SEGMENT_PER_QUARTER, SEGMENT_PER_QUARTER, controlPoints2);

        controlPoints2 = new Point[]{
                new Point(entryPoint.x, entryPoint.y + yOffset*2),
                new Point(entryPoint.x - xOffset, entryPoint.y + yOffset*2),
                new Point(entryPoint.x - xOffset, entryPoint.y + yOffset),
        };
        drawSegment(start+2*SEGMENT_PER_QUARTER, SEGMENT_PER_QUARTER, controlPoints2);

        controlPoints2 = new Point[]{
                new Point(entryPoint.x - xOffset, entryPoint.y + yOffset),
                new Point(entryPoint.x - xOffset, entryPoint.y + yOffset/3),
                new Point(entryPoint.x, entryPoint.y + yOffset/3)
        };
        drawSegment(start+3*SEGMENT_PER_QUARTER, SEGMENT_PER_QUARTER, controlPoints2);


        if(exitPoint != null){
            controlPoints2 = new Point[]{
                    new Point(entryPoint.x, entryPoint.y + yOffset/3),
                    new Point(exitPoint.x, entryPoint.y + yOffset/3),
                    exitPoint
            };
            drawSegment(start+4*SEGMENT_PER_QUARTER, SEGMENT_PER_QUARTER, controlPoints2);

            return start+4*SEGMENT_PER_QUARTER+SEGMENT_PER_QUARTER;
        }

        return start+3*SEGMENT_PER_QUARTER+SEGMENT_PER_QUARTER;
    }

    /** draws the 'beam' shooting down onto caret line */
    private void drawLanternSegment(Graphics2D g2d, int start, Point controlPoint) {
        if (stemPointsToDraw < start) return;


        g2d.setPaint(this.c);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.c.getAlpha()/255f * 0.3f));
        Path2D path = new Path2D.Double();

//        if(moveWithCaret && this.y > controlPoint.y + 20) { //if we're 'below' lantern
        if(moveWithCaret) { //if we're 'below' lantern
            path.moveTo(controlPoint.x, controlPoint.y);
            Point p1 = new Point(this.x + 100, this.y);
            Point p2 = new Point(this.x - 100, this.y);
            if(this.y < controlPoint.y) {
                p1 = new Point(this.x + 100, this.y + lineHeight);
                p2 = new Point(this.x - 100, this.y + lineHeight);
            }
            path.lineTo(p1.x, p1.y);
            path.lineTo(p2.x, p2.y);
            path.lineTo(controlPoint.x, controlPoint.y);
        }else{
            path.moveTo(controlPoint.x, controlPoint.y);
            path.lineTo(controlPoint.x + 100 + randomNum, editor.getContentComponent().getHeight());
            path.lineTo(controlPoint.x - 100 + randomNum, editor.getContentComponent().getHeight());
            path.lineTo(controlPoint.x, controlPoint.y);

        }
        g2d.fill(path);
    }

    private int drawSegment(int start, int SEGMENT_POINTS, Point[] controlPoints){

        Point[] points = new Point[SEGMENT_POINTS];

        double t = 0.0;
        for (int i = 0; i < SEGMENT_POINTS; i++) {
            t += (1.0/SEGMENT_POINTS);
            Point p = this.quadTo(controlPoints[0], controlPoints[1], controlPoints[2], t);
            points[i] = p;
            pathPoints.add(p);
            if(i == SEGMENT_POINTS -1){
                jointPoints.add(p);
            }
        }
        return start+SEGMENT_POINTS;
    }





}
