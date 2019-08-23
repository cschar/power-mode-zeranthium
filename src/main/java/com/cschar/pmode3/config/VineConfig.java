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
        secondCol.setLayout(new BoxLayout(secondCol, BoxLayout.Y_AXIS));
        JLabel headerLabel = new JLabel("Vine Options");
        headerLabel.setFont(new Font ("Arial", Font.BOLD, 20));
        secondCol.add(headerLabel);
        headerLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        mainPanel.add(secondCol);


        JPanel vineTopColorPanel = Config.getColorPickerPanel("Vine Top Color", PowerMode3.SpriteType.VINE, settings, Color.GREEN);
        JPanel vineBottomColorPanel = Config.getColorPickerPanel("Vine Bottom Color", PowerMode3.SpriteType.VINE, settings, Color.CYAN);
        secondCol.add(vineTopColorPanel);
        secondCol.add(vineBottomColorPanel);


        JPanel maxPsi = new JPanel();
        maxPsi.add(new JLabel("Max Psi Anchor Scan Distance: "));
        this.maxPsiAnchorDistanceTextField = new JTextField("");
        this.maxPsiAnchorDistanceTextField.setMinimumSize(new Dimension(50,20));
        maxPsi.add(maxPsiAnchorDistanceTextField);
        maxPsi.setLayout(new FlowLayout(FlowLayout.RIGHT));
        maxPsi.setAlignmentX(Component.RIGHT_ALIGNMENT);
        maxPsi.setMaximumSize(new Dimension(500, 50));
        secondCol.add(maxPsi);

        JPanel minPsi = new JPanel();
        minPsi.add(new JLabel("Min Psi Anchor Scan Distance: "));
        this.minPsiAnchorDistanceTextField = new JTextField("");
        this.minPsiAnchorDistanceTextField.setMinimumSize(new Dimension(50,20));
        minPsi.add(minPsiAnchorDistanceTextField);
        minPsi.setLayout(new FlowLayout(FlowLayout.RIGHT));
        minPsi.setAlignmentX(Component.RIGHT_ALIGNMENT);
        minPsi.setMaximumSize(new Dimension(500, 50));
        secondCol.add(minPsi);

        JPanel spriteEnabledPanel = new JPanel();
        this.spriteEnabled = new JCheckBox("Sprite Enabled?");
        spriteEnabledPanel.add(spriteEnabled);
        spriteEnabledPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        spriteEnabledPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        spriteEnabledPanel.setMaximumSize(new Dimension(500, 50));
        firstCol.add(spriteEnabledPanel);


        this.growFromRight = new JCheckBox("Grow From Right?");
        this.growFromRight.setAlignmentX(Component.LEFT_ALIGNMENT);
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

        int minVinePsiDistance = Config.getJTextFieldWithinBoundsInt(this.minPsiAnchorDistanceTextField,
                0, maxPsiSearchLimit,
                "Min Distance to Psi Anchors will use when spawning vines (cannot be greater than max defined at top)");
        settings.setSpriteTypeProperty(PowerMode3.SpriteType.VINE, "minPsiSearchDistance", String.valueOf(minVinePsiDistance));


        int maxVinePsiDistance = Config.getJTextFieldWithinBoundsInt(this.maxPsiAnchorDistanceTextField,
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
