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


import com.cschar.pmode3.actionHandlers.MyPasteHandler;
import com.cschar.pmode3.actionHandlers.MySpecialActionHandler;
import com.cschar.pmode3.config.*;
import com.cschar.pmode3.config.common.SoundData;
import com.cschar.pmode3.config.common.SpriteData;
import com.cschar.pmode3.config.common.SpriteDataAnimated;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.BaseComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.actionSystem.*;
import com.intellij.openapi.progress.*;

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
 *
 * modified by cschar
 */

//https://corochann.com/intellij-plugin-development-introduction-persiststatecomponent-903.html

//This will save this object as state
//https://www.jetbrains.org/intellij/sdk/docs/basics/persisting_state_of_components.html#defining-the-storage-location
@State(
        name = "PowerMode3",

        //./build/idea-sandbox/config/options/power.mode3.xml
        storages = {@Storage(value = "$APP_CONFIG$/power.mode3.xml")}
        //storages = {@Storage("com.cschar.com.cschar.pmode3.xml")}
)
public class PowerMode3 implements BaseComponent,
        PersistentStateComponent<PowerMode3> {

    //https://www.jetbrains.org/intellij/sdk/docs/basics/persisting_state_of_components.html#implementing-the-persistentstatecomponent-interface
    private static final Logger LOGGER = Logger.getLogger( PowerMode3.class.getName() );

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
    public AnchorTypes anchorType = AnchorTypes.BRACE;




    public enum ConfigType {
        BASIC_PARTICLE,
        LIGHTNING,
        LIGHTNING_ALT,
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
        TAP_ANIM
    }

    static class JSONLoader {

        public static HashMap<ConfigType,SmartList<String>> loadDefaultJSONTableConfigs(){

            HashMap<ConfigType, String> defaultJSONTables = new HashMap<ConfigType, String>() {{

                put(ConfigType.LOCKED_LAYER, "LOCKED_LAYER.json");
                put(ConfigType.COPYPASTEVOID, "COPYPASTEVOID.json");
                put(ConfigType.DROSTE, "DROSTE.json");
                put(ConfigType.LIGHTNING_ALT, "LIGHTNING_ALT2.json");
                put(ConfigType.LINKER, "LINKER.json");
                put(ConfigType.LANTERN, "LANTERN.json");
                put(ConfigType.LIZARD, "LIZARD.json");
                put(ConfigType.MULTI_LAYER, "MULTI_LAYER.json");
                put(ConfigType.TAP_ANIM, "TAP_ANIM.json");


                put(ConfigType.MUSIC_TRIGGER, "MUSIC_TRIGGERS.json");
                put(ConfigType.SOUND, "SOUND.json");
                put(ConfigType.SPECIAL_ACTION_SOUND, "SPECIAL_ACTION_SOUND.json");

            }};
            HashMap<ConfigType,SmartList<String>> pathDataMap = new HashMap<ConfigType,SmartList<String>>();


            for(ConfigType t: defaultJSONTables.keySet()){
                String jsonFile = defaultJSONTables.get(t);
                InputStream inputStream = JSONLoader.class.getResourceAsStream("/configJSON/"+jsonFile);


                StringBuilder sb = new StringBuilder();
                Scanner s = new Scanner(inputStream);
                while(s.hasNextLine()){
                    sb.append(s.nextLine());
                }

                SmartList<String> smartList = new SmartList<>();
                try {
                    JSONObject jo = new JSONObject(sb.toString());

                    //TODO this is extra work, we go .json --> JSONObject --> string ( ..later --> JSONObject ---> pathData)
                    JSONArray tableData = jo.getJSONArray("data");
                    for(int i=0; i < tableData.length(); i++){
                        smartList.add(tableData.getJSONObject(i).toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pathDataMap.put(t, smartList);
            }

            return pathDataMap;
        }

    }


    @com.intellij.util.xmlb.annotations.MapAnnotation
    private Map<ConfigType,SmartList<String>> pathDataMap = new HashMap<ConfigType,SmartList<String>>(){{
        //populated by loading in .json files in resources folder.
    }};

    //consider JSON https://stackabuse.com/reading-and-writing-json-in-java/ ??
    @com.intellij.util.xmlb.annotations.MapAnnotation  //this tells it to copy its inner values, wont serialize without it
    private Map<String,String> configMap = new HashMap<String,String>(){{

        put("sprite"+ ConfigType.BASIC_PARTICLE + "Enabled", "true");
        put("sprite"+ ConfigType.LIGHTNING + "Enabled", "false");
        put("sprite"+ ConfigType.LIGHTNING_ALT + "Enabled", "false");
        put("sprite"+ ConfigType.LIZARD + "Enabled", "true");


        put("sprite"+ ConfigType.MOMA+ "Enabled", "false");

        put("sprite"+ ConfigType.VINE + "Enabled", "false");
        put("sprite"+ ConfigType.MULTI_LAYER + "Enabled", "false");
        put("sprite"+ ConfigType.LINKER + "Enabled", "false");
        put("sprite"+ ConfigType.DROSTE + "Enabled", "false");
        put("sprite"+ ConfigType.COPYPASTEVOID + "Enabled", "true");
        put("sprite"+ ConfigType.LOCKED_LAYER + "Enabled", "true");
        put("sprite"+ ConfigType.LANTERN + "Enabled", "false");
        put("sprite"+ ConfigType.TAP_ANIM + "Enabled", "true");

        put("sprite"+ ConfigType.SOUND + "Enabled", "true");
        put("sprite"+ ConfigType.SPECIAL_ACTION_SOUND + "Enabled", "false");
    }};



    public static PowerMode3 getInstance() {
        return ApplicationManager.getApplication().getComponent(PowerMode3.class);
    }

    @Override
    public void initializeComponent() {

        //Load JSON
        JSONLoader.loadDefaultJSONTableConfigs();

        //Setup SOUND handler
        final EditorActionManager actionManager = EditorActionManager.getInstance();

        final TypedAction typedAction2 = actionManager.getTypedAction();
        TypedActionHandler rawHandler2 = typedAction2.getRawHandler();
        typedAction2.setupRawHandler(new TypedActionHandler() {
                                      @Override
                                      public void execute(@NotNull Editor editor, char c, @NotNull DataContext dataContext) {
//                                          PowerMode3 settings = PowerMode3.getInstance();
                                          if(PowerMode3.this.isEnabled() && PowerMode3.this.getSpriteTypeEnabled(ConfigType.SOUND)) {

                                              int winner = SoundData.getWeightedAmountWinningIndex(SoundConfig.soundData);
                                              if( winner != -1){
                                                  //TODO refactor to just return object or null
                                                  SoundData d = SoundConfig.soundData.get(winner);
                                                  Sound s = new Sound(d.getPath(), !d.customPathValid);
                                                  s.play();
                                              }

                                          }
                                          rawHandler2.execute(editor,c,dataContext);
                                      }
                                  });



        this.setupActionEditorKeys();


        this.particleColor = new JBColor(new Color(this.getParticleRGB()), new Color(this.getParticleRGB()));

        final EditorActionManager editorActionManager = EditorActionManager.getInstance();
        final EditorFactory editorFactory = EditorFactory.getInstance();
        particleContainerManager = new ParticleContainerManager(this);
        editorFactory.addEditorFactoryListener(particleContainerManager, new Disposable() {
            @Override
            public void dispose() {

            }
        });
        final TypedAction typedAction = editorActionManager.getTypedAction();
        final TypedActionHandler rawHandler = typedAction.getRawHandler();
        typedAction.setupRawHandler(new TypedActionHandler() {
            @Override
            public void execute(@NotNull final Editor editor, final char c, @NotNull final DataContext dataContext) {

                updateEditor(editor);
                rawHandler.execute(editor, c, dataContext);
            }
        });
    }

    private void updateEditor(@NotNull final Editor editor) {

        particleContainerManager.update(editor);

    }



    @Override
    public void disposeComponent() {
        particleContainerManager.dispose();
        particleContainerManager = null;
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "PowerMode3";
    }





    @Nullable
    @Override
    public PowerMode3 getState() {
        //http://www.jetbrains.org/intellij/sdk/docs/basics/persisting_state_of_components.html#persistent-component-lifecycle
        return this;
    }

    @Override
    public void noStateLoaded() {
        LOGGER.info("NO State loaded previously");
        this.setParticleRGB(JBColor.darkGray.getRGB());
        pathDataMap = JSONLoader.loadDefaultJSONTableConfigs();

        loadConfigData();
    }

    private List<ConfigType> getMissingPathConfigs(){
        List<ConfigType> missing = new ArrayList<>();
        for(ConfigType c: ConfigType.values()){
            if(c == ConfigType.BASIC_PARTICLE || c == ConfigType.LIGHTNING || c == ConfigType.MOMA || c == ConfigType.VINE){
                continue;
            }
            Object val =  pathDataMap.get(c);
            if(val == null){
                missing.add(c);
            }
        }
        return missing;
    }

    @Override
    public void loadState(@NotNull PowerMode3 state) {
        LOGGER.info("previous state found -- setting up...");
//        pathDataMap = ConfigLoader.loadDefaultJSONTableConfigs();

        XmlSerializerUtil.copyBean(state, this);


        //check for missing config data e.g. new setting has been added
        List<ConfigType> missingConfigs = getMissingPathConfigs();
        if (missingConfigs.size() != 0) {
            LOGGER.info("Missing configs found: " + missingConfigs.size() + " -- loading defaults");
            for(ConfigType c: missingConfigs){
                LOGGER.info(c.name());
            }
            //TODO only load missingConfigs default setting , not all of them
            pathDataMap = JSONLoader.loadDefaultJSONTableConfigs();
        }


        loadConfigData();

    }

    @com.intellij.util.xmlb.annotations.Transient
    public boolean isConfigLoaded = false;

    // for use with experimental editor onCreated in particleManager
//    @com.intellij.util.xmlb.annotations.Transient
//    public boolean isLoading = false;

    public void loadConfigData(){
//        boolean DO_BACKGROUND_LOAD = false;
        boolean DO_BACKGROUND_LOAD = true;

        if(DO_BACKGROUND_LOAD) {
            Task.Backgroundable bgTask = new Task.Backgroundable(null, "Zeranthium Setup...",
                    false, null) {
//            Task.Modal bgTask = new Task.Modal(null, "Zeranthium Setup...",
//                    false) {
                @Override
                public void run(@NotNull ProgressIndicator progressIndicator) {
                    loadConfigDataAsync(progressIndicator);
                }
            };
            ProgressManager.getInstance().run(bgTask);
        }else {
            loadConfigDataAtSplash();
        }
    }



    private void loadConfigDataAsync(ProgressIndicator progressIndicator){
        progressIndicator.setIndeterminate(false);
        progressIndicator.setText("loading assets.. ");

        boolean wasEnabled = this.enabled;
        this.enabled = false;

        if(!this.isConfigLoaded) {
            LOGGER.info("Loading assets");
//            for(ConfigType)


            setUpdateProgress(progressIndicator, "lightning", 0.1);

//            pathData = new ArrayList<String>(s);

            LightningAltConfig.setSparkData(this.deserializeSpriteData(pathDataMap.get(ConfigType.LIGHTNING_ALT)));

            setUpdateProgress(progressIndicator, "Multi Layer", 0.2);
            MultiLayerConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.MULTI_LAYER)));
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
            if(ui != null) {
                ui.updateConfigUIAfterAssetsAreLoaded(wasEnabled);
            }
        }
    }

    private void setUpdateProgress(ProgressIndicator progressIndicator, String info, double amt){

        String s = "Loading - " + info + " " + amt;

//        progressIndicator.setText(s);
        progressIndicator.setText2(s);
        progressIndicator.setFraction(amt);
        MenuConfigurableUI.loadingLabel.setText(s);
//        try {              Thread.sleep(3000);          } catch (InterruptedException e) {          }

    }


    private void loadConfigDataAtSplash(){
        if(!this.isConfigLoaded) {
            LightningAltConfig.setSparkData(this.deserializeSpriteData(pathDataMap.get(ConfigType.LIGHTNING_ALT)));

            MultiLayerConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.MULTI_LAYER)));
            LizardConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.LIZARD)));
            LinkerConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.LINKER)));
            DrosteConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.DROSTE)));
            CopyPasteVoidConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.COPYPASTEVOID)));
            LockedLayerConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.LOCKED_LAYER)));
            LanternConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.LANTERN)));
            TapAnimConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.TAP_ANIM)));

            SoundConfig.setSoundData(this.deserializeSoundData(pathDataMap.get(ConfigType.SOUND)));
            MusicTriggerConfig.setSoundData(this.deserializeSoundData(pathDataMap.get(ConfigType.MUSIC_TRIGGER)));
            SpecialActionSoundConfig.setSoundData(this.deserializeSoundData(pathDataMap.get(ConfigType.SPECIAL_ACTION_SOUND)));
        }
        this.isConfigLoaded = true;

    }



    public ArrayList<SoundData> deserializeSoundData(SmartList<String> target){
        ArrayList<SoundData> sd = new ArrayList<>();
        for(String s1: target){
            sd.add( SoundData.fromJsonObjectString(s1));
        }
        return sd;
    }

    public ArrayList<SpriteData> deserializeSpriteData(SmartList<String> target){
        ArrayList<SpriteData> sd = new ArrayList<SpriteData>();
        for(String s1: target){

            sd.add( SpriteData.fromJsonObjectString(s1));

        }
        return sd;
    }

    public ArrayList<SpriteDataAnimated> deserializeSpriteDataAnimated(SmartList<String> target){
        LOGGER.info("deserializing sprite data animated");
        ArrayList<SpriteDataAnimated> sd = new ArrayList<SpriteDataAnimated>();
        for(String s1: target){
            sd.add( SpriteDataAnimated.fromJsonObjectString(s1));
        }
        return sd;
    }



    public void setSerializedSpriteData(ArrayList<SpriteData> spriteData, ConfigType configType){
        SmartList<String> serialized = new SmartList<>();
        for( SpriteData d: spriteData){
            serialized.add(d.toJSONObject().toString());
//            serialized.add(new String[]{String.valueOf(d.enabled), String.valueOf(d.scale), String.valueOf(d.val1),
//                    String.valueOf(d.defaultPath), String.valueOf(d.customPath)});
        }
        this.pathDataMap.put(configType, serialized);
    }

    public void setSerializedSpriteDataAnimated(ArrayList<SpriteDataAnimated> spriteData, ConfigType configType){
//        try {
            SmartList<String> serialized = new SmartList<>();
            for (SpriteDataAnimated d : spriteData) {
                serialized.add(d.toJSONObject().toString());

//                serialized.add(String.join(",",
//                        new String[]{String.valueOf(d.enabled), String.valueOf(d.scale), String.valueOf(d.speedRate),
//                        String.valueOf(d.defaultPath), String.valueOf(d.customPath),
//                        String.valueOf(d.isCyclic), String.valueOf(d.val2),
//                        String.valueOf(d.alpha), String.valueOf(d.val1)}));
            }

            this.pathDataMap.put(configType, serialized);

    }

    public void setSerializedSoundData(ArrayList<SoundData> soundData, ConfigType configType){
        SmartList<String> serialized = new SmartList<>();
        for (SoundData d : soundData) {
            serialized.add(d.toJSONObject().toString());
//            serialized.add(String.join(",",new String[]{String.valueOf(d.enabled), String.valueOf(d.val1),
//                    String.valueOf(d.defaultPath), String.valueOf(d.customPath)}));
        }
        this.pathDataMap.put(configType, serialized);
    }





    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    void setSpriteTypeEnabled(Boolean enabled, ConfigType type){
        configMap.put("sprite" + type + "Enabled", enabled.toString());
    }
    public boolean getSpriteTypeEnabled(ConfigType type){
        return Boolean.parseBoolean(configMap.get("sprite"+type+"Enabled"));
    }

    public void setSpriteTypeProperty(ConfigType type, String property, String value){
        configMap.put(String.format("sprite%s_%s", type, property), value);
    }

    public String getSpriteTypeProperty(ConfigType type, String property){
        return configMap.get(String.format("sprite%s_%s", type, property));
    }



    public int getLifetime() { return lifetime; }
    public void setLifetime(int l) { lifetime=l;}


    public int getParticleRGB() {     return particleRGB; }
    public void setParticleRGB(int particleRGB) {
        this.particleRGB = particleRGB;
        this.particleColor = new JBColor(new Color(particleRGB), new Color(particleRGB));
    }

    public int getMaxPsiSearchDistance() {  return maxPsiSearchDistance; }
    public void setMaxPsiSearchDistance(int maxPsiSearchDistance) {this.maxPsiSearchDistance = maxPsiSearchDistance;}


    public int getShakeDistance() {  return shakeDistance;  }
    public void setShakeDistance(int shakeDistance) {   this.shakeDistance = shakeDistance;  }


    // Save/load config panel view when exit/enter
    public int getScrollBarPosition() {   return scrollBarPosition;  }
    public void setScrollBarPosition(int scrollBarPosition) {  this.scrollBarPosition = scrollBarPosition; }
    public int getLastTabIndex() {    return lastTabIndex;  }
    public void setLastTabIndex(int lastTabIndex) {    this.lastTabIndex = lastTabIndex;   }




    private void setupActionEditorKeys(){
        final EditorActionManager actionManager = EditorActionManager.getInstance();


        MySpecialActionHandler h1;
        EditorActionHandler origHandler;

        //COPYPASTEVOID
        origHandler = actionManager.getActionHandler(IdeActions.ACTION_EDITOR_PASTE);
        MyPasteHandler myPasteHandler = new MyPasteHandler(origHandler);
        actionManager.setActionHandler(IdeActions.ACTION_EDITOR_PASTE, myPasteHandler);

        //SPECIAL_ACTION_SOUND
        origHandler = actionManager.getActionHandler(IdeActions.ACTION_EDITOR_COPY);
        h1 = new MySpecialActionHandler(origHandler, SpecialActionSoundConfig.KEYS.COPY);
        actionManager.setActionHandler(IdeActions.ACTION_EDITOR_COPY, h1);

        origHandler = actionManager.getActionHandler(IdeActions.ACTION_EDITOR_PASTE);
        h1 = new MySpecialActionHandler(origHandler, SpecialActionSoundConfig.KEYS.PASTE);
        actionManager.setActionHandler(IdeActions.ACTION_EDITOR_PASTE, h1);

        origHandler = actionManager.getActionHandler(IdeActions.ACTION_EDITOR_DELETE);
        h1 = new MySpecialActionHandler(origHandler, SpecialActionSoundConfig.KEYS.DELETE);
        actionManager.setActionHandler(IdeActions.ACTION_EDITOR_DELETE, h1);

        origHandler = actionManager.getActionHandler(IdeActions.ACTION_EDITOR_BACKSPACE);
        h1 = new MySpecialActionHandler(origHandler, SpecialActionSoundConfig.KEYS.BACKSPACE);
        actionManager.setActionHandler(IdeActions.ACTION_EDITOR_BACKSPACE, h1);

        origHandler = actionManager.getActionHandler(IdeActions.ACTION_EDITOR_ENTER);
        h1 = new MySpecialActionHandler(origHandler, SpecialActionSoundConfig.KEYS.ENTER);
        actionManager.setActionHandler(IdeActions.ACTION_EDITOR_ENTER, h1);


    }



}


