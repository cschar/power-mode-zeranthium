package com.cschar.pmode3;



import com.intellij.ide.plugins.DynamicPluginListener;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class PowerMode3DynamicListener implements DynamicPluginListener {
    private static final Logger LOGGER = Logger.getInstance(PowerMode3DynamicListener.class);

    @Override
    public void beforePluginUnload(@NotNull IdeaPluginDescriptor pluginDescriptor, boolean isUpdate) {
        LOGGER.warn("--=-=-=-==- BEFORE UNLOADED \n\n" + pluginDescriptor.toString());
        System.out.println("908sad908asd908saasd -=-=-=-==--=");
    }

}