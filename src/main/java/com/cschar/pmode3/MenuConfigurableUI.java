package com.cschar.pmode3;

import com.intellij.openapi.options.ConfigurableUi;
import com.intellij.openapi.options.ConfigurationException;
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
    private JCheckBox enableLightningCheckBox;
    private JCheckBox enableLizardCheckBox;
    private JCheckBox enableMOMACheckBox;
    private JCheckBox MOMAEmitBottomCheckBox;
    private JCheckBox MOMAEmitTopCheckBox;
    private JPanel myCustomCreatePanel;
    private JCheckBox enableBasicParticleCheckBox;

    public MenuConfigurableUI(PowerMode3 powerMode3) {
        isEnabledCheckBox.setSelected(powerMode3.isEnabled());
        lifetimeTextField.setText(Integer.toString(powerMode3.getLifetime()));
        sliderMaxSize.setValue(powerMode3.getParticleSize());

        particleColorLabel.setOpaque(true); //to show background  https://stackoverflow.com/a/2380328/403403
        particleColorLabel.setBackground(powerMode3.particleColor);
        chooseColorButton.addActionListener(e -> clickChooseColorButton(powerMode3));


        enableBasicParticleCheckBox.setSelected(powerMode3.getBasicParticleEnabled());

        if(powerMode3.getSpriteTypeEnabled() == 1){
            enableLightningCheckBox.setSelected(true);
        }
        else if(powerMode3.getSpriteTypeEnabled() == 2){
            enableLizardCheckBox.setSelected(true);
        }
    }

    private void clickChooseColorButton(PowerMode3 powerMode3){
        Color newColor = JColorChooser.showDialog(this.mainPanel, "Choose particle color",
                JBColor.darkGray);

        powerMode3.setParticleRGB(newColor.getRGB());
        particleColorLabel.setBackground(powerMode3.particleColor);

    }


    @Override
    public void reset(@NotNull PowerMode3 settings) {
        isEnabledCheckBox.setSelected(settings.isEnabled());
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


        settings.setBasicParticleEnabled(enableBasicParticleCheckBox.isSelected());

        if(enableLightningCheckBox.isSelected()){
            settings.setSpriteTypeEnabled(1);
        }else if(enableLizardCheckBox.isSelected()){
            settings.setSpriteTypeEnabled(2);
        }else{
            settings.setSpriteTypeEnabled(0);
        }

    }

    @NotNull
    @Override
    public JComponent getComponent() {
        return this.mainPanel;
    }

//    private void createUIComponents() {
//        // TODO: place custom component creation code here
//
//        JLabel jj = new JLabel();
//        jj.setText("Hello hello");
//        this.myCustomCreatePanel.add(jj);
//        JPanel jp = new JPanel();
//        jp.setPreferredSize(new Dimension(400,400));
//        jp.setBackground(Color.cyan);
//        this.myCustomCreatePanel.add(jp);
//
//        JLabel jj2 = new JLabel();
//        jj2.setText("Hello hello");
//        jp.add(jj2);
//
//
//
//    }
}
