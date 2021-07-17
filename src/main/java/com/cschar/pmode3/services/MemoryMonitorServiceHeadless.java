package com.cschar.pmode3.services;

import com.cschar.pmode3.MenuConfigurableUI;

public class MemoryMonitorServiceHeadless implements MemoryMonitorService {
    MenuConfigurableUI menuConfigurableUI;
    @Override
    public void setUi(MenuConfigurableUI ui){
        System.out.println("headless memory set");
    }
    @Override
    public void dispose(){}
    @Override
    public void cleanup(){}
    @Override
    public void updateUi(){}
}
