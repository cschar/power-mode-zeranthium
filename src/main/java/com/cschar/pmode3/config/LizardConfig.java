package com.cschar.pmode3.config;

import com.cschar.pmode3.PowerMode3;
import com.intellij.openapi.options.ConfigurationException;

import javax.swing.*;
import java.awt.*;

public class LizardConfig extends JPanel {

    JPanel mainPanel;
    PowerMode3 settings;

    public JTextField maxPsiAnchorDistanceTextField;

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
        secondCol.add(headerLabel);
        mainPanel.add(secondCol);

        JPanel lizardColorPanel = Config.getColorPickerPanel("Lizard Color", PowerMode3.SpriteType.LIZARD, settings);
        secondCol.add(lizardColorPanel);

        //lizard density
        JPanel lop1 = new JPanel();
        lop1.add(new JLabel("Psi Anchor Scan Distance: "));
        this.maxPsiAnchorDistanceTextField = new JTextField("");

        this.maxPsiAnchorDistanceTextField.setMinimumSize(new Dimension(50,20));
        lop1.add(maxPsiAnchorDistanceTextField);

        secondCol.add(lop1);




        this.loadValues();

    }

    public JPanel getConfigPanel(){
        return this.mainPanel;
    }


    public void loadValues(){
        String psiSearchDistance = settings.getSpriteTypeProperty(PowerMode3.SpriteType.LIZARD, "maxPsiSearchDistance");
        if(psiSearchDistance != null){
            this.maxPsiAnchorDistanceTextField.setText(psiSearchDistance);
        }else{
            this.maxPsiAnchorDistanceTextField.setText("0");
        }

    }

    public void saveValues(int maxPsiSearchLimit) throws ConfigurationException {

        int lizardPsiDistance = Config.getJTextFieldWithinBounds(this.maxPsiAnchorDistanceTextField,
                0, maxPsiSearchLimit,
                "Distance to Psi Anchors will use when spawning lizards (cannot be greater than max defined at top)");
        settings.setSpriteTypeProperty(PowerMode3.SpriteType.LIZARD, "maxPsiSearchDistance", String.valueOf(lizardPsiDistance));
    }



    public static int MAX_PSI_SEARCH(PowerMode3 settings) {
        String value = settings.getSpriteTypeProperty(PowerMode3.SpriteType.LIZARD, "maxPsiSearchDistance");
        if(value != null){
            return Integer.parseInt(value);
        }else{
            return 0;
        }
    }
}
