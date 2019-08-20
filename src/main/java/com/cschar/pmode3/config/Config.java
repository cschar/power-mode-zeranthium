package com.cschar.pmode3.config;

import com.cschar.pmode3.PowerMode3;
import com.intellij.openapi.options.ConfigurationException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Config extends JPanel {

    JPanel mainPanel;
    PowerMode3 settings;

    public Config(PowerMode3 settings){
        this.settings = settings;
    }


    public static JPanel getColorPickerPanel(String labelTextKey, PowerMode3.SpriteType spriteType, PowerMode3 settings){
        //Lizard color
        JPanel colorPickerPanel = new JPanel();
        JLabel colorLabel = new JLabel(labelTextKey);
        colorLabel.setOpaque(true); //to show background  https://stackoverflow.com/a/2380328/403403
        colorPickerPanel.add(colorLabel);
        if(settings.getSpriteTypeProperty(spriteType, labelTextKey) == null){

            colorLabel.setBackground(Color.GRAY);
            settings.setSpriteTypeProperty(spriteType, labelTextKey,
                    String.valueOf(Color.GRAY.getRGB()));
        }else{
            String colorRGB = settings.getSpriteTypeProperty(spriteType, labelTextKey);
            Color newColor = new Color(Integer.parseInt(colorRGB));
            colorLabel.setBackground(newColor);
        };


        JButton colorPickerButton = new JButton("Pick color");
        colorPickerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String colorRGB = settings.getSpriteTypeProperty(spriteType, labelTextKey);
                Color prevColor = new Color(Integer.parseInt(colorRGB));

                Color newColor = JColorChooser.showDialog(colorPickerPanel, "Choose Color",
                        prevColor);

                if(newColor != null) {
                    colorLabel.setBackground(newColor);
                    settings.setSpriteTypeProperty(spriteType, labelTextKey,
                            String.valueOf(newColor.getRGB()));
                }
            }
        });
        colorPickerPanel.add(colorPickerButton);

        return colorPickerPanel;
    }

    public static int getJTextFieldWithinBounds(JTextField j, int min, int max, String name) throws ConfigurationException {
        int newValue;
        String errorMsg = String.format("%s : Please give a value between %d-%d", name, min,max);
        try {
            newValue = Integer.parseInt(j.getText());
            if (newValue < min || newValue > max) {
                throw new ConfigurationException(errorMsg);
            }
        } catch (NumberFormatException e){
            throw new ConfigurationException(errorMsg);
        }
        return newValue;
    }


    public static boolean getBoolProperty(PowerMode3 settings, PowerMode3.SpriteType type, String propertyName){
        String property = settings.getSpriteTypeProperty(type, propertyName);
        if(property != null){
            return Boolean.parseBoolean(property);
        }else{
            return false;
        }
    }

    public static int getIntProperty(PowerMode3 settings, PowerMode3.SpriteType type, String propertyName){
        String property = settings.getSpriteTypeProperty(type, propertyName);
        if(property != null){
            return Integer.parseInt(property);
        }else{
            return 0;
        }
    }


}
