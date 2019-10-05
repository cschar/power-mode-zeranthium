
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


/**
 * @author Baptiste Mesta
 *
 * modified by cschar
 */
public class Particle {
    int x;
    int y;
    final int dx;
    final int dy;
    final int size;
    int life;
    final Color c;
    //higher will be rendered first
    public int renderZIndex = 1;
    PowerMode3 settings;

    public Particle(int x, int y, int dx, int dy, int size, int life, Color c) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.size = size;
        this.life = life;
        this.c = c;
        settings = PowerMode3.getInstance();
    }

    public boolean update() {
        x += dx;
        y += dy;
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
