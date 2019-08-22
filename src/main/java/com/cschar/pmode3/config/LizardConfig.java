package com.cschar.pmode3.config;

import com.cschar.pmode3.PowerMode3;
import com.intellij.openapi.options.ConfigurationException;

import javax.swing.*;
import java.awt.*;

public class LizardConfig extends JPanel {

    JPanel mainPanel;
    PowerMode3 settings;

    public JTextField maxPsiAnchorDistanceTextField;
    public JTextField minPsiAnchorDistanceTextField;
    private JTextField chanceOfSpawnTextField;
    private JTextField maxAnchorsToUse;
    private JTextField chancePerKeyPressTextField;


    public LizardConfig(PowerMode3 settings){
        this.settings = settings;
//        this.add(new JLabel("Lizard options"));

        //LIZARD
        //Build without GUI designer

        mainPanel = new JPanel();
        mainPanel.setMaximumSize(new Dimension(1000,300));
        mainPanel.setLayout(new GridLayout(0,2));
        JPanel firstCol = new JPanel();
        firstCol.setLayout(new BoxLayout(firstCol, BoxLayout.PAGE_AXIS));
        mainPanel.add(firstCol);

        JPanel secondCol = new JPanel();
        secondCol.setLayout(new BoxLayout(secondCol, BoxLayout.PAGE_AXIS));
        JLabel headerLabel = new JLabel("Lizard Options");
        headerLabel.setFont(new Font ("Arial", Font.BOLD, 20));
//        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerLabel.setBounds(10,10,50,30);
        secondCol.add(headerLabel);
        mainPanel.add(secondCol);

//        JPanel lizardColorPanel = Config.getColorPickerPanel("Lizard Color", PowerMode3.SpriteType.LIZARD, settings, Color.GREEN);
//        secondCol.add(lizardColorPanel);


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

        this.chancePerKeyPressTextField = new JTextField();
        JLabel chancePerKeyPressLabel = new JLabel("Chance per keypress (0-100)");
        JPanel chancePerKeyPressPanel = new JPanel();
        chancePerKeyPressPanel.add(chancePerKeyPressLabel);
        chancePerKeyPressPanel.add(chancePerKeyPressTextField);
        secondCol.add(chancePerKeyPressPanel);

        this.chanceOfSpawnTextField = new JTextField();
        JLabel chanceOfSpawnLabel = new JLabel("Chance of Lizard per anchor (0-100)");
        JPanel chancePanel = new JPanel();
        chancePanel.add(chanceOfSpawnLabel);
        chancePanel.add(chanceOfSpawnTextField);
        secondCol.add(chancePanel);

        this.maxAnchorsToUse = new JTextField();
//        this.maxAnchorsToUse.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JLabel maxAnchorsLabel = new JLabel("Max Anchors to Use (1-100)");
        JPanel maxAnchorsPanel = new JPanel();
        maxAnchorsPanel.add(maxAnchorsLabel);
        maxAnchorsPanel.add(maxAnchorsToUse);
//        maxAnchorsPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        secondCol.add(maxAnchorsPanel);



        this.loadValues();

    }

    public JPanel getConfigPanel(){
        return this.mainPanel;
    }


    public void loadValues(){
        this.maxPsiAnchorDistanceTextField.setText(String.valueOf(Config.getIntProperty(settings, PowerMode3.SpriteType.LIZARD,"maxPsiSearchDistance", 300)));
        this.minPsiAnchorDistanceTextField.setText(String.valueOf(Config.getIntProperty(settings, PowerMode3.SpriteType.LIZARD,"minPsiSearchDistance", 100)));

        this.chancePerKeyPressTextField.setText(String.valueOf(Config.getIntProperty(settings, PowerMode3.SpriteType.LIZARD,"chancePerKeyPress")));
        this.chanceOfSpawnTextField.setText(String.valueOf(Config.getIntProperty(settings, PowerMode3.SpriteType.LIZARD,"chanceOfSpawn")));

        this.maxAnchorsToUse.setText(String.valueOf(Config.getIntProperty(settings, PowerMode3.SpriteType.LIZARD,"maxAnchorsToUse", 1)));

    }

    public void saveValues(int maxPsiSearchLimit) throws ConfigurationException {

        int minLizardPsiDistance = Config.getJTextFieldWithinBounds(this.minPsiAnchorDistanceTextField,
                0, maxPsiSearchLimit,
                "Min Distance to Psi Anchors will use when spawning lizards (cannot be greater than max defined at top)");
        settings.setSpriteTypeProperty(PowerMode3.SpriteType.LIZARD, "minPsiSearchDistance",
                String.valueOf(minLizardPsiDistance));

        int maxLizardPsiDistance = Config.getJTextFieldWithinBounds(this.maxPsiAnchorDistanceTextField,
                0, maxPsiSearchLimit,
                "Max Distance to Psi Anchors will use when spawning lizards (cannot be greater than max defined at top)");
        settings.setSpriteTypeProperty(PowerMode3.SpriteType.LIZARD, "maxPsiSearchDistance",
                String.valueOf(maxLizardPsiDistance));

        int chancePerKeyPress = Config.getJTextFieldWithinBounds(this.chancePerKeyPressTextField,
                0, 100,
                "chance lizards spawn per keypress");
        settings.setSpriteTypeProperty(PowerMode3.SpriteType.LIZARD, "chancePerKeyPress",
                String.valueOf(chancePerKeyPress));

        int chanceOfSpawn = Config.getJTextFieldWithinBounds(this.chanceOfSpawnTextField,
                0, 100,
                "chance lizard spawns for anchor");
        settings.setSpriteTypeProperty(PowerMode3.SpriteType.LIZARD, "chanceOfSpawn",
                String.valueOf(chanceOfSpawn));

        int maxAnchorsToUse = Config.getJTextFieldWithinBounds(this.maxAnchorsToUse,
                1, 100,
                "max anchors to use when spawning lizards");
        settings.setSpriteTypeProperty(PowerMode3.SpriteType.LIZARD, "maxAnchorsToUse",
                String.valueOf(maxAnchorsToUse));
    }



    public static int MAX_PSI_SEARCH(PowerMode3 settings) {
        return Config.getIntProperty(settings, PowerMode3.SpriteType.LIZARD,"maxPsiSearchDistance");
    }
    public static int MIN_PSI_SEARCH(PowerMode3 settings) {
        return Config.getIntProperty(settings, PowerMode3.SpriteType.LIZARD,"minPsiSearchDistance");
    }

    public static int CHANCE_OF_SPAWN(PowerMode3 settings){
        return Config.getIntProperty(settings, PowerMode3.SpriteType.LIZARD, "chanceOfSpawn");
    }

    public static int MAX_ANCHORS_TO_USE(PowerMode3 settings){
        return Config.getIntProperty(settings, PowerMode3.SpriteType.LIZARD, "maxAnchorsToUse");
    }

    public static int CHANCE_PER_KEY_PRESS(PowerMode3 settings){
        return Config.getIntProperty(settings, PowerMode3.SpriteType.LIZARD, "chancePerKeyPress");
    }
}
