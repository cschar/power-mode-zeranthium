package com.cschar.pmode3.hotkeys;


import com.cschar.pmode3.PowerMode3;
import com.cschar.pmode3.Sound;
import com.cschar.pmode3.config.MusicTriggerConfig;
import com.cschar.pmode3.config.common.SoundData;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;


//https://www.jetbrains.org/intellij/sdk/docs/tutorials/action_system/grouping_action.html
//TODO: could add dynamic actions, e.g. more triggers if added in config panel
public class HotKeyMusicTriggerAAction extends AnAction {

    //Added to bypass OLD_EDT deprecation message
    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
//        return ActionUpdateThread.BGT
                return ActionUpdateThread.EDT;
//        return super.getActionUpdateThread();
    }

    public HotKeyMusicTriggerAAction() {
        super();
    }

    public HotKeyMusicTriggerAAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {

        if(PowerMode3.getInstance().isEnabled()) {

            SoundData d = MusicTriggerConfig.soundData.get(0);
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