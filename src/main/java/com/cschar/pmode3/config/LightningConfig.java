package com.cschar.pmode3.config;

import com.cschar.pmode3.ParticleSpriteLightningAlt;
import com.cschar.pmode3.PowerMode3;
import com.intellij.openapi.options.ConfigurationException;

import javax.swing.*;
import java.awt.*;

public class LightningConfig extends JPanel {

    JPanel mainPanel;
    PowerMode3 settings;

    private JCheckBox innerBeamEnabledCheckBox;
    private JCheckBox outerBeamEnabledCheckBox;

    private JTextField chanceOfLightningTextField;
    private Color originalColorInner;
    private Color originalColorOuter;

    public LightningConfig(PowerMode3 settings){
        this.settings = settings;


        mainPanel = new JPanel();
        mainPanel.setMaximumSize(new Dimension(1000,300));
        mainPanel.setLayout(new GridLayout(0,2));
        JPanel firstCol = new JPanel();
        firstCol.setLayout(new BoxLayout(firstCol, BoxLayout.PAGE_AXIS));
        mainPanel.add(firstCol);

        JPanel secondCol = new JPanel();
        secondCol.setLayout(new BoxLayout(secondCol, BoxLayout.PAGE_AXIS));
        JLabel headerLabel = new JLabel("Lightning Options");
        headerLabel.setFont(new Font ("Arial", Font.BOLD, 20));
        secondCol.add(headerLabel);
        mainPanel.add(secondCol);


        //save values so we can check if we need to reload sprites
        String colorRGBInner = settings.getSpriteTypeProperty(PowerMode3.SpriteType.LIGHTNING, "inner Beam Color");
        if(colorRGBInner != null){
            Color originalColor = new Color(Integer.parseInt(colorRGBInner));
            this.originalColorInner =  originalColor;
        }
        String colorRGBOuter = settings.getSpriteTypeProperty(PowerMode3.SpriteType.LIGHTNING, "outer Beam Color");
        if(colorRGBOuter != null){
            Color originalColorOuter = new Color(Integer.parseInt(colorRGBOuter));
            this.originalColorOuter =  originalColorOuter;
        }



        JPanel innerBeamColorPanel = Config.getColorPickerPanel("inner Beam Color", PowerMode3.SpriteType.LIGHTNING, settings);
        JPanel outerBeamColorPanel = Config.getColorPickerPanel("outer Beam Color", PowerMode3.SpriteType.LIGHTNING, settings);

        JPanel innerBeamJPanel = new JPanel();
        this.innerBeamEnabledCheckBox = new JCheckBox("is enabled?", true);
        innerBeamJPanel.add(innerBeamEnabledCheckBox);
        innerBeamJPanel.add(innerBeamColorPanel);
        secondCol.add(innerBeamJPanel);

        JPanel outerBeamJPanel = new JPanel();
        this.outerBeamEnabledCheckBox = new JCheckBox("is enabled?", true);
        outerBeamJPanel.add(outerBeamEnabledCheckBox);
        outerBeamJPanel.add(outerBeamColorPanel);
        secondCol.add(outerBeamJPanel);

        this.chanceOfLightningTextField = new JTextField();
        JLabel chanceOfLightningLabel = new JLabel("Chance of Lightning per particle (0-100)");
        JPanel chancePanel = new JPanel();
        chancePanel.add(chanceOfLightningLabel);
        chancePanel.add(chanceOfLightningTextField);



        secondCol.add(chancePanel);





    }

    public static int CHANCE_OF_LIGHTNING(PowerMode3 settings){
        String chanceOfLightning = settings.getSpriteTypeProperty(PowerMode3.SpriteType.LIGHTNING, "chanceOfLightning");
        if(chanceOfLightning != null){
            return Integer.parseInt(chanceOfLightning);
        }else{
            return 0;
        }
    }

    public static boolean INNER_BEAM_ENABLED(PowerMode3 settings){

        String isEnabled = settings.getSpriteTypeProperty(PowerMode3.SpriteType.LIGHTNING, "innerBeamEnabled");
        if(isEnabled != null){
            return Boolean.valueOf(isEnabled);
        }else{
            return false;
        }
    }

    public static boolean OUTER_BEAM_ENABLED(PowerMode3 settings){

        String isEnabled = settings.getSpriteTypeProperty(PowerMode3.SpriteType.LIGHTNING, "outerBeamEnabled");
        if(isEnabled != null){
            return Boolean.valueOf(isEnabled);
        }else{
            return false;
        }
    }

    public void loadValues(){
        String chanceOfLightning = settings.getSpriteTypeProperty(PowerMode3.SpriteType.LIGHTNING, "chanceOfLightning");
        if(chanceOfLightning != null){
            this.chanceOfLightningTextField.setText(chanceOfLightning);
        }else{
            this.chanceOfLightningTextField.setText("0");
        }

        String isInnerBeamEnabled = settings.getSpriteTypeProperty(PowerMode3.SpriteType.LIGHTNING, "innerBeamEnabled");
        if(isInnerBeamEnabled != null){
            this.innerBeamEnabledCheckBox.setSelected(Boolean.valueOf(isInnerBeamEnabled));
        }

        String isOuterBeamEnabled = settings.getSpriteTypeProperty(PowerMode3.SpriteType.LIGHTNING, "outerBeamEnabled");
        if(isOuterBeamEnabled != null){
            this.outerBeamEnabledCheckBox.setSelected(Boolean.valueOf(isOuterBeamEnabled));
        }

    }

    public void saveValues() throws ConfigurationException {

        int chanceOfLightning = Config.getJTextFieldWithinBounds(this.chanceOfLightningTextField,
                0, 100,
                "chance lightning spawns on keypress");
        settings.setSpriteTypeProperty(PowerMode3.SpriteType.LIGHTNING, "chanceOfLightning", String.valueOf(chanceOfLightning));


        settings.setSpriteTypeProperty(PowerMode3.SpriteType.LIGHTNING, "innerBeamEnabled", String.valueOf(innerBeamEnabledCheckBox.isSelected()));
        settings.setSpriteTypeProperty(PowerMode3.SpriteType.LIGHTNING, "outerBeamEnabled", String.valueOf(outerBeamEnabledCheckBox.isSelected()));


        this.reloadSprites();
    }

    public void reloadSprites(){
        //Only load new sprites when we are closing UI
        String colorRGBInner = settings.getSpriteTypeProperty(PowerMode3.SpriteType.LIGHTNING, "inner Beam Color");
        Color newColorInner = new Color(Integer.parseInt(colorRGBInner));

        String colorRGBOuter = settings.getSpriteTypeProperty(PowerMode3.SpriteType.LIGHTNING, "outer Beam Color");
        Color newColorOuter = new Color(Integer.parseInt(colorRGBOuter));

        if(this.originalColorInner.getRGB() != newColorInner.getRGB() ||
           this.originalColorOuter.getRGB() != newColorOuter.getRGB()) {
            ParticleSpriteLightningAlt.reloadSpritesWithColors(newColorInner, newColorOuter);
        }
    }

    public JPanel getConfigPanel(){
        return this.mainPanel;
    }


}
