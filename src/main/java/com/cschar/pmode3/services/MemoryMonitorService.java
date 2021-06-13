package com.cschar.pmode3.services;

import com.cschar.pmode3.MenuConfigurableUI;
import com.intellij.openapi.Disposable;

public interface MemoryMonitorService extends Disposable {

    void setUi(MenuConfigurableUI ui);
    void updateUi();
    void cleanup();

}
