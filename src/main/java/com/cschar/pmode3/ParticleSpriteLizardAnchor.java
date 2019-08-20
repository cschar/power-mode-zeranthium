
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

import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class ParticleSpriteLizardAnchor extends Particle{






    static ArrayList<BufferedImage> sprites;

    static {

        sprites = new ArrayList<BufferedImage>();
        for(int i=1; i <= 10; i++){
            BufferedImage tmp = ParticleUtils.loadSprite(String.format("/blender/lizard/0%03d.png", i));
            BufferedImage resized_image =  Scalr.resize(tmp, Scalr.Method.BALANCED,
                    tmp.getWidth()/3, tmp.getHeight()/3);
            sprites.add(resized_image);

        }
        System.out.println("LizardSprites initialized");


    }


    int anchorX;
    int anchorY;
    int initialX;
    int initialY;

    int maxLife;


    int dir2anchorX;
    int dir2anchorY;

    private Anchor[] anchors;
    private ConcurrentLinkedQueue<Particle> parentList;
    private ParticleContainer parent;

    private int anchorIndex;

    private BufferedImage sprite;

    private Anchor anchorDest;

    public ParticleSpriteLizardAnchor(int x, int y, int dx, int dy, int anchorIndex, int size, int life, Color c,
                                      Anchor[] anchors, ConcurrentLinkedQueue<Particle> parentList) {
        super(x,y,dx,dy,size,life,c);
        sprite = sprites.get(0);

        this.anchors = anchors;
        this.parentList = parentList;

        this.anchorIndex = anchorIndex;

        this.maxLife = life;
        this.anchorX = anchors[anchorIndex].p.x;
        this.anchorY = anchors[anchorIndex].p.y;
//        this.anchorX = anchorX;
//        this.anchorY = anchorY;
        this.initialX = x;
        this.initialY = y;

        this.dir2anchorX = ((anchorX - initialX)/70);
        this.dir2anchorY = (anchorY - initialY)/70;

        astate = anim_state.standing;
    }


    enum anim_state {
        standing,
        jumping
    }

    private anim_state astate;
    private int frameDir = 1;
    private int frame = 0;

    private boolean updateLizardAnim() {


        int distX = (anchorX - x);
        int distY = (anchorY - y);


        //strategy 1
        //Goes fast -> slow until distance 10.. then FAST
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
            //g2d.setColor(c);
            //g2d.fillRect(origX - (size / 2), origY - (size / 2), size, size);


            AffineTransform at = new AffineTransform();

            at.translate(x , y );
            at.translate(-sprite.getWidth()/2,
                    -sprite.getHeight()/2 - 15); // around bracket height


            //every X updates, increment frame, this controls how fast it animates
            if( this.life % 3 == 0){
                frame += frameDir;
                if (frame >= 10) {
                    frame = 9;
                }else if(frame < 0){
                    frame = 0;
                }
            }

            if(this.anchorX < this.initialX){ //flip image
                at.scale(-1.0f, 1.0f);
                at.translate(-1*sprite.getWidth(), 0);  //* -1 now actually sends it RIght on screen
            }

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g2d.drawImage(sprites.get(frame), at, null);

//            if(frameDir > 0) {
//                g2d.drawImage(sprites.get(frame), at, null);
//            }
//            if(frameDir < 0){
//                g2d.drawImage(sprites.get(frame), at, null);
//            }




            g2d.dispose();
        }
    }

    @Override
    public String toString() {
        return "Particle{" +
                "x=" + x +
                ", y=" + y +
                ", dx=" + dx +
                ", dy=" + dy +
                ", size=" + size +
                ", life=" + life +
                ", c=" + c +
                '}';
    }
}
