package com.cschar.pmode3.services;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.ConfigurableUi;

public interface MemoryMonitorService extends Disposable {

    void setUi(ConfigurableUi ui);
    void updateUi();
    void cleanup();

}
