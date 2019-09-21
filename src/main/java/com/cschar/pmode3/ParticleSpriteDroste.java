
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
import com.intellij.notification.Notification;
import com.intellij.notification.Notifications;
import com.intellij.openapi.ui.Messages;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;


//TODO: rename to ReverseDrosteEffect ?
public class ParticleSpriteDroste extends Particle{

    private static ArrayList<BufferedImage> sprites;

    public static ArrayList<SpriteDataAnimated> spriteDataAnimated;

    static {
        //TODO load up sprites here if the list is empty....
        // the list will only be empty if an options panel as not been opened.

//        Messages.showInfoMessage("Loading Droste","Loading Droste Sprites");
//        Notification n = new Notification()?
//        Notifications.Bus.notify();
        sprites = new ArrayList<BufferedImage>();
        for(int i=1; i <= 60; i++){
            sprites.add(ParticleUtils.loadSprite(String.format("/blender/droste1/0%03d.png", i)));
        }
//        for(int i=1; i <= 1; i++){
//            sprites.add(ParticleUtils.loadSprite(String.format("/blender/droste2/0%03d.png", i)));
//        }

    }


    private static BufferedImage sprite;



    private int frame = 0;


    private float maxAlpha;


    public static int cursorX;
    public static int cursorY;

    static int[] margins = new int[4];
    static double[][] expandScales;

    public static int[] CUR_COUNT = new int[2];


    private int maxLife;
    private int frameLife;

    private int spriteDataIndex;
    public static int expandOffset;
//    private int frameSpeed;

    private SpriteDataAnimated d;
    private String spritePath;

    private int editorWidth;
    private int editorHeight;

    public static boolean needsUpdate = false;

    public ParticleSpriteDroste(int x, int y, int dx, int dy,
                                int size, int life, int spriteDataIndex, Color c,
                                int editorHeight, int editorWidth, int expandOffset) {
        super(x,y,dx,dy,size,life,c);
        this.maxLife = life;
        this.frameLife = 10000;
        this.editorHeight = editorHeight;
        this.editorWidth = editorWidth;
        cursorX = x;
        cursorY = y;
        ParticleSpriteDroste.expandOffset = expandOffset;

        sprite = spriteDataAnimated.get(spriteDataIndex).image;
        sprites = spriteDataAnimated.get(spriteDataIndex).images;
        this.spriteDataIndex = spriteDataIndex;
//        this.frameSpeed = spriteDataAnimated.get(spriteDataIndex).speedRate;
        this.d = spriteDataAnimated.get(spriteDataIndex);
        this.spritePath = d.customPath;
//        this.spriteScale = d.scale;

        //TODO option to 'rotate' and rotate speed
        //TODO inital scale increase in config

        //TODO
        CUR_COUNT[spriteDataIndex] += 1;
        recalculateExpandScales(editorWidth, editorHeight);
    }


    public static void recalculateExpandScales(int editorWidth, int editorHeight){
        margins[0] = cursorX - sprite.getWidth()/2;
        margins[1] = editorWidth - (cursorX + sprite.getWidth()/2);
        margins[2] = cursorY - sprite.getHeight() / 2;
        margins[3] = editorHeight - (cursorY + sprite.getHeight()/2);


        int maxMargin = Arrays.stream(margins).max().getAsInt();
        int expandSize = expandOffset;
        int expands = (maxMargin / expandSize) + 1;

        double currentX = sprite.getWidth();
        double currentY = sprite.getHeight();

//        System.out.println("Expands: " + expands + " Max found:" + maxMargin);
        expandScales = new double[expands][2];
        for(int i = 0; i < expands; i++){
            currentX += expandSize*2;
            currentY += expandSize*2;

            double scaleX = currentX/sprite.getWidth();
            double scaleY = currentY/sprite.getHeight();
//            System.out.print(scaleX + " ");
            expandScales[i][0] = scaleX;
            expandScales[i][1] = scaleY;
        }
    }

    public boolean update() {
//        If entire plugin is turned off
        if(!PowerMode3.getInstance().isEnabled()){
            CUR_COUNT[spriteDataIndex] -= 1;
            return true;
        }

        life--;
        frameLife--;

        if( this.frameLife % d.speedRate == 0){
            frame += 1;
            if (frame >= ParticleSpriteDroste.sprites.size()){
                frame = 0;
            }
            if(frameLife < 100){ //TODO can use greatest common denominator to sync up here
                frameLife = 10000;
            }
        }

        //ARHGHG my eyes
        //way to update without relying on "typing" triggering addParticle in ParticleContainer
        if(needsUpdate){
            if(d.val1 != expandOffset){ //val1 --> expandOffset
                expandOffset = d.val1;
                ParticleSpriteDroste.recalculateExpandScales(editorWidth, editorHeight);
            }
            needsUpdate = false;
        }

        boolean lifeOver = (life <= 0);
        if(lifeOver) { //ready to reset?
            if (d.isCyclic) {
                if(!d.enabled){
                    CUR_COUNT[spriteDataIndex] -= 1;
                    return true;
                }

                if(!d.customPath.equals(this.spritePath)){
                    //we've changed the sprites being cycled, so kill this particle
                    CUR_COUNT[spriteDataIndex] -= 1;
                    return true;
                }




                //cyclic: reset next cycle
                this.life = 99;
                return false;
            } else {
                CUR_COUNT[spriteDataIndex] -= 1;
            }
        }

        return lifeOver;
    }







    @Override
    public void render(Graphics g) {

        if (life > 0) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(c);

            //every X updates, increment frame, this controls how fast it animates




            AffineTransform at = new AffineTransform();
            at.translate((int)cursorX ,(int)cursorY);
            at.translate(-sprite.getWidth()/2,
                             -sprite.getHeight()/2); //move image 100% up so lightning lands at bottom

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, d.alpha));


            for(int i = expandScales.length-1; i >= 0; i--){
                double scaleX = expandScales[i][0];
                double scaleY = expandScales[i][1];

                AffineTransform atExpand = new AffineTransform();
                atExpand.scale(scaleX, scaleY);
                atExpand.translate((int) cursorX * (1/scaleX), (int) cursorY * (1/scaleY));

                atExpand.translate(-sprite.getWidth()/2,
                        -sprite.getHeight()/2); //around bracket height
//                atExpand.translate(-(sprite.getWidth()*(1/scaleX))/2,
//                        -(sprite.getHeight()*(1/scaleY))/2); //around bracket height
                g2d.drawImage(sprites.get(frame), atExpand, null);
            }


            g2d.drawImage(sprites.get(frame), at, null);


            g2d.dispose();
        }
    }

}
