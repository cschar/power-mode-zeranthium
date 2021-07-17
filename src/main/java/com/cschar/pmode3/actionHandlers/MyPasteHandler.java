package com.cschar.pmode3.actionHandlers;


import com.cschar.pmode3.*;
import com.cschar.pmode3.config.BasicParticleConfig;
import com.cschar.pmode3.config.CopyPasteVoidConfig;
import com.cschar.pmode3.config.common.SpriteDataAnimated;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorTextInsertHandler;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.openapi.util.TextRange;
import com.intellij.util.Producer;
import org.bouncycastle.util.Arrays;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.geom.Path2D;
import java.util.logging.Logger;


public class MyPasteHandler extends EditorActionHandler implements EditorTextInsertHandler {
    private static final java.util.logging.Logger LOGGER = Logger.getLogger(MyPasteHandler.class.getName());

    //   deprecated
//    @Override
//    public void execute(Editor editor, DataContext dataContext, Producer<Transferable> producer) {
//        System.out.println("PasteHandler executed");
//        LOGGER.info("Psting...in project.." + editor.getProject().getName());
//        if (origEditorActionHandler != null && origEditorActionHandler instanceof EditorTextInsertHandler) {
//            ((EditorTextInsertHandler) origEditorActionHandler).execute(editor, dataContext, producer);
//        }
//    }

    @Override
    public void execute(Editor editor, DataContext dataContext, @Nullable Producer<? extends Transferable> producer) {
        System.out.println("PasteHandler executed");
        LOGGER.info("Psting...in project.." + editor.getProject().getName());
        if (origEditorActionHandler != null && origEditorActionHandler instanceof EditorTextInsertHandler) {
            ((EditorTextInsertHandler) origEditorActionHandler).execute(editor, dataContext, producer);
        }
    }


    private EditorActionHandler origEditorActionHandler;

    public MyPasteHandler(@NotNull EditorActionHandler origEditorActionHandler) {
        LOGGER.info("creating MyPasteHandler");
        this.origEditorActionHandler = origEditorActionHandler;
    }

    @Override
    protected void doExecute(@NotNull Editor editor,  @Nullable Caret caret, DataContext dataContext) {

        LOGGER.info("Pasting doExectue..in project.." + editor.getProject().getName());
        PowerMode3 settings = PowerMode3.getInstance();

        if(!settings.isEnabled() || !settings.getSpriteTypeEnabled(PowerMode3.ConfigType.COPYPASTEVOID)){
            origEditorActionHandler.execute(editor, caret, dataContext);
            return;
        }


        Runnable s = new Runnable() {
            @Override
            public void run() {

                ScrollingModel scrollingModel = editor.getScrollingModel();

                //disable so sprites render at correct spot and arent slowed down by visual scroll
                scrollingModel.disableAnimation();
                try {
                    int beforePasteOffset = scrollingModel.getVerticalScrollOffset();
                }catch(UnsupportedOperationException e){
                    LOGGER.fine("paste unsupported, exiting early");
                    //if we are pasting into a search box via ctrl+f, etc.. (not a real editor),
                    // e.g. com.intellij.openapi.editor.textarea.TextComponentScrollingModel
                    // don't draw to GUI, just paste and exit
                    EditorCopyPasteHelper.getInstance().pasteFromClipboard(editor);
                    return;
                }

                TextRange[] pasted = EditorCopyPasteHelper.getInstance().pasteFromClipboard(editor);
                if(pasted.length != 1) return; //ensure we have a single TextRange to work with
                TextRange t = pasted[0];


                //TODO: figure out how to calculate each pasted character x,y coord without incrementing caret
                //Disable CaretListener for the following...
                MyCaretListener.enabled = false;

                //   |hey|                           |   |
                //   |ya|                 ---->      |  |
                //   |hello there hi|                |           |

                //Begin moving caret along all of selected text to build an outline of
                //the 'selected' blob. When outline built, add effect to it.
                int afterPasteOffset = scrollingModel.getVerticalScrollOffset();
                scrollingModel.enableAnimation();
//                System.out.println("scroll before after " + beforePasteOffset + " " + afterPasteOffset);
//                int charWidth = EditorUtil.getPlainSpaceWidth(editor);
                int lineHeight = editor.getLineHeight();
                int start = t.getStartOffset();
                int offset = t.getLength();  //offset is # of characters typed

                //paste outline points,
                //to show where each line starts and ends with 2 points
                // max size possible for line numbers is offset
                // ex: paste offset 5, but contents is 5 characters w/ newlines
                Point[][] poPoints = new Point[offset][2];

                Caret curCaret = editor.getCaretModel().getCurrentCaret();
                Point prevPoint = null;
                Point point = null;
                int curLine = 0;

                for(int i = start; i <= start + offset; i++){
                    curCaret.moveToOffset(i);
                    VisualPosition visualPosition = curCaret.getVisualPosition();
                    point = editor.visualPositionToXY(visualPosition);
                    point.x = point.x - scrollingModel.getHorizontalScrollOffset();
                    point.y = point.y - afterPasteOffset;

                    if(prevPoint == null){ //only happens once
                        poPoints[0][0] = point;
                    }else{
                        if(prevPoint.y == point.y){ //on same line, dont add anything
                            //continue
                        }else {//we've moved to a new line,
                            // so add prevPoint as END of last line,
                            poPoints[curLine][1] = prevPoint;
                            curLine += 1;
                            //add new line boundary
                            poPoints[curLine][0] = point;
                        }
                    }
                    prevPoint = point;

                }

                poPoints[curLine][1] = point;


                //Now, with poPoints containing outline for every caret spot in blob
                //flesh out the pasteShape
                Path2D pasteShape = new Path2D.Double();

                for(int i = 0; i <= curLine; i++) {
                    if (i == 0) { //immediately go to right side
                        pasteShape.moveTo(poPoints[i][0].x, poPoints[i][0].y);
                        pasteShape.lineTo(poPoints[i][1].x, poPoints[i][1].y);
                    } else { //work way down to bottom
                        //move down
                        pasteShape.lineTo(poPoints[i-1][1].x, poPoints[i-1][1].y+lineHeight);
                        //move either left/right to meet next line point
                        pasteShape.lineTo(poPoints[i][1].x, poPoints[i][1].y);
                    }
                }
                //wrap around
                pasteShape.lineTo(poPoints[curLine][1].x, poPoints[curLine][1].y + lineHeight);
                pasteShape.lineTo(poPoints[curLine][0].x, poPoints[curLine][0].y + lineHeight);

                //work way back up
                for(int i = curLine; i > 0; i--) {
                    pasteShape.lineTo(poPoints[i][0].x, poPoints[i][0].y);
                }
                pasteShape.lineTo(poPoints[0][0].x, poPoints[0][0].y+lineHeight);
                pasteShape.lineTo(poPoints[0][0].x, poPoints[0][0].y);


                int winningIndex = SpriteDataAnimated.getWeightedAmountWinningIndex(ParticleSpritePasteShape.spriteDataAnimated);
                if(winningIndex == -1 && !CopyPasteVoidConfig.FADE_ENABLED(settings)){
                    //do nothing
                }else {
                    ParticleSpritePasteShape pFontShape = new ParticleSpritePasteShape(pasteShape, 100,
                            CopyPasteVoidConfig.FADE_COLOR(settings),
                            CopyPasteVoidConfig.FADE_AMOUNT(settings),
                            CopyPasteVoidConfig.FADE_ENABLED(settings),
                            winningIndex,
                            editor);
                    ParticleContainerManager.particleContainers.get(editor).addExternalParticle(pFontShape);
                }


                MyCaretListener.enabled = true;
                curCaret.moveToOffset(start+offset);
                scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE);

            }
        };


        WriteCommandAction.runWriteCommandAction(editor.getProject(), s);

    }



}