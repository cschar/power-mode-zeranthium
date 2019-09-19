package com.cschar.pmode3;


import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.actionSystem.TypedAction;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.PsiFile;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;


public class SoundTypedActionHandler implements TypedActionHandler {

    TypedAction typedAction;
    public SoundTypedActionHandler(TypedAction typedAction){
        this.typedAction = typedAction;
    }

    @Override
    public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext) {

//        this.typedAction.getRawHandler().execute(editor, charTyped, dataContext);
//        final Document document = editor.getDocument();
//        CaretModel caretModel = editor.getCaretModel();
//        document.insertString(caretModel.getOffset(), String.format("%c", charTyped));
//        caretModel.moveToOffset(caretModel.getOffset() + 1);
//        VisualPosition visualPosition = caretModel.getVisualPosition();
//        Project project = editor.getProject();

        System.out.println("T-");

        final Document document = editor.getDocument();
        Project project = editor.getProject();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                document.insertString(0, "Typed\n");
            }
        };
        WriteCommandAction.runWriteCommandAction(project, runnable);

    }
}