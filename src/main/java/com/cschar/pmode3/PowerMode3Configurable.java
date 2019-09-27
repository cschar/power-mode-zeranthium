package com.cschar.pmode3;

import com.intellij.openapi.options.ConfigurableBase;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author Baptiste Mesta
 *
 * Modified by cschar
 */
public class PowerMode3Configurable extends ConfigurableBase<MenuConfigurableUI, PowerMode3> {

    private final PowerMode3 settings;


    public PowerMode3Configurable(@NotNull PowerMode3 settings) {
        super("com.cschar.pmode3",
            "PowerMode Zeranthium",
            "Zeranthium");
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
//        if(!settings.isConfigLoaded) {
//            LoadingUI z = new LoadingUI(settings);
//            return z;
//        }
        return new MenuConfigurableUI(settings);
    }


    static class LoadingUI extends MenuConfigurableUI{

        public static JLabel label = new JLabel("Loading Config");

        public LoadingUI(PowerMode3 powerMode3) {
            settings = powerMode3;
//            super(powerMode3);
            System.out.println("zone");
        }

        @Override
        public void reset(@NotNull PowerMode3 o) {

        }

        @Override
        public boolean isModified(@NotNull PowerMode3 o) {
            return false;
        }

        @Override
        public void apply(@NotNull PowerMode3 o) throws ConfigurationException {

        }

        @NotNull
        @Override
        public JComponent getComponent() {
            JPanel j = new JPanel();
            j.add(label);
            return j;
        }
    }


    @Override
    public void disposeUIResources() {
        super.disposeUIResources();

        //stop any playing sounds that were triggered in the settings
        Sound.closeAllPlayers();
    }
}


