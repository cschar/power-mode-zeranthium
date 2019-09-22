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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.geom.Path2D;


public class MyPasteHandler extends EditorActionHandler implements EditorTextInsertHandler {
    private EditorActionHandler origEditorActionHandler;

    public MyPasteHandler(@NotNull EditorActionHandler origEditorActionHandler) {
        this.origEditorActionHandler = origEditorActionHandler;
    }

    @Override
    protected void doExecute(@NotNull Editor editor,  @Nullable Caret caret, DataContext dataContext) {


        PowerMode3 settings = PowerMode3.getInstance();
        if(!settings.getSpriteTypeEnabled(PowerMode3.ConfigType.COPYPASTEVOID)){
            origEditorActionHandler.execute(editor, caret, dataContext);
            return;
        }


        Runnable s = new Runnable() {
            @Override
            public void run() {

                ScrollingModel scrollingModel = editor.getScrollingModel();

                //disable so sprites render at correct spot and arent slowed down by visual scroll
                scrollingModel.disableAnimation();
                int beforePasteOffset = scrollingModel.getVerticalScrollOffset();
                TextRange[] pasted = EditorCopyPasteHelper.getInstance().pasteFromClipboard(editor);
                if(pasted.length != 1) return;
                TextRange t = pasted[0];

                int afterPasteOffset = scrollingModel.getVerticalScrollOffset();
                scrollingModel.enableAnimation();


//                System.out.println("scroll before after " + beforePasteOffset + " " + afterPasteOffset);




                int dx, dy;
                int lineHeight = editor.getLineHeight();
                int charWidth = EditorUtil.getPlainSpaceWidth(editor);

                int size = 5;


                int start = t.getStartOffset();
                int offset = t.getLength();

                //TODO: figure out how to calculate each pasted character x,y coord without incrementing caret
                MyCaretListener.enabled = false;



                //paste outline points  //max size pssible is offset
                Point[][] poPoints = new Point[offset][2];
                int curLine = 0;



                Caret curCaret = editor.getCaretModel().getCurrentCaret();
                Point prevPoint = null;
                Point point = null;

                for(int i = start; i <= start + offset; i++){
                    curCaret.moveToOffset(i);
                    VisualPosition visualPosition = curCaret.getVisualPosition();
                    point = editor.visualPositionToXY(visualPosition);
                    point.x = point.x - scrollingModel.getHorizontalScrollOffset();
                    point.y = point.y - afterPasteOffset;

                    if(prevPoint == null){
                        poPoints[0][0] = point;
                    }else{
                        //on same line, dont add anything
                        if(prevPoint.y == point.y){
                            //continue
                        }else {//we've moved to a new line, so add prevPoint as END of last line, add new line boundary
                            poPoints[curLine][1] = prevPoint;
                            curLine += 1;
                            poPoints[curLine][0] = point;
                        }
                    }
                    prevPoint = point;

//                    ParticleSpritePasteSingle pFont = new ParticleSpritePasteSingle(point.x, point.y,size,
//                            lineHeight, charWidth,
//                            100,
//                            BasicParticleConfig.BASIC_PARTICLE_COLOR(settings));
//                    ParticleContainerManager.particleContainers.get(editor).particles.add(pFont);
                }

                poPoints[curLine][1] = point;


                Path2D pasteShape = new Path2D.Double();

                for(int i = 0; i <= curLine; i++) {
                    if (i == 0) {
                        pasteShape.moveTo(poPoints[i][0].x, poPoints[i][0].y);
                        pasteShape.lineTo(poPoints[i][1].x, poPoints[i][1].y);
                    } else {
                        pasteShape.lineTo(poPoints[i-1][1].x, poPoints[i-1][1].y+lineHeight);
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
                            winningIndex);
                    ParticleContainerManager.particleContainers.get(editor).particles.add(pFontShape);
                }


                MyCaretListener.enabled = true;
                curCaret.moveToOffset(start+offset);

            }
        };


        WriteCommandAction.runWriteCommandAction(editor.getProject(), s);

    }

 //   deprecated
    @Override
    public void execute(Editor editor, DataContext dataContext, Producer<Transferable> producer) {
        if (origEditorActionHandler != null && origEditorActionHandler instanceof EditorTextInsertHandler) {
            ((EditorTextInsertHandler) origEditorActionHandler).execute(editor, dataContext, producer);
        }
    }

}