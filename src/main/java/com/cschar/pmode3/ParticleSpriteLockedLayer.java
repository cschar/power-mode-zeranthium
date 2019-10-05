
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
import org.imgscalr.Scalr;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;



public class ParticleSpriteLockedLayer extends Particle{

    private static ArrayList<BufferedImage> sprites = new ArrayList<BufferedImage>();

    public static ArrayList<SpriteDataAnimated> spriteDataAnimated;



    private static BufferedImage sprite;
    private int frame = 0;
    public static int cursorX;
    public static int cursorY;



    private int frameLife;
    private int spriteDataIndex;

    private SpriteDataAnimated d;
    private String spritePath;

    private int editorWidth;
    private int editorHeight;

    private PowerMode3 settings;

    private Editor editor;

//    private ArrayList<>

    public ParticleSpriteLockedLayer(int x, int y, int dx, int dy,
                                     int size, int life, int spriteDataIndex, Color c,
                                     Editor editor) {
        super(x,y,dx,dy,size,life,c);
        //TODO add to parent class
        settings = PowerMode3.getInstance();

        this.editor = editor;
        this.frameLife = 10000;

        cursorX = x;
        cursorY = y;



        sprites = spriteDataAnimated.get(spriteDataIndex).images;
        this.spriteDataIndex = spriteDataIndex;

        this.d = spriteDataAnimated.get(spriteDataIndex);
        this.spritePath = d.customPath;

        sprite = spriteDataAnimated.get(spriteDataIndex).image;

    }





    public boolean update() {
//        If entire plugin is turned off

        if(!settings.isEnabled()){
            //CUR_COUNT[spriteDataIndex] -= 1;
            return true;
        }

        if(!settings.getSpriteTypeEnabled(PowerMode3.ConfigType.LOCKED_LAYER)){
            //CUR_COUNT[spriteDataIndex] -= 1;
            return true;
        }

        life--;
        frameLife--;

        if( this.frameLife % d.speedRate == 0){
            frame += 1;
            if (frame >= ParticleSpriteLockedLayer.sprites.size()){
                frame = 0;
            }
            if(frameLife < 100){ //TODO can use greatest common denominator to sync up here
                frameLife = 10000;
            }
        }


        boolean lifeOver = (life <= 0);
        if(lifeOver) { //ready to reset?
            if (d.isCyclic) {
                if(!d.enabled){
                //    CUR_COUNT[spriteDataIndex] -= 1;
                    return true;
                }

                if(!d.customPath.equals(this.spritePath)){
                    //we've changed the sprites being cycled, so kill this particle
                  //  CUR_COUNT[spriteDataIndex] -= 1;
                    return true;
                }




                //cyclic: reset next cycle
                this.life = 99;
                return false;
            } else {
              //  CUR_COUNT[spriteDataIndex] -= 1;
            }
        }

        return lifeOver;
    }







    @Override
    public void render(Graphics g) {

        if (life > 0) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(c);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, d.alpha));



            //Finally draw original sprite
            AffineTransform at = new AffineTransform();


            //stretch
            Component ec = editor.getComponent();

            double widthScale = ec.getWidth() / (double) sprite.getWidth();
            double heightScale = ec.getHeight() / (double) sprite.getHeight();

            Point midPoint = new Point(ec.getBounds().x + ec.getWidth()/2, ec.getBounds().y + ec.getHeight()/2);
            g2d.drawRect(midPoint.x, midPoint.y, 30, 30);

            at.scale(widthScale, heightScale);
            at.translate((int)midPoint.x*(1/widthScale) ,(int)midPoint.y *(1/heightScale));

            at.translate(-sprite.getWidth()/2,
                             -sprite.getHeight()/2);

            g2d.drawImage(sprites.get(frame), at, null);


            g2d.dispose();
        }
    }

}
