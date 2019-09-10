package com.cschar.pmode3.config;

import com.cschar.pmode3.ParticleSpriteLightning;
import com.cschar.pmode3.PowerMode3;
import com.intellij.openapi.options.ConfigurationException;

import javax.swing.*;
import java.awt.*;

public class MandalaConfig extends JPanel {

    JPanel mainPanel;
    PowerMode3 settings;

    private JCheckBox innerBeamEnabledCheckBox;
    private JCheckBox outerBeamEnabledCheckBox;

    private JTextField chanceOfLightningTextField;
    private Color originalColorInner = Color.WHITE;
    private Color originalColorOuter = Color.CYAN;

    public MandalaConfig(PowerMode3 settings){
        this.settings = settings;


        mainPanel = new JPanel();
        mainPanel.setMaximumSize(new Dimension(1000,300));
        mainPanel.setLayout(new GridLayout(0,2));
        JPanel firstCol = new JPanel();
        firstCol.setLayout(new BoxLayout(firstCol, BoxLayout.PAGE_AXIS));
        mainPanel.add(firstCol);

        JPanel secondCol = new JPanel();
        secondCol.setLayout(new BoxLayout(secondCol, BoxLayout.PAGE_AXIS));
        JLabel headerLabel = new JLabel("Mandala Options");
        headerLabel.setFont(new Font ("Arial", Font.BOLD, 20));
        headerLabel.setAlignmentX( Component.RIGHT_ALIGNMENT);//0.0
        secondCol.add(headerLabel);
        mainPanel.add(secondCol);



    }


    public void loadValues(){

    }

    public void saveValues() throws ConfigurationException {


    }




    public JPanel getConfigPanel(){
        return this.mainPanel;
    }


    public static int CHANCE_OF_LIGHTNING(PowerMode3 settings){
        return Config.getIntProperty(settings, PowerMode3.SpriteType.LIGHTNING, "chanceOfLightning");
    }




}
