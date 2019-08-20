package com.cschar.pmode3.config;

import com.cschar.pmode3.PowerMode3;
import com.intellij.openapi.options.ConfigurationException;

import javax.swing.*;
import java.awt.*;

public class VineConfig extends JPanel {

    JPanel mainPanel;
    PowerMode3 settings;

    public JTextField maxPsiAnchorDistanceTextField;
    JCheckBox spriteEnabled;
    JCheckBox growFromRight;

    public VineConfig(PowerMode3 settings){
        this.settings = settings;


        mainPanel = new JPanel();
        mainPanel.setMaximumSize(new Dimension(1000,300));
        mainPanel.setLayout(new GridLayout(0,2));
        JPanel firstCol = new JPanel();
        firstCol.setLayout(new BoxLayout(firstCol, BoxLayout.PAGE_AXIS));
        mainPanel.add(firstCol);

        JPanel secondCol = new JPanel();
        secondCol.setLayout(new BoxLayout(secondCol, BoxLayout.PAGE_AXIS));
        JLabel headerLabel = new JLabel("Vine Options");
        headerLabel.setFont(new Font ("Arial", Font.BOLD, 20));
        secondCol.add(headerLabel);
        mainPanel.add(secondCol);

        JPanel vineColorPanel = Config.getColorPickerPanel("Vine Color", PowerMode3.SpriteType.VINE, settings);
        secondCol.add(vineColorPanel);


        JPanel lop1 = new JPanel();
        lop1.add(new JLabel("Psi Anchor Scan Distance: "));
        this.maxPsiAnchorDistanceTextField = new JTextField("");

        this.maxPsiAnchorDistanceTextField.setMinimumSize(new Dimension(50,20));
        lop1.add(maxPsiAnchorDistanceTextField);

        secondCol.add(lop1);

        JPanel spriteEnabledPanel = new JPanel();
        spriteEnabledPanel.add(new JLabel("Use Sprite "));
        this.spriteEnabled = new JCheckBox("Sprite Enabled?");
        spriteEnabledPanel.add(spriteEnabled);
        firstCol.add(spriteEnabledPanel);


        this.growFromRight = new JCheckBox("Grow From Right?");

        firstCol.add(this.growFromRight);



        this.loadValues();

    }

    public JPanel getConfigPanel(){
        return this.mainPanel;
    }


    public void loadValues(){
        String psiSearchDistance = settings.getSpriteTypeProperty(PowerMode3.SpriteType.VINE, "maxPsiSearchDistance");
        if(psiSearchDistance != null){
            this.maxPsiAnchorDistanceTextField.setText(psiSearchDistance);
        }else{
            this.maxPsiAnchorDistanceTextField.setText("0");
        }

        String spriteEnabled = settings.getSpriteTypeProperty(PowerMode3.SpriteType.VINE, "spriteEnabled");
        if(spriteEnabled != null){
            this.spriteEnabled.setSelected(Boolean.valueOf(spriteEnabled));
        }else{
            this.spriteEnabled.setSelected(false);
        }

        String growFromRight = settings.getSpriteTypeProperty(PowerMode3.SpriteType.VINE, "growFromRight");
        if(growFromRight != null){
            this.growFromRight.setSelected(Boolean.valueOf(growFromRight));
        }else{
            this.growFromRight.setSelected(false);
        }


    }

    public void saveValues(int maxPsiSearchLimit) throws ConfigurationException {

        int vinePsiDistance = Config.getJTextFieldWithinBounds(this.maxPsiAnchorDistanceTextField,
                0, maxPsiSearchLimit,
                "Distance to Psi Anchors will use when spawning vines (cannot be greater than max defined at top)");
        settings.setSpriteTypeProperty(PowerMode3.SpriteType.VINE, "maxPsiSearchDistance", String.valueOf(vinePsiDistance));

        settings.setSpriteTypeProperty(PowerMode3.SpriteType.VINE, "spriteEnabled", String.valueOf(spriteEnabled.isSelected()));
        settings.setSpriteTypeProperty(PowerMode3.SpriteType.VINE, "growFromRight", String.valueOf(growFromRight.isSelected()));
    }



    public static boolean USE_SPRITES(PowerMode3 settings){
        return Config.getBoolProperty(settings, PowerMode3.SpriteType.VINE,"spriteEnabled");
    }


    public static int MAX_PSI_SEARCH(PowerMode3 settings) {
        return Config.getIntProperty(settings, PowerMode3.SpriteType.VINE,"maxPsiSearchDistance");
    }

    public static boolean GROW_FROM_RIGHT(PowerMode3 settings){
        return Config.getBoolProperty(settings, PowerMode3.SpriteType.VINE,"growFromRight");
    }




}