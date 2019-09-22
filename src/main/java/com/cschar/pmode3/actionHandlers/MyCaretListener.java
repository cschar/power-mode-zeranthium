package com.cschar.pmode3.actionHandlers;

import com.cschar.pmode3.ParticleSpriteDroste;
import com.cschar.pmode3.ParticleSpriteLinkerAnchor;
import com.cschar.pmode3.ParticleSpriteMandala;
import com.cschar.pmode3.PowerMode3;
import com.cschar.pmode3.config.LinkerConfig;
import com.cschar.pmode3.config.Mandala2Config;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollingModel;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class MyCaretListener implements CaretListener {

    public static boolean enabled = true;

    @Override
    public void caretPositionChanged(@NotNull CaretEvent event) {

        if(enabled) {

            Editor editor = event.getEditor();
            VisualPosition visualPosition = event.getCaret().getVisualPosition();
            Point point = editor.visualPositionToXY(visualPosition);
            ScrollingModel scrollingModel = editor.getScrollingModel();
            point.x = point.x - scrollingModel.getHorizontalScrollOffset();
            point.y = point.y - scrollingModel.getVerticalScrollOffset();



            ParticleSpriteDroste.cursorX = point.x;
            ParticleSpriteDroste.cursorY = point.y;

            PowerMode3 settings = PowerMode3.getInstance();
            if(settings.getSpriteTypeEnabled(PowerMode3.ConfigType.MANDALA) &&
                    Mandala2Config.MOVE_WITH_CARET(settings)){


//                int movespeed = Mandala2Config.CARET_MOVE_SPEED(settings);
//                if()

                ParticleSpriteMandala.targetX = point.x;
                ParticleSpriteMandala.targetY = point.y;
                ParticleSpriteMandala.moveSpeed = Mandala2Config.CARET_MOVE_SPEED(settings);

            }

            if(settings.getSpriteTypeEnabled(PowerMode3.ConfigType.LINKER) &&
                    LinkerConfig.MOVE_WITH_CARET(settings)){


//                int movespeed = Mandala2Config.CARET_MOVE_SPEED(settings);
//                if()

//                ParticleSpriteLinkerAnchor.cursorX = point.x;
//                ParticleSpriteLinkerAnchor.cursorY = point.y;
                ParticleSpriteLinkerAnchor.targetX = point.x;
                ParticleSpriteLinkerAnchor.targetY = point.y;
                ParticleSpriteLinkerAnchor.moveSpeed = LinkerConfig.CARET_MOVE_SPEED(settings);

            }
        }
    }

    @Override
    public void caretAdded(@NotNull CaretEvent event) {

    }

    @Override
    public void caretRemoved(@NotNull CaretEvent event) {

    }
};
