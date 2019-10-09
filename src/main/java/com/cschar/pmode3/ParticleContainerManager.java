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
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.intellij.openapi.editor.event.EditorFactoryAdapter;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.progress.impl.ProgressManagerImpl;
import com.intellij.openapi.progress.util.BackgroundTaskUtil;
import com.intellij.openapi.vcs.changes.BackgroundFromStartOption;
import com.intellij.psi.*;

import com.intellij.psi.util.PsiUtil;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.intellij.util.concurrency.NonUrgentExecutor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * @author Baptiste Mesta
 *
 * modified by cschar
 */
public class ParticleContainerManager extends EditorFactoryAdapter {

    private Thread thread;
    public static Map<Editor, ParticleContainer> particleContainers = new HashMap<>();
//    private Map<Editor, ParticleContainer> particleContainers = new HashMap<>();

    private PowerMode3 settings;

    public ParticleContainerManager(PowerMode3 settings) {
        this.settings = settings;

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
                    } catch (ConcurrentModificationException modified){
                        // ignore it
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

        MyCaretListener cl = new MyCaretListener();
        MyCaretListener.enabled = true;

        editor.getCaretModel().addCaretListener(cl);

    }

    @Override
    public void editorReleased(@NotNull EditorFactoryEvent event)
    {
        ParticleContainer pc = particleContainers.get(event.getEditor());

        pc.cleanupParticles();

        particleContainers.remove(event.getEditor());


    }

    public void update(final Editor editor, PsiFile psiFile) {

        if (PowerMode3.getInstance().isEnabled()) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    updateInUI(editor, psiFile);
                }
            });
        }
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

            Anchor[] anchors = getAnchors(psiFile,editor,particleContainer);
            particleContainer.updateWithAnchors(point, anchors);
        }


    }

    public Anchor[] getAnchors(PsiFile psiFile, Editor editor, ParticleContainer particleContainer){

        ScrollingModel scrollingModel = editor.getScrollingModel();

//        PsiElement firstChild = psiFile.getFirstChild();
//        ArrayList<PsiElement> allTopLevelElements = new ArrayList<PsiElement>();
//
//        while(firstChild != null){
//            allTopLevelElements.add(firstChild);
//            firstChild = firstChild.getNextSibling();
//        }
//        System.out.println(String.format("top level children length: %d", allTopLevelElements.size()));
//        for ( PsiElement e: allTopLevelElements) {
//            System.out.print(String.format("%s (%s) - ", e, e.getClass()));
//            System.out.print("-- text: " + e.getText()); //gets all text of class from start to end bracket
            // class is of type PsiClassImpl
//        }
//        System.out.println();
//        PsiClass
        //PsiUtil.getTopLevelClass()


        //https://www.jetbrains.org/intellij/sdk/docs/basics/architectural_overview/psi_elements.html#how-do-i-get-a-psi-element
        int caretOffset = editor.getCaretModel().getOffset();

        ArrayList<Anchor> points = new ArrayList<Anchor>();
//        int searchLength = 200;
        int searchLength = PowerMode3.getInstance().getMaxPsiSearchDistance();
//        int searchLength = settings.getMaxPsiSearchDistance();

        for(int i =0; i < searchLength*2; i++){
            int anchorOffset = caretOffset - (searchLength) + i;
            if(anchorOffset <= 0){ continue; }
            PsiElement e = PsiUtil.getElementAtOffset(psiFile, anchorOffset);
            if(e.toString() == "PsiWhiteSpace"){
                continue;
            }



            boolean addPoint = false;
            if(settings.anchorType == PowerMode3.AnchorTypes.BRACE
                    && (e.toString().contains("PsiJavaToken:RBRACE") || e.toString().contains("PsiJavaToken:LBRACE"))){
                addPoint = true;
            }

            if(settings.anchorType == PowerMode3.AnchorTypes.PARENTHESIS
                    && (e.toString().contains("PsiJavaToken:RPARENTH") || e.toString().contains("PsiJavaToken:LPARENTH"))){
                addPoint = true;
            }

            if(settings.anchorType == PowerMode3.AnchorTypes.BRACKET
                    && (e.toString().contains("PsiJavaToken:RBRACKET") || e.toString().contains("PsiJavaToken:LBRACKET"))){
                addPoint = true;
            }
            if(settings.anchorType == PowerMode3.AnchorTypes.COLON
                    && (e.toString().contains("PsiJavaToken:COLON"))) {
                addPoint = true;
            }

//            if(addPoint){
//                System.out.println("anchorType: " + settings.anchorType + " - " + anchorOffset + "FOUND: " + e.toString());
//            }


            if(addPoint){
                Point offsetPoint = editor.offsetToXY(anchorOffset);
                offsetPoint.x = offsetPoint.x - scrollingModel.getHorizontalScrollOffset();
                offsetPoint.y = offsetPoint.y - scrollingModel.getVerticalScrollOffset();

                Anchor anchor = new Anchor(offsetPoint,anchorOffset, caretOffset);
                points.add(anchor);
            }

        }


        return points.toArray(new Anchor[points.size()]);
    }

    public void dispose() {
        thread.interrupt();
        particleContainers.clear();


    }
}
