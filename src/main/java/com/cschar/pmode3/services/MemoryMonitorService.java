package com.cschar.pmode3.services;

import com.cschar.pmode4.PowerMode3SettingsJComponent;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.ConfigurableUi;

public interface MemoryMonitorService extends Disposable {

    void setUi(PowerMode3SettingsJComponent ui);
    void updateUi();
    void cleanup();

}
