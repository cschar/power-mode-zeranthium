
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
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class ParticleSpritePasteShape extends Particle{

    public static ArrayList<SpriteDataAnimated> spriteDataAnimated;

    private ArrayList<BufferedImage> sprites = new ArrayList<>();


    private int frame = 0;
    private float maxAlpha;
    private Shape shape;


    private int winningIndex;

    private SpriteDataAnimated d;
    private BufferedImage sprite;
    private float fadeAmount;
    private boolean fadeColorEnabled;
    private int speedRate;
    private Editor editor;

    public ParticleSpritePasteShape(Shape shape, int life, Color c, float fadeAmount, boolean fadeColorEnabled, int winningIndex, Editor editor) {
        super(0,0,0,0,0,life,c);

        this.fadeAmount = fadeAmount;
        this.fadeColorEnabled = fadeColorEnabled;
        this.shape = shape;
        this.winningIndex = winningIndex;
        this.editor = editor;
        if(winningIndex != -1) {
            d = spriteDataAnimated.get(winningIndex);
            sprites = spriteDataAnimated.get(winningIndex).images;
            this.maxAlpha = d.alpha;
            speedRate = d.speedRate;
        }else{
            this.maxAlpha = 1.0f;
            sprites = new ArrayList<>();
            speedRate = 2;
        }
        sprite = sprites.get(0);
    }

    public boolean update() {
        //every X updates, increment frame, this controls how fast it animates
        if( this.life % speedRate == 0){
            frame += 1;
            if (frame >= sprites.size()){
                frame = 0;
            }
            this.maxAlpha -= fadeAmount;
            if(this.maxAlpha < 0){
                this.maxAlpha = 0.0f;
                this.life = 0;
            }
        }


        life--;
        return life <= 0;
    }







    @Override
    public void render(Graphics g) {

        if (life > 0) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(c);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.maxAlpha));

            if(this.fadeColorEnabled) {
                g2d.fill(this.shape);
            }


            if(d != null) {
                Rectangle bounds = this.shape.getBounds();
                int x = bounds.x + bounds.width / 2;
                int y = bounds.y + bounds.height / 2;


                g2d.setClip(this.shape);
                //TODO rename isCyclic to boolGeneric1
                if (d.isCyclic) { //isCyllic is boolean placeholder for 'center to shape'

                    AffineTransform at = new AffineTransform();
                    at.scale(d.scale, d.scale);
                    at.translate((int) x * (1 / d.scale), (int) y * (1 / d.scale));


                    at.translate(-sprites.get(frame).getWidth() / 2,
                            -sprites.get(frame).getHeight() / 2);
                    g2d.drawImage(sprites.get(frame), at, null);

                } else {
                    Rectangle editorBounds = editor.getComponent().getBounds();

                    bounds.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);

                    AffineTransform at = new AffineTransform();
                    double widthScale = (editorBounds.width) / (double) sprite.getWidth();
                    double heightScale = editorBounds.height / (double) sprite.getHeight();
                    Point midPoint = new Point(editorBounds.width / 2, editorBounds.height / 2);
                    at.scale(widthScale, heightScale);
                    at.translate((int) midPoint.x * (1 / widthScale), (int) midPoint.y * (1 / heightScale));

                    at.translate(-sprite.getWidth() / 2,
                            -sprite.getHeight() / 2);
                    g2d.drawImage(sprites.get(frame), at, null);
                }
            }



            g2d.dispose();
        }
    }

}
