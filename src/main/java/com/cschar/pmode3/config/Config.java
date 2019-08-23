package com.cschar.pmode3.config;

import com.cschar.pmode3.PowerMode3;
import com.intellij.openapi.options.ConfigurationException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Config extends JPanel {


    PowerMode3 settings;

    public Config(PowerMode3 settings){
        this.settings = settings;
    }


    public static JPanel getColorPickerPanel(String labelTextKey, PowerMode3.SpriteType spriteType, PowerMode3 settings, Color defaultColor){
        //Lizard color
        JPanel colorPickerPanel = new JPanel();
        JLabel colorLabel = new JLabel(labelTextKey);
        JLabel colorPreviewLabel = new JLabel("[----]");
        colorPreviewLabel.setOpaque(true);
        colorLabel.setOpaque(true); //to show background  https://stackoverflow.com/a/2380328/403403
        colorPickerPanel.add(colorLabel);
        colorPickerPanel.add(colorPreviewLabel);
        if(settings.getSpriteTypeProperty(spriteType, labelTextKey) == null){

            colorPreviewLabel.setBackground(defaultColor);
//            colorLabel.setBackground(Color.GRAY);
            settings.setSpriteTypeProperty(spriteType, labelTextKey,
                    String.valueOf(defaultColor.getRGB()));
        }else{
            String colorRGB = settings.getSpriteTypeProperty(spriteType, labelTextKey);
            Color newColor = new Color(Integer.parseInt(colorRGB));
//            colorLabel.setBackground(newColor);
            colorPreviewLabel.setBackground(newColor);
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
//                    colorLabel.setBackground(newColor);
                    colorPreviewLabel.setBackground(newColor);
                    settings.setSpriteTypeProperty(spriteType, labelTextKey,
                            String.valueOf(newColor.getRGB()));
                }
            }
        });
        colorPickerPanel.add(colorPickerButton);


        colorPickerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        colorPickerPanel.setAlignmentX( Component.RIGHT_ALIGNMENT);//0.0
        colorPickerPanel.setMaximumSize(new Dimension(500, 50));

        return colorPickerPanel;
    }

    public static int getJTextFieldWithinBoundsInt(JTextField j, int min, int max, String name) throws ConfigurationException {
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

    public static float getJTextFieldWithinBoundsFloat(JTextField j, float min, float max, String name) throws ConfigurationException {
        float newValue;
        String errorMsg = String.format("%s : Please give a value between %.3f-%.3f", name, min,max);
        try {
            newValue = Float.parseFloat(j.getText());
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

    public static boolean getBoolProperty(PowerMode3 settings, PowerMode3.SpriteType type, String propertyName, boolean defaultValue){
        String property = settings.getSpriteTypeProperty(type, propertyName);
        if(property != null){
            return Boolean.parseBoolean(property);
        }else{
            return defaultValue;
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

    public static int getIntProperty(PowerMode3 settings, PowerMode3.SpriteType type, String propertyName, int defaultValue){
        String property = settings.getSpriteTypeProperty(type, propertyName);
        if(property != null){
            return Integer.parseInt(property);
        }else{
            return defaultValue;
        }
    }

    public static Color getColorProperty(PowerMode3 settings, PowerMode3.SpriteType type, String propertyName){

        String colorRGB = settings.getSpriteTypeProperty(type, propertyName);
        if(colorRGB != null){
            return new Color(Integer.parseInt(colorRGB));
        }else{
            return Color.GRAY;
        }
    }

    public static float getFloatProperty(PowerMode3 settings, PowerMode3.SpriteType type, String propertyName, float defaultValue){
        String property = settings.getSpriteTypeProperty(type, propertyName);
        if(property != null){
            return Float.parseFloat(property);
        }else{
            return defaultValue;
        }
    }


    public static JPanel generateCheckboxPanel(JCheckBox checkbox, String message){
        JPanel checkboxPanel = new JPanel();
        checkbox = new JCheckBox(message, true);
        checkboxPanel.add(checkbox);
        return checkboxPanel;

    }


}
