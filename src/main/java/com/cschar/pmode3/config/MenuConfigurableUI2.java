package com.cschar.pmode3.config;

import com.cschar.pmode3.PowerMode3;
import com.cschar.pmode3.config.common.ui.ZeranthiumColors;
import com.cschar.pmode3.services.MemoryMonitorService;
import com.cschar.pmode3.services.MyJComponent;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.keymap.KeymapManager;
import com.intellij.openapi.options.ConfigurableUi;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.VerticalSeparatorComponent;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.logging.Logger;

public class MenuConfigurableUI2 implements ConfigurableUi<PowerMode3>, Disposable {
    private static final Logger LOGGER = Logger.getLogger( MenuConfigurableUI2.class.getName() );
    private JPanel mainPanel;
    private JTextField lifetimeTextField;
    private JCheckBox isEnabledCheckBox;
    private JLabel toggleHotkeyLabel;
    
    private JPanel particleSettingsPanel;
    private JPanel soundSettingsPanel;
    private JTabbedPane settingsTabbedPane;

    private SoundConfig soundConfig;
    private MusicTriggerConfig musicTriggerConfig;
    private SpecialActionSoundConfig specialActionSoundConfig;


    private JCheckBox enableLizardCheckBox;
    private JCheckBox enableMOMACheckBox;
    private JPanel theCustomCreatePanel;
    private JCheckBox enableBasicParticleCheckBox;
    private JTextField maxPsiSearchDistanceTextField;

    private JTextField shakeDistanceTextField;
    private JCheckBox enableVineCheckBox;
    private JCheckBox enableMultilayerCheckbox;
    private JCheckBox enableLinkerCheckbox;
    private JCheckBox enableDrosteCheckbox;
    private JCheckBox enableCopyPasteVoid;
    private JPanel mainBottomPanel;
    private JPanel mainTopPanel;
    private JButton loadPackButton;
    private JCheckBox enableBasicSound;
    private JCheckBox enableActionSound;
    private JCheckBox enableLockedLayerCheckbox;
    private JCheckBox enableLantern;
    private JCheckBox enableTapAnim;
    private JPanel memoryStatsPanel;
    private JButton anchorConfigButton;
    private JCheckBox enableMultiLayerChance;


    private BasicParticleConfig basicParticleConfig;
    private LizardConfig lizardConfig;
    private VineConfig vineConfig;
    private MOMAConfig momaConfig;
    private MultiLayerConfig multiLayerConfig;
    private MultiLayerChanceConfig multiLayerChanceConfig;
    private LinkerConfig linkerConfig;
    private DrosteConfig drosteConfig;
    private CopyPasteVoidConfig copyPasteVoidConfig;
    private LockedLayerConfig lockedLayerConfig;
    private LanternConfig lanternConfig;
    private TapAnimConfig tapAnimConfig;

    PowerMode3 settings;

    public static JLabel loadingLabel = new JLabel("Loading Config");

    // static variable single_instance of type Singleton
    private static MenuConfigurableUI2 single_instance = null;



    // static method to create instance of Singleton class
    public static MenuConfigurableUI2 getInstance()
    {
        return single_instance;
    }


    private MenuConfigurableUI2() {};


    JPanel ultraPanel;

    //Constructor is called _AFTER_ createUIComponents when using IntelliJ GUI designer
    public MenuConfigurableUI2(PowerMode3 powerMode3) {
        LOGGER.info("Creating MenuConfigurableUI...");
        this.ultraPanel = new JPanel();
        settings = ApplicationManager.getApplication().getService(PowerMode3.class);

        JBTabbedPane settingsTabbedPane = new JBTabbedPane(JTabbedPane.LEFT);
        settingsTabbedPane.setOpaque(false);


        JPanel panel2 = new JPanel();
        JBScrollPane scrollPane = new JBScrollPane(panel2);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//        panel2.setBackground(JBColor.CYAN);
        panel2.setBorder(JBUI.Borders.empty(2, 2, 200, 2));
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.PAGE_AXIS));
        ImageIcon sliderIcon2 = new ImageIcon(this.getClass().getResource("/icons/bar_small.png"));
        settingsTabbedPane.addTab("Particle 2", sliderIcon2, scrollPane);

        mainTopPanel = makeTopSettings(settings);
        panel2.add(mainTopPanel);

        mainBottomPanel = makeBotSettings();
        panel2.add(mainBottomPanel);

        theCustomCreatePanel = new JPanel();
        theCustomCreatePanel.setOpaque(false);
        theCustomCreatePanel.setLayout(new BoxLayout(theCustomCreatePanel, BoxLayout.PAGE_AXIS));
        panel2.add(theCustomCreatePanel);



        //If we're background loading assets, and the User tries to access UI in settings beforehand
        if(!settings.isConfigLoaded){
            loadingLabel.setFont(new Font ("Arial", Font.BOLD, 30));
            theCustomCreatePanel.add(loadingLabel);
        }else{
            LOGGER.info("loading settings (they were alreayd loaded)");
            createConfig();
        }


        /////////////////////////
        // Pack loader UI
        /////////////////////////
        JPanel panel1 = new JPanel();
        panel1.setBackground(JBColor.orange);
        panel1.setBorder(JBUI.Borders.empty(2, 2, 50, 2));
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));
        ImageIcon sliderIcon = new ImageIcon(this.getClass().getResource("/icons/bar_small.png"));
        settingsTabbedPane.addTab("Particle Settings", sliderIcon, panel1);
        MyJComponent jComponent = new MyJComponent("title");
        panel1.add(jComponent);


        this.ultraPanel.setMaximumSize(new Dimension(1000,1000));
        this.ultraPanel.setLayout(new BoxLayout(this.ultraPanel, BoxLayout.X_AXIS));
        this.ultraPanel.add(settingsTabbedPane);





        MenuConfigurableUI2.single_instance = this;
        settings = powerMode3;


        //If user has opened settings, but config values arent loaded yet from filesystem...
        if(!settings.isConfigLoaded){
            isEnabledCheckBox.setEnabled(false);
            mainTopPanel.setEnabled(false);
            for(Component j : mainTopPanel.getComponents()){
                j.setEnabled(false);
                if(j instanceof JComponent){
                    for(Component i : ((JComponent)j).getComponents()){
                        i.setEnabled(false);
                        if(i instanceof JComponent){
                            for(Component k : ((JComponent)i).getComponents()){
                                k.setEnabled(false);
                            }
                        }
                    }
                }
            }


            mainBottomPanel.setEnabled(false);
            for(Component j : mainBottomPanel.getComponents()){
                j.setEnabled(false);
                if(j instanceof JComponent){
                    for(Component i : ((JComponent)j).getComponents()){
                        i.setEnabled(false);
                        if(i instanceof JComponent){
                            for(Component k : ((JComponent)i).getComponents()){
                                k.setEnabled(false);
                            }
                        }
                    }
                }
            }
            return;
        }

        loadConfigValues();


    }

    public JPanel makeTopSettings(PowerMode3 settings){
        mainTopPanel = new JPanel();

        //Col 1
        JPanel col1 = new JPanel();
        col1.setLayout(new BoxLayout(col1, BoxLayout.Y_AXIS));
        col1.setBorder(JBUI.Borders.empty(10, 10, 50, 10));
//        col1.setBackground(JBColor.DARK_GRAY);

        //Col1 - IsEnabled
        isEnabledCheckBox = new JCheckBox("Is enabled?");
        col1.add(isEnabledCheckBox);
        col1.add(new VerticalSeparatorComponent());
        isEnabledCheckBox.setSelected(settings.isEnabled());

        //Col1 - Hotkey label
        toggleHotkeyLabel =  new JLabel();
        KeymapManager km = KeymapManager.getInstance();
        //defined in the META-INF/plugin.xml <action id=""> param
        Shortcut[] zShortcuts = km.getActiveKeymap().getShortcuts("com.cschar.pmode3.PowerModeZeranthiumToggleEnabled");
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < zShortcuts.length; i++){
            Shortcut sc = zShortcuts[i];
            if( i % 2 == 0) {
                sb.append(" <br> " + sc);
            }else{
                sb.append( sc );
            }
        }
        String labelText = String.format("<html>Toggle hotkey: - %s</html>", sb.toString());
        toggleHotkeyLabel.setText(labelText);
        col1.add(toggleHotkeyLabel);

        //Col2
        JPanel col2 = new JPanel();
        col2.setLayout(new BoxLayout(col2, BoxLayout.Y_AXIS));
        col2.setBorder(JBUI.Borders.empty(10, 10, 50, 10));
//        col2.setBackground(JBColor.darkGray);

        //Col2 - Shake distance
        shakeDistanceTextField = new JTextField();
        shakeDistanceTextField.setText(Integer.toString(settings.getShakeDistance()));
        JPanel shakePanel = new JPanel();
        shakePanel.setLayout(new BoxLayout(shakePanel, BoxLayout.X_AXIS));
        JLabel shakeDistanceLbl = new JLabel("Shake distance:");
        shakeDistanceLbl.setBorder(JBUI.Borders.empty(0, 100, 0, 10));
        shakeDistanceLbl.setPreferredSize(new Dimension(300, shakeDistanceLbl.getHeight()));
        JTextField shakeDistance = new JTextField();
        shakeDistance.setName("name");
        shakePanel.add(shakeDistanceLbl);
        shakePanel.add(shakeDistanceTextField);
        col2.add(shakePanel);

        //Col2 - Lifetime
        lifetimeTextField = new JTextField();
        lifetimeTextField.setText(Integer.toString(settings.getLifetime()));
//        lifetimeTextField.setBorder(JBUI.Borders.empty(0, 100, 0, 0));
        JPanel lifeTimePanel = new JPanel();
        lifeTimePanel.setLayout(new BoxLayout(lifeTimePanel, BoxLayout.X_AXIS));
        JLabel lifeTimeLbl = new JLabel("Lifetime:");
        lifeTimeLbl.setBorder(JBUI.Borders.empty(0, 100, 0, 10));
        lifeTimeLbl.setPreferredSize(new Dimension(300,lifeTimeLbl.getHeight()));
        lifeTimePanel.add(lifeTimeLbl);
        lifeTimePanel.add(lifetimeTextField);
        col2.add(lifeTimePanel);


        //Col2 - max search distance
        maxPsiSearchDistanceTextField = new JTextField();
        maxPsiSearchDistanceTextField.setText(Integer.toString(settings.getMaxPsiSearchDistance()));
        JPanel maxSearchDistPanel = new JPanel();
        maxSearchDistPanel.setLayout(new BoxLayout(maxSearchDistPanel, BoxLayout.X_AXIS));
        JLabel searchDistLbl = new JLabel("max anchor search distance from caret:");
        searchDistLbl.setBorder(JBUI.Borders.empty(0, 100, 0, 10));

        maxSearchDistPanel.add(searchDistLbl);
        maxSearchDistPanel.add(maxPsiSearchDistanceTextField);
        col2.add(maxSearchDistPanel);

        //Col3
        JPanel col3 = new JPanel();
        col3.setLayout(new BoxLayout(col3, BoxLayout.Y_AXIS));
        col3.setBorder(JBUI.Borders.empty(10, 10, 50, 10));
        col3.setBackground(JBColor.GREEN);

        //Col3 - Memory panel
        this.memoryStatsPanel = new JPanel();
        reinitMemoryStatsPanel();
        MemoryMonitorService powerMemoryService = ApplicationManager.getApplication().getService(MemoryMonitorService.class);
        powerMemoryService.setUi(this);
        powerMemoryService.updateUi();
        col3.add(memoryStatsPanel);

        //Col3 - Anchor config
        setupAnchorConfigButton();
        col3.add(anchorConfigButton);



        mainTopPanel.add(col1);
        mainTopPanel.add(col2);
        mainTopPanel.add(col3);

        return mainTopPanel;
    }

    int CHECKBOX_BOT_PAD5 = 7;
    public JCheckBox setCheckBox(JPanel parent, String title){
        JCheckBox checkbox = new JCheckBox(title);
        checkbox.setBorder(JBUI.Borders.empty(0,0, CHECKBOX_BOT_PAD5,0));
        parent.add(checkbox);
        return checkbox;
    }

    public JPanel makeBotSettings(){
        mainBottomPanel = new JPanel();
//        mainBottomPanel.setBorder(JBUI.Borders.customLine(JBColor.black, 1));


        JPanel col1 = new JPanel();
        col1.setLayout(new BoxLayout(col1, BoxLayout.Y_AXIS));
        col1.setBorder(JBUI.Borders.empty(10, 0, 10, 0));
//        col1.setBackground(JBColor.CYAN);
        col1.setPreferredSize(new Dimension(175,150));
//        col1.add(new JLabel("fill"));


        int checkboxheight = 18 + CHECKBOX_BOT_PAD5;
        LOGGER.info("checkbox height is " + checkboxheight);

        enableCopyPasteVoid = setCheckBox(col1, "Enable CopyPasteVoid");
        enableCopyPasteVoid.setBorder(JBUI.Borders.empty(checkboxheight,0, CHECKBOX_BOT_PAD5,0));
        if(settings.getSpriteTypeEnabled(PowerMode3.ConfigType.COPYPASTEVOID)){
            enableCopyPasteVoid.setSelected(true);
        }
        enableDrosteCheckbox = setCheckBox(col1, "Enable Droste");
        if(settings.getSpriteTypeEnabled(PowerMode3.ConfigType.DROSTE)) {
            enableDrosteCheckbox.setSelected(true);
        }

        enableLockedLayerCheckbox = setCheckBox(col1, "Enable Locked Layer");
        if(settings.getSpriteTypeEnabled(PowerMode3.ConfigType.LOCKED_LAYER)) {
            enableLockedLayerCheckbox.setSelected(true);
        }

        enableTapAnim = setCheckBox(col1, "Enable Tap Anim");
        if(settings.getSpriteTypeEnabled(PowerMode3.ConfigType.TAP_ANIM)) {
            enableTapAnim.setSelected(true);
        }





        JPanel col2 = new JPanel();
        col2.setLayout(new BoxLayout(col2, BoxLayout.Y_AXIS));
        col2.setBorder(JBUI.Borders.empty(10, 0, 10, 0));
//        col2.setBackground(JBColor.GREEN);
        col2.setPreferredSize(new Dimension(200,150));

        enableBasicParticleCheckBox = setCheckBox(col2, "Enable Basic Particle");
        if(settings.getSpriteTypeEnabled(PowerMode3.ConfigType.BASIC_PARTICLE)){
            enableBasicParticleCheckBox.setSelected(true);
        }

        enableLizardCheckBox = setCheckBox(col2, "Enable Lizard");
        if(settings.getSpriteTypeEnabled(PowerMode3.ConfigType.LIZARD)){
            enableLizardCheckBox.setSelected(true);
        }

        enableMOMACheckBox = setCheckBox(col2, "Enable MOMA");
        if(settings.getSpriteTypeEnabled(PowerMode3.ConfigType.MOMA)){
            enableMOMACheckBox.setSelected(true);
        }

        enableMultilayerCheckbox = setCheckBox(col2, "Enable MultiLayer");
        if(settings.getSpriteTypeEnabled(PowerMode3.ConfigType.MULTI_LAYER)){
            enableMultilayerCheckbox.setSelected(true);
        }


        enableMultiLayerChance = setCheckBox(col2, "Enable MultiLayer Chance");
        if(settings.getSpriteTypeEnabled(PowerMode3.ConfigType.MULTI_LAYER_CHANCE)){
            enableMultiLayerChance.setSelected(true);
        }



        JPanel col3 = new JPanel();
        col3.setLayout(new BoxLayout(col3, BoxLayout.Y_AXIS));
        col3.setBorder(JBUI.Borders.empty(10, 0, 10, 0));
//        col3.setBackground(JBColor.GREEN);
        col3.setPreferredSize(new Dimension(150,150));


        enableVineCheckBox = setCheckBox(col3, "Enable Vine");
        enableVineCheckBox.setBorder(JBUI.Borders.empty(checkboxheight,0, CHECKBOX_BOT_PAD5,0));
        if(settings.getSpriteTypeEnabled(PowerMode3.ConfigType.VINE)){
            enableVineCheckBox.setSelected(true);
        }

        enableLantern = setCheckBox(col3, "Enable Lantern");
        if(settings.getSpriteTypeEnabled(PowerMode3.ConfigType.LANTERN)){
            enableLantern.setSelected(true);
        }

        enableLinkerCheckbox = setCheckBox(col3, "Enable Linker");
        if(settings.getSpriteTypeEnabled(PowerMode3.ConfigType.LINKER)){
            enableLinkerCheckbox.setSelected(true);
        }



        JPanel col4 = new JPanel();
        col4.setLayout(new BoxLayout(col4, BoxLayout.Y_AXIS));
        col4.setBorder(JBUI.Borders.empty(10, 0, 10, 0));
//        col4.setBackground(JBColor.DARK_GRAY);
        col4.setPreferredSize(new Dimension(150,150));

        //Sound
        enableBasicSound = setCheckBox(col4, "Enable Basic Sound");
        enableBasicSound.setBorder(JBUI.Borders.empty(checkboxheight,0, CHECKBOX_BOT_PAD5,0));
        if(settings.getSpriteTypeEnabled(PowerMode3.ConfigType.SOUND)){
            enableBasicSound.setSelected(true);
        }
        enableActionSound = setCheckBox(col4, "Enable Action Sound");
        if(settings.getSpriteTypeEnabled(PowerMode3.ConfigType.SPECIAL_ACTION_SOUND)){
            enableActionSound.setSelected(true);
        }




        mainBottomPanel.add(col1);
        mainBottomPanel.add(col2);
        mainBottomPanel.add(col3);
        mainBottomPanel.add(col4);
        return mainBottomPanel;
    }

    private void loadConfigValues(){


        //already initialized from createUIComponents below
        this.basicParticleConfig.loadValues();
        this.lizardConfig.loadValues();
        this.vineConfig.loadValues();
        this.momaConfig.loadValues();
        this.multiLayerConfig.loadValues();
        this.lockedLayerConfig.loadValues();
        this.linkerConfig.loadValues();
        this.drosteConfig.loadValues();
        this.copyPasteVoidConfig.loadValues();
        this.lanternConfig.loadValues();
        this.tapAnimConfig.loadValues();
        this.multiLayerChanceConfig.loadValues();


        //sound panel
        this.soundConfig.loadValues();
        this.musicTriggerConfig.loadValues();
        this.specialActionSoundConfig.loadValues();
    }


    @Override
    public void reset(@NotNull PowerMode3 settings) {
        isEnabledCheckBox.setSelected(settings.isEnabled());
    }

    public boolean refreshMemoryWidget = true;
    @Override
    public boolean isModified(@NotNull PowerMode3 settings) {

//        isOpen = false;
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
        settings.setSpriteTypeEnabled(enableBasicParticleCheckBox.isSelected(), PowerMode3.ConfigType.BASIC_PARTICLE);

        settings.setSpriteTypeEnabled(enableLizardCheckBox.isSelected(), PowerMode3.ConfigType.LIZARD);
        settings.setSpriteTypeEnabled(enableMOMACheckBox.isSelected(), PowerMode3.ConfigType.MOMA);
        settings.setSpriteTypeEnabled(enableVineCheckBox.isSelected(), PowerMode3.ConfigType.VINE);
        settings.setSpriteTypeEnabled(enableMultilayerCheckbox.isSelected(), PowerMode3.ConfigType.MULTI_LAYER);
        settings.setSpriteTypeEnabled(enableLinkerCheckbox.isSelected(), PowerMode3.ConfigType.LINKER);
        settings.setSpriteTypeEnabled(enableDrosteCheckbox.isSelected(), PowerMode3.ConfigType.DROSTE);
        settings.setSpriteTypeEnabled(enableCopyPasteVoid.isSelected(), PowerMode3.ConfigType.COPYPASTEVOID);
        settings.setSpriteTypeEnabled(enableLockedLayerCheckbox.isSelected(), PowerMode3.ConfigType.LOCKED_LAYER);
        settings.setSpriteTypeEnabled(enableLantern.isSelected(), PowerMode3.ConfigType.LANTERN);
        settings.setSpriteTypeEnabled(enableTapAnim.isSelected(), PowerMode3.ConfigType.TAP_ANIM);
        settings.setSpriteTypeEnabled(enableMultiLayerChance.isSelected(), PowerMode3.ConfigType.MULTI_LAYER_CHANCE);


        //Sound
        settings.setSpriteTypeEnabled(enableBasicSound.isSelected(), PowerMode3.ConfigType.SOUND);
        settings.setSpriteTypeEnabled(enableActionSound.isSelected(), PowerMode3.ConfigType.SPECIAL_ACTION_SOUND);


        if(!settings.isConfigLoaded){
            return;
        }

        //save values
        int maxPsiSearch = Integer.parseInt(maxPsiSearchDistanceTextField.getText());
        this.basicParticleConfig.saveValues();
        this.lizardConfig.saveValues(maxPsiSearch);

        this.vineConfig.saveValues(maxPsiSearch);
        this.momaConfig.saveValues();
        this.multiLayerConfig.saveValues(enableMultilayerCheckbox.isSelected());
        this.linkerConfig.saveValues(maxPsiSearch, enableLinkerCheckbox.isSelected());
        this.drosteConfig.saveValues();
        this.copyPasteVoidConfig.saveValues();
        this.lockedLayerConfig.saveValues();
        this.lanternConfig.saveValues();
        this.tapAnimConfig.saveValues();
        this.multiLayerChanceConfig.saveValues();

        //sound panel
        this.soundConfig.saveValues();
        this.musicTriggerConfig.saveValues();
        this.specialActionSoundConfig.saveValues();

    }

    @NotNull
    @Override
    public JComponent getComponent() {
//        JBScrollPane scrollPane = new JBScrollPane(this.mainPanel);
////        scrollPane.setLayout(new BoxLayout(scrollPane, BoxLayout.Y_AXIS));
//        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//
//
//        if(settings.isConfigLoaded) {
//            scrollPane.getVerticalScrollBar().getValue();
//            scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
//                @Override
//                public void adjustmentValueChanged(AdjustmentEvent e) {
//                    ApplicationManager.getApplication().invokeLater(new Runnable() {
//                        @Override
//                        public void run() {
//                            settings.setScrollBarPosition(e.getValue());
//                            settings.setLastTabIndex(settingsTabbedPane.getSelectedIndex());
//
//                        }
//                    });
//                }
//            });
//
//            //http://www.jetbrains.org/intellij/sdk/docs/basics/architectural_overview/general_threading_rules.html?search=BackgroundTaskUtil#invokelater
//            //https://stackoverflow.com/a/46204157/403403
//            ApplicationManager.getApplication().invokeLater(new Runnable() {
//                @Override
//                public void run() {
//                    scrollPane.getVerticalScrollBar().setValue(settings.getScrollBarPosition());
//                    settingsTabbedPane.setSelectedIndex(settings.getLastTabIndex());
//                }
//            });
//        }


//        return scrollPane;
        return this.ultraPanel;
    }


    public void updateConfigUIAfterAssetsAreLoaded(boolean wasEnabled){
        //TODO weird extra setting 'wasEnabled' here, needed when 'enabled' manually switched to "false" during load
        // (since we dont want to have plugin enabled before assets are loaded)
        // but ConfiguraleUI has already been loaded with 'false' setting
        // ***other settings are loaded from config dict which isn't modified so not needed

        isEnabledCheckBox.setEnabled(true);
        isEnabledCheckBox.setSelected(wasEnabled);
        settings.setEnabled(isEnabledCheckBox.isSelected());

        mainTopPanel.setEnabled(true);
        for(Component j : mainTopPanel.getComponents()){
            j.setEnabled(true);
            if(j instanceof JComponent){
                for(Component i : ((JComponent)j).getComponents()){
                    i.setEnabled(true);
                    if(i instanceof JComponent){
                        for(Component k : ((JComponent)i).getComponents()){
                            k.setEnabled(true);
                        }
                    }
                }
            }
        }

        mainBottomPanel.setEnabled(true);
        for(Component j : mainBottomPanel.getComponents()){
            j.setEnabled(true);
            if(j instanceof JComponent){
                for(Component i : ((JComponent)j).getComponents()){
                    i.setEnabled(true);
                    if(i instanceof JComponent){
                        for(Component k : ((JComponent)i).getComponents()){
                            k.setEnabled(true);
                        }
                    }
                }
            }
        }

        this.createConfig();
        this.loadConfigValues();
    }

    private void createConfig(){

        if (loadingLabel.getParent() == this.theCustomCreatePanel) {
            this.theCustomCreatePanel.remove(loadingLabel);
        }

        settingsTabbedPane = new JBTabbedPane();
        settingsTabbedPane.setOpaque(false);


        particleSettingsPanel = new JPanel();
        particleSettingsPanel.setBorder(JBUI.Borders.empty(2, 2, 200, 2));
        particleSettingsPanel.setLayout(new BoxLayout(particleSettingsPanel, BoxLayout.PAGE_AXIS));
        ImageIcon sliderIcon = new ImageIcon(this.getClass().getResource("/icons/bar_small.png"));
        settingsTabbedPane.addTab("Particle Settings", sliderIcon, particleSettingsPanel);



        soundSettingsPanel = new JPanel();
        soundSettingsPanel.setLayout(new BoxLayout(soundSettingsPanel, BoxLayout.PAGE_AXIS));
        ImageIcon soundIcon = new ImageIcon(this.getClass().getResource("/icons/sound_small.png"));
        settingsTabbedPane.addTab("Sound Settings", soundIcon, soundSettingsPanel);





        //Sound Settings tab
        soundSettingsPanel.add(this.createSpacer());
        soundConfig = new SoundConfig(settings);
        soundSettingsPanel.add(soundConfig);

        soundSettingsPanel.add(this.createSpacer());
        specialActionSoundConfig = new SpecialActionSoundConfig(settings);
        soundSettingsPanel.add(specialActionSoundConfig);

        soundSettingsPanel.add(this.createSpacer());
        musicTriggerConfig = new MusicTriggerConfig(settings);
        soundSettingsPanel.add(musicTriggerConfig);



        JPanel footerPanel = new JPanel();
        footerPanel.setMinimumSize(new Dimension(100, 300));
        soundSettingsPanel.add(footerPanel);


        //Particle Settings tab
        particleSettingsPanel.add(this.createSpacer());
        this.basicParticleConfig = new BasicParticleConfig(settings);
        particleSettingsPanel.add(this.basicParticleConfig);
        particleSettingsPanel.add(this.createSpacer());

        this.multiLayerConfig = new MultiLayerConfig(settings);
        particleSettingsPanel.add(multiLayerConfig);
        particleSettingsPanel.add(this.createSpacer());

        this.linkerConfig = new LinkerConfig(settings);
        particleSettingsPanel.add(linkerConfig);
        particleSettingsPanel.add(this.createSpacer());

        this.lanternConfig = new LanternConfig(settings);
        particleSettingsPanel.add(lanternConfig);
        particleSettingsPanel.add(this.createSpacer());

        this.multiLayerChanceConfig = new MultiLayerChanceConfig(settings);
        particleSettingsPanel.add(multiLayerChanceConfig);
        particleSettingsPanel.add(this.createSpacer());


        this.lizardConfig = new LizardConfig(settings);
        particleSettingsPanel.add(lizardConfig);
        particleSettingsPanel.add(this.createSpacer());


        this.copyPasteVoidConfig = new CopyPasteVoidConfig(settings);
        particleSettingsPanel.add(copyPasteVoidConfig);
        particleSettingsPanel.add(this.createSpacer());

        this.drosteConfig = new DrosteConfig(settings);
        particleSettingsPanel.add(drosteConfig);
        particleSettingsPanel.add(this.createSpacer());

        this.lockedLayerConfig = new LockedLayerConfig(settings);
        particleSettingsPanel.add(lockedLayerConfig);
        particleSettingsPanel.add(this.createSpacer());

        this.tapAnimConfig = new TapAnimConfig(settings);
        particleSettingsPanel.add(tapAnimConfig);
        particleSettingsPanel.add(this.createSpacer());

        this.vineConfig = new VineConfig(settings);
        particleSettingsPanel.add(vineConfig);
        particleSettingsPanel.add(this.createSpacer());

        this.momaConfig = new MOMAConfig(settings);
        particleSettingsPanel.add(momaConfig);
        particleSettingsPanel.add(this.createSpacer());

        footerPanel = new JPanel();
        footerPanel.setMinimumSize(new Dimension(100, 300));
        particleSettingsPanel.add(footerPanel);

        theCustomCreatePanel.add(settingsTabbedPane);
    }

    public void reinitMemoryStatsPanel(){
        this.memoryStatsPanel.removeAll();

        this.memoryStatsPanel.setBackground(ZeranthiumColors.specialOption3);
        memoryStatsPanel.setLayout(new BoxLayout(memoryStatsPanel, BoxLayout.PAGE_AXIS));
        memoryStatsPanel.setBorder(JBUI.Borders.empty(10));

        long allocatedMemory      = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024/1024;
        JLabel freeMemoryLabel = new JLabel("allocated memory (MB): " + allocatedMemory);
        JLabel maxMemoryLabel = new JLabel("MAX memory (MB): " + Runtime.getRuntime().maxMemory()/1024/1024);
        this.memoryStatsPanel.add(freeMemoryLabel);
        this.memoryStatsPanel.add(maxMemoryLabel);

        this.memoryStatsPanel.revalidate();
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




        private void disableAllParticleSettings(){

            enableBasicParticleCheckBox.setSelected(false);
            enableLizardCheckBox.setSelected(false);
            enableMOMACheckBox.setSelected(false);
            enableVineCheckBox.setSelected(false);
            enableMultilayerCheckbox.setSelected(false);
            enableLinkerCheckbox.setSelected(false);
            enableDrosteCheckbox.setSelected(false);
            enableCopyPasteVoid.setSelected(false);
            enableLockedLayerCheckbox.setSelected(false);
            enableLantern.setSelected(false);
            enableTapAnim.setSelected(false);
            

            //Sound
            
            enableBasicSound.setSelected(false);
            enableActionSound.setSelected(false);
        
        }

    private void setupAnchorConfigButton(){
        anchorConfigButton = new JButton();

        java.util.Map<PowerMode3.AnchorTypes, String> choices = new HashMap<PowerMode3.AnchorTypes, String>(){{
            put(PowerMode3.AnchorTypes.BRACE, "BRACE {}");
            put(PowerMode3.AnchorTypes.PARENTHESIS, "PARENTHESIS ()");
            put(PowerMode3.AnchorTypes.BRACKET, "BRACKET []");
            put(PowerMode3.AnchorTypes.COLON, "COLON :");
        }};

        anchorConfigButton.setText("<html> anchor config: <br/>" +
                choices.get(settings.anchorType) + "  </html>");
        anchorConfigButton.addActionListener(new ActionListener() {
            private Object Map;

            @Override
            public void actionPerformed(ActionEvent e) {

                String[] options = new String[PowerMode3.AnchorTypes.values().length];
                for(int i = 0; i < options.length; i++){
                    options[i] = choices.get(PowerMode3.AnchorTypes.values()[i]);
                }

                ImageIcon sliderIcon = new ImageIcon(this.getClass().getResource("/icons/bar_small.png"));
                int result = Messages.showDialog("Choose Anchor Type:", "Choose Anchor Type",
                        options,
                        settings.anchorType.ordinal(), settings.anchorType.ordinal(), sliderIcon, null);

                if(result != -1){
                    settings.anchorType = PowerMode3.AnchorTypes.values()[result];
                    anchorConfigButton.setText("<html> anchor config: <br/>" + choices.get(settings.anchorType) + "  </html>");
                }
            }
        });
    }

    @Override
    public void dispose(){
        this.refreshMemoryWidget = false; //stop bg thread from updating
        MemoryMonitorService powerMemoryService = ApplicationManager.getApplication().getService(MemoryMonitorService.class);
        powerMemoryService.cleanup();
    }






}
