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

import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.event.EditorFactoryAdapter;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.psi.*;

import com.intellij.psi.util.PsiElementFilter;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Baptiste Mesta
 *
 * modified by cschar
 */
class ParticleContainerManager extends EditorFactoryAdapter {

    private Thread thread;
    private Map<Editor, ParticleContainer> particleContainers = new HashMap<>();

    private PowerMode3 settings;

    public ParticleContainerManager() {
        //this.settings = PowerMode3.getInstance();

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    for (ParticleContainer particleContainer : particleContainers.values()) {
                        particleContainer.updateParticles();
                    }
                    try {
                        Thread.sleep(1000 / 60);
                    } catch (InterruptedException ignored) {
                        //thread interrupted, shutdown
                    }
                }
            }

        });
        thread.start();
    }

    @Override
    public void editorCreated(@NotNull EditorFactoryEvent event) {
        final Editor editor = event.getEditor();

        particleContainers.put(editor, new ParticleContainer(editor));
    }

    @Override
    public void editorReleased(@NotNull EditorFactoryEvent event)
    {
        particleContainers.remove(event.getEditor());
    }

    public void update(final Editor editor, PsiFile psiFile) {

        if (PowerMode3.getInstance().isEnabled())
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    updateInUI(editor, psiFile);
                }
            });
    }

    private void updateInUI(Editor editor, PsiFile psiFile) {
        VisualPosition visualPosition = editor.getCaretModel().getVisualPosition();
        Point point = editor.visualPositionToXY(visualPosition);
        ScrollingModel scrollingModel = editor.getScrollingModel();
        point.x = point.x - scrollingModel.getHorizontalScrollOffset();
        point.y = point.y - scrollingModel.getVerticalScrollOffset();
        final ParticleContainer particleContainer = particleContainers.get(editor);

        if (particleContainer != null) {
//            particleContainer.update(point);

            Point[] anchors = getAnchors(psiFile,editor,particleContainer);
            particleContainer.updateWithAnchors(point, anchors);
        }


    }

    public class AnchorPoint{
        Point p;
        int anchorOffset;
        int cursorOffset;
    }

    public Point[] getAnchors(PsiFile psiFile, Editor editor, ParticleContainer particleContainer){

        ScrollingModel scrollingModel = editor.getScrollingModel();

        PsiElement dummyHead;
        PsiElement firstChild = psiFile.getFirstChild();
        dummyHead = firstChild;
        ArrayList<PsiElement> allTopLevelElements = new ArrayList<PsiElement>();

        while(firstChild != null){
            allTopLevelElements.add(firstChild);
            firstChild = firstChild.getNextSibling();
        }
        System.out.println(String.format("top level children length: %d", allTopLevelElements.size()));

        for ( PsiElement e: allTopLevelElements) {
            System.out.print(String.format("%s (%s) - ", e, e.getClass()));
//            System.out.print("-- text: " + e.getText()); //gets all text of class from start to end bracket
            // class is of type PsiClassImpl
        }
        System.out.println();

//        PsiClass

        //PsiUtil.getTopLevelClass()

        //System.out.println(String.format("filtered elements: %d", PsiTreeUtil.collectElements(dummyHead,psf).length));

        //https://www.jetbrains.org/intellij/sdk/docs/basics/architectural_overview/psi_elements.html#how-do-i-get-a-psi-element
        int offset = editor.getCaretModel().getOffset();

        ArrayList<Point> points = new ArrayList<Point>();
//        int searchLength = 200;
        int searchLength = PowerMode3.getInstance().getMaxPsiSearchDistance();
//        int searchLength = settings.getMaxPsiSearchDistance();

        for(int i =0; i < searchLength; i++){
            int surroundOffset = offset - (searchLength/2) + i;
            if(surroundOffset <= 0){ continue; }
            PsiElement e = PsiUtil.getElementAtOffset(psiFile, surroundOffset);
            if(e.toString() == "PsiWhiteSpace"){
                continue;
            }

            if(e.toString().contains("PsiJavaToken:RBRACE") || e.toString().contains("PsiJavaToken:LBRACE")){
                System.out.print(String.format(" %d - ", i));
                Point offsetPoint = editor.offsetToXY(surroundOffset);
                offsetPoint.x = offsetPoint.x - scrollingModel.getHorizontalScrollOffset();
                offsetPoint.y = offsetPoint.y - scrollingModel.getVerticalScrollOffset();

                points.add(offsetPoint);
                //particleContainer.update(offsetPoint);
                //need some method like
                // particleContainer.updateANCHORS(offsetPoint);
                // then any particle in system that users "Anchors" will act accordingly
                //  E.g   LizardSpriteAnchor  --> towards anchor movement
                //        LizardSprite      ---> normal movement
            }


//            PsiJavaToken:SEMICOLON
            //PsiJavaToken:RBRACE
            //PsiJavaToken:LBRACE
        }


        return points.toArray(new Point[points.size()]);
    }

    public void dispose() {
        thread.interrupt();
        particleContainers.clear();
    }
}
