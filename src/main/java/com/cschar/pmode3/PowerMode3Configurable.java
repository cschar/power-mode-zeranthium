package com.cschar.pmode3;

import com.intellij.openapi.options.ConfigurableBase;
import org.jetbrains.annotations.NotNull;

/**
 * @author Baptiste Mesta
 *
 * Modified by cschar
 */
public class PowerMode3Configurable extends ConfigurableBase<MenuConfigurableUI, PowerMode3> {

    private final PowerMode3 settings;


    public PowerMode3Configurable(@NotNull PowerMode3 settings) {
        super("power.mode3",
            "Power mode 3333<<<",
            "power.mode3");
        this.settings = settings;
    }
    public PowerMode3Configurable() {
        this(PowerMode3.getInstance());
    }


    @NotNull
    @Override
    protected PowerMode3 getSettings() {
        if (settings == null) {
            throw new IllegalStateException("power mode is null");
        }
        return settings;
    }

    @Override
    protected MenuConfigurableUI createUi()
    {
        return new MenuConfigurableUI(settings);
    }
}


