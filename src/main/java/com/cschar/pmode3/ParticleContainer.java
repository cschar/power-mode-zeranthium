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

import com.cschar.pmode3.listeners.MyCaretListener;
import com.cschar.pmode3.config.*;
import com.cschar.pmode3.config.common.SpriteDataAnimated;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.CaretListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Queue;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Baptiste Mesta
 *
 * Modified by cschar
 */
public class ParticleContainer extends JComponent implements ComponentListener, Disposable {
    private static final Logger LOGGER = Logger.getInstance(ParticleContainer.class);

    public final JComponent parentJComponent;
    private final Editor editor;
    private boolean shakeDir;
    private ConcurrentLinkedQueue<Particle> particles = new ConcurrentLinkedQueue<Particle>();

//    private ArrayList<Particle> particles = new ArrayList<>(50);

    private CaretListener myCaretListener;
    public ParticleContainer(Editor editor) {
        this.editor = editor;
        parentJComponent = this.editor.getContentComponent();
        parentJComponent.add(this);
        updateBounds();
        setVisible(true);
        parentJComponent.addComponentListener(this);

        myCaretListener = new MyCaretListener();
        MyCaretListener.enabled = true; //TODO make this non-static wtf lol
        editor.getCaretModel().addCaretListener(myCaretListener);
    }

    @Override
    public void dispose() {
        LOGGER.debug("Particle instance disposing..." + this);
        this.cleanupReferences();
    }

    public void cleanupReferences() {
        LOGGER.trace("Removing JComponent Listener");
        parentJComponent.removeComponentListener(this);
        parentJComponent.remove(this);
        LOGGER.trace("Removing Caret Listener");
        editor.getCaretModel().removeCaretListener(this.myCaretListener);
    }



    public void addExternalParticle(Particle p){
        this.particles.add(p);
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
        //todo clear all?
//        if(!PowerMode3.getInstance().isEnabled()){
//            particles.clear();
//            return;
//        }

        if (!particles.isEmpty()) {
//            ArrayList<Particle> tempParticles = new ArrayList<>(particles);
//            final Iterator<Particle> particleIterator = tempParticles.iterator();
//            while (particleIterator.hasNext()) {
//                if (particleIterator.next().update()) {
//                    particleIterator.remove();
//                }
//            }
//            particles = tempParticles;


            final Iterator<Particle> particleIterator = particles.iterator();
            while (particleIterator.hasNext()) {
                if (particleIterator.next().update()) {
                    particleIterator.remove();
                }
            }
            this.repaint();
        }

    }

    void resetParticles(){
        this.particles.clear();
        this.cleanupParticles();
    }

    void cleanupParticles(){

        ParticleSpriteLockedLayer.cleanup(this.editor);
        ParticleSpriteDroste.cleanup(this.editor);
        ParticleSpriteTapAnim.cleanup(this.editor);
        ParticleSpriteMultiLayer.cleanup(this.editor);
        ParticleSpriteLinkerAnchor.cleanup(this.editor);

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


        if (settings.getSpriteTypeEnabled(PowerMode3.ConfigType.BASIC_PARTICLE)) {
//            LOGGER.debug("adding basic_particle");

            int maxSize = settings.BASIC_PARTICLE.maxParticleSize;
            Color basicColor = settings.BASIC_PARTICLE.basicColor;
            int numParticles = settings.BASIC_PARTICLE.numParticles;

            for(int i = 0; i < numParticles; i++){

                size = (int) (Math.random() * maxSize + 1);


                if(settings.BASIC_PARTICLE.emitTop){
                    dx = (int) (Math.random() * 4) * (Math.random() > 0.5 ? -1 : 1);
                    dy = (int) (Math.random() * -3 - 1);

                    final Particle e = new Particle(x, y, dx, dy, size, lifeSetting, basicColor);
                    particles.add(e);
                }

                if(settings.BASIC_PARTICLE.emitBot){
                    dx = (int) (Math.random() * 4) * (Math.random() > 0.5 ? -1 : 1);
                    dy = (int) (Math.random() * 3 + 1);
//                    y = y + 20;
                    final Particle e2 = new Particle(x, y + 20, dx, dy, size, lifeSetting, basicColor);
                    particles.add(e2);
                }
            }
        }

        if(settings.getSpriteTypeEnabled(PowerMode3.ConfigType.TAP_ANIM)){
//            LOGGER.trace("adding tap_anim");

            ParticleSpriteTapAnim.updateCursor(this.editor, x, y);
            ParticleSpriteTapAnim.incrementFrame(this.editor);

            for(int i = 0; i < ParticleSpriteTapAnim.spriteDataAnimated.size(); i++){

                SpriteDataAnimated d = ParticleSpriteTapAnim.spriteDataAnimated.get(i);

                if(!d.enabled) continue;

                if(ParticleSpriteTapAnim.spawnMap.get(this.editor) == null) {
                    final ParticleSpriteTapAnim eTapAnim = new ParticleSpriteTapAnim(x, y, dx, dy, size, lifeSetting, i,
                            Color.ORANGE, editor);
                    particles.add(eTapAnim);
                }
            }
        }


        if(settings.getSpriteTypeEnabled(PowerMode3.ConfigType.LANTERN)) {

            boolean doCreate = false;
            if(ParticleSpriteLantern.spawnMap.get(this.editor) == null){
                doCreate = true;
            }

            if(ParticleSpriteLantern.spawnMap.get(this.editor) != null &&
                    LanternConfig.MAX_PARTICLES(settings) > ParticleSpriteLantern.spawnMap.get(this.editor)){
                doCreate = true;
            }
            if(doCreate){
                final ParticleSpriteLantern l = new ParticleSpriteLantern(x, y, dx, dy, size, lifeSetting,
                        LanternConfig.TRACER_COLOR(settings),
                        editor,
                        LanternConfig.TRACER_ENABLED(settings),
                        LanternConfig.MOVE_WITH_CARET(settings),
                        LanternConfig.CARET_MOVE_SPEED(settings),
                        LanternConfig.IS_CYCLIC_ENABLED(settings),
                        LanternConfig.ADD_LOOP(settings));
                particles.add(l);
            }

        }


        if(settings.getSpriteTypeEnabled(PowerMode3.ConfigType.LOCKED_LAYER)) {

                for(int i = 0; i < ParticleSpriteLockedLayer.spriteDataAnimated.size(); i++){
                    SpriteDataAnimated d = ParticleSpriteLockedLayer.spriteDataAnimated.get(i);
                    if(!d.enabled) continue;
                    if(ParticleSpriteLockedLayer.spawnMap.get(this.editor) == null
                            || ParticleSpriteLockedLayer.spawnMap.get(this.editor)[i] == 0) {

                        final ParticleSpriteLockedLayer p = new ParticleSpriteLockedLayer(x, y, dx, dy, size, lifeSetting,
                                i, editor);
                        particles.add(p);
                    }
                }
        }


        if(settings.getSpriteTypeEnabled(PowerMode3.ConfigType.DROSTE)){

//            int h = this.editor.getContentComponent().getHeight();
//            int w = this.editor.getContentComponent().getWidth();
            int h = this.editor.getComponent().getHeight(); // has gutter
            int w = this.editor.getComponent().getWidth();

            ParticleSpriteDroste.cursorX = x;
            ParticleSpriteDroste.cursorY = y;


            boolean anyEnabled = false;

            for(int i = 0; i < ParticleSpriteDroste.spriteDataAnimated.size(); i++){

                SpriteDataAnimated d = ParticleSpriteDroste.spriteDataAnimated.get(i);

                if(!d.enabled) continue;

                anyEnabled = true;
                if(ParticleSpriteDroste.spawnMap.get(this.editor) == null) {
                    final ParticleSpriteDroste eVoid = new ParticleSpriteDroste(x, y, dx, dy, size, lifeSetting, i,
                            Color.ORANGE, h, w, d.val1, editor);
                    particles.add(eVoid);
                }
            }
            if(anyEnabled) {
                //TODO: add custom CaretListeners for each Type of ParticleSprite?
                ParticleSpriteDroste.recalculateExpandScales(w, h);
            }
        }

        if(settings.getSpriteTypeEnabled(PowerMode3.ConfigType.MULTI_LAYER)){
            //update static value for all other rings still spawned.
//            ParticleSpriteMandala.cursorX = x;
//            ParticleSpriteMandala.cursorY = y;
            for(int i = 0; i < ParticleSpriteMultiLayer.spriteDataAnimated.size(); i++){

                SpriteDataAnimated d = ParticleSpriteMultiLayer.spriteDataAnimated.get(i);
                if (ParticleSpriteMultiLayer.spawnMap.get(this.editor) != null &&
                        ParticleSpriteMultiLayer.spawnMap.get(this.editor)[i] >= d.val2){
                    continue;
                }

                if(!d.enabled) continue;


                final ParticleSpriteMultiLayer e = new ParticleSpriteMultiLayer(x,y,dx,dy,size,lifeSetting, i, editor);
                particles.add(e);
            }
        }

        if(settings.getSpriteTypeEnabled(PowerMode3.ConfigType.MULTI_LAYER_CHANCE)){

            for(int i = 0; i < ParticleSpriteMultiLayerChance.spriteDataAnimated.size(); i++){
                SpriteDataAnimated d = ParticleSpriteMultiLayerChance.spriteDataAnimated.get(i);
                if(!d.enabled) continue;


                int r = ThreadLocalRandom.current().nextInt(1, MultiLayerChanceConfig.MAX_SPAWN_CHANCE +1);
                if(d.val1 >= r){
                    final ParticleSpriteMultiLayerChance e = new ParticleSpriteMultiLayerChance(x,y,dx,dy,size,lifeSetting, i, editor);
                    particles.add(e);
                }
            }
        }


        if (settings.getSpriteTypeEnabled(PowerMode3.ConfigType.MOMA)){


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




    }

    public void addParticleUsingAnchors(int x, int y, Anchor[] anchors, PowerMode3 settings) {

        int dx, dy;
        dx = (int) (Math.random() * 4) * (Math.random() > 0.5 ? -1 : 1);
        dy = (int) (Math.random() * -3 - 1);

        int size = 5;
        int life = settings.getLifetime();


        int lizard_chance = LizardConfig.CHANCE_PER_KEY_PRESS(settings);
        int r = ThreadLocalRandom.current().nextInt(1, 100 +1);
        if (settings.getSpriteTypeEnabled(PowerMode3.ConfigType.LIZARD) && (r <= lizard_chance)){


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
        if(settings.getSpriteTypeEnabled(PowerMode3.ConfigType.VINE) && (r <= vine_chance)){
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




                final ParticleSpriteVineAnchor e = new ParticleSpriteVineAnchor(x, y, dx, dy, a.p.x, a.p.y,
                        size, life,
                        VineConfig.VINE_TOP_COLOR(settings),
                        VineConfig.VINE_BOTTOM_COLOR(settings),
                        VineConfig.USE_SPRITE(settings));
                particles.add(e);

            }
        }



//        int linkerI_chance = LinkerConfig.CHANCE_PER_KEY_PRESS(settings);
//        r = ThreadLocalRandom.current().nextInt(1, 100 +1);
//        if(settings.getSpriteTypeEnabled(PowerMode3.SpriteType.LINKER) && (r <= linkerI_chance)){
        if(settings.getSpriteTypeEnabled(PowerMode3.ConfigType.LINKER)){

            ParticleSpriteLinkerAnchor.typeX = x;
            ParticleSpriteLinkerAnchor.typeY = y;

            ArrayList<Anchor> validAnchors = new ArrayList<>();
            int minPsiSearch = LinkerConfig.MIN_PSI_SEARCH(settings);
            int maxPsiSearch = LinkerConfig.MAX_PSI_SEARCH(settings);

            for (Anchor a : anchors) {
                if (Math.abs(a.anchorOffset - a.cursorOffset) > (maxPsiSearch)) {
                    continue;
                }
                if (Math.abs(a.anchorOffset - a.cursorOffset) < (minPsiSearch)) {
                    continue;
                }
                validAnchors.add(a);
            }

            boolean createParticle = true;
            if(LinkerConfig.IS_CYCLIC_ENABLED(settings)){
                //TODO make a config init() on each particle that loads when plugin launches
                //so its loaded on startup without cranking open config
                ParticleSpriteLinkerAnchor.MAX_CYCLE_PARTICLES = LinkerConfig.MAX_CYCLE_PARTICLES(settings);
                ParticleSpriteLinkerAnchor.cyclicToggleEnabled = true;

                Integer CUR_PARTICLES = ParticleSpriteLinkerAnchor.spawnMap.get(this.editor);
                if( CUR_PARTICLES != null && (CUR_PARTICLES >= ParticleSpriteLinkerAnchor.MAX_CYCLE_PARTICLES)) {
                    createParticle = false;
                }
            }

            if(createParticle){
                final ParticleSpriteLinkerAnchor e = new ParticleSpriteLinkerAnchor(x, y, dx, dy,
                        size, life, LinkerConfig.TRACER_COLOR(settings), validAnchors,
                        LinkerConfig.DISTANCE_FROM_CENTER(settings),
                        LinkerConfig.MAX_LINKS(settings),
                        LinkerConfig.WOBBLE_AMOUNT(settings),
                        LinkerConfig.TRACER_ENABLED(settings),
                        LinkerConfig.CURVE1_AMOUNT(settings),
                        LinkerConfig.IS_CYCLIC_ENABLED(settings),
                        this.editor);
                particles.add(e);
            }




        }

    }

    public void renderParticles(Graphics g) {
//        for (Particle particle : particles) {
//            particle.render(g);
//        }
        Queue<Particle> lowZIndex = new LinkedList<>();
        Queue<Particle> highZIndex = new LinkedList<>();
        for (Particle particle : particles) {
            if(particle.renderZIndex == 1){
                lowZIndex.add(particle);
            }
            if(particle.renderZIndex == 2){
                highZIndex.add(particle);
            }
        }

        for(Particle p: lowZIndex){
            p.render(g);
        }

        for(Particle p: highZIndex){
            p.render(g);
        }

    }


    public void updateWithAnchors(Point point, Anchor[] anchors){
        PowerMode3 settings = PowerMode3.getInstance();

        addParticle(point.x, point.y, settings);
        addParticleUsingAnchors(point.x, point.y, anchors, settings);

        shakeEditor(parentJComponent, settings.getShakeDistance(), settings.getShakeDistance(), shakeDir);
        shakeDir = !shakeDir;
        this.repaint();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        updateBounds();
//        Logger.getInstance(this.getClass()).info("Resized");
    }

    private void updateBounds() {
        ParticleContainer.this.setBounds(editor.getScrollingModel().getVisibleArea().getBounds());
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        updateBounds();
    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }


}
