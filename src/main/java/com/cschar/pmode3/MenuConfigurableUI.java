package com.cschar.pmode3;

import com.cschar.pmode3.config.*;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.keymap.KeymapManager;
import com.intellij.openapi.options.ConfigurableUi;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MenuConfigurableUI implements ConfigurableUi<PowerMode3> {
    private static final Logger LOGGER = Logger.getLogger( MenuConfigurableUI.class.getName() );
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
    private JCheckBox enableDrosteCheckbox;
    private JCheckBox enableCopyPasteVoid;
    private JPanel mainBottomPanel;
    private JPanel mainTopPanel;
    private JButton loadPackButton;


    private BasicParticleConfig basicParticleConfig;
    private LightningConfig lightningConfig;
    private LightningAltConfig lightningAltConfig;
    private LizardConfig lizardConfig;
    private VineConfig vineConfig;
    private MOMAConfig momaConfig;
    private Mandala2Config mandala2Config;
    private LinkerConfig linkerConfig;
    private DrosteConfig drosteConfig;
    private CopyPasteVoidConfig copyPasteVoidConfig;

    PowerMode3 settings;

    public static JLabel loadingLabel = new JLabel("Loading Config");

    // static variable single_instance of type Singleton
    private static MenuConfigurableUI single_instance = null;

    // variable of type String
    public String s;


    // static method to create instance of Singleton class
    public static MenuConfigurableUI getInstance()
    {
        return single_instance;
    }

    public MenuConfigurableUI() {};

    //Constructor is called _AFTER_ createUIComponents when using IntelliJ GUI designer
    public MenuConfigurableUI(PowerMode3 powerMode3) {
//        particleSettingsPanel.setBackground(theCustomCreatePanel.getBackground());
        MenuConfigurableUI.single_instance = this;

        settings = powerMode3;
        isEnabledCheckBox.setSelected(powerMode3.isEnabled());


        setupHotkeyText();

        setupPackLoaderButton();


        shakeDistanceTextField.setText(Integer.toString(powerMode3.getShakeDistance()));
        lifetimeTextField.setText(Integer.toString(powerMode3.getLifetime()));
        maxPsiSearchDistanceTextField.setText(Integer.toString(powerMode3.getMaxPsiSearchDistance()));



//        enableBasicParticleCheckBox.setSelected(powerMode3.getBasicParticleEnabled());

        if(powerMode3.getSpriteTypeEnabled(PowerMode3.ConfigType.BASIC_PARTICLE)){
            enableBasicParticleCheckBox.setSelected(true);
        }

        if(powerMode3.getSpriteTypeEnabled(PowerMode3.ConfigType.LIGHTNING)){
            enableLightningCheckBox.setSelected(true);
        }
        if(powerMode3.getSpriteTypeEnabled(PowerMode3.ConfigType.LIGHTNING_ALT)){
            lightningAltCheckBox.setSelected(true);
        }
        if(powerMode3.getSpriteTypeEnabled(PowerMode3.ConfigType.LIZARD)){
            enableLizardCheckBox.setSelected(true);
        }
        if(powerMode3.getSpriteTypeEnabled(PowerMode3.ConfigType.MOMA)){
            enableMOMACheckBox.setSelected(true);
        }
        if(powerMode3.getSpriteTypeEnabled(PowerMode3.ConfigType.VINE)){
            enableVineCheckBox.setSelected(true);
        }
        if(powerMode3.getSpriteTypeEnabled(PowerMode3.ConfigType.MANDALA)){
            enableMandalaCheckbox.setSelected(true);
        }
        if(powerMode3.getSpriteTypeEnabled(PowerMode3.ConfigType.LINKER)){
            enableLinkerCheckbox.setSelected(true);
        }
        if(powerMode3.getSpriteTypeEnabled(PowerMode3.ConfigType.DROSTE)){
            enableDrosteCheckbox.setSelected(true);
        }
        if(powerMode3.getSpriteTypeEnabled(PowerMode3.ConfigType.COPYPASTEVOID)){
            enableCopyPasteVoid.setSelected(true);
        }

        if(!settings.isConfigLoaded){
            isEnabledCheckBox.setEnabled(false);
            mainTopPanel.setEnabled(false);
            for(Component j : mainTopPanel.getComponents()){
                j.setEnabled(false);
            }


            mainBottomPanel.setEnabled(false);
            for(Component j : mainBottomPanel.getComponents()){
                j.setEnabled(false);
            }
            return;
        }

        loadConfigValues();


    }

    private void loadConfigValues(){


        //already initialized from createUIComponents below
        this.basicParticleConfig.loadValues();
        this.lizardConfig.loadValues();
        this.lightningConfig.loadValues();
        this.lightningAltConfig.loadValues();
        this.vineConfig.loadValues();
        this.momaConfig.loadValues();
        this.mandala2Config.loadValues();
        this.linkerConfig.loadValues();
        this.drosteConfig.loadValues();
        this.copyPasteVoidConfig.loadValues();



        //sound panel
        this.soundConfig.loadValues();
        this.musicTriggerConfig.loadValues();
        this.specialActionSoundConfig.loadValues();
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
        settings.setSpriteTypeEnabled(enableBasicParticleCheckBox.isSelected(), PowerMode3.ConfigType.BASIC_PARTICLE);

        settings.setSpriteTypeEnabled(enableLightningCheckBox.isSelected(), PowerMode3.ConfigType.LIGHTNING);
        settings.setSpriteTypeEnabled(lightningAltCheckBox.isSelected(), PowerMode3.ConfigType.LIGHTNING_ALT);
        settings.setSpriteTypeEnabled(enableLizardCheckBox.isSelected(), PowerMode3.ConfigType.LIZARD);
        settings.setSpriteTypeEnabled(enableMOMACheckBox.isSelected(), PowerMode3.ConfigType.MOMA);
        settings.setSpriteTypeEnabled(enableVineCheckBox.isSelected(), PowerMode3.ConfigType.VINE);
        settings.setSpriteTypeEnabled(enableMandalaCheckbox.isSelected(), PowerMode3.ConfigType.MANDALA);
        settings.setSpriteTypeEnabled(enableLinkerCheckbox.isSelected(), PowerMode3.ConfigType.LINKER);
        settings.setSpriteTypeEnabled(enableDrosteCheckbox.isSelected(), PowerMode3.ConfigType.DROSTE);
        settings.setSpriteTypeEnabled(enableCopyPasteVoid.isSelected(), PowerMode3.ConfigType.COPYPASTEVOID);


        if(!settings.isConfigLoaded){
            return;
        }

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
        this.drosteConfig.saveValues();
        this.copyPasteVoidConfig.saveValues();

        //sound panel
        this.soundConfig.saveValues();
        this.musicTriggerConfig.saveValues();
        this.specialActionSoundConfig.saveValues();

    }

    @NotNull
    @Override
    public JComponent getComponent() {
        JBScrollPane scrollPane = new JBScrollPane(this.mainPanel);

        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);


        if(settings.isConfigLoaded) {
            scrollPane.getVerticalScrollBar().getValue();
            scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
                @Override
                public void adjustmentValueChanged(AdjustmentEvent e) {
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            settings.setScrollBarPosition(e.getValue());
                            settings.setLastTabIndex(settingsTabbedPane.getSelectedIndex());

                        }
                    });
                }
            });


            //TODO switch up
            //http://www.jetbrains.org/intellij/sdk/docs/basics/architectural_overview/general_threading_rules.html?search=BackgroundTaskUtil#invokelater
            //ApplicationManager.getApplication().invokeLater()
            //https://stackoverflow.com/a/46204157/403403
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    scrollPane.getVerticalScrollBar().setValue(settings.getScrollBarPosition());
                    settingsTabbedPane.setSelectedIndex(settings.getLastTabIndex());
                }
            });
        }


        return scrollPane;
    }


    public void updateConfigUIAfterAssetsAreLoaded(boolean wasEnabled){
        //TODO weird extra setting needed here when enabled manually switched to "false"
        // (since we dont want to have plugin enabled before assets are loaded)
        // but ConfiguraleUI has already been loaded with 'false' setting
        // ***other settings are loaded from config dict which isn't modified so not needed

        isEnabledCheckBox.setEnabled(true);
        isEnabledCheckBox.setSelected(wasEnabled);
        settings.setEnabled(isEnabledCheckBox.isSelected());

        mainTopPanel.setEnabled(true);
        for(Component j : mainTopPanel.getComponents()){
            j.setEnabled(true);
        }

        mainBottomPanel.setEnabled(true);
        for(Component j : mainBottomPanel.getComponents()){
            j.setEnabled(true);
        }

        this.createConfig();
        this.loadConfigValues();
    }

    private void createConfig(){
        //TODO , put as side tabs, but tab main options nad sound options, leave particles visible at all times below
//        JBTabbedPane tabbedPane = new JBTabbedPane(JTabbedPane.LEFT);
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


        theCustomCreatePanel.add(settingsTabbedPane);


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
        //TODO make them all extend JPANEL
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


        this.copyPasteVoidConfig = new CopyPasteVoidConfig(settings);
        particleSettingsPanel.add(copyPasteVoidConfig.getConfigPanel());
        particleSettingsPanel.add(this.createSpacer());

        this.drosteConfig = new DrosteConfig(settings);
        particleSettingsPanel.add(drosteConfig);
        particleSettingsPanel.add(this.createSpacer());


        this.vineConfig = new VineConfig(settings);
        particleSettingsPanel.add(vineConfig.getConfigPanel());
        particleSettingsPanel.add(this.createSpacer());

        this.momaConfig = new MOMAConfig(settings);
        particleSettingsPanel.add(momaConfig.getConfigPanel());
        particleSettingsPanel.add(this.createSpacer());

        footerPanel = new JPanel();
        footerPanel.setMinimumSize(new Dimension(100, 300));
        particleSettingsPanel.add(footerPanel);


    }

    //Constructor is called _AFTER_ createUIComponents when using IntelliJ GUI designer
    //https://www.jetbrains.com/help/idea/creating-form-initialization-code.html
    private void createUIComponents() {
        PowerMode3 settings = PowerMode3.getInstance();

        theCustomCreatePanel = new JPanel();
        theCustomCreatePanel.setOpaque(false);
        theCustomCreatePanel.setLayout(new BoxLayout(theCustomCreatePanel, BoxLayout.PAGE_AXIS));


        if(!settings.isConfigLoaded){
            loadingLabel.setFont(new Font ("Arial", Font.BOLD, 30));
            theCustomCreatePanel.add(loadingLabel);
            return;
        }else{
            createConfig();
        }
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

    private void setupHotkeyText(){
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
//            if( i % 2 == 0) {
//                sb.append(" <br> (<b>" + sc + "</b>)");
//            }else{
//                sb.append("(<b>" + sc + "</b>)");
//            }
        }
        String labelText = String.format("<html>%s - %s</html>", toggleHotkeyLabel.getText(), sb.toString());

        toggleHotkeyLabel.setText(labelText);
    }

    public void setupPackLoaderButton(){
        loadPackButton.addActionListener((event) -> {

            int result = Messages.showYesNoDialog(null,
                    "<html> <h1> Load config pack? </h1>" +
                            " \n Config <b>packs</b> can be found on the " +
                            " <a href='https://github.com/cschar/zeranthium-extras'> zeranthium-extras </a>" +
                            "github repo. " +
                            "\n\n" +
                            "Please select a <b> manifest.json </b> file found inside one of the packs </html>",
                    "LOAD PACK","yes","no", null);

            if(result == Messages.YES){
                FileChooserDescriptor fd = new FileChooserDescriptor(true,false,false,false,false,false);
//                fd.setForcedToUseIdeaFileChooser(true);
                FileChooserDialog fcDialog = FileChooserFactory.getInstance().createFileChooser(fd, null, null);


                VirtualFile[] vfs = fcDialog.choose(null);
                if(vfs.length == 1){
                    if(!vfs[0].getName().equals("manifest.json")){
                        Messages.showInfoMessage("Please select a manifest.json file in a pack directory", "Pack Load Failed");
                    }else {
                        try {
                            this.loadConfigPack(vfs[0].getPath());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch( JSONException je){
                            Messages.showErrorDialog("<html><h1>Pack Failed to load</h1>" +
                                            "\n There was an error processing the .json info" +
                                            "\n <pre>" + je.toString() + "</pre>" +
                                            "" +
                                            "</html>",
                                    "Pack Load Failed");
                            LOGGER.log(Level.SEVERE, je.toString(), je);
                        }
                    }
                }
            }
        });
    }

//    static class PackLoader {

        private void loadConfigPack(String manifestPath) throws FileNotFoundException, JSONException {
            PowerMode3 settings = PowerMode3.getInstance();
            Path path = Paths.get(manifestPath);
    
    
    
            InputStream inputStream = new FileInputStream(manifestPath);
    
            StringBuilder sb = new StringBuilder();
            Scanner s = new Scanner(inputStream);
            while(s.hasNextLine()){
                sb.append(s.nextLine());
            }
    
    
    
            JSONObject jo = new JSONObject(sb.toString());
    
            JSONArray configsToLoad = jo.getJSONArray("configsToLoad");
            JSONObject configSettings = jo.getJSONObject("configSettings");
            for(int i=0; i < configsToLoad.length(); i++){
                String configKey = configsToLoad.getString(i);
    
                JSONObject configKeyData = configSettings.getJSONObject(configKey);
                if(configKey.equals("LIZARD")){
                    LizardConfig.loadJSONConfig(configKeyData, path.getParent());
                    enableLizardCheckBox.setSelected(true);
//                    settings.setSpriteTypeEnabled(enableLizardCheckBox.isSelected(), PowerMode3.ConfigType.LIZARD);
                }
    
                if(configKey.equals("SOUND")){
                    soundConfig.loadJSONConfig(configKeyData, path.getParent());
                }

                //DROSTE
                if(configKey.equals("DROSTE")){
                    enableDrosteCheckbox.setSelected(true);
                    DrosteConfig.loadJSONConfig(configKeyData, path.getParent());
                }

                //MANDALA
                if(configKey.equals("MANDALA")){
                    enableMandalaCheckbox.setSelected(true);
                    Mandala2Config.loadJSONConfig(configKeyData, path.getParent());
                }


                LOGGER.info("Loaded pack for " + configKey);
            }

            Notification n = new Notification(PowerMode3.NOTIFICATION_GROUP_DISPLAY_ID,
                    PowerMode3.NOTIFICATION_GROUP_DISPLAY_ID + ": Loaded Config Pack",
                    "Successfully loaded pack: " + path,
                    NotificationType.INFORMATION);
            Notifications.Bus.notify(n);
            
        }
        
        private void disableAllParticleSettings(){
                enableBasicParticleCheckBox.setSelected(false);
                enableLightningCheckBox.setSelected(false);
                lightningAltCheckBox.setSelected(false);
                enableLizardCheckBox.setSelected(false);
                enableMOMACheckBox.setSelected(false);
                enableVineCheckBox.setSelected(false);
                enableMandalaCheckbox.setSelected(false);
                enableLinkerCheckbox.setSelected(false);
                enableDrosteCheckbox.setSelected(false);
                enableCopyPasteVoid.setSelected(false);
        }

}
