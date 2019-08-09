/*
 * Copyright 2015 Baptiste Mesta
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cschar.pmode3;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Baptiste Mesta
 */
public class ParticleContainer extends JComponent implements ComponentListener {

    private final JComponent parent;
    private final Editor editor;
    private boolean shakeDir;
    private ArrayList<Particle> particles = new ArrayList<>(50);

    public ParticleContainer(Editor editor) {
        this.editor = editor;
        parent = this.editor.getContentComponent();
        parent.add(this);
        updateBounds();
        setVisible(true);
        parent.addComponentListener(this);
    }

    private void shakeEditor(JComponent parent, int dx, int dy, boolean dir) {
        final Rectangle bounds = parent.getBounds();
        parent.setBounds(bounds.x + (dir ? -dx : dx), bounds.y + (dir ? -dy : dy), bounds.width, bounds.height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderParticles(g);
    }

    public void updateParticles() {
        if (!particles.isEmpty()) {
            ArrayList<Particle> tempParticles = new ArrayList<>(particles);
            final Iterator<Particle> particleIterator = tempParticles.iterator();
            while (particleIterator.hasNext()) {
                if (particleIterator.next().update()) {
                    particleIterator.remove();
                }
            }
            particles = tempParticles;
            this.repaint();
        }

    }

    public void addParticle(int x, int y) {
        //TODO configurable
        PowerMode3 settings = PowerMode3.getInstance();

        int dx, dy;
        dx = (int) (Math.random() * 4) * (Math.random() > 0.5 ? -1 : 1);
        dy = (int) (Math.random() * -3 - 1);

//        int size = (int) (Math.random() * 3 + 1);
        int size = (int) (Math.random() * settings.getParticleSize() + 1);
        int life = 45;
        int lifeSetting = settings.getLifetime();

        if (settings.getSpriteTypeEnabled(PowerMode3.SpriteType.LIGHTNING)){
            final ParticleSpriteLightning e = new ParticleSpriteLightning(x, y, dx, dy, size, lifeSetting, Color.ORANGE);
            particles.add(e);

        }

        if (settings.getSpriteTypeEnabled(PowerMode3.SpriteType.MOMA)){

            if(settings.getSpriteTypeDirections(PowerMode3.SpriteType.MOMA)[0]) {
                final ParticleSpriteMOMA e = new ParticleSpriteMOMA(x, y, dx, dy, size, lifeSetting, settings.particleColor);
                particles.add(e);
            }
            if(settings.getSpriteTypeDirections(PowerMode3.SpriteType.MOMA)[1]) {
                int momaDx = (int) (Math.random() * 4) * (Math.random() > 0.5 ? -1 : 1);
                int momaDy = (int) (Math.random() * 3 + 1);
                int momaY = y + 60;

                final ParticleSpriteMOMA e3 = new ParticleSpriteMOMA(x, momaY, momaDx, momaDy, size, lifeSetting, settings.particleColor);
                particles.add(e3);
            }
        }


        if(settings.getBasicParticleEnabled()) {
            final Particle e = new Particle(x, y, dx, dy, size, lifeSetting, settings.particleColor);
            particles.add(e);

            dx = (int) (Math.random() * 4) * (Math.random() > 0.5 ? -1 : 1);
            dy = (int) (Math.random() * 3 + 1);
            y = y + 20;

            final Particle e2 = new Particle(x, y, dx, dy, size, lifeSetting, settings.particleColor);
            particles.add(e2);
        }

    }

    public void renderParticles(Graphics g) {
        for (Particle particle : particles) {
            particle.render(g);
        }
    }

    public void update(Point point) {
        //TODO configurable
        for (int i = 0; i < 7; i++) {
            addParticle(point.x, point.y);
        }
        shakeEditor(parent, 5, 5, shakeDir);
        shakeDir = !shakeDir;
        this.repaint();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        updateBounds();

        Logger.getInstance(this.getClass()).info("Resized");

    }

    private void updateBounds() {
        ParticleContainer.this.setBounds(editor.getScrollingModel().getVisibleArea().getBounds());
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        updateBounds();
        Logger.getInstance(this.getClass()).info("Moved");

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}
