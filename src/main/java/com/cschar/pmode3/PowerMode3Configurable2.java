package com.cschar.pmode3;

import com.intellij.openapi.options.ConfigurableBase;
import org.jetbrains.annotations.NotNull;

/**
 * @author Baptiste Mesta
 *
 * Modified by cschar
 */
public class PowerMode3Configurable2 extends ConfigurableBase<PowerMode3ConfigurableUI2, PowerMode3> {

    private final PowerMode3 settings;


    public PowerMode3Configurable2(@NotNull PowerMode3 settings) {
        super("com.cschar.pmode3",
            "PowerMode Zeranthium",
            "Zeranthium");
        this.settings = settings;
    }
    private PowerMode3Configurable2() {
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
    protected PowerMode3ConfigurableUI2 createUi() {
        return new PowerMode3ConfigurableUI2(settings);
    }



    @Override
    public void disposeUIResources() {
        super.disposeUIResources();


        //stop any playing sounds that were triggered in the settings
        Sound.closeAllPlayers();
    }
}


