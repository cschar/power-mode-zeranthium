package com.cschar.pmode3.hotkeys;


import com.cschar.pmode3.PowerMode3;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class HotKeyEnabledAction extends AnAction {




    public HotKeyEnabledAction() {
        super();
    }



    public HotKeyEnabledAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon);
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {

        PowerMode3.getInstance().setEnabled(!PowerMode3.getInstance().isEnabled());

    }

    /**
     * Determines whether this menu item is available for the current context.
     * Requires a project to be open.
     * @param e Event received when the associated group-id menu is chosen.
     */
    @Override
    public void update(AnActionEvent e) {
        // Set the availability based on whether a project is open
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }

}