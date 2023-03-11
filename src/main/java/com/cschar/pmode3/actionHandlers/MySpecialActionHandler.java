package com.cschar.pmode3.actionHandlers;


import com.cschar.pmode3.PowerMode3;
import com.cschar.pmode3.Sound;
import com.cschar.pmode3.config.SpecialActionSoundConfig;
import com.cschar.pmode3.config.common.SoundData;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorTextInsertHandler;
import com.intellij.util.Producer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.datatransfer.Transferable;


public class MySpecialActionHandler extends EditorActionHandler implements EditorTextInsertHandler {
    private EditorActionHandler origEditorActionHandler;
    private SpecialActionSoundConfig.KEYS key;

    public MySpecialActionHandler(@NotNull EditorActionHandler origEditorActionHandler, SpecialActionSoundConfig.KEYS key) {
        this.origEditorActionHandler = origEditorActionHandler;
        this.key  = key;
    }

//    @Override
//    public void execute(Editor editor, DataContext dataContext, @Nullable Producer<Transferable> producer) {
//        if (origEditorActionHandler != null && origEditorActionHandler instanceof EditorTextInsertHandler) {
//            ((EditorTextInsertHandler) origEditorActionHandler).execute(editor, dataContext, producer);
//        }
//    }

    @Override
    public void execute(Editor editor, DataContext dataContext, @Nullable Producer<? extends Transferable> producer) {
        if (origEditorActionHandler != null && origEditorActionHandler instanceof EditorTextInsertHandler) {
            ((EditorTextInsertHandler) origEditorActionHandler).execute(editor, dataContext, producer);
        }
    }

    @Override
    protected void doExecute(@NotNull Editor editor,  @Nullable Caret caret, DataContext dataContext) {


        PowerMode3 settings = PowerMode3.getInstance();

        if(!settings.isEnabled() || !settings.getSpriteTypeEnabled(PowerMode3.ConfigType.SPECIAL_ACTION_SOUND)){
//        if(!settings.isEnabled()){
            origEditorActionHandler.execute(editor, caret, dataContext);
            return;
        }

        SoundData d = SpecialActionSoundConfig.soundData.get(key.ordinal());
        if(d.enabled) {
            Sound s = new Sound(d.getPath(), !d.customPathValid);
            s.play();
        }
        origEditorActionHandler.execute(editor, caret, dataContext);

    }



}