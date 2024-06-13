package com.cschar.pmode3;


/*
 * Copyright 2015 Baptiste Mesta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import com.cschar.pmode3.config.*;
import com.cschar.pmode3.config.common.SoundData;
import com.cschar.pmode3.config.common.SpriteDataAnimated;
import com.cschar.pmode3.services.GitPackLoaderProgressMonitor;
import com.cschar.pmode3.services.GitPackLoaderService;
import com.cschar.pmode4.PowerMode3SettingsJComponent;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.actionSystem.TypedAction;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;

import com.intellij.ui.JBColor;
import com.intellij.util.SmartList;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.OptionTag;
import com.intellij.util.xmlb.annotations.Text;
import com.intellij.util.xmlb.annotations.Transient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.List;
import java.util.*;



/**
 * @author Baptiste Mesta
 * <p>
 * modified by cschar
 */

//https://corochann.com/intellij-plugin-development-introduction-persiststatecomponent-903.html

//This will save this object as state
//https://www.jetbrains.org/intellij/sdk/docs/basics/persisting_state_of_components.html#defining-the-storage-location
@State(
        name = "PowerMode3Zeranthium",

        //./build/idea-sandbox/config/options/power.mode.3.Zeranthium.xml
        storages = {@Storage(value = "$APP_CONFIG$/power.mode.3.Zeranthium.xml")}
)
@Service(Service.Level.APP)
final public class PowerMode3 implements
        PersistentStateComponent<PowerMode3>, Disposable {
    public static String NOTIFICATION_GROUP_DISPLAY_ID = "PowerMode - Zeranthium";
    private static final Logger LOGGER = Logger.getInstance(PowerMode3.class);

    @com.intellij.util.xmlb.annotations.Transient
    private ZJSONLoader JSONLoader;

    @com.intellij.util.xmlb.annotations.Transient
    public int dummyValue = 2;

    public void PowerMode3(){
        LOGGER.debug("Constructing PowerMode3...");
        JSONLoader = new ZJSONLoader();
        JSONLoader.getDefaultJSONTableConfigs();

    }
    @com.intellij.util.xmlb.annotations.Transient
    public PowerMode3StartupActivity startup1;
    @com.intellij.util.xmlb.annotations.Transient
    public TypedActionHandler origHandler;

    @Override
    public void dispose() {
//      See https://jetbrains.org/intellij/sdk/docs/basics/disposers.html for more details.
        LOGGER.debug("Disposing PowerMode3");
        this.particleContainerManager = null;
        //unload special action handlers for COPY/PASTE etc..
        this.startup1.teardownActionEditorKeys();
        this.startup1 = null;
        this.BASIC_PARTICLE = null;


        LOGGER.trace("Restoring original TypedAction");
        //replace TypedActionHandler with original one
        final TypedAction typedAction = TypedAction.getInstance();
        typedAction.setupRawHandler(origHandler);

        LOGGER.trace("Unloading MULTI_LAYER sprites...");
        if(MultiLayerConfig.spriteDataAnimated != null) {
            for (SpriteDataAnimated sda : MultiLayerConfig.spriteDataAnimated) {
                sda.unloadImages();
            }
            MultiLayerConfig.spriteDataAnimated.clear();
        }
        MultiLayerConfig.spriteDataAnimated = null;
        ParticleSpriteMultiLayer.spriteDataAnimated = null;



        LOGGER.trace("Unloading LIZARD sprites...");
        if(LizardConfig.spriteDataAnimated != null) {
            for (SpriteDataAnimated sda : LizardConfig.spriteDataAnimated) {
                sda.unloadImages();
            }
            LizardConfig.spriteDataAnimated.clear();
        }
        LizardConfig.spriteDataAnimated = null;
        ParticleSpriteLizardAnchor.spriteDataAnimated = null;

        LOGGER.trace("Unloading MULTI_LAYER_Chance sprites...");
        if(MultiLayerChanceConfig.spriteDataAnimated != null) {
            for (SpriteDataAnimated sda : MultiLayerChanceConfig.spriteDataAnimated) {
                sda.unloadImages();
            }
            MultiLayerChanceConfig.spriteDataAnimated.clear();
        }
        MultiLayerChanceConfig.spriteDataAnimated = null;
        ParticleSpriteMultiLayerChance.spriteDataAnimated = null;

        LOGGER.trace("Unloading LINKER sprites...");
        if(LinkerConfig.spriteDataAnimated != null) {
            for (SpriteDataAnimated sda : LinkerConfig.spriteDataAnimated) {
                sda.unloadImages();
            }
            LinkerConfig.spriteDataAnimated.clear();
        }
        LinkerConfig.spriteDataAnimated = null;
        ParticleSpriteLinkerAnchor.spriteDataAnimated = null;


        LOGGER.trace("Unloading DROSTE sprites...");
        if(DrosteConfig.spriteDataAnimated != null) {
            for (SpriteDataAnimated sda : DrosteConfig.spriteDataAnimated) {
                sda.unloadImages();
            }
            DrosteConfig.spriteDataAnimated.clear();
        }
        DrosteConfig.spriteDataAnimated = null;
        ParticleSpriteDroste.spriteDataAnimated = null;

        LOGGER.trace("Unloading COPYPASTEVOID sprites...");
        if(CopyPasteVoidConfig.spriteDataAnimated != null) {
            for (SpriteDataAnimated sda : CopyPasteVoidConfig.spriteDataAnimated) {
                sda.unloadImages();
            }
            CopyPasteVoidConfig.spriteDataAnimated.clear();
        }
        CopyPasteVoidConfig.spriteDataAnimated = null;
        ParticleSpritePasteShape.spriteDataAnimated = null;

        LOGGER.trace("Unloading LOCKEDLAYER sprites...");
        if(LockedLayerConfig.spriteDataAnimated != null) {
            for (SpriteDataAnimated sda : LockedLayerConfig.spriteDataAnimated) {
                sda.unloadImages();
            }
            LockedLayerConfig.spriteDataAnimated.clear();
        }
        LockedLayerConfig.spriteDataAnimated = null;
        ParticleSpriteLockedLayer.spriteDataAnimated = null;

        LOGGER.trace("Unloading LANTERN sprites...");
        if(LanternConfig.spriteDataAnimated != null) {
            for (SpriteDataAnimated sda : LanternConfig.spriteDataAnimated) {
                sda.unloadImages();
            }
            LanternConfig.spriteDataAnimated.clear();
        }
        LanternConfig.spriteDataAnimated = null;
        ParticleSpriteLantern.spriteDataAnimated = null;

        LOGGER.trace("Unloading TAP_ANIM sprites...");
        if(TapAnimConfig.spriteDataAnimated != null) {
            for (SpriteDataAnimated sda : TapAnimConfig.spriteDataAnimated) {
                sda.unloadImages();
            }
            TapAnimConfig.spriteDataAnimated.clear();
        }
        TapAnimConfig.spriteDataAnimated = null;
        ParticleSpriteTapAnim.spriteDataAnimated = null;


        LOGGER.trace("Unloading SOUND data...");
        if(SoundConfig.soundData != null) {
            SoundConfig.soundData.clear();
        }
        //TODO: this is bugged method, static constructor rlies on sounds.size() not to be null
        SoundConfigTableModel.emptySounds();
        SoundConfig.soundData = null;

        LOGGER.trace("Unloading MUSIC_TRIGGER data...");
        if(MusicTriggerConfig.soundData != null) {
            MusicTriggerConfig.soundData.clear();
        }
        MusicTriggerConfig.soundData = null;
        MusicTriggerConfigTableModel.data = null;
        MusicTriggerConfigTableModel.emptySounds();

        LOGGER.trace("Unloading SPECIAL_ACTION_SOUND data...");
        if(SpecialActionSoundConfig.soundData != null) {
            SpecialActionSoundConfig.soundData.clear();
        }
        SpecialActionSoundConfig.soundData = null;
        SpecialActionSoundConfigTableModel.emptySounds();

        LOGGER.trace("Unloading TEXT_COMPLETION_SOUND data...");
        if(TextCompletionSoundConfig.soundData != null) {
            TextCompletionSoundConfig.soundData.clear();
        }
        TextCompletionSoundConfig.soundData = null;
//        SpecialActionSoundConfigTableModel.emptySounds();



        this.pathDataMap.clear();
        this.pathDataMap = null;

        this.configMap.clear();
        this.configMap = null;

        this.ui = null;
        //Clear soundConfigTableMOdel sound playings...

        LOGGER.trace("stopping all playing sounds...");
        Sound.stopAll();

//        Disposer.dispose(this.configurableUI2);
//        this.configurableUI2 = null;
        LOGGER.trace(" ===== Done disposing Powermode3 ======= ");

    }


    @com.intellij.util.xmlb.annotations.Transient
    private ParticleContainerManager particleContainerManager;

    @com.intellij.util.xmlb.annotations.Transient
    Color particleColor;

    private String packDownloadPath;
    public String getPackDownloadPath(){
        return packDownloadPath;
    }
    public void setPackDownloadPath(String s){
        packDownloadPath = s;
    }

    //Basic particle
    private int particleRGB;

    @OptionTag(converter = ZStateBasicParticleConverter.class)
    public ZStateBasicParticle BASIC_PARTICLE = new ZStateBasicParticle();

    private int lastTabIndex = 0;
    private int scrollBarPosition = 0;
    private boolean enabled = true;
    private int shakeDistance = 0;
    private int lifetime = 200;
    private int maxPsiSearchDistance = 400;  //amount of total characters searched around caret for anchors

    public enum AnchorTypes {
        BRACE,
        PARENTHESIS,
        BRACKET,
        COLON
    }

    public AnchorTypes anchorType = AnchorTypes.PARENTHESIS;

    public static class CONF {
        public static int MY_BASIC_PARTICLE = 1;
        public static int MY_LIZARD = 2;
    }
    public enum ConfigType {
        BASIC_PARTICLE,

        LIZARD,
        MOMA,
        VINE,
        MULTI_LAYER,
        LINKER,
        SOUND,
        MUSIC_TRIGGER,
        DROSTE,
        COPYPASTEVOID,
        SPECIAL_ACTION_SOUND,
        LOCKED_LAYER,
        LANTERN,
        TAP_ANIM,
        MULTI_LAYER_CHANCE,
        TEXT_COMPLETION_SOUND,
    }



    @com.intellij.util.xmlb.annotations.MapAnnotation
    public Map<ConfigType, SmartList<String>> pathDataMap = new HashMap<ConfigType, SmartList<String>>() {{
        //populated by loading in .json files in resources folder.
    }};

    //consider JSON https://stackabuse.com/reading-and-writing-json-in-java/ ??
    //this tells it to copy its inner values, wont serialize without it
    @com.intellij.util.xmlb.annotations.MapAnnotation
    //Map to keep track of what's enabled currently
    private Map<String, String> configMap = new HashMap<String, String>() {{
        put("sprite" + ConfigType.BASIC_PARTICLE + "Enabled", "true");
        put("sprite" + ConfigType.LIZARD + "Enabled", "false");
        put("sprite" + ConfigType.MOMA + "Enabled", "false");
        put("sprite" + ConfigType.VINE + "Enabled", "false");
        put("sprite" + ConfigType.MULTI_LAYER + "Enabled", "false");
        put("sprite" + ConfigType.LINKER + "Enabled", "false");
        put("sprite" + ConfigType.DROSTE + "Enabled", "false");
        put("sprite" + ConfigType.COPYPASTEVOID + "Enabled", "true");
        put("sprite" + ConfigType.LOCKED_LAYER + "Enabled", "true");
        put("sprite" + ConfigType.LANTERN + "Enabled", "false");
        put("sprite" + ConfigType.TAP_ANIM + "Enabled", "true");
        put("sprite" + ConfigType.MULTI_LAYER_CHANCE + "Enabled", "false");
        put("sprite" + ConfigType.SOUND + "Enabled", "true");
        put("sprite" + ConfigType.SPECIAL_ACTION_SOUND + "Enabled", "false");
        put("sprite" + ConfigType.TEXT_COMPLETION_SOUND + "Enabled", "false");
    }};


    public static PowerMode3 getInstance() {
        return ApplicationManager.getApplication().getService(PowerMode3.class);
    }

    @Override
    public void initializeComponent() {
        LOGGER.debug("Initializing pmode3 component...");
        //construct JBColor out of serialized normal Color
        this.particleColor = new JBColor(new Color(this.getParticleRGB()), new Color(this.getParticleRGB()));

        setupSoundTyper();
        setupParticles();
        LOGGER.debug("Done initializing...");
    }


    private void setupParticles(){
        //call this on the main UI thread, since setup is done in a background task.
        // https://plugins.jetbrains.com/docs/intellij/general-threading-rules.html#write-access
        ApplicationManager.getApplication().invokeLater(() -> {
            
            final EditorFactory editorFactory = EditorFactory.getInstance();
            particleContainerManager = new ParticleContainerManager(this);

            Editor[] allEditors = EditorFactory.getInstance().getAllEditors();
            for(Editor e : allEditors){
                particleContainerManager.bootstrapEditor(e);
            }
            editorFactory.addEditorFactoryListener(particleContainerManager, this);
            EditorFactory.getInstance().refreshAllEditors();
            LOGGER.debug("Bootstrapped previous editors...");


            //Setup the handler to listen when typing... e.x. for anchor style particles
            final TypedAction typedAction = TypedAction.getInstance();
            final TypedActionHandler rawHandler = typedAction.getRawHandler();
            typedAction.setupRawHandler(new TypedActionHandler() {
                @Override
                public void execute(@NotNull final Editor editor, final char c, @NotNull final DataContext dataContext) {
                    if (PowerMode3.this.isConfigLoaded && PowerMode3.this.enabled) {
                        updateEditor(editor);
                    }
                    //this handler is Sound wrapped one below
                    rawHandler.execute(editor, c, dataContext);
                }
            });
        }, ModalityState.nonModal());
        
    }


    /** Keep track of index of each word */
    private int[] charIndexLadder = new int[10];

    private void setupSoundTyper(){
        //Setup SOUND handler
        final TypedAction typedAction2 = TypedAction.getInstance();
        TypedActionHandler rawHandler2 = typedAction2.getRawHandler();
        this.origHandler = rawHandler2;
        typedAction2.setupRawHandler(
                new TypedActionHandler() {
                    @Override
                    public void execute(@NotNull Editor editor, char c, @NotNull DataContext dataContext) {
                        if (PowerMode3.this.isConfigLoaded
                            && PowerMode3.this.enabled
                            && PowerMode3.this.getSpriteTypeEnabled(ConfigType.SOUND)) {

                            int winner = SoundData.getWeightedAmountWinningIndex(SoundConfig.soundData);
                            if (winner != -1) {
                                //TODO refactor to just return object or null
                                SoundData d = SoundConfig.soundData.get(winner);
                                Sound s = new Sound(d.getPath(), !d.customPathValid);
                                s.play();
                            }
                            LOGGER.debug("sound type");
                        }
                        if (PowerMode3.this.isConfigLoaded
                            && PowerMode3.this.enabled
                            && PowerMode3.this.getSpriteTypeEnabled(ConfigType.TEXT_COMPLETION_SOUND)) {


                            // IncrementLadder(ladder int[], char c)
                            // PlayCompleted(ladder int[])

                            var sounds = TextCompletionSoundConfig.soundData;
                            int[] results = TextCompletionSoundConfig.incrementLadder(charIndexLadder, c, sounds);

                            for(int j=0; j<results.length; j++){
                                if ( results[j] == 1){
                                    LOGGER.debug("text completion: playing sound for " + sounds.get(j).soundExtra1);
                                    SoundData d = sounds.get(j);
                                    Sound s = new Sound(d.getPath(), !d.customPathValid);
                                    s.play();
                                }
                            }
                            LOGGER.debug("text completion ladder  : " + Arrays.toString(charIndexLadder));
                            LOGGER.debug("text completion results : " + Arrays.toString(results));
                        }

                        rawHandler2.execute(editor, c, dataContext);
                    }
                });
    }

    private void updateEditor(@NotNull final Editor editor){
        particleContainerManager.update(editor);
    }



    @Nullable
    @Override
    public PowerMode3 getState() {
        LOGGER.debug("Powermode3: Getting State");
        //http://www.jetbrains.org/intellij/sdk/docs/basics/persisting_state_of_components.html#persistent-component-lifecycle
        return this;
    }

    @Override
    public void noStateLoaded() {
        LOGGER.info("No State loaded previously");
        this.setParticleRGB(JBColor.darkGray.getRGB());
        this.setPackDownloadPath(FileSystemView.getFileSystemView().getDefaultDirectory().getPath());
        if(JSONLoader == null){
            JSONLoader =  new ZJSONLoader();
        }
        pathDataMap = JSONLoader.getDefaultJSONTableConfigs();

        loadConfigData();
    }


    @Override
    public void loadState(@NotNull PowerMode3 state) {
        LOGGER.debug("previous state found: setting up...");
        XmlSerializerUtil.copyBean(state, this);
        if(JSONLoader == null){
            JSONLoader =  new ZJSONLoader();
        }

        //check for missing config data e.g. new setting has been added
        List<ConfigType> missingConfigs = new ArrayList<>();
        for (ConfigType c : ConfigType.values()) {
            if (c == ConfigType.BASIC_PARTICLE || c == ConfigType.MOMA || c == ConfigType.VINE) {
                continue;
            }
            Object val = pathDataMap.get(c);
            if (val == null) {
                missingConfigs.add(c);
            }
        }

        if (missingConfigs.size() != 0) {
            LOGGER.debug("Missing configs found: " + missingConfigs.size() + " -- loading defaults");
            for (ConfigType c : missingConfigs) {
                LOGGER.debug("loading missing config : " + c.name());
                JSONLoader.loadSingleJSONTableConfig(pathDataMap, c);
            }
        }


        loadConfigData();
        LOGGER.debug("state loaded...");
    }



    public void loadConfigData() {
        Task.Backgroundable bgTask = new Task.Backgroundable(null, "Zeranthium Setup...",
                false, null) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                loadConfigDataAsync(progressIndicator);
            }
        };
        ProgressManager.getInstance().run(bgTask);

    }

    @com.intellij.util.xmlb.annotations.Transient
    public boolean isConfigLoaded = false;

    /**
     * Load config data from default files
     */
    private void loadConfigDataAsync(ProgressIndicator progressIndicator) {
        progressIndicator.setIndeterminate(false);
        progressIndicator.setText("loading assets.. ");

        boolean wasEnabled = this.enabled;
        this.enabled = false;

        if (!this.isConfigLoaded) {
            
            long startTime = System.nanoTime();

//            if(this.getSpriteTypeEnabled(ConfigType.MULTI_LAYER)) {
                setUpdateProgress(progressIndicator, "Multi Layer", 0.1);
                MultiLayerConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.MULTI_LAYER)));
//            }
//            if(this.getSpriteTypeEnabled(ConfigType.MULTI_LAYER_CHANCE)) {
                setUpdateProgress(progressIndicator, "Multi Layer Chance", 0.2);
                MultiLayerChanceConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.MULTI_LAYER_CHANCE)));
//            }

//            if(this.getSpriteTypeEnabled(ConfigType.LIZARD)) {
                setUpdateProgress(progressIndicator, "lizard", 0.3);
                LizardConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.LIZARD)));
//            }

//            if(this.getSpriteTypeEnabled(ConfigType.LINKER)) {
                setUpdateProgress(progressIndicator, "linker", 0.4);
                LinkerConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.LINKER)));
//            }
//            if(this.getSpriteTypeEnabled(ConfigType.DROSTE)) {
                setUpdateProgress(progressIndicator, "Droste", 0.5);
                DrosteConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.DROSTE)));
//            }
//            if(this.getSpriteTypeEnabled(ConfigType.COPYPASTEVOID)) {
                setUpdateProgress(progressIndicator, "Copypaste", 0.6);
                CopyPasteVoidConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.COPYPASTEVOID)));
//            }
//            if(this.getSpriteTypeEnabled(ConfigType.LOCKED_LAYER)) {
                setUpdateProgress(progressIndicator, "Locked Layer", 0.7);
                LockedLayerConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.LOCKED_LAYER)));
//            }
//            if(this.getSpriteTypeEnabled(ConfigType.LANTERN)) {
                setUpdateProgress(progressIndicator, "Lantern", 0.8);
                LanternConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.LANTERN)));
//            }


//            if(this.getSpriteTypeEnabled(ConfigType.TAP_ANIM)){
                setUpdateProgress(progressIndicator, "Tap Anim", 0.85);
                TapAnimConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.TAP_ANIM)));
//            }

            setUpdateProgress(progressIndicator, "Sounds", 0.9);
            LOGGER.debug("======== LOADING SOUNDS ========= ");
            SoundConfig.setSoundData(this.deserializeSoundData(pathDataMap.get(ConfigType.SOUND)));
            MusicTriggerConfig.setSoundData(this.deserializeSoundData(pathDataMap.get(ConfigType.MUSIC_TRIGGER)));
            SpecialActionSoundConfig.setSoundData(this.deserializeSoundData(pathDataMap.get(ConfigType.SPECIAL_ACTION_SOUND)));
            TextCompletionSoundConfig.setSoundData(this.deserializeSoundData(pathDataMap.get(ConfigType.TEXT_COMPLETION_SOUND)));

            long endTime = System.nanoTime();
            long duration = (endTime - startTime);
            LOGGER.debug("Loaded config in " + (duration / 1000000 / 1000.0) + " seconds");




            this.enabled = wasEnabled;
            this.isConfigLoaded = true;

            //TODO: replace this with a message or signal or listener or something..
            // remove the static reference plumbing
            if (ui != null) {
                ui.updateConfigUIAfterAssetsAreLoaded(wasEnabled);
            }
        }
    }

    @Transient
    public PowerMode3SettingsJComponent ui;

    private void setUpdateProgress(ProgressIndicator progressIndicator, String info, double amt) {
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

        String s = "Loading - " + info + " " + amt;
//        progressIndicator.setText(s);
        progressIndicator.setText2(s);
        progressIndicator.setFraction(amt);
        PowerMode3SettingsJComponent.loadingLabel.setText(s);

    }


    public ArrayList<SoundData> deserializeSoundData(List<String> target) {
        ArrayList<SoundData> sd = new ArrayList<>();
        for (String s1 : target) {
            sd.add(SoundData.fromJsonObjectString(s1));
        }
        return sd;
    }

//    public ArrayList<SpriteData> deserializeSpriteData(SmartList<String> target) {
//        ArrayList<SpriteData> sd = new ArrayList<SpriteData>();
//        for (String s1 : target) {
//
//            sd.add(SpriteData.fromJsonObjectString(s1));
//
//        }
//        return sd;
//    }

    public ArrayList<SpriteDataAnimated> deserializeSpriteDataAnimated(List<String> target) {
        LOGGER.debug("deserializing sprite data animated" + target);
        ArrayList<SpriteDataAnimated> sd = new ArrayList<SpriteDataAnimated>();
        for (String s1 : target) {
            sd.add(SpriteDataAnimated.fromJsonObjectString(s1));
        }
        return sd;
    }


//    public void setSerializedSpriteData(ArrayList<SpriteData> spriteData, ConfigType configType) {
//        SmartList<String> serialized = new SmartList<>();
//        for (SpriteData d : spriteData) {
//            serialized.add(d.toJSONObject().toString());
//        }
//        this.pathDataMap.put(configType, serialized);
//    }

    public void setSerializedSDAJsonInfo(ArrayList<SpriteDataAnimated> spriteData, ConfigType configType) {
        SmartList<String> serialized = new SmartList<>();
        for (SpriteDataAnimated d : spriteData) {
            serialized.add(d.toJSONObject().toString());
        }
        this.pathDataMap.put(configType, serialized);
    }

    public void setSerializedSoundData(ArrayList<SoundData> soundData, ConfigType configType) {
        SmartList<String> serialized = new SmartList<>();
        for (SoundData d : soundData) {
            serialized.add(d.toJSONObject().toString());
        }
        this.pathDataMap.put(configType, serialized);
    }


    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if(!this.enabled){
            Sound.stopAll();
        }
    }


    public void setSpriteTypeEnabled(Boolean enabled, ConfigType type) {
        configMap.put("sprite" + type + "Enabled", enabled.toString());
    }

    public boolean getSpriteTypeEnabled(ConfigType type) {
        return Boolean.parseBoolean(configMap.get("sprite" + type + "Enabled"));
    }

    public void setSpriteTypeProperty(ConfigType type, String property, String value) {
        configMap.put(String.format("sprite%s_%s", type, property), value);
    }

    public String getSpriteTypeProperty(ConfigType type, String property) {
        return configMap.get(String.format("sprite%s_%s", type, property));
    }


    public int getLifetime() {
        return lifetime;
    }

    public void setLifetime(int l) {
        lifetime = l;
    }


    public int getParticleRGB() {
        return particleRGB;
    }

    public void setParticleRGB(int particleRGB) {
        this.particleRGB = particleRGB;
        this.particleColor = new JBColor(new Color(particleRGB), new Color(particleRGB));
    }

    public int getMaxPsiSearchDistance() {
        return maxPsiSearchDistance;
    }

    public void setMaxPsiSearchDistance(int maxPsiSearchDistance) {
        this.maxPsiSearchDistance = maxPsiSearchDistance;
    }


    public int getShakeDistance() {
        return shakeDistance;
    }

    public void setShakeDistance(int shakeDistance) {
        this.shakeDistance = shakeDistance;
    }


    // Save/load config panel view when exit/enter
    public int getScrollBarPosition() {
        return scrollBarPosition;
    }

    public void setScrollBarPosition(int scrollBarPosition) {
        this.scrollBarPosition = scrollBarPosition;
    }

    public int getLastTabIndex() {
        return lastTabIndex;
    }

    public void setLastTabIndex(int lastTabIndex) {
        this.lastTabIndex = lastTabIndex;
    }


}


