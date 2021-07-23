package com.cschar.pmode3.services;

import com.cschar.pmode3.PowerMode3ConfigurableUI2;
import com.intellij.openapi.options.ConfigurableUi;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;


public class MemoryMonitorServiceImpl implements MemoryMonitorService {
    private static final Logger LOGGER = Logger.getLogger(MemoryMonitorServiceImpl.class.getName());
    PowerMode3ConfigurableUI2 menuConfigurableUI;

    @Override
    public void setUi(ConfigurableUi ui) {
        this.menuConfigurableUI = (PowerMode3ConfigurableUI2) ui;

    }

    @Override
    public void cleanup() {
        LOGGER.info("Cleanup");
        this.menuConfigurableUI = null;
    }

    @Override
    public void dispose() {
        LOGGER.info("Disposing");
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