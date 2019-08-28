package com.cschar.pmode3.config;

import com.cschar.pmode3.PowerMode3;
import com.intellij.openapi.options.ConfigurationException;

import javax.swing.*;
import java.awt.*;

public class LightningAltConfig extends BaseConfig {


    JCheckBox sparksEnabled;

    public LightningAltConfig(PowerMode3 settings){
        super(settings, PowerMode3.SpriteType.LIGHTNING_ALT, "Lightning Alt Options");


        JPanel sparksEnabledPanel = new JPanel();
        sparksEnabled = new JCheckBox("Sparks Enabled?");
        sparksEnabledPanel.add(sparksEnabled);
        sparksEnabledPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        sparksEnabledPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sparksEnabledPanel.setMaximumSize(new Dimension(500, 50));
        firstCol.add(sparksEnabledPanel);




    }



    public void loadValues(){
        super.loadValues();
        this.maxAlphaTextField.setText(String.valueOf(Config.getFloatProperty(settings, spriteType,"maxAlpha", 0.5f)));
        this.sparksEnabled.setSelected(Config.getBoolProperty(settings, PowerMode3.SpriteType.LIGHTNING_ALT,"sparksEnabled", true));

    }

    public void saveValues() throws ConfigurationException {
        super.saveValues();

        settings.setSpriteTypeProperty(PowerMode3.SpriteType.LIGHTNING_ALT, "sparksEnabled", String.valueOf(sparksEnabled.isSelected()));

    }


    public static int CHANCE_PER_KEY_PRESS(PowerMode3 settings){
        return Config.getIntProperty(settings, PowerMode3.SpriteType.LIGHTNING_ALT, "chancePerKeyPress");
    }

    public static float MAX_ALPHA(PowerMode3 settings){
        return Config.getFloatProperty(settings, PowerMode3.SpriteType.LIGHTNING_ALT, "maxAlpha", 0.5f);
    }
}
