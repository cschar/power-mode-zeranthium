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

import com.cschar.pmode3.actionHandlers.MyCaretListener;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.*;


import com.intellij.openapi.editor.event.EditorFactoryEvent;

import com.intellij.openapi.editor.event.EditorFactoryListener;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author Baptiste Mesta
 * <p>
 * modified by cschar
 */
public class ParticleContainerManager implements EditorFactoryListener, Disposable {
    private static final Logger LOGGER = Logger.getLogger(ParticleContainerManager.class.getName());

    private Thread thread;
    public static Map<Editor, ParticleContainer> particleContainers = new HashMap<>();
//    private Map<Editor, ParticleContainer> particleContainers = new HashMap<>();

    private PowerMode3 settings;

    public ParticleContainerManager(PowerMode3 settings) {
        LOGGER.info("\n\n----ParticleContainerManager initialized \n\n");
        this.settings = settings;

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    for (ParticleContainer particleContainer : particleContainers.values()) {
                        particleContainer.updateParticles();
                    }
                    try {
                        // 1000ms/60 = 16.6666666667      60 fps
                        Thread.sleep(17);
                    } catch (InterruptedException ignored) {
                        //thread interrupted, shutdown
                    } catch (ConcurrentModificationException modified) {
                        // ignore it
                    }
                }
            }

        });
        thread.start();
    }

    /**
     * Dirty way to simulate what happens when a new editor is created without having listener attached beforehand.
    */
    public void bootstrapEditor(Editor editor){
        //        LOGGER.warning("BOOSTRAP=====Editor Created with name:" + editor.toString());
//        EditorFactoryEvent ev = new EditorFactoryEvent(EditorFactory.getInstance(), e);
//        final Editor editor = ev.getEditor();

        particleContainers.put(editor, new ParticleContainer(editor));
        MyCaretListener cl = new MyCaretListener();
        MyCaretListener.enabled = true;
//        editor.getCaretModel().addCaretListener(cl);
        editor.getCaretModel().addCaretListener(cl, this);
    }


    @Override
    public void editorCreated(@NotNull EditorFactoryEvent event) {
        //        LOGGER.info("=====Editor Created with name:" + editor.toString());

        final Editor editor = event.getEditor();
        particleContainers.put(editor, new ParticleContainer(editor));
        MyCaretListener cl = new MyCaretListener();
        MyCaretListener.enabled = true;
        editor.getCaretModel().addCaretListener(cl, this);

    }

    @Override
    public void editorReleased(@NotNull EditorFactoryEvent event) {
        ParticleContainer pc = particleContainers.get(event.getEditor());
        LOGGER.info("releasing editor with particleContainer: " + pc);
        if(pc != null) {
            pc.cleanupParticles();
        }
        particleContainers.remove(event.getEditor());
    }

    public void update(final Editor editor) {

        if (settings.isEnabled()) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run()
                {
                    updateInUI(editor);
                }
            });
        }
    }

    private void updateInUI(Editor editor) {
        VisualPosition visualPosition = editor.getCaretModel().getVisualPosition();
        Point point = editor.visualPositionToXY(visualPosition);
        ScrollingModel scrollingModel = editor.getScrollingModel();
        point.x = point.x - scrollingModel.getHorizontalScrollOffset();
        point.y = point.y - scrollingModel.getVerticalScrollOffset();
        final ParticleContainer particleContainer = particleContainers.get(editor);

        if (particleContainer != null) {
//            particleContainer.update(point);

            Anchor[] anchors = getAnchors(editor, particleContainer);
            particleContainer.updateWithAnchors(point, anchors);
        }


    }


    public Anchor[] getAnchors(Editor editor, ParticleContainer particleContainer) {

        ScrollingModel scrollingModel = editor.getScrollingModel();


        //https://www.jetbrains.org/intellij/sdk/docs/basics/architectural_overview/psi_elements.html#how-do-i-get-a-psi-element
        int caretOffset = editor.getCaretModel().getOffset();

        ArrayList<Anchor> points = new ArrayList<Anchor>();
        int searchLength = settings.getMaxPsiSearchDistance();

        String documentText = editor.getDocument().getText();
        for (int i = 0; i < searchLength * 2; i++) {
            int anchorOffset = caretOffset - (searchLength) + i;
            if (anchorOffset <= 0) {
                continue;
            }
            if (anchorOffset >= documentText.length()) {
                break;
            }

            char c = documentText.charAt(anchorOffset);


            boolean addPoint = false;
            if (settings.anchorType == PowerMode3.AnchorTypes.BRACE
                    && (c == '{' || c == '}')) {
                addPoint = true;
            }

            if (settings.anchorType == PowerMode3.AnchorTypes.PARENTHESIS
                    && (c == '(' || c == ')')) {
                addPoint = true;
            }

            if (settings.anchorType == PowerMode3.AnchorTypes.BRACKET
                    && (c == '[' || c == ']')) {
                addPoint = true;
            }
            if (settings.anchorType == PowerMode3.AnchorTypes.COLON
                    && (c == ':')) {
                addPoint = true;
            }


            if (addPoint) {
                Point offsetPoint = editor.offsetToXY(anchorOffset);
                offsetPoint.x = offsetPoint.x - scrollingModel.getHorizontalScrollOffset();
                offsetPoint.y = offsetPoint.y - scrollingModel.getVerticalScrollOffset();

                Anchor anchor = new Anchor(offsetPoint, anchorOffset, caretOffset);
                points.add(anchor);
            }

        }


        return points.toArray(new Anchor[points.size()]);
    }

    public void dispose() {
        thread.interrupt();
        resetAllContainers();
        particleContainers.clear();
        LOGGER.info("Disposing ParticleContainerManager....");

    }

    public static void resetAllContainers() {
        for (ParticleContainer pc : particleContainers.values()) {
            pc.resetParticles();
        }
    }
}
