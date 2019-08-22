package com.cschar.pmode3.config;

import com.cschar.pmode3.PowerMode3;
import com.intellij.openapi.options.ConfigurationException;

import javax.swing.*;
import java.awt.*;

public class VineConfig extends JPanel {

    JPanel mainPanel;
    PowerMode3 settings;

    public JTextField maxPsiAnchorDistanceTextField;
    public JTextField minPsiAnchorDistanceTextField;
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

        JPanel vineTopColorPanel = Config.getColorPickerPanel("Vine Top Color", PowerMode3.SpriteType.VINE, settings);
        JPanel vineBottomColorPanel = Config.getColorPickerPanel("Vine Bottom Color", PowerMode3.SpriteType.VINE, settings);
        secondCol.add(vineTopColorPanel);
        secondCol.add(vineBottomColorPanel);


        JPanel lop1 = new JPanel();
        lop1.add(new JLabel("Max Psi Anchor Scan Distance: "));
        this.maxPsiAnchorDistanceTextField = new JTextField("");
        this.maxPsiAnchorDistanceTextField.setMinimumSize(new Dimension(50,20));
        lop1.add(maxPsiAnchorDistanceTextField);
        secondCol.add(lop1);

        JPanel minPsi = new JPanel();
        minPsi.add(new JLabel("Min Psi Anchor Scan Distance: "));
        this.minPsiAnchorDistanceTextField = new JTextField("");
        this.minPsiAnchorDistanceTextField.setMinimumSize(new Dimension(50,20));
        minPsi.add(minPsiAnchorDistanceTextField);
        secondCol.add(minPsi);

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


        this.minPsiAnchorDistanceTextField.setText(String.valueOf(Config.getIntProperty(settings, PowerMode3.SpriteType.VINE,"minPsiSearchDistance", 100)));
        this.maxPsiAnchorDistanceTextField.setText(String.valueOf(Config.getIntProperty(settings, PowerMode3.SpriteType.VINE,"maxPsiSearchDistance", 300)));
        this.spriteEnabled.setSelected(Config.getBoolProperty(settings, PowerMode3.SpriteType.VINE,"spriteEnabled"));
        this.growFromRight.setSelected(Config.getBoolProperty(settings, PowerMode3.SpriteType.VINE,"growFromRight"));


    }

    public void saveValues(int maxPsiSearchLimit) throws ConfigurationException {

        int minVinePsiDistance = Config.getJTextFieldWithinBounds(this.minPsiAnchorDistanceTextField,
                0, maxPsiSearchLimit,
                "Min Distance to Psi Anchors will use when spawning vines (cannot be greater than max defined at top)");
        settings.setSpriteTypeProperty(PowerMode3.SpriteType.VINE, "minPsiSearchDistance", String.valueOf(minVinePsiDistance));


        int maxVinePsiDistance = Config.getJTextFieldWithinBounds(this.maxPsiAnchorDistanceTextField,
                0, maxPsiSearchLimit,
                "Max Distance to Psi Anchors will use when spawning vines (cannot be greater than max defined at top)");
        settings.setSpriteTypeProperty(PowerMode3.SpriteType.VINE, "maxPsiSearchDistance", String.valueOf(maxVinePsiDistance));

        settings.setSpriteTypeProperty(PowerMode3.SpriteType.VINE, "spriteEnabled", String.valueOf(spriteEnabled.isSelected()));
        settings.setSpriteTypeProperty(PowerMode3.SpriteType.VINE, "growFromRight", String.valueOf(growFromRight.isSelected()));
    }



    public static boolean USE_SPRITES(PowerMode3 settings){
        return Config.getBoolProperty(settings, PowerMode3.SpriteType.VINE,"spriteEnabled");
    }


    public static int MAX_PSI_SEARCH(PowerMode3 settings) {
        return Config.getIntProperty(settings, PowerMode3.SpriteType.VINE,"maxPsiSearchDistance");
    }

    public static int MIN_PSI_SEARCH(PowerMode3 settings) {
        return Config.getIntProperty(settings, PowerMode3.SpriteType.VINE,"minPsiSearchDistance");
    }

    public static boolean GROW_FROM_RIGHT(PowerMode3 settings){
        return Config.getBoolProperty(settings, PowerMode3.SpriteType.VINE,"growFromRight");
    }

    public static Color VINE_TOP_COLOR(PowerMode3 settings){
        return Config.getColorProperty(settings, PowerMode3.SpriteType.VINE,"Vine Top Color");
    }

    public static Color VINE_BOTTOM_COLOR(PowerMode3 settings){
        return Config.getColorProperty(settings, PowerMode3.SpriteType.VINE,"Vine Bottom Color");
    }




}
