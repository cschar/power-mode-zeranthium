package com.cschar.pmode3.services;

import com.cschar.pmode3.MenuConfigurableUI;
import com.intellij.openapi.options.ConfigurableUi;

public class MemoryMonitorServiceHeadless implements MemoryMonitorService {
    @Override
    public void setUi(ConfigurableUi ui){
        System.out.println("headless memory set");
    }
    @Override
    public void dispose(){}
    @Override
    public void cleanup(){}
    @Override
    public void updateUi(){}
}
