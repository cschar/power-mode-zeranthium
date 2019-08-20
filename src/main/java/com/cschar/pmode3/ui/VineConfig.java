package com.cschar.pmode3.ui;

import com.bmesta.powermode.PowerMode;
import com.cschar.pmode3.PowerMode3;
import com.intellij.openapi.options.ConfigurationException;

import javax.swing.*;
import java.awt.*;

public class VineConfig extends JPanel {

    JPanel mainPanel;
    PowerMode3 settings;

    public JTextField maxPsiAnchorDistanceTextField;
    JCheckBox spriteEnabled;

    public VineConfig(PowerMode3 settings){
        this.settings = settings;


        mainPanel = new JPanel();
        mainPanel.setMaximumSize(new Dimension(1000,300));
        mainPanel.setLayout(new GridLayout(0,2));
        JPanel firstCol = new JPanel();
        mainPanel.add(firstCol);

        JPanel secondCol = new JPanel();
        secondCol.setLayout(new BoxLayout(secondCol, BoxLayout.PAGE_AXIS));
        JLabel headerLabel = new JLabel("Vine Options");
        headerLabel.setFont(new Font ("Arial", Font.BOLD, 20));
        secondCol.add(headerLabel);
        mainPanel.add(secondCol);

        JPanel vineColorPanel = ConfigPanel.getColorPickerPanel("Vine Color", PowerMode3.SpriteType.VINE, settings);
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


    }

    public void saveValues(int maxPsiSearchLimit) throws ConfigurationException {

        int vinePsiDistance = ConfigPanel.getJTextFieldWithinBounds(this.maxPsiAnchorDistanceTextField,
                0, maxPsiSearchLimit,
                "Distance to Psi Anchors will use when spawning vines (cannot be greater than max defined at top)");
        settings.setSpriteTypeProperty(PowerMode3.SpriteType.VINE, "maxPsiSearchDistance", String.valueOf(vinePsiDistance));
        settings.setSpriteTypeProperty(PowerMode3.SpriteType.VINE, "spriteEnabled", String.valueOf(spriteEnabled.isSelected()));
    }



    public static boolean USE_SPRITES(PowerMode3 settings){
        String spriteEnabled = settings.getSpriteTypeProperty(PowerMode3.SpriteType.VINE, "spriteEnabled");
        if(spriteEnabled != null){
            return Boolean.parseBoolean(spriteEnabled);
        }else{
            return false;
        }
    }


    public static int MAX_PSI_SEARCH(PowerMode3 settings) {
        String value = settings.getSpriteTypeProperty(PowerMode3.SpriteType.VINE, "maxPsiSearchDistance");
        if(value != null){
            return Integer.parseInt(value);
        }else{
            return 0;
        }
    }


}
