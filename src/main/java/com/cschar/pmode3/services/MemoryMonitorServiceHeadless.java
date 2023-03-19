package com.cschar.pmode3.services;


import com.cschar.pmode4.PowerMode3SettingsJComponent;
import com.intellij.openapi.options.ConfigurableUi;

/**
 * Headless implementation for testing
 */
public class MemoryMonitorServiceHeadless implements MemoryMonitorService {
    @Override
    public void setUi(PowerMode3SettingsJComponent ui){
        
    }
    @Override
    public void dispose(){}
    @Override
    public void cleanup(){}
    @Override
    public void updateUi(){}
}
