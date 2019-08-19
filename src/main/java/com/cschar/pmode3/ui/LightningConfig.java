package com.cschar.pmode3.ui;

import com.cschar.pmode3.PowerMode3;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LightningConfig extends JPanel {

    JPanel mainPanel;
    PowerMode3 settings;

    private JTextField chanceOfLightningTextField;

    public LightningConfig(PowerMode3 settings){
        this.settings = settings;

        mainPanel = new JPanel();
        mainPanel.setMaximumSize(new Dimension(1000,300));
        mainPanel.setLayout(new GridLayout(0,2));
        JPanel firstCol = new JPanel();
        mainPanel.add(firstCol);

        JPanel secondCol = new JPanel();
        secondCol.setLayout(new BoxLayout(secondCol, BoxLayout.PAGE_AXIS));
        JLabel headerLabel = new JLabel("Lightning Options");
        headerLabel.setFont(new Font ("Arial", Font.BOLD, 20));
        secondCol.add(headerLabel);
        mainPanel.add(secondCol);



        JPanel innerBeamColorPanel = ConfigPanel.getColorPickerPanel("inner Beam Color", PowerMode3.SpriteType.LIGHTNING_ALT, settings);
        JPanel outerBeamColorPanel = ConfigPanel.getColorPickerPanel("outer Beam Color", PowerMode3.SpriteType.LIGHTNING_ALT, settings);
        secondCol.add(innerBeamColorPanel);
        secondCol.add(outerBeamColorPanel);

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

    public void loadValues(){
        String chanceOfLightning = settings.getSpriteTypeProperty(PowerMode3.SpriteType.LIGHTNING, "chanceOfLightning");
        if(chanceOfLightning != null){
            this.chanceOfLightningTextField.setText(chanceOfLightning);
        }else{
            this.chanceOfLightningTextField.setText("0");
        }

    }

    public void saveValues() throws ConfigurationException {

        int chanceOfLightning = ConfigPanel.getJTextFieldWithinBounds(this.chanceOfLightningTextField,
                0, 100,
                "chance lightning spawns on keypress");
        settings.setSpriteTypeProperty(PowerMode3.SpriteType.LIGHTNING, "chanceOfLightning", String.valueOf(chanceOfLightning));
    }

    public JPanel getConfigPanel(){
        return this.mainPanel;
    }


}
