package com.cschar.pmode3.config;

import com.cschar.pmode3.PowerMode3;
import com.cschar.pmode3.config.common.ui.ZeranthiumColors;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;

public abstract class BaseConfig extends JPanel {



    PowerMode3 settings;

     JTextField maxAlphaTextField;
     JTextField chancePerKeyPressTextField;

     JPanel firstCol;
     JPanel secondCol;

    PowerMode3.ConfigType configType;
    private String title = "Base Options";
    public BaseConfig(PowerMode3 settings, PowerMode3.ConfigType type, String title){
        this.settings = settings;
        this.configType = type;
        this.title = title;

        this.setMaximumSize(new Dimension(1000,300));
        this.setLayout(new GridLayout(0,2)); //as many rows as necessary
        firstCol = new JPanel();
        firstCol.setLayout(new BoxLayout(firstCol, BoxLayout.PAGE_AXIS));
        this.add(firstCol);

        secondCol = new JPanel();
        secondCol.setLayout(new BoxLayout(secondCol, BoxLayout.Y_AXIS));
        JPanel headerPanel = new JPanel();
        JLabel headerLabel = new JLabel(this.title);
        headerLabel.setFont(new Font ("Arial", Font.BOLD, 20));
        headerPanel.add(headerLabel);
        headerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        headerPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        headerPanel.setMaximumSize(new Dimension(300,100));

        secondCol.add(headerPanel);
        this.add(secondCol);


        JPanel maxAlpha = new JPanel();
        maxAlpha.setLayout(new FlowLayout(FlowLayout.RIGHT));
        maxAlpha.add(new JLabel("Max Alpha: (0.1 - 1.0)"));
        this.maxAlphaTextField = new JTextField("");
        this.maxAlphaTextField.setMinimumSize(new Dimension(50,20));
        maxAlpha.add(maxAlphaTextField);
        maxAlpha.setAlignmentX( Component.RIGHT_ALIGNMENT);//0.0
        maxAlpha.setMaximumSize(new Dimension(500, 50));
        maxAlpha.setLayout(new FlowLayout(FlowLayout.RIGHT));
        secondCol.add(maxAlpha);


        this.chancePerKeyPressTextField = new JTextField();
        JLabel chancePerKeyPressLabel = new JLabel("Chance per keypress (0-100)");
        JPanel chancePerKeyPressPanel = new JPanel();
        chancePerKeyPressPanel.add(chancePerKeyPressLabel);
        chancePerKeyPressPanel.add(chancePerKeyPressTextField);
        chancePerKeyPressPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        chancePerKeyPressPanel.setAlignmentX( Component.RIGHT_ALIGNMENT);//0.0
        chancePerKeyPressPanel.setMaximumSize(new Dimension(400, 50));
        chancePerKeyPressPanel.setBackground(ZeranthiumColors.specialOption2);
        secondCol.add(chancePerKeyPressPanel);




    }



    public void loadValues(){
        this.maxAlphaTextField.setText(String.valueOf(Config.getFloatProperty(settings, configType,"maxAlpha", 1.0f)));
        this.chancePerKeyPressTextField.setText(String.valueOf(Config.getIntProperty(settings, configType,"chancePerKeyPress", 100)));

    }

    public void saveValues() throws ConfigurationException {


        int chancePerKeyPress = Config.getJTextFieldWithinBoundsInt(this.chancePerKeyPressTextField,
                0, 100,
                "chance particle spawns per keypress");
        settings.setSpriteTypeProperty(configType, "chancePerKeyPress",
                String.valueOf(chancePerKeyPress));

        float maxAlpha = Config.getJTextFieldWithinBoundsFloat(this.maxAlphaTextField,
                0.1f, 1.0f,
                "starting alpha value for particle");
        settings.setSpriteTypeProperty(configType, "maxAlpha",
                String.valueOf(maxAlpha));
    }

}
