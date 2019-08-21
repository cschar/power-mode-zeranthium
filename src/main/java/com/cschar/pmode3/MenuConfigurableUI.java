package com.cschar.pmode3;

import com.cschar.pmode3.config.LightningConfig;
import com.cschar.pmode3.config.LizardConfig;
import com.cschar.pmode3.config.VineConfig;
import com.intellij.openapi.options.ConfigurableUi;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

public class MenuConfigurableUI implements ConfigurableUi<PowerMode3> {
    private JPanel mainPanel;
    private JTextField lifetimeTextField;
    private JCheckBox isEnabledCheckBox;
    private JSlider sliderMaxSize;
    private JButton chooseColorButton;
    private JLabel particleColorLabel;
    private JCheckBox enableLightningCheckBox;
    private JCheckBox enableLizardCheckBox;
    private JCheckBox enableMOMACheckBox;
    private JCheckBox MOMAEmitBottomCheckBox;
    private JCheckBox MOMAEmitTopCheckBox;
    private JPanel theCustomCreatePanel;
    private JCheckBox enableBasicParticleCheckBox;
    private JCheckBox lightningAltCheckBox;
    private JTextField maxPsiSearchDistanceTextField;
    private JTextField numOfParticlesTextField;
    private JTextField shakeDistanceTextField;
    private JCheckBox enableVineCheckBox;


    private LightningConfig lightningConfig;
    private LizardConfig lizardConfig;
    private VineConfig vineConfig;

    PowerMode3 settings;
    //Constructor is called _AFTER_ createUIComponents when using IntelliJ GUI designer
    public MenuConfigurableUI(PowerMode3 powerMode3) {
        settings = powerMode3;
        isEnabledCheckBox.setSelected(powerMode3.isEnabled());

        shakeDistanceTextField.setText(Integer.toString(powerMode3.getShakeDistance()));
        numOfParticlesTextField.setText(Integer.toString(powerMode3.getNumOfParticles()));
        lifetimeTextField.setText(Integer.toString(powerMode3.getLifetime()));
        maxPsiSearchDistanceTextField.setText(Integer.toString(powerMode3.getMaxPsiSearchDistance()));
        sliderMaxSize.setValue(powerMode3.getParticleSize());

        particleColorLabel.setOpaque(true); //to show background  https://stackoverflow.com/a/2380328/403403
        particleColorLabel.setBackground(powerMode3.particleColor);
        chooseColorButton.addActionListener(e -> clickChooseColorButton(powerMode3));


        enableBasicParticleCheckBox.setSelected(powerMode3.getBasicParticleEnabled());


        if(powerMode3.getSpriteTypeEnabled(PowerMode3.SpriteType.LIGHTNING)){
            enableLightningCheckBox.setSelected(true);
        }
        if(powerMode3.getSpriteTypeEnabled(PowerMode3.SpriteType.LIGHTNING_ALT)){
            lightningAltCheckBox.setSelected(true);
        }
        if(powerMode3.getSpriteTypeEnabled(PowerMode3.SpriteType.LIZARD)){
            enableLizardCheckBox.setSelected(true);
        }
        if(powerMode3.getSpriteTypeEnabled(PowerMode3.SpriteType.MOMA)){
            enableMOMACheckBox.setSelected(true);
        }
        if(powerMode3.getSpriteTypeEnabled(PowerMode3.SpriteType.VINE)){
            enableVineCheckBox.setSelected(true);
        }

        boolean[] topBotEnabled = powerMode3.getSpriteTypeDirections(PowerMode3.SpriteType.MOMA);
        MOMAEmitTopCheckBox.setSelected(topBotEnabled[0]);
        MOMAEmitBottomCheckBox.setSelected(topBotEnabled[1]);

        //already initialized from createUIComponents below
        this.lizardConfig.loadValues();
        this.lightningConfig.loadValues();
        this.vineConfig.loadValues();
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
        settings.setEnabled(isEnabledCheckBox.isSelected());
        settings.setShakeDistance(getJTextFieldWithinBounds(shakeDistanceTextField, 0, 30, "Distance to shake editor in pixels when typing"));
        settings.setNumOfParticles(getJTextFieldWithinBounds(numOfParticlesTextField, 0, 10, "Number of Particles per keystroke"));
        settings.setLifetime(getJTextFieldWithinBounds(lifetimeTextField, 5, 500, "Lifetime"));
        settings.setMaxPsiSearchDistance(getJTextFieldWithinBounds(maxPsiSearchDistanceTextField,
                10, 1000, "Max Psi/Anchor Search Distance"));



        settings.setParticleSize(sliderMaxSize.getValue());

        //basic particle
        settings.setBasicParticleEnabled(enableBasicParticleCheckBox.isSelected());

        //lightning
        settings.setSpriteTypeEnabled(enableLightningCheckBox.isSelected(), PowerMode3.SpriteType.LIGHTNING);
        settings.setSpriteTypeEnabled(lightningAltCheckBox.isSelected(), PowerMode3.SpriteType.LIGHTNING_ALT);
        //lizard
        settings.setSpriteTypeEnabled(enableLizardCheckBox.isSelected(), PowerMode3.SpriteType.LIZARD);
        //MOMA
        settings.setSpriteTypeEnabled(enableMOMACheckBox.isSelected(), PowerMode3.SpriteType.MOMA);
        settings.setSpriteTypeDirections(PowerMode3.SpriteType.MOMA, MOMAEmitTopCheckBox.isSelected(), MOMAEmitBottomCheckBox.isSelected());

        //Vine
        settings.setSpriteTypeEnabled(enableVineCheckBox.isSelected(), PowerMode3.SpriteType.VINE);

        //LightningALT CUSTOM



        //save values
        int maxPsiSearch = Integer.parseInt(maxPsiSearchDistanceTextField.getText());
        this.lizardConfig.saveValues(maxPsiSearch);

        this.lightningConfig.saveValues();
        this.vineConfig.saveValues(maxPsiSearch);


    }

    @NotNull
    @Override
    public JComponent getComponent() {
        JBScrollPane scrollPane = new JBScrollPane(this.mainPanel);

        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);


        scrollPane.getVerticalScrollBar().getValue();
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        settings.setScrollBarPosition(e.getValue());
//                        PowerMode3.getInstance().setScrollBarPosition(e.getValue());
                    }
                });
            }
        });

        //https://stackoverflow.com/a/46204157/403403
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                scrollPane.getVerticalScrollBar().setValue(settings.getScrollBarPosition());
            }
        });

//        return contentPane;
        return scrollPane;
//        return this.mainPanel;
    }

    //https://www.jetbrains.com/help/idea/creating-form-initialization-code.html
    private void createUIComponents() {
        PowerMode3 settings = PowerMode3.getInstance();
        // TODO: place custom component creation code here
        System.out.println("custom create");
        theCustomCreatePanel = new JPanel();
        theCustomCreatePanel.setOpaque(false);
        theCustomCreatePanel.setBorder(new EmptyBorder(10, 10, 200, 10));
        //https://docs.oracle.com/javase/tutorial/uiswing/layout/visual.html
        theCustomCreatePanel.setLayout(new BoxLayout(theCustomCreatePanel, BoxLayout.PAGE_AXIS));


        this.theCustomCreatePanel.add(this.createSpacer());

        this.lightningConfig = new LightningConfig(settings);
        this.theCustomCreatePanel.add(lightningConfig.getConfigPanel());


        this.theCustomCreatePanel.add(this.createSpacer());
        this.lizardConfig = new LizardConfig(settings);
        this.theCustomCreatePanel.add(lizardConfig.getConfigPanel());
        this.theCustomCreatePanel.add(this.createSpacer());


        this.vineConfig = new VineConfig(settings);
        this.theCustomCreatePanel.add(vineConfig.getConfigPanel());
        this.theCustomCreatePanel.add(this.createSpacer());

        JPanel footerPanel = new JPanel();
        footerPanel.setMinimumSize(new Dimension(100, 300));
        this.theCustomCreatePanel.add(footerPanel);
    }


    private JPanel createSpacer(){
        JPanel spacer1 = new JPanel();
        spacer1.setMinimumSize(new Dimension(100,30));
        spacer1.setMaximumSize(new Dimension(1000,30));
        spacer1.setBackground(Color.lightGray);

        return spacer1;
    }


    private int getJTextFieldWithinBounds(JTextField j, int min, int max, String name) throws ConfigurationException{
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

}
