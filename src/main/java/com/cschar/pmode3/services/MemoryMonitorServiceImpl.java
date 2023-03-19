package com.cschar.pmode3.services;

import com.cschar.pmode4.PowerMode3SettingsJComponent;
import com.intellij.openapi.options.ConfigurableUi;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.diagnostic.Logger;

// ? ? ?
// TODO: remove this, just make it a thread task inside the JComponent
public class MemoryMonitorServiceImpl implements MemoryMonitorService {
    private static final Logger LOGGER = Logger.getInstance(MemoryMonitorServiceImpl.class.getName());
    PowerMode3SettingsJComponent menuConfigurableUI;

    @Override
    public void setUi(PowerMode3SettingsJComponent ui) {
        this.menuConfigurableUI = (PowerMode3SettingsJComponent) ui;

    }

    @Override
    public void cleanup() {
        LOGGER.debug("Cleanup");
        this.menuConfigurableUI = null;
    }

    @Override
    public void dispose() {
        LOGGER.debug("Disposing");
        this.menuConfigurableUI = null;
    }

    @Override
    public void updateUi() {
        Task.Backgroundable bgTask = new Task.Backgroundable(null, "Zeranthium Config...",
                false, null) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                while (menuConfigurableUI != null && menuConfigurableUI.refreshMemoryWidget) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try{
                        menuConfigurableUI.reinitMemoryStatsPanel();
                    }catch(NullPointerException e){

                    }
                }
            }
        };
        ProgressManager.getInstance().run(bgTask);


    }

}
