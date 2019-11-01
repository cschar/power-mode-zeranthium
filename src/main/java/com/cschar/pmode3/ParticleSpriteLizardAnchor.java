
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

import com.cschar.pmode3.config.common.SpriteData;
import com.cschar.pmode3.config.common.SpriteDataAnimated;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

public class ParticleSpriteLizardAnchor extends Particle{





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
    private ConcurrentLinkedQueue<Particle> parentList;


    private int anchorIndex;
    private BufferedImage sprite;
    private ArrayList<BufferedImage> lizardSprites;
    private float scale=1.0f;
    private float alpha=1.0f;

    public ParticleSpriteLizardAnchor(int x, int y, int dx, int dy, int anchorIndex, int size, int life, Color c,
                                      Anchor[] anchors, ConcurrentLinkedQueue<Particle> parentList) {
        super(x,y,dx,dy,size,life,c);


        this.anchors = anchors;
        this.parentList = parentList;
        this.anchorIndex = anchorIndex;

        this.maxLife = life;
        this.anchorX = anchors[anchorIndex].p.x;
        this.anchorY = anchors[anchorIndex].p.y;

        this.initialX = x;
        this.initialY = y;

        this.dir2anchorX = ((anchorX - initialX)/70);
        this.dir2anchorY = (anchorY - initialY)/70;


        int winnerIndex = SpriteData.getWeightedAmountWinningIndex(spriteDataAnimated);
        if(winnerIndex == -1) {
            this.life = 0; // no sprites are enabled
        }else {

            this.scale = spriteDataAnimated.get(winnerIndex).scale;
            this.alpha = spriteDataAnimated.get(winnerIndex).alpha;
            lizardSprites = spriteDataAnimated.get(winnerIndex).images;
            sprite = lizardSprites.get(0);
        }

    }



    private int frameDir = 1;
    private int frame = 0;

    private boolean updateLizardAnim() {

        //every X updates, increment frame, this controls how fast it animates
        if( this.life % 3 == 0){
            frame += frameDir;
            if (frame >= lizardSprites.size()) {
                frame = lizardSprites.size() - 1;
            }else if(frame < 0){
                frame = 0;
            }
        }

        int distX = (anchorX - x);
        int distY = (anchorY - y);


        if(Math.abs(distX) > 100){
//            this.dir2anchorX = distX/50;
            this.dir2anchorX = distX/25;
        }else if(Math.abs(distX) > 50){
            this.dir2anchorX = distX/8;
        }else if(Math.abs(distX) > 10) {
            this.dir2anchorX = distX/5;
        }else{
            this.dir2anchorX = distX/2;
        }

        if(Math.abs(distY) > 100){
            this.dir2anchorY = distY/50;
        }else if(Math.abs(distY) > 50){
            this.dir2anchorY = distY/10;
        }else if(Math.abs(distY) > 10) {
            this.dir2anchorY = distY/5;
        }else{
            this.dir2anchorY = distY/2;
        }


        double dist = Math.sqrt(Math.pow((double) distX, 2.0f) + Math.pow((double) distY, 2.0f));
        if( dist > 40 ){
            //going to jumping
            frameDir = 1;
        }else{
            //going to standing
            frameDir = -1;
        }

        if(dir2anchorX == 0 && dir2anchorY == 0){
            life -= 6;   //if were standing still, decay faster
        }

        x += dir2anchorX;
        y += dir2anchorY;


        life--;

        if(life <= 0 && this.anchors != null){


            if(this.anchorIndex > (this.anchors.length / 2) + 1) {  //jump down
                if (this.anchorIndex < this.anchors.length - 1) { // don't spawn if we're at last anchor
//                int nextIndex = this.anchorIndex + 1;
                    int nextIndex = ThreadLocalRandom.current().nextInt(this.anchorIndex, this.anchors.length + 1);

                    if (nextIndex == this.anchors.length) {
                        //dont jump, just fade away
                    } else {
                        final ParticleSpriteLizardAnchor nextTransition = new ParticleSpriteLizardAnchor(this.x, this.y, 0, 0,
                                nextIndex, this.size, this.maxLife, this.c, anchors, this.parentList);

                        this.parentList.add(nextTransition);
                    }
                }
            }else{ //jump up
                if (this.anchorIndex != 0) { // don't spawn if we're at top of screen
//                int nextIndex = this.anchorIndex + 1;
                    int nextIndex = ThreadLocalRandom.current().nextInt(0, this.anchorIndex+1);

                    if (nextIndex == this.anchorIndex) {
                        //dont jump, just fade away
                    } else {
                        final ParticleSpriteLizardAnchor nextTransition = new ParticleSpriteLizardAnchor(this.x, this.y, 0, 0,
                                nextIndex, this.size, this.maxLife, this.c, anchors, this.parentList);

                        this.parentList.add(nextTransition);
                    }
                }
            }


        }
        return life <= 0;
    }

    public boolean update(){
        return this.updateLizardAnim();
    }



    public void render(Graphics g) {

        if (life > 0) {
            Graphics2D g2d = (Graphics2D) g.create();



            AffineTransform at = new AffineTransform();
            at.scale(scale, scale);
            at.translate((int) x * (1/scale), (int) y * (1/scale));

//            at.translate(-sprite.getWidth()/2,
//                    -sprite.getHeight()/2 - 15); //around bracket height
            at.translate(-sprite.getWidth()/2,
                    -sprite.getHeight()/2 - sprite.getHeight()/7); //around bracket height
            //TODO: use editor.getLineHeight
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.alpha));



            if(this.anchorX < this.initialX){ //flip image
                at.scale(-1.0f, 1.0f);
                at.translate(-1*sprite.getWidth(), 0);  //* -1 now actually sends it RIght on screen
            }


            g2d.drawImage(lizardSprites.get(frame), at, null);

            g2d.dispose();
        }
    }


}
