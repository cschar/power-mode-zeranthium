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

import com.cschar.pmode3.config.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Baptiste Mesta
 *
 * Modified by cschar
 */
public class ParticleContainer extends JComponent implements ComponentListener {

    private final JComponent parent;
    private final Editor editor;
    private boolean shakeDir;
    ConcurrentLinkedQueue<Particle> particles = new ConcurrentLinkedQueue<Particle>();

//    private ArrayList<Particle> particles = new ArrayList<>(50);

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
//            ArrayList<Particle> tempParticles = new ArrayList<>(particles);
//            final Iterator<Particle> particleIterator = tempParticles.iterator();
//            while (particleIterator.hasNext()) {
//                if (particleIterator.next().update()) {
//                    particleIterator.remove();
//                }
//            }
//            particles = tempParticles;

//            ConcurrentLinkedQueue <Particle> tempParticles = new ConcurrentLinkedQueue<>(particles);
            final Iterator<Particle> particleIterator = particles.iterator();
            while (particleIterator.hasNext()) {
                if (particleIterator.next().update()) {
                    particleIterator.remove();
                }
            }
//            particles = tempParticles;
            this.repaint();
        }

    }

    public void addParticle(int x, int y, PowerMode3 settings) {


        int dx, dy;
        dx = (int) (Math.random() * 4) * (Math.random() > 0.5 ? -1 : 1);
        dy = (int) (Math.random() * -3 - 1);

        int size = 5;
//        int size = (int) (Math.random() * 3 + 1);
//        int size = (int) (Math.random() * settings.getParticleSize() + 1);
        int life = 45;
        int lifeSetting = settings.getLifetime();


        if(settings.getSpriteTypeEnabled(PowerMode3.SpriteType.MANDALA)){
            //update static value for all other rings still spawned.
            ParticleSpriteMandalaRing.cursorX = x;
            ParticleSpriteMandalaRing.cursorY = y;
            for(int i = 0; i <ParticleSpriteMandalaRing.mandalaRingData.size(); i++){

                final ParticleSpriteMandalaRing e = new ParticleSpriteMandalaRing(x,y,dx,dy,size,lifeSetting, i);
                particles.add(e);
            }
        }

        if (settings.getSpriteTypeEnabled(PowerMode3.SpriteType.LIGHTNING_ALT)){
            final ParticleSpriteLightningAlt e = new ParticleSpriteLightningAlt(x, y, dx, dy, size, lifeSetting,
                    Color.ORANGE,
                    LightningAltConfig.CHANCE_PER_KEY_PRESS(settings),
                    LightningAltConfig.MAX_ALPHA(settings),
                    LightningAltConfig.SPARKS_ENABLED(settings));
            particles.add(e);

        }

        if (settings.getSpriteTypeEnabled(PowerMode3.SpriteType.LIGHTNING)){
            final ParticleSpriteLightning e = new ParticleSpriteLightning(x, y, dx, dy, size, lifeSetting, Color.ORANGE,
                    LightningConfig.CHANCE_OF_LIGHTNING(settings),
                    LightningConfig.INNER_BEAM_ENABLED(settings),
                    LightningConfig.OUTER_BEAM_ENABLED(settings));
            particles.add(e);

        }

//        if (settings.getSpriteTypeEnabled(PowerMode3.SpriteType.LIZARD)){
//            final ParticleSpriteLizard e = new ParticleSpriteLizard(x, y, dx, dy, size, lifeSetting, Color.GREEN);
//            particles.add(e);
//
//        }

        if (settings.getSpriteTypeEnabled(PowerMode3.SpriteType.MOMA)){


            Color[] colors = new Color[]{
            MOMAConfig.ONE_SQUARE_COLOR(settings),
            MOMAConfig.TWO_SQUARE_COLOR(settings),
            MOMAConfig.THREE_SQUARE_COLOR(settings)
            };

            boolean[] enabled = new boolean[]{MOMAConfig.ONE_SQUARE_ENABLED(settings),
                                                MOMAConfig.TWO_SQUARE_ENABLED(settings),
                                                MOMAConfig.THREE_SQUARE_ENABLED(settings)};
            int momaY;
            if(MOMAConfig.EMIT_TOP(settings)) {
                momaY = y - 30;
                //LOL this constructor
                final ParticleSpriteMOMA e = new ParticleSpriteMOMA(x, momaY, dx, dy, size, lifeSetting,
                        colors, enabled);
                particles.add(e);
            }
            if(MOMAConfig.EMIT_BOTTOM(settings)) {
                int momaDx = (int) (Math.random() * 4) * (Math.random() > 0.5 ? -1 : 1);
                int momaDy = (int) (Math.random() * 3 + 1);
                momaY = y + 60;

                final ParticleSpriteMOMA e3 = new ParticleSpriteMOMA(x, momaY, momaDx, momaDy, size, lifeSetting,
                        colors, enabled);
                particles.add(e3);
            }
        }


        if (settings.getSpriteTypeEnabled(PowerMode3.SpriteType.BASIC_PARTICLE)) {
            int maxSize = BasicParticleConfig.MAX_PARTICLE_SIZE(settings);
            Color basicColor = BasicParticleConfig.BASIC_PARTICLE_COLOR(settings);
            int numParticles = BasicParticleConfig.NUM_PARTICLES(settings);

            for(int i = 0; i < numParticles; i++){

                size = (int) (Math.random() * maxSize + 1);

                if(BasicParticleConfig.EMIT_TOP(settings)){
                    dx = (int) (Math.random() * 4) * (Math.random() > 0.5 ? -1 : 1);
                    dy = (int) (Math.random() * -3 - 1);

                    final Particle e = new Particle(x, y, dx, dy, size, lifeSetting, basicColor);
                    particles.add(e);
                }
                if(BasicParticleConfig.EMIT_BOTTOM(settings)) {
                    dx = (int) (Math.random() * 4) * (Math.random() > 0.5 ? -1 : 1);
                    dy = (int) (Math.random() * 3 + 1);
//                    y = y + 20;
                    final Particle e2 = new Particle(x, y + 20, dx, dy, size, lifeSetting, basicColor);
                    particles.add(e2);
                }
            }

        }

    }

    public void addParticleUsingAnchors(int x, int y, Anchor[] anchors, PowerMode3 settings) {

        int dx, dy;
        dx = (int) (Math.random() * 4) * (Math.random() > 0.5 ? -1 : 1);
        dy = (int) (Math.random() * -3 - 1);

        int size = 5;
        int life = settings.getLifetime();


        int lizard_chance = LizardConfig.CHANCE_PER_KEY_PRESS(settings);
        int r = ThreadLocalRandom.current().nextInt(1, 100 +1);
        if (settings.getSpriteTypeEnabled(PowerMode3.SpriteType.LIZARD) && (r <= lizard_chance)){


//            String colorRGB = settings.getSpriteTypeProperty(PowerMode3.SpriteType.LIZARD, "lizardColor");
//            Color lizardColor = new Color(Integer.parseInt(colorRGB));

            int maxPsiSearch = LizardConfig.MAX_PSI_SEARCH(settings);
            int minPsiSearch = LizardConfig.MIN_PSI_SEARCH(settings);
            int chanceOfSpawn =  LizardConfig.CHANCE_OF_SPAWN(settings);
            int maxAnchorsToUse = LizardConfig.MAX_ANCHORS_TO_USE(settings);
            int anchorsUsed = 0;

            ArrayList<Anchor> anchorList = new ArrayList<Anchor>(Arrays.asList(anchors));
//            Collections.shuffle(anchorList);
//            for(Anchor a: anchorList){

            for(int i =0; i < anchorList.size(); i++){
                Anchor a = anchorList.get(i);
//            for(Anchor a: anchors){
                if(anchorsUsed >= maxAnchorsToUse) break;

                if( Math.abs(a.anchorOffset - a.cursorOffset) > (maxPsiSearch) ){
                    continue;
                }
                if( Math.abs(a.anchorOffset - a.cursorOffset) < (minPsiSearch) ){
                    continue;
                }

                r = ThreadLocalRandom.current().nextInt(1, 100 +1);
                if(r > chanceOfSpawn){
                    continue;
                }

                anchorsUsed += 1;
                final ParticleSpriteLizardAnchor e = new ParticleSpriteLizardAnchor(x, y, dx, dy, i,
                        size, life, Color.GREEN, anchors, particles);
                particles.add(e);
            }
        }

        int vine_chance = VineConfig.CHANCE_PER_KEY_PRESS(settings);
        r = ThreadLocalRandom.current().nextInt(1, 100 +1);
        if(settings.getSpriteTypeEnabled(PowerMode3.SpriteType.VINE) && (r <= vine_chance)){
            int minPsiSearch = VineConfig.MIN_PSI_SEARCH(settings);
            int maxPsiSearch = VineConfig.MAX_PSI_SEARCH(settings);
            for(Anchor a: anchors){
                if( Math.abs(a.anchorOffset - a.cursorOffset) > (maxPsiSearch) ){
                    continue;
                }
                if( Math.abs(a.anchorOffset - a.cursorOffset) < (minPsiSearch) ){
                    continue;
                }

                if(!VineConfig.GROW_FROM_RIGHT(settings) && ((x - a.p.x) < 0) ){
                    continue;
                }



                if(VineConfig.USE_SPRITE2(settings)) {
                    //TODO combine classes
                    final ParticleSpriteVineAnchorAnimated e = new ParticleSpriteVineAnchorAnimated(x, y, dx, dy, a.p.x, a.p.y,
                            size, life,
                            VineConfig.VINE_TOP_COLOR(settings),
                            VineConfig.VINE_BOTTOM_COLOR(settings),
                            VineConfig.USE_SPRITE2(settings));
                    particles.add(e);
                }else{
                    final ParticleSpriteVineAnchor e = new ParticleSpriteVineAnchor(x, y, dx, dy, a.p.x, a.p.y,
                            size, life,
                            VineConfig.VINE_TOP_COLOR(settings),
                            VineConfig.VINE_BOTTOM_COLOR(settings),
                            VineConfig.USE_SPRITE(settings));
                    particles.add(e);
                }
            }
        }


    }

    public void renderParticles(Graphics g) {
        for (Particle particle : particles) {
            particle.render(g);
        }
    }


//    public void update(Point point) {
//        //TODO configurable
//        for (int i = 0; i < 7; i++) {
//            addParticle(point.x, point.y);
//        }
//        shakeEditor(parent, 5, 5, shakeDir);
//        shakeDir = !shakeDir;
//        this.repaint();
//    }

    public void updateWithAnchors(Point point, Anchor[] anchors){
        PowerMode3 settings = PowerMode3.getInstance();

        //TODO call once, then have each spriteType use its own specified # of particles
//        for (int i = 0; i < settings.getNumOfParticles(); i++) {
            addParticle(point.x, point.y, settings);
//        }

        addParticleUsingAnchors(point.x, point.y, anchors, settings);

        shakeEditor(parent, settings.getShakeDistance(), settings.getShakeDistance(), shakeDir);
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
