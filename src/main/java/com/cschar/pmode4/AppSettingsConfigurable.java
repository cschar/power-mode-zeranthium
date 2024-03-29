package com.cschar.pmode4;

import com.cschar.pmode3.PowerMode3;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Provides controller functionality for application settings.
 */
public class AppSettingsConfigurable implements Configurable {
    private static final Logger LOGGER = Logger.getInstance( AppSettingsConfigurable.class.getName() );
    private PowerMode3SettingsJComponent myPowerMode3SettingsComponent;

    // A default constructor with no arguments is required because this implementation
    // is registered as an applicationConfigurable EP

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "PowerMode Zeranthium";
    }

//    @Override
//    public JComponent getPreferredFocusedComponent() {
//        LOGGER.debug("AppSettings: getPreferredFocusedComponent...");
//        return myPowerMode3SettingsComponent.ultraPanel;
//    }

    @Nullable
    @Override
    public JComponent createComponent() {
        LOGGER.debug("AppSettings: CreateComponent...");
        PowerMode3 settings = PowerMode3.getInstance();
        myPowerMode3SettingsComponent = new PowerMode3SettingsJComponent(settings);
        return myPowerMode3SettingsComponent.getComponent();
    }

    @Override
    public boolean isModified() {
//        AppSettingsState settings = AppSettingsState.getInstance();
//        boolean modified = !mySettingsComponent.getUserNameText().equals(settings.userId);
//        modified |= mySettingsComponent.getIdeaUserStatus() != settings.ideaStatus;
//        return modified;
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        PowerMode3 settings = PowerMode3.getInstance();
        try {
            myPowerMode3SettingsComponent.apply(settings);
        } catch (ConfigurationException e) {
            throw e;
        }
//        AppSettingsState settings = AppSettingsState.getInstance();
//        settings.userId = mySettingsComponent.getUserNameText();
//        settings.ideaStatus = mySettingsComponent.getIdeaUserStatus();
    }

    @Override
    public void reset() {
//        AppSettingsState settings = AppSettingsState.getInstance();
//        mySettingsComponent.setUserNameText(settings.userId);
//        mySettingsComponent.setIdeaUserStatus(settings.ideaStatus);
    }

    @Override
    public void disposeUIResources()
    {
        LOGGER.debug("AppSettings: disposeUIResources...");
        myPowerMode3SettingsComponent = null;
    }

}
