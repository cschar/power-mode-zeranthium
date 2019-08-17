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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    private JCheckBox lightningAltCheckBox;

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
        if(powerMode3.getSpriteTypeEnabled(PowerMode3.SpriteType.LIGHTNING_ALT)){
            lightningAltCheckBox.setSelected(true);
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
        settings.setSpriteTypeEnabled(lightningAltCheckBox.isSelected(), PowerMode3.SpriteType.LIGHTNING_ALT);
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
        jj.setText("Lightning Options");
        this.myCustomCreatePanel.add(jj);


        LightningConfigUI lightningConfigUI = new LightningConfigUI(PowerMode3.getInstance());
        lightningConfigUI.imagePreviewPanel.setBackground(Color.WHITE);

        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/blender/lightning/lightning10101.png"));
        imageIcon = new ImageIcon(imageIcon.getImage().getScaledInstance(100,100, Image.SCALE_DEFAULT));
        lightningConfigUI.imagePreviewLabel.setIcon(new ImageIcon(getClass().getResource("/blender/lightning/lightning10101.png")));
        lightningConfigUI.imagePreviewLabel.setIcon(imageIcon);

        JComponent p = lightningConfigUI.getComponent();
        p.setMaximumSize(new Dimension(1000,400));

        this.myCustomCreatePanel.add(p);

        this.myCustomCreatePanel.add(this.createSpacer());

//        LightningConfigUI lightningConfigUI2 = new LightningConfigUI();
//        lightningConfigUI2.imagePreviewPanel.setBackground(Color.WHITE);
//        JComponent lcui2 = lightningConfigUI2.getComponent();
//        lcui2.setMaximumSize(new Dimension(500,400));
//        this.myCustomCreatePanel.add(lcui2);

        //LIZARD
        //Build without GUI designer
        JPanel lizardPanel = new JPanel();
        lizardPanel.setMaximumSize(new Dimension(1000,300));

        lizardPanel.setLayout(new GridLayout(0,2));

        JPanel lizardPreviewPanel = new JPanel();
        lizardPanel.add(lizardPreviewPanel);

        JPanel lizardOptionPanel = new JPanel();
        lizardOptionPanel.setLayout(new BoxLayout(lizardOptionPanel, BoxLayout.PAGE_AXIS));
        lizardOptionPanel.add(new JLabel("Options"));

        //Lizard color
        JPanel lizardColorPanel = new JPanel();
        JLabel lizardColorLabel = new JLabel("Lizard Color");
        lizardColorLabel.setOpaque(true); //to show background  https://stackoverflow.com/a/2380328/403403
        lizardColorPanel.add(lizardColorLabel);
        JButton lizardColorPickerButton = new JButton("Pick color");
        lizardColorPickerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(lizardColorPanel, "Choose lizard color",
                        JBColor.darkGray);

//                PowerMode3.getInstance().setSomePersistantState
                lizardColorLabel.setBackground(newColor);
            }
        });
        lizardColorPanel.add(lizardColorPickerButton);
        lizardOptionPanel.add(lizardColorPanel);

        //lizard density
        JPanel lop1 = new JPanel();
        lop1.add(new JLabel("lizard density: "));
        lop1.add(new JTextField("test"));

        lizardOptionPanel.add(lop1);

        lizardPanel.add(lizardOptionPanel);


        this.myCustomCreatePanel.add(this.createSpacer());
        this.myCustomCreatePanel.add(lizardPanel);

    }


    private JPanel createSpacer(){
        JPanel spacer1 = new JPanel();
        spacer1.setMinimumSize(new Dimension(100,30));
        spacer1.setMaximumSize(new Dimension(1000,30));
        spacer1.setBackground(Color.lightGray);

        return spacer1;
    }

}
