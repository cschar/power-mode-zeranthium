package com.cschar.pmode3.hotkeys;


import com.cschar.pmode3.PowerMode3;
import com.cschar.pmode3.Sound;
import com.cschar.pmode3.config.MusicTriggerConfig;
import com.cschar.pmode3.config.common.SoundData;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class HotKeyMusicTriggerBAction extends AnAction {




    public HotKeyMusicTriggerBAction() {
        super();
    }

    public HotKeyMusicTriggerBAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {

        if(PowerMode3.getInstance().isEnabled()) {
            SoundData d = MusicTriggerConfig.soundData.get(1);
            Sound s = new Sound(d.getPath(), !d.customPathValid);
            s.play();
        }

    }

    @Override
    public void update(AnActionEvent e) {
        // Set the availability based on whether a project is open
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }

}