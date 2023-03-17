package com.cschar.pmode3;

import com.cschar.pmode3.config.common.SpriteDataAnimated;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.ConfigurableBase;
import com.intellij.openapi.util.Disposer;
import org.jetbrains.annotations.NotNull;

/**
 * @author Baptiste Mesta
 *
 * Modified by cschar
 */
public class PowerMode3Configurable2 extends ConfigurableBase<PowerMode3ConfigurableUI2, PowerMode3> implements Disposable {
    private static final Logger LOGGER = Logger.getInstance( PowerMode3Configurable2.class.getName() );
    private PowerMode3 settings;

    public PowerMode3Configurable2() {
        super("com.cschar.pmode3",
                "PowerMode Zeranthium",
                "Zeranthium");
        LOGGER.debug("Creating ConfigurableUI...");
        settings = ApplicationManager.getApplication().getService(PowerMode3.class);
//        Disposer.register(settings, this);
        settings.configurableUI2 = this;
    }

//    public PowerMode3Configurable2(@NotNull PowerMode3 settings) {
//        super("com.cschar.pmode3",
//            "PowerMode Zeranthium",
//            "Zeranthium");
//        this.settings = settings;
//        this.settings.dummyValue = 30;
//        Disposer.register(settings, this);
//    }

//    private PowerMode3Configurable2() {
//        this(PowerMode3.getInstance());
//    }


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
        LOGGER.debug("creating ui..");
        return new PowerMode3ConfigurableUI2(settings);
    }



    @Override
    public void disposeUIResources() {
        super.disposeUIResources();
        LOGGER.debug("Configurable: disposing UIResources...");

        //stop any playing sounds that were triggered in the settings
        Sound.closeAllPlayers();
    }

    @Override
    public void dispose() {
        LOGGER.debug(" ConfigurableUi: Disposing... ======== ==== ========");
        this.settings = null;

    }
}


