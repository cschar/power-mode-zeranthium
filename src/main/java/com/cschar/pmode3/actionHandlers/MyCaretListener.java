package com.cschar.pmode3.actionHandlers;

import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import org.jetbrains.annotations.NotNull;

public class MyCaretListener implements CaretListener {

    public static boolean enabled = true;

    @Override
    public void caretPositionChanged(@NotNull CaretEvent event) {

        if(enabled) {
//            System.out.println("changed");
        }
    }

    @Override
    public void caretAdded(@NotNull CaretEvent event) {

    }

    @Override
    public void caretRemoved(@NotNull CaretEvent event) {

    }
};
