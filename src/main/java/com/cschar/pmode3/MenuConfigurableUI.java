package com.cschar.pmode3;

import com.cschar.pmode3.config.*;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.KeymapManager;
import com.intellij.openapi.options.ConfigurableUi;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.util.ui.JBUI;
import org.intellij.lang.annotations.JdkConstants;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.KeyManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.net.URL;
import java.util.concurrent.ThreadLocalRandom;

public class MenuConfigurableUI implements ConfigurableUi<PowerMode3> {
    private JPanel mainPanel;
    private JTextField lifetimeTextField;
    private JCheckBox isEnabledCheckBox;
    private JLabel toggleHotkeyLabel;
    
    private JPanel particleSettingsPanel;
    private JPanel soundSettingsPanel;
    private SoundConfig soundConfig;

    private JCheckBox enableLightningCheckBox;
    private JCheckBox enableLizardCheckBox;
    private JCheckBox enableMOMACheckBox;
    private JPanel theCustomCreatePanel;
    private JCheckBox enableBasicParticleCheckBox;
    private JCheckBox lightningAltCheckBox;
    private JTextField maxPsiSearchDistanceTextField;

    private JTextField shakeDistanceTextField;
    private JCheckBox enableVineCheckBox;
    private JCheckBox enableMandalaCheckbox;
    private JCheckBox enableLinkerCheckbox;



    private BasicParticleConfig basicParticleConfig;
    private LightningConfig lightningConfig;
    private LightningAltConfig lightningAltConfig;
    private LizardConfig lizardConfig;
    private VineConfig vineConfig;
    private MOMAConfig momaConfig;
    private Mandala2Config mandala2Config;
    private LinkerConfig linkerConfig;

    PowerMode3 settings;
    //Constructor is called _AFTER_ createUIComponents when using IntelliJ GUI designer
    public MenuConfigurableUI(PowerMode3 powerMode3) {
//        particleSettingsPanel.setBackground(theCustomCreatePanel.getBackground());

        settings = powerMode3;
        isEnabledCheckBox.setSelected(powerMode3.isEnabled());

        KeymapManager km = KeymapManager.getInstance();
        //defined in the META-INF/plugin.xml <action id=""> param
        Shortcut[] zShortcuts = km.getActiveKeymap().getShortcuts("togglePowerModeZeranthium");
        StringBuffer sb = new StringBuffer();
        for(Shortcut sc: zShortcuts){
            sb.append("(<b>" + sc + "</b>)");
        }
        String labelText = String.format("<html>%s - %s</html>", toggleHotkeyLabel.getText(), sb.toString());
//        toggleHotkeyLabel.setText(toggleHotkeyLabel.getText() + sb.toString());
        toggleHotkeyLabel.setText(labelText);







//        HotKeyEnabledAction.ACTIONS_KEY.

        shakeDistanceTextField.setText(Integer.toString(powerMode3.getShakeDistance()));
        lifetimeTextField.setText(Integer.toString(powerMode3.getLifetime()));
        maxPsiSearchDistanceTextField.setText(Integer.toString(powerMode3.getMaxPsiSearchDistance()));



//        enableBasicParticleCheckBox.setSelected(powerMode3.getBasicParticleEnabled());

        if(powerMode3.getSpriteTypeEnabled(PowerMode3.SpriteType.BASIC_PARTICLE)){
            enableBasicParticleCheckBox.setSelected(true);
        }

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
        if(powerMode3.getSpriteTypeEnabled(PowerMode3.SpriteType.MANDALA)){
            enableMandalaCheckbox.setSelected(true);
        }
        if(powerMode3.getSpriteTypeEnabled(PowerMode3.SpriteType.LINKER)){
            enableLinkerCheckbox.setSelected(true);
        }


        //already initialized from createUIComponents below
        this.basicParticleConfig.loadValues();
        this.lizardConfig.loadValues();
        this.lightningConfig.loadValues();
        this.lightningAltConfig.loadValues();
        this.vineConfig.loadValues();
        this.momaConfig.loadValues();
        this.mandala2Config.loadValues();
        this.linkerConfig.loadValues();
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

        settings.setLifetime(getJTextFieldWithinBounds(lifetimeTextField, 5, 500, "Lifetime"));
        settings.setMaxPsiSearchDistance(getJTextFieldWithinBounds(maxPsiSearchDistanceTextField,
                10, 1000, "Max Psi/Anchor Search Distance"));



        //basic particle
//        settings.setBasicParticleEnabled(enableBasicParticleCheckBox.isSelected());
        settings.setSpriteTypeEnabled(enableBasicParticleCheckBox.isSelected(), PowerMode3.SpriteType.BASIC_PARTICLE);

        settings.setSpriteTypeEnabled(enableLightningCheckBox.isSelected(), PowerMode3.SpriteType.LIGHTNING);
        settings.setSpriteTypeEnabled(lightningAltCheckBox.isSelected(), PowerMode3.SpriteType.LIGHTNING_ALT);
        settings.setSpriteTypeEnabled(enableLizardCheckBox.isSelected(), PowerMode3.SpriteType.LIZARD);
        settings.setSpriteTypeEnabled(enableMOMACheckBox.isSelected(), PowerMode3.SpriteType.MOMA);
        settings.setSpriteTypeEnabled(enableVineCheckBox.isSelected(), PowerMode3.SpriteType.VINE);

        settings.setSpriteTypeEnabled(enableMandalaCheckbox.isSelected(), PowerMode3.SpriteType.MANDALA);

        settings.setSpriteTypeEnabled(enableLinkerCheckbox.isSelected(), PowerMode3.SpriteType.LINKER);


        //save values
        int maxPsiSearch = Integer.parseInt(maxPsiSearchDistanceTextField.getText());
        this.basicParticleConfig.saveValues();
        this.lizardConfig.saveValues(maxPsiSearch);
        this.lightningAltConfig.saveValues();
        this.lightningConfig.saveValues();
        this.vineConfig.saveValues(maxPsiSearch);
        this.momaConfig.saveValues();
        this.mandala2Config.saveValues(enableMandalaCheckbox.isSelected());
        this.linkerConfig.saveValues(maxPsiSearch, enableLinkerCheckbox.isSelected());


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




        theCustomCreatePanel = new JPanel();
        theCustomCreatePanel.setOpaque(false);
        theCustomCreatePanel.setLayout(new BoxLayout(theCustomCreatePanel, BoxLayout.PAGE_AXIS));


        //TODO , put as side tabs, but tab main options nad sound options, leave particles visible at all times below
//        JBTabbedPane tabbedPane = new JBTabbedPane(JTabbedPane.LEFT);
        JBTabbedPane tabbedPane = new JBTabbedPane();
        tabbedPane.setOpaque(false);


        particleSettingsPanel = new JPanel();
        particleSettingsPanel.setBorder(JBUI.Borders.empty(2, 2, 200, 2));
        particleSettingsPanel.setLayout(new BoxLayout(particleSettingsPanel, BoxLayout.PAGE_AXIS));
//        particleSettingsPanel.setOpaque(false); // makes background grey?

        ImageIcon sliderIcon = new ImageIcon(this.getClass().getResource("/icons/bar_small.png"));
        tabbedPane.addTab("Particle Settings", sliderIcon, particleSettingsPanel,
                "Set yer particles!");



        JComponent panel2 = new JPanel();
        soundConfig = new SoundConfig(settings);
        panel2.add(soundConfig);



        ImageIcon soundIcon = new ImageIcon(this.getClass().getResource("/icons/sound_small.png"));
        tabbedPane.addTab("Sound Settings", soundIcon, panel2,
                "Set yer sounds!");


        theCustomCreatePanel.add(tabbedPane);


        particleSettingsPanel.add(this.createSpacer());
        this.basicParticleConfig = new BasicParticleConfig(settings);
        particleSettingsPanel.add(this.basicParticleConfig);

        particleSettingsPanel.add(this.createSpacer());
        this.mandala2Config = new Mandala2Config(settings);
        particleSettingsPanel.add(mandala2Config);

        particleSettingsPanel.add(this.createSpacer());
        this.linkerConfig = new LinkerConfig(settings);
        particleSettingsPanel.add(linkerConfig.getConfigPanel());


        particleSettingsPanel.add(this.createSpacer());
        this.lightningConfig = new LightningConfig(settings);
        particleSettingsPanel.add(lightningConfig.getConfigPanel());

        particleSettingsPanel.add(this.createSpacer());
        this.lightningAltConfig = new LightningAltConfig(settings);
        particleSettingsPanel.add(lightningAltConfig);


        particleSettingsPanel.add(this.createSpacer());
        this.lizardConfig = new LizardConfig(settings);
        particleSettingsPanel.add(lizardConfig.getConfigPanel());
        particleSettingsPanel.add(this.createSpacer());


        this.vineConfig = new VineConfig(settings);
        particleSettingsPanel.add(vineConfig.getConfigPanel());
        particleSettingsPanel.add(this.createSpacer());

        this.momaConfig = new MOMAConfig(settings);
        particleSettingsPanel.add(momaConfig.getConfigPanel());
        particleSettingsPanel.add(this.createSpacer());

        JPanel footerPanel = new JPanel();
        footerPanel.setMinimumSize(new Dimension(100, 300));
        particleSettingsPanel.add(footerPanel);




//        this.theCustomCreatePanel.add(this.createSpacer());
//        this.basicParticleConfig = new BasicParticleConfig(settings);
//        this.theCustomCreatePanel.add(this.basicParticleConfig);
//
//        this.theCustomCreatePanel.add(this.createSpacer());
//        this.mandala2Config = new Mandala2Config(settings);
//        this.theCustomCreatePanel.add(mandala2Config);
//
//        this.theCustomCreatePanel.add(this.createSpacer());
//        this.linkerConfig = new LinkerConfig(settings);
//        this.theCustomCreatePanel.add(linkerConfig.getConfigPanel());
//
//
//        this.theCustomCreatePanel.add(this.createSpacer());
//        this.lightningConfig = new LightningConfig(settings);
//        this.theCustomCreatePanel.add(lightningConfig.getConfigPanel());
////
//        this.theCustomCreatePanel.add(this.createSpacer());
//        this.lightningAltConfig = new LightningAltConfig(settings);
//        this.theCustomCreatePanel.add(lightningAltConfig);
//
//
//        this.theCustomCreatePanel.add(this.createSpacer());
//        this.lizardConfig = new LizardConfig(settings);
//        this.theCustomCreatePanel.add(lizardConfig.getConfigPanel());
//        this.theCustomCreatePanel.add(this.createSpacer());
//
//
//        this.vineConfig = new VineConfig(settings);
//        this.theCustomCreatePanel.add(vineConfig.getConfigPanel());
//        this.theCustomCreatePanel.add(this.createSpacer());
//
//        this.momaConfig = new MOMAConfig(settings);
//        this.theCustomCreatePanel.add(momaConfig.getConfigPanel());
//        this.theCustomCreatePanel.add(this.createSpacer());
//
//        JPanel footerPanel = new JPanel();
//        footerPanel.setMinimumSize(new Dimension(100, 300));
//        this.theCustomCreatePanel.add(footerPanel);
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
