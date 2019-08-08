package com.cschar.pmode3;

import com.cschar.pmode3.ui.LightningConfigUI;
import com.intellij.openapi.options.ConfigurableUi;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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


        if(powerMode3.getSpriteTypeEnabled(PowerMode3.SpriteType.LIGHTNING)){
            enableLightningCheckBox.setSelected(true);
        }
        if(powerMode3.getSpriteTypeEnabled(PowerMode3.SpriteType.LIZARD)){
            enableLizardCheckBox.setSelected(true);
        }
        if(powerMode3.getSpriteTypeEnabled(PowerMode3.SpriteType.MOMA)){
            enableMOMACheckBox.setSelected(true);
        }

        boolean[] topBotEnabled = powerMode3.getSpriteTypeDirections(PowerMode3.SpriteType.MOMA);
        MOMAEmitTopCheckBox.setSelected(topBotEnabled[0]);
        MOMAEmitBottomCheckBox.setSelected(topBotEnabled[1]);

        setCustom();
    }

    private void setCustom(){
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

        //basic particle
        settings.setBasicParticleEnabled(enableBasicParticleCheckBox.isSelected());

        //lightning
        settings.setSpriteTypeEnabled(enableLightningCheckBox.isSelected(), PowerMode3.SpriteType.LIGHTNING);
        //lizard
        settings.setSpriteTypeEnabled(enableLizardCheckBox.isSelected(), PowerMode3.SpriteType.LIZARD);
        //MOMA
        settings.setSpriteTypeEnabled(enableMOMACheckBox.isSelected(), PowerMode3.SpriteType.MOMA);
        settings.setSpriteTypeDirections(PowerMode3.SpriteType.MOMA, MOMAEmitTopCheckBox.isSelected(), MOMAEmitBottomCheckBox.isSelected());


    }

    @NotNull
    @Override
    public JComponent getComponent() {
        JBScrollPane scrollPane = new JBScrollPane(this.mainPanel);

        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
//        scrollPane.setBounds(50, 30, 700, 200);

//        JPanel contentPane = new JPanel(null);
//        contentPane.setPreferredSize(new Dimension(500, 400));
//        contentPane.add(scrollPane);

//        return contentPane;
        return scrollPane;
//        return this.mainPanel;
    }

    //https://www.jetbrains.com/help/idea/creating-form-initialization-code.html
    private void createUIComponents() {
        // TODO: place custom component creation code here
        System.out.println("custom create");
        myCustomCreatePanel = new JPanel();
        myCustomCreatePanel.setBorder(new EmptyBorder(10, 10, 200, 10));
        //https://docs.oracle.com/javase/tutorial/uiswing/layout/visual.html
        myCustomCreatePanel.setLayout(new BoxLayout(myCustomCreatePanel, BoxLayout.PAGE_AXIS));

        JLabel jj = new JLabel();
        jj.setText("Hello hello");
        this.myCustomCreatePanel.add(jj);
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.PAGE_AXIS));
//        jp.setPreferredSize(new Dimension(400,400));
        jp.setBackground(Color.cyan);
        this.myCustomCreatePanel.add(jp);

        for(int i =0; i < 10; i++) {
            JLabel jj2 = new JLabel();
            jj2.setText("Hello \n hello");
            jp.add(jj2);
        }

        LightningConfigUI lightningConfigUI = new LightningConfigUI();
        JComponent p = lightningConfigUI.getComponent();
//        p.setPreferredSize(new Dimension(400,400));

        this.myCustomCreatePanel.add(p);

        JPanel spacer1 = new JPanel();
        spacer1.setMinimumSize(new Dimension(100,100));
        spacer1.setBackground(Color.GRAY);

        this.myCustomCreatePanel.add(spacer1);


        LightningConfigUI lightningConfigUI2 = new LightningConfigUI();
        lightningConfigUI2.infoPanel.setBackground(Color.WHITE);

        JComponent lcui2 = lightningConfigUI2.getComponent();
        this.myCustomCreatePanel.add(lcui2);

        JPanel lizardPanel = new JPanel();
    }

}
