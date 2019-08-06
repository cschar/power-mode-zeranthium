package com.cschar.pmode3;

import com.bmesta.powermode.PowerMode;
import com.intellij.openapi.options.ConfigurableUi;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class MenuConfigurableUI implements ConfigurableUi<PowerMode3> {
    private JPanel mainPanel;
    private JTextField lifetimeTextField;
    private JLabel isEnabledLabel;
    private JCheckBox isEnabledCheckBox;
    private JSlider sliderMaxSize;
    private JButton chooseColorButton;
    private JLabel particleColorLabel;

    public MenuConfigurableUI(PowerMode3 powerMode3) {
        isEnabledCheckBox.setSelected(powerMode3.isEnabled());
        lifetimeTextField.setText(Integer.toString(powerMode3.getLifetime()));

        sliderMaxSize.setValue(powerMode3.getParticleSize());


        particleColorLabel.setOpaque(true); //to show background  https://stackoverflow.com/a/2380328/403403
        particleColorLabel.setBackground(powerMode3.particleColor);
        chooseColorButton.addActionListener(e -> clickChooseColorButton(powerMode3));
    }

    private void clickChooseColorButton(PowerMode3 powerMode3){
        JColorChooser colorChooser = new JColorChooser();



//        Color newColor = JColorChooser.showDialog(this.mainPanel, "Choose particle color",
//                                                              powerMode3.getParticleColor());
        Color newColor = JColorChooser.showDialog(this.mainPanel, "Choose particle color",
                JBColor.darkGray);

        //powerMode3.particleColor = newColor;
        powerMode3.setParticleRGB(newColor.getRGB());
        particleColorLabel.setBackground(powerMode3.particleColor);

    }

    private void setSettings(PowerMode3 settings){

//        int newLifeTime=5;
//        newLifeTime = Integer.parseInt(lifetimeTextField.getText());
//        lifetimeTextField.setText(String.format("%d",newLifeTime));
    }

    @Override
    public void reset(@NotNull PowerMode3 settings) {
        isEnabledCheckBox.setSelected(settings.isEnabled());

        //setSettings(settings);
    }

    @Override
    public boolean isModified(@NotNull PowerMode3 settings) {
        //ideally check if checkbox is equal to settings boolean
        return true;
    }

    @Override
    public void apply(@NotNull PowerMode3 settings) throws ConfigurationException {
        int newLifetime;
        int min = 5;
        int max = 500;


        String errorMsg = String.format("Please give a lifetime between %d-%d", min,max);
        try {
            newLifetime = Integer.parseInt(lifetimeTextField.getText());
            if (newLifetime < min || newLifetime > max) {
               throw new ConfigurationException(errorMsg);
            }
        } catch (NumberFormatException e){
            throw new ConfigurationException(errorMsg);
        }

        settings.setLifetime(newLifetime);


        settings.setParticleSize(sliderMaxSize.getValue());
    }

    @NotNull
    @Override
    public JComponent getComponent() {
        return this.mainPanel;
    }
}
