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
import com.cschar.pmode3.config.common.SpriteData;
import com.cschar.pmode3.config.common.SpriteDataAnimated;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.actionSystem.*;
import com.intellij.openapi.progress.*;

import com.intellij.openapi.util.Disposer;
import com.intellij.ui.JBColor;
import com.intellij.util.SmartList;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;


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
public class PowerMode3 implements
        PersistentStateComponent<PowerMode3>, Disposable {

    @Override
    public void dispose() {
//       See https://jetbrains.org/intellij/sdk/docs/basics/disposers.html for more details.
        //particleContainerManager = null;
        //why isnt this disposing

        LOGGER.info("Disposing PowerMode3");
        Disposer.dispose(particleContainerManager);
    }

    //https://www.jetbrains.org/intellij/sdk/docs/basics/persisting_state_of_components.html#implementing-the-persistentstatecomponent-interface
    private static final Logger LOGGER = Logger.getLogger(PowerMode3.class.getName());

    public static String NOTIFICATION_GROUP_DISPLAY_ID = "PowerMode - Zeranthium";

    @com.intellij.util.xmlb.annotations.Transient
    private ParticleContainerManager particleContainerManager;

    @com.intellij.util.xmlb.annotations.Transient
    Color particleColor;

    private int particleRGB;


    private int lastTabIndex = 0;
    private int scrollBarPosition = 0;

//    public String scrollBarPosition2 = "0";

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
    }



    static class JSONLoader {
        private static HashMap<ConfigType, String> defaultJSONTables;

        static {
            JSONLoader.defaultJSONTables = new HashMap<ConfigType, String>() {{
                put(ConfigType.LOCKED_LAYER, "LOCKED_LAYER.json");
                put(ConfigType.COPYPASTEVOID, "COPYPASTEVOID.json");
                put(ConfigType.DROSTE, "DROSTE.json");
                put(ConfigType.LINKER, "LINKER.json");
                put(ConfigType.LANTERN, "LANTERN.json");
                put(ConfigType.LIZARD, "LIZARD.json");
                put(ConfigType.MULTI_LAYER, "MULTI_LAYER.json");
                put(ConfigType.MULTI_LAYER_CHANCE, "MULTI_LAYER_CHANCE.json");
                put(ConfigType.TAP_ANIM, "TAP_ANIM.json");

                put(ConfigType.MUSIC_TRIGGER, "MUSIC_TRIGGERS.json");
                put(ConfigType.SOUND, "SOUND.json");
                put(ConfigType.SPECIAL_ACTION_SOUND, "SPECIAL_ACTION_SOUND.json");

            }};
        }

        public static void loadSingleJSONTableConfig(Map<ConfigType, SmartList<String>> pathDataMap, ConfigType t) {

            String jsonFile = defaultJSONTables.get(t);
            InputStream inputStream = JSONLoader.class.getResourceAsStream("/configJSON/" + jsonFile);

            StringBuilder sb = new StringBuilder();
            Scanner s = new Scanner(inputStream);
            while (s.hasNextLine()) {
                sb.append(s.nextLine());
            }

            SmartList<String> smartList = new SmartList<>();
            try {
                JSONObject jo = new JSONObject(sb.toString());

                //TODO this is extra work, we go .json --> JSONObject --> string ( ..later --> JSONObject ---> pathData)
                JSONArray tableData = jo.getJSONArray("data");
                for (int i = 0; i < tableData.length(); i++) {
                    smartList.add(tableData.getJSONObject(i).toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            pathDataMap.put(t, smartList);
        }


        public static HashMap<ConfigType, SmartList<String>> getDefaultJSONTableConfigs() {
            HashMap<ConfigType, SmartList<String>> pathDataMap = new HashMap<>();
            for (ConfigType t : defaultJSONTables.keySet()) {
                loadSingleJSONTableConfig(pathDataMap, t);
            }
            return pathDataMap;
        }

    }


    @com.intellij.util.xmlb.annotations.MapAnnotation
    protected Map<ConfigType, SmartList<String>> pathDataMap = new HashMap<ConfigType, SmartList<String>>() {{
        //populated by loading in .json files in resources folder.
    }};

    //consider JSON https://stackabuse.com/reading-and-writing-json-in-java/ ??
    @com.intellij.util.xmlb.annotations.MapAnnotation
    //this tells it to copy its inner values, wont serialize without it
    private Map<String, String> configMap = new HashMap<String, String>() {{

        put("sprite" + ConfigType.BASIC_PARTICLE + "Enabled", "true");
        put("sprite" + ConfigType.LIZARD + "Enabled", "true");


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
    }};


    public static PowerMode3 getInstance() {
        return ApplicationManager.getApplication().getService(PowerMode3.class);
    }

    @Override
    public void initializeComponent() {
        LOGGER.info("Initializing pmode3 component...");

        this.particleColor = new JBColor(new Color(this.getParticleRGB()), new Color(this.getParticleRGB()));

        //Setup SOUND handler
        final EditorActionManager actionManager = EditorActionManager.getInstance();
        final TypedAction typedAction2 = TypedAction.getInstance();
        TypedActionHandler rawHandler2 = typedAction2.getRawHandler();
        typedAction2.setupRawHandler(
                new TypedActionHandler() {
                    @Override
                    public void execute(@NotNull Editor editor, char c, @NotNull DataContext dataContext) {
                        if (PowerMode3.this.isEnabled() && PowerMode3.this.getSpriteTypeEnabled(ConfigType.SOUND)) {

                            int winner = SoundData.getWeightedAmountWinningIndex(SoundConfig.soundData);
                            if (winner != -1) {
                                //TODO refactor to just return object or null
                                SoundData d = SoundConfig.soundData.get(winner);
                                Sound s = new Sound(d.getPath(), !d.customPathValid);
                                s.play();
                            }

                        }
                        rawHandler2.execute(editor, c, dataContext);
                    }
                });



        //Setup Main particle stuff
        //Ensure when a new editor is created,  a particleContainerManager is attached to it
        final EditorFactory editorFactory = EditorFactory.getInstance();


        particleContainerManager = new ParticleContainerManager(this);

        Editor[] allEditors = EditorFactory.getInstance().getAllEditors();
        for(Editor e : allEditors){
//            LOGGER.info("Editor " + e.toString() + " is open");
            particleContainerManager.bootstrapEditor(e);
        }

        editorFactory.addEditorFactoryListener(particleContainerManager, this);
        EditorFactory.getInstance().refreshAllEditors();

        LOGGER.info("Bootstrapped previous editors...");

        //Setup the handler to listen when typing... e.x. for anchor style particles
        final TypedAction typedAction = TypedAction.getInstance();
        final TypedActionHandler rawHandler = typedAction.getRawHandler();
        typedAction.setupRawHandler(new TypedActionHandler() {
            @Override
            public void execute(@NotNull final Editor editor, final char c, @NotNull final DataContext dataContext) {
                updateEditor(editor);
                rawHandler.execute(editor, c, dataContext);
            }
        });
        LOGGER.info("Done initializing...");
    }

    private void updateEditor(@NotNull final Editor editor){
        particleContainerManager.update(editor);
    }



    @Nullable
    @Override
    public PowerMode3 getState() {
        //http://www.jetbrains.org/intellij/sdk/docs/basics/persisting_state_of_components.html#persistent-component-lifecycle
        return this;
    }

    @Override
    public void noStateLoaded() {
        LOGGER.info("No State loaded previously");
        this.setParticleRGB(JBColor.darkGray.getRGB());
        pathDataMap = JSONLoader.getDefaultJSONTableConfigs();

        loadConfigData();
    }


    @Override
    public void loadState(@NotNull PowerMode3 state) {
        LOGGER.info("previous state found -- setting up...");

        XmlSerializerUtil.copyBean(state, this);

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
            LOGGER.info("Missing configs found: " + missingConfigs.size() + " -- loading defaults");
            for (ConfigType c : missingConfigs) {
                LOGGER.info(c.name());
                JSONLoader.loadSingleJSONTableConfig(pathDataMap, c);
            }
        }


        loadConfigData();
        LOGGER.info("state loaded...");
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

    private void loadConfigDataAsync(ProgressIndicator progressIndicator) {
        progressIndicator.setIndeterminate(false);
        progressIndicator.setText("loading assets.. ");

        boolean wasEnabled = this.enabled;
        this.enabled = false;

        if (!this.isConfigLoaded) {
            LOGGER.info("Loading assets...");

            setUpdateProgress(progressIndicator, "Multi Layer", 0.1);
            MultiLayerConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.MULTI_LAYER)));
            setUpdateProgress(progressIndicator, "Multi Layer Chance", 0.2);
            MultiLayerChanceConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.MULTI_LAYER_CHANCE)));

            setUpdateProgress(progressIndicator, "lizard", 0.3);
            LizardConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.LIZARD)));
            setUpdateProgress(progressIndicator, "linker", 0.4);
            LinkerConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.LINKER)));
            setUpdateProgress(progressIndicator, "Droste", 0.5);
            DrosteConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.DROSTE)));
            setUpdateProgress(progressIndicator, "Copypaste", 0.6);
            CopyPasteVoidConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.COPYPASTEVOID)));
            setUpdateProgress(progressIndicator, "Locked Layer", 0.7);
            LockedLayerConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.LOCKED_LAYER)));
            setUpdateProgress(progressIndicator, "Lantern", 0.8);
            LanternConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.LANTERN)));
            setUpdateProgress(progressIndicator, "Tap Anim", 0.85);
            TapAnimConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.TAP_ANIM)));

            setUpdateProgress(progressIndicator, "Sounds", 0.9);
            SoundConfig.setSoundData(this.deserializeSoundData(pathDataMap.get(ConfigType.SOUND)));
            MusicTriggerConfig.setSoundData(this.deserializeSoundData(pathDataMap.get(ConfigType.MUSIC_TRIGGER)));
            SpecialActionSoundConfig.setSoundData(this.deserializeSoundData(pathDataMap.get(ConfigType.SPECIAL_ACTION_SOUND)));


            this.enabled = wasEnabled;
            this.isConfigLoaded = true;

            MenuConfigurableUI ui = MenuConfigurableUI.getInstance();
            if (ui != null) {
                ui.updateConfigUIAfterAssetsAreLoaded(wasEnabled);
            }
        }
    }

    private void setUpdateProgress(ProgressIndicator progressIndicator, String info, double amt) {
        String s = "Loading - " + info + " " + amt;
//        progressIndicator.setText(s);
        progressIndicator.setText2(s);
        progressIndicator.setFraction(amt);
        MenuConfigurableUI.loadingLabel.setText(s);
//        try {              Thread.sleep(3000);          } catch (InterruptedException e) {          }

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
        LOGGER.info("deserializing sprite data animated" + target);
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

    public void setSerializedSpriteDataAnimated(ArrayList<SpriteDataAnimated> spriteData, ConfigType configType) {
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
    }


    void setSpriteTypeEnabled(Boolean enabled, ConfigType type) {
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


