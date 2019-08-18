
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

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class ParticleSpriteLizardAnchor extends Particle{

    int anchorX;
    int anchorY;
    int initialX;
    int initialY;

    int maxLife;
    boolean spawnAtAnchor = false;

    int dir2anchorX;
    int dir2anchorY;

    public ParticleSpriteLizardAnchor(int x, int y, int dx, int dy, int anchorX, int anchorY, int size, int life, Color c) {
        super(x,y,dx,dy,size,life,c);

        this.maxLife = life;
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        this.initialX = x;
        this.initialY = y;

        this.dir2anchorX = ((anchorX - initialX)/70);
        this.dir2anchorY = (anchorY - initialY)/70;

        int randomNum = ThreadLocalRandom.current().nextInt(1, 100 +1);
////        System.out.println(randomNum);
//        if(randomNum < 2){
//            spawnAtAnchor = true;
//            this.x = this.anchorX;
//            this.y = this.anchorY;
//
//            this.life = 1000;
//        }

    }

    public boolean update() {
        if(spawnAtAnchor){
            x += dx;
            y += dy;
            life--;
            return life <= 0;
        }

        int distX = (anchorX - x);
        int distY = (anchorY - y);


        //strategy 1
        //Goes fast -> slow until distance 10.. then FAST
        if(Math.abs(distX) > 100){
            this.dir2anchorX = distX/50;
        }else if(Math.abs(distX) > 50){
            this.dir2anchorX = distX/10;
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


//        if(life > this.maxLife - 50){
//            x += dx;
//            y += dy;
//        }else{
            x += dir2anchorX;
            y += dir2anchorY;
//        }

        life--;
        return life <= 0;
    }

    public void render(Graphics g) {
        if (life > 0) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(c);
            g2d.fillRect(x - (size / 2), y - (size / 2), size, size);
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
