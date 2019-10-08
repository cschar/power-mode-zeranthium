package com.cschar.pmode3.config;

import com.cschar.pmode3.PowerMode3;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VineConfig extends JPanel {

    JPanel mainPanel;
    PowerMode3 settings;

    public JTextField maxPsiAnchorDistanceTextField;
    public JTextField minPsiAnchorDistanceTextField;
    public JTextField chancePerKeyPressTextField;
    JCheckBox spriteEnabled;
    JCheckBox sprite2Enabled;
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


        JPanel vineTopColorPanel = Config.getColorPickerPanel("Vine Top Color", PowerMode3.ConfigType.VINE, settings, Color.GREEN);
        JPanel vineBottomColorPanel = Config.getColorPickerPanel("Vine Bottom Color", PowerMode3.ConfigType.VINE, settings, Color.CYAN);
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

        this.chancePerKeyPressTextField = new JTextField();
        JLabel chancePerKeyPressLabel = new JLabel("Chance per keypress (0-100)");
        JPanel chancePerKeyPressPanel = new JPanel();
        chancePerKeyPressPanel.add(chancePerKeyPressLabel);
        chancePerKeyPressPanel.add(chancePerKeyPressTextField);
        chancePerKeyPressPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        chancePerKeyPressPanel.setAlignmentX( Component.RIGHT_ALIGNMENT);//0.0
        chancePerKeyPressPanel.setMaximumSize(new Dimension(400, 50));
        chancePerKeyPressPanel.setBackground(JBColor.LIGHT_GRAY);
        secondCol.add(chancePerKeyPressPanel);


        JPanel spriteEnabledPanel = new JPanel();
        this.spriteEnabled = new JCheckBox("Sprite Enabled?");
        spriteEnabledPanel.add(spriteEnabled);
        spriteEnabledPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        spriteEnabledPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        spriteEnabledPanel.setMaximumSize(new Dimension(500, 50));
        firstCol.add(spriteEnabledPanel);
        this.spriteEnabled.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(spriteEnabled.isSelected()){
                    sprite2Enabled.setSelected(false);
                }
            }
        });

        JPanel sprite2EnabledPanel = new JPanel();
        this.sprite2Enabled = new JCheckBox("Sprite2 Enabled?");
        sprite2EnabledPanel.add(sprite2Enabled);
        sprite2EnabledPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        sprite2EnabledPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sprite2EnabledPanel.setMaximumSize(new Dimension(500, 50));
        firstCol.add(sprite2EnabledPanel);
        this.sprite2Enabled.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(sprite2Enabled.isSelected()){
                    spriteEnabled.setSelected(false);
                }
            }
        });


        JPanel growFromRightPanel = new JPanel();
        this.growFromRight = new JCheckBox("Grow From Right?");
        growFromRightPanel.add(growFromRight);
        growFromRightPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        growFromRightPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        growFromRightPanel.setMaximumSize(new Dimension(500, 50));
        firstCol.add(growFromRightPanel);




        this.loadValues();

    }

    public JPanel getConfigPanel(){
        return this.mainPanel;
    }


    public void loadValues(){


        this.minPsiAnchorDistanceTextField.setText(String.valueOf(Config.getIntProperty(settings, PowerMode3.ConfigType.VINE,"minPsiSearchDistance", 100)));
        this.maxPsiAnchorDistanceTextField.setText(String.valueOf(Config.getIntProperty(settings, PowerMode3.ConfigType.VINE,"maxPsiSearchDistance", 300)));
        this.chancePerKeyPressTextField.setText(String.valueOf(Config.getIntProperty(settings, PowerMode3.ConfigType.VINE,"chancePerKeyPress", 100)));

        this.spriteEnabled.setSelected(Config.getBoolProperty(settings, PowerMode3.ConfigType.VINE,"spriteEnabled"));
        this.sprite2Enabled.setSelected(Config.getBoolProperty(settings, PowerMode3.ConfigType.VINE,"sprite2Enabled"));
        this.growFromRight.setSelected(Config.getBoolProperty(settings, PowerMode3.ConfigType.VINE,"growFromRight"));


    }

    public void saveValues(int maxPsiSearchLimit) throws ConfigurationException {

        int minVinePsiDistance = Config.getJTextFieldWithinBoundsInt(this.minPsiAnchorDistanceTextField,
                0, maxPsiSearchLimit,
                "Min Distance to Psi Anchors will use when spawning vines (cannot be greater than max defined at top)");
        settings.setSpriteTypeProperty(PowerMode3.ConfigType.VINE, "minPsiSearchDistance", String.valueOf(minVinePsiDistance));


        int maxVinePsiDistance = Config.getJTextFieldWithinBoundsInt(this.maxPsiAnchorDistanceTextField,
                0, maxPsiSearchLimit,
                "Max Distance to Psi Anchors will use when spawning vines (cannot be greater than max defined at top)");
        settings.setSpriteTypeProperty(PowerMode3.ConfigType.VINE, "maxPsiSearchDistance", String.valueOf(maxVinePsiDistance));

        int chancePerKeyPress = Config.getJTextFieldWithinBoundsInt(this.chancePerKeyPressTextField,
                0, 100,
                "chance per keypress");
        settings.setSpriteTypeProperty(PowerMode3.ConfigType.VINE, "chancePerKeyPress",
                String.valueOf(chancePerKeyPress));


        settings.setSpriteTypeProperty(PowerMode3.ConfigType.VINE, "spriteEnabled", String.valueOf(spriteEnabled.isSelected()));
        settings.setSpriteTypeProperty(PowerMode3.ConfigType.VINE, "sprite2Enabled", String.valueOf(sprite2Enabled.isSelected()));
        settings.setSpriteTypeProperty(PowerMode3.ConfigType.VINE, "growFromRight", String.valueOf(growFromRight.isSelected()));
    }



    public static boolean USE_SPRITE(PowerMode3 settings){
        return Config.getBoolProperty(settings, PowerMode3.ConfigType.VINE,"spriteEnabled");
    }

    public static boolean USE_SPRITE2(PowerMode3 settings){
        return Config.getBoolProperty(settings, PowerMode3.ConfigType.VINE,"sprite2Enabled");
    }


    public static int MAX_PSI_SEARCH(PowerMode3 settings) {
        return Config.getIntProperty(settings, PowerMode3.ConfigType.VINE,"maxPsiSearchDistance");
    }

    public static int MIN_PSI_SEARCH(PowerMode3 settings) {
        return Config.getIntProperty(settings, PowerMode3.ConfigType.VINE,"minPsiSearchDistance");
    }

    public static boolean GROW_FROM_RIGHT(PowerMode3 settings){
        return Config.getBoolProperty(settings, PowerMode3.ConfigType.VINE,"growFromRight");
    }

    public static Color VINE_TOP_COLOR(PowerMode3 settings){
        return Config.getColorProperty(settings, PowerMode3.ConfigType.VINE,"Vine Top Color");
    }

    public static Color VINE_BOTTOM_COLOR(PowerMode3 settings){
        return Config.getColorProperty(settings, PowerMode3.ConfigType.VINE,"Vine Bottom Color");
    }

    public static int CHANCE_PER_KEY_PRESS(PowerMode3 settings){
        return Config.getIntProperty(settings, PowerMode3.ConfigType.VINE, "chancePerKeyPress");
    }




}
