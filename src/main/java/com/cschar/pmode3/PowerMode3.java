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
import com.cschar.pmode3.config.*;
import com.cschar.pmode3.config.common.SoundData;
import com.cschar.pmode3.config.common.SpriteData;
import com.cschar.pmode3.config.common.SpriteDataAnimated;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.components.BaseComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.actionSystem.*;
import com.intellij.openapi.progress.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.VetoableProjectManagerListener;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.PsiFile;
import com.intellij.ui.JBColor;
import com.intellij.util.SmartList;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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






    public enum ConfigType {
        BASIC_PARTICLE,
        LIGHTNING,
        LIGHTNING_ALT,
        LIZARD,
        MOMA,
        VINE,
        MANDALA,
        LINKER,
        SOUND,
        MUSIC_TRIGGER,
        DROSTE,
        COPYPASTEVOID
    }

    @com.intellij.util.xmlb.annotations.XCollection
    private ArrayList<String[]> copyPasteVoidDataStringArrays = new ArrayList<String[]>(){{
        //enabled, scale, speed, defaultPath, customPath, isCyclic, val2, alpha, val1
        add(new String[]{"true","0.4f","2","/blender/droste2","", "false","1","1.0f","2"});
        add(new String[]{"true","0.6f","2","/blender/droste2","", "false","1","0.6f","2"});
    }};

    @com.intellij.util.xmlb.annotations.XCollection
    private ArrayList<String[]> drosteDataStringArrays = new ArrayList<String[]>(){{
        //enabled, scale, speed, defaultPath, customPath, isCyclic, val2, alpha, val1
        //enabled, scale, speed, defaultPath, customPath, isCyclic, val2, alpha, val1--->expandOffset
        add(new String[]{"true","0.4f","2", "/blender/droste1","", "false","20","0.3f","40"});
        add(new String[]{"false","0.6f","2", "/blender/droste2","", "false","20","1.0f","80"});
    }};


    @com.intellij.util.xmlb.annotations.XCollection
    private ArrayList<String[]> musicTriggerSoundDataStringArrays = new ArrayList<String[]>(){{
        //enabled,val1,defaultPath,customPath
        add(new String[]{"true","0","/sounds/music_triggers/trigger1.mp3",""});
        add(new String[]{"true","1","/sounds/music_triggers/trigger2.mp3",""});
    }};

    @com.intellij.util.xmlb.annotations.XCollection
    private ArrayList<String[]> soundDataStringArrays = new ArrayList<String[]>(){{
        //enabled,val1,defaultPath,customPath
        add(new String[]{"true","20","/sounds/h1.mp3",""});
        add(new String[]{"true","20","/sounds/h2.mp3",""});
        add(new String[]{"true","20","/sounds/h3.mp3",""});
        add(new String[]{"true","20","/sounds/h4.mp3",""});
    }};

    @com.intellij.util.xmlb.annotations.XCollection
    private ArrayList<String[]> linkerDataStringArrays = new ArrayList<String[]>(){{
        //enabled, scale, speed, defaultPath, customPath, isCyclic, val2, alpha, val1
        add(new String[]{"true","0.3f","2","/blender/linkerI/chain1","", "false","100","1.0f","1"});
        add(new String[]{"true","0.3f","2","/blender/linkerI/chain5","", "false","6","0.6f","2"});
        add(new String[]{"true","0.2f","2","/blender/linkerI/chain4","", "false","20","1.0f","10"});
    }};

    @com.intellij.util.xmlb.annotations.XCollection
    private ArrayList<String[]> lizardDataStringArrays = new ArrayList<String[]>(){{
        //enabled, scale, speed, defaultPath, customPath, isCyclic, val2, alpha, val1
        add(new String[]{"true","0.4f","2","/blender/lizard","", "false","1","1.0f","2"});
        add(new String[]{"true","0.6f","2","/blender/lizard2","", "false","1","0.6f","2"});
        add(new String[]{"true","0.2f","2","/blender/lizard","", "false","1","1.0f","10"});
    }};

    @com.intellij.util.xmlb.annotations.XCollection
    private ArrayList<String[]> sparkDataStringArrays = new ArrayList<String[]>(){{
        //enabled,scale,weight,defaultPath,customPath
        add(new String[]{"true","1.0f","6","/blender/lightningAlt/spark4/0150.png",""});
        add(new String[]{"true","1.0f","30","/blender/lightningAlt/spark5/0150.png",""});
        add(new String[]{"true","1.0f","80","/blender/lightningAlt/spark6/0150.png",""});
    }};

    @com.intellij.util.xmlb.annotations.XCollection
    private ArrayList<String[]> mandalaDataStringArrays = new ArrayList<String[]>(){{
        //enabled, scale, speed, defaultPath, customPath, isCyclic, val2, alpha, val1
        add(new String[]{"true","1.0f","3","/blender/mandala1/","","false","5", "1.0f", "1"});
        add(new String[]{"true","1.0f","2","/blender/mandala2/","","true","4", "1.0f", "1"});
        add(new String[]{"true","1.0f","2","/blender/mandala3/","","true","5", "1.0f", "1"});
        add(new String[]{"true","1.2f","4","/blender/mandala9/","","false","4", "1.0f", "1"});
    }};

//    @com.intellij.util.xmlb.annotations.XCollection

    @com.intellij.util.xmlb.annotations.MapAnnotation
    private Map<ConfigType,SmartList<String>> pathDataMap = new HashMap<ConfigType,SmartList<String>>(){{
        put(ConfigType.MANDALA, new SmartList<String>(){{
//            SpriteDataAnimated(int previewSize, boolean enabled, float scale, int speedRate, String defaultPath, String customPath,
//            boolean isCyclic, int val2, float alpha, int val1) {
//            SpriteDataAnimated sd = ;
//            add((new SpriteDataAnimated(120,true, 1.0f, 3, "/blender/mandala1/",
//                    "",false,5,1.0f,1)).toJSONObject().toString() );
//            add((new SpriteDataAnimated(120,true, 1.0f, 2, "/blender/mandala2/",
//                    "",true,5,1.0f,1)).toJSONObject().toString() );
//            add((new SpriteDataAnimated(120,true, 1.0f, 2, "/blender/mandala3/",
//                    "",true,5,1.0f,1)).toJSONObject().toString() );
//            add((new SpriteDataAnimated(120,true, 1.0f, 4, "/blender/mandala9/",
//                    "",false,5,1.0f,1)).toJSONObject().toString() );

            add("{\"previewSize\":120,\"customPath\":\"\",\"defaultPath\":\"/blender/mandala1/\",\"val2\":5,\"isCyclic\":false,\"alpha\":1,\"val1\":1,\"scale\":1,\"enabled\":true,\"speedRate\":3}");
            add("{\"previewSize\":120,\"customPath\":\"\",\"defaultPath\":\"/blender/mandala2/\",\"val2\":5,\"isCyclic\":true,\"alpha\":1,\"val1\":1,\"scale\":1,\"enabled\":true,\"speedRate\":2}");
            add("{\"previewSize\":120,\"customPath\":\"\",\"defaultPath\":\"/blender/mandala3/\",\"val2\":5,\"isCyclic\":true,\"alpha\":1,\"val1\":1,\"scale\":1,\"enabled\":true,\"speedRate\":2}");
            add("{\"previewSize\":120,\"customPath\":\"\",\"defaultPath\":\"/blender/mandala9/\",\"val2\":5,\"isCyclic\":false,\"alpha\":1,\"val1\":1,\"scale\":1,\"enabled\":true,\"speedRate\":4}");


//            add(String.join(",",new String[]{"true","1.0f","3","/blender/mandala1/","","false","5", "1.0f", "1"}));
//            add(String.join(",",new String[]{"true","1.0f","2","/blender/mandala2/","","true","4", "1.0f", "1"}));
//            add(String.join(",",new String[]{"true","1.0f","2","/blender/mandala3/","","true","5", "1.0f", "1"}));
//            add(String.join(",",new String[]{"true","1.2f","4","/blender/mandala9/","","false","4", "1.0f", "1"}));
        }});

        put(ConfigType.LIGHTNING_ALT, new SmartList<String>(){{
            //public SpriteData(boolean enabled, float scale, int val1, String defaultPath, String customPath) {
//            add( (new SpriteData(true,1.0f,6,
//                    "/blender/lightningAlt/spark4/0150.png", "")).toJSONObject().toString());
//            add( (new SpriteData(true,1.0f,30,
//                    "/blender/lightningAlt/spark5/0150.png", "")).toJSONObject().toString());
//            add( (new SpriteData(true,1.0f,80,
//                    "/blender/lightningAlt/spark6/0150.png", "")).toJSONObject().toString());

            add("{\"customPath\":\"\",\"defaultPath\":\"/blender/lightningAlt/spark4/0150.png\",\"val1\":6,\"scale\":1,\"enabled\":true}");
            add("{\"customPath\":\"\",\"defaultPath\":\"/blender/lightningAlt/spark5/0150.png\",\"val1\":30,\"scale\":1,\"enabled\":true}");
            add("{\"customPath\":\"\",\"defaultPath\":\"/blender/lightningAlt/spark6/0150.png\",\"val1\":80,\"scale\":1,\"enabled\":true}");


//            add(String.join(",",new String[]{"true","1.0f","6","/blender/lightningAlt/spark4/0150.png",""}));
//            add(String.join(",",new String[]{"true","1.0f","30","/blender/lightningAlt/spark5/0150.png",""}));
//            add(String.join(",",new String[]{"true","1.0f","80","/blender/lightningAlt/spark6/0150.png",""}));
        }});

        put(ConfigType.LIZARD, new SmartList<String>(){{
            add("{\"previewSize\":60,\"customPath\":\"\",\"defaultPath\":\"/blender/lizard\",\"val2\":1,\"isCyclic\":false,\"alpha\":1,\"val1\":2,\"scale\":0.4000000059604645,\"enabled\":true,\"speedRate\":3}");
            add("{\"previewSize\":60,\"customPath\":\"\",\"defaultPath\":\"/blender/lizard2\",\"val2\":1,\"isCyclic\":false,\"alpha\":0.6000000238418579,\"val1\":2,\"scale\":0.6000000238418579,\"enabled\":true,\"speedRate\":3}");
            add("{\"previewSize\":60,\"customPath\":\"\",\"defaultPath\":\"/blender/lizard\",\"val2\":1,\"isCyclic\":false,\"alpha\":1,\"val1\":10,\"scale\":0.20000000298023224,\"enabled\":true,\"speedRate\":3}");
//            add((new SpriteDataAnimated(60,true, 0.4f, 3, "/blender/lizard",
//                    "",false,1,1.0f,2)).toJSONObject().toString() );
//            add((new SpriteDataAnimated(60,true, 0.6f, 3, "/blender/lizard2",
//                    "",false,1,0.6f,2)).toJSONObject().toString() );
//            add((new SpriteDataAnimated(60,true, 0.2f, 3, "/blender/lizard",
//                    "",false,1,1.0f,10)).toJSONObject().toString() );

//            add(String.join(",",new String[]{"true","0.4f","2","/blender/lizard","", "false","1","1.0f","2"}));
//            add(String.join(",",new String[]{"true","0.6f","2","/blender/lizard2","", "false","1","0.6f","2"}));
//            add(String.join(",",new String[]{"true","0.2f","2","/blender/lizard","", "false","1","1.0f","10"}));
        }});

        put(ConfigType.LINKER, new SmartList<String>(){{
//            add((new SpriteDataAnimated(120,true, 0.3f, 2, "/blender/linkerI/chain1",
//                    "",false,100,1.0f,1)).toJSONObject().toString() );
//            add((new SpriteDataAnimated(120,true, 0.3f, 2, "/blender/linkerI/chain5",
//                    "",false,6,0.6f,2)).toJSONObject().toString() );
//            add((new SpriteDataAnimated(120,true, 0.2f, 2, "/blender/linkerI/chain4",
//                    "",false,20,1.0f,10)).toJSONObject().toString() );

            add("{\"previewSize\":60,\"customPath\":\"\",\"defaultPath\":\"/blender/linkerI/chain1\",\"val2\":100,\"isCyclic\":false,\"alpha\":1,\"val1\":1,\"scale\":0.30000001192092896,\"enabled\":true,\"speedRate\":2}");
            add("{\"previewSize\":60,\"customPath\":\"\",\"defaultPath\":\"/blender/linkerI/chain5\",\"val2\":6,\"isCyclic\":false,\"alpha\":0.6000000238418579,\"val1\":2,\"scale\":0.30000001192092896,\"enabled\":true,\"speedRate\":2}");
            add("{\"previewSize\":60,\"customPath\":\"\",\"defaultPath\":\"/blender/linkerI/chain4\",\"val2\":20,\"isCyclic\":false,\"alpha\":1,\"val1\":10,\"scale\":0.20000000298023224,\"enabled\":true,\"speedRate\":2}");

//            add(String.join(",", new String[]{"true","0.3f","2","/blender/linkerI/chain1","", "false","100","1.0f","1"}));
//            add(String.join(",", new String[]{"true","0.3f","2","/blender/linkerI/chain5","", "false","6","0.6f","2"}));
//            add(String.join(",", new String[]{"true","0.2f","2","/blender/linkerI/chain4","", "false","20","1.0f","10"}));
        }});

        put(ConfigType.SOUND, new SmartList<String>(){{
            //public SoundData(boolean enabled, int val1, String defaultPath, String customPath) {
//            add( (new SoundData(true,20,"/sounds/h1.mp3","")).toJSONObject().toString());
//            add( (new SoundData(true,20,"/sounds/h2.mp3","")).toJSONObject().toString());
//            add( (new SoundData(true,20,"/sounds/h3.mp3","")).toJSONObject().toString());
//            add( (new SoundData(true,20,"/sounds/h4.mp3","")).toJSONObject().toString());

            add("{\"customPath\":\"\",\"defaultPath\":\"/sounds/h1.mp3\",\"val1\":20,\"enabled\":true}");
            add("{\"customPath\":\"\",\"defaultPath\":\"/sounds/h2.mp3\",\"val1\":20,\"enabled\":true}");
            add("{\"customPath\":\"\",\"defaultPath\":\"/sounds/h3.mp3\",\"val1\":20,\"enabled\":true}");
            add("{\"customPath\":\"\",\"defaultPath\":\"/sounds/h4.mp3\",\"val1\":20,\"enabled\":true}");

//            add(String.join(",", new String[]{"true","20","/sounds/h1.mp3",""}));
//            add(String.join(",", new String[]{"true","20","/sounds/h2.mp3",""}));
//            add(String.join(",", new String[]{"true","20","/sounds/h3.mp3",""}));
//            add(String.join(",", new String[]{"true","20","/sounds/h4.mp3",""}));
        }});

        put(ConfigType.MUSIC_TRIGGER, new SmartList<String>(){{
//            add( (new SoundData(true,0,"/sounds/music_triggers/trigger1.mp3","")).toJSONObject().toString());
//            add( (new SoundData(true,1,"/sounds/music_triggers/trigger2.mp3","")).toJSONObject().toString());
            add("{\"customPath\":\"\",\"defaultPath\":\"/sounds/music_triggers/trigger1.mp3\",\"val1\":0,\"enabled\":true}");
            add("{\"customPath\":\"\",\"defaultPath\":\"/sounds/music_triggers/trigger2.mp3\",\"val1\":1,\"enabled\":true}");

//            add(String.join(",", new String[]{"true","0","/sounds/music_triggers/trigger1.mp3",""}));
//            add(String.join(",", new String[]{"true","1","/sounds/music_triggers/trigger2.mp3",""}));
        }});

        put(ConfigType.DROSTE, new SmartList<String>(){{

            add("{\"previewSize\":120,\"customPath\":\"\",\"defaultPath\":\"/blender/droste1\",\"val2\":20,\"isCyclic\":false,\"alpha\":0.30000001192092896,\"val1\":40,\"scale\":0.4000000059604645,\"enabled\":true,\"speedRate\":2}");
            add("{\"previewSize\":120,\"customPath\":\"\",\"defaultPath\":\"/blender/droste2\",\"val2\":20,\"isCyclic\":false,\"alpha\":1,\"val1\":80,\"scale\":0.6000000238418579,\"enabled\":false,\"speedRate\":2}");
//            add((new SpriteDataAnimated(120,true, 0.4f, 2, "/blender/droste1",
//                    "",false,20,0.3f,40)).toJSONObject().toString() );
//            add((new SpriteDataAnimated(120,false, 0.6f, 2, "/blender/droste2",
//                    "",false,20,1.0f,80)).toJSONObject().toString() );

//            add(String.join(",", new String[]{"true","0.4f","2", "/blender/droste1","", "false","20","0.3f","40"}));
//            add(String.join(",", new String[]{"false","0.6f","2", "/blender/droste2","", "false","20","1.0f","80"}));
        }});

        put(ConfigType.COPYPASTEVOID, new SmartList<String>(){{
            add("{\"previewSize\":60,\"customPath\":\"\",\"defaultPath\":\"/blender/droste1\",\"val2\":1,\"isCyclic\":false,\"alpha\":1,\"val1\":2,\"scale\":0.4000000059604645,\"enabled\":true,\"speedRate\":2}");
            add("{\"previewSize\":60,\"customPath\":\"\",\"defaultPath\":\"/blender/droste2\",\"val2\":1,\"isCyclic\":false,\"alpha\":0.6000000238418579,\"val1\":2,\"scale\":0.6000000238418579,\"enabled\":true,\"speedRate\":2}");

//            add((new SpriteDataAnimated(60,true, 0.4f, 2, "/blender/droste1",
//                    "",false,1,1.0f,2)).toJSONObject().toString() );
//            add((new SpriteDataAnimated(60,true, 0.6f, 2, "/blender/droste2",
//                    "",false,1,0.6f,2)).toJSONObject().toString() );

//            add(String.join(",", new String[]{"true","0.4f","2","/blender/droste2","", "false","1","1.0f","2"}));
//            add(String.join(",", new String[]{"true","0.6f","2","/blender/droste2","", "false","1","0.6f","2"}));
        }});

//        put(ConfigType.MUSIC_TRIGGER, new ArrayList<String[]>(){{
//
//
//        }});
    }};

    //consider JSON https://stackabuse.com/reading-and-writing-json-in-java/ ??
    @com.intellij.util.xmlb.annotations.MapAnnotation  //this tells it to copy its inner values, wont serialize without it
    private Map<String,String> configMap = new HashMap<String,String>(){{

        put("sprite"+ ConfigType.BASIC_PARTICLE + "Enabled", "true");
        put("sprite"+ ConfigType.LIGHTNING + "Enabled", "false");
        put("sprite"+ ConfigType.LIGHTNING_ALT + "Enabled", "false");
        put("sprite"+ ConfigType.LIZARD + "Enabled", "false");


        put("sprite"+ ConfigType.MOMA+ "Enabled", "false");

        put("sprite"+ ConfigType.VINE + "Enabled", "false");
        put("sprite"+ ConfigType.MANDALA + "Enabled", "false");
        put("sprite"+ ConfigType.LINKER + "Enabled", "false");
        put("sprite"+ ConfigType.DROSTE + "Enabled", "false");
        put("sprite"+ ConfigType.COPYPASTEVOID + "Enabled", "true");
    }};



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










    public static PowerMode3 getInstance() {
        return ApplicationManager.getApplication().getComponent(PowerMode3.class);
    }

    @Override
    public void initializeComponent() {

        //Setup SOUND handler
        final EditorActionManager actionManager = EditorActionManager.getInstance();
//        final TypedAction typedAction0 = actionManager.getTypedAction();
//        typedAction0.setupHandler(new SoundTypedActionHandler(typedAction0));

        final TypedAction typedAction2 = actionManager.getTypedAction();
        TypedActionHandler rawHandler2 = typedAction2.getRawHandler();
        typedAction2.setupRawHandler(new TypedActionHandler() {
                                      @Override
                                      public void execute(@NotNull Editor editor, char c, @NotNull DataContext dataContext) {
//                                          PowerMode3 settings = PowerMode3.getInstance();
                                          if(PowerMode3.this.isEnabled()) {

                                              if (SoundConfig.SOUND_ENABLED(PowerMode3.this)) {
                                                  int winner = SoundData.getWeightedAmountWinningIndex(SoundConfig.soundData);
//                                              int r = ThreadLocalRandom.current().nextInt(0, SoundConfig.soundData.size());
                                                  if( winner != -1){
                                                      //TODO refactor to just return object or null
                                                      SoundData d = SoundConfig.soundData.get(winner);
                                                      Sound s = new Sound(d.getPath(), !d.customPathValid);
                                                      s.play();
                                                  }
                                              }

                                          }

                                          rawHandler2.execute(editor,c,dataContext);
                                      }
                                  });


//        this.setupCursorMovementAction();
        this.setupExtraEditorActions();



        this.particleColor = new JBColor(new Color(this.getParticleRGB()), new Color(this.getParticleRGB()));

        final EditorActionManager editorActionManager = EditorActionManager.getInstance();
        final EditorFactory editorFactory = EditorFactory.getInstance();
        particleContainerManager = new ParticleContainerManager();
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


                PsiFile psiFile = dataContext.getData(CommonDataKeys.PSI_FILE);

                updateEditor(editor, psiFile);
                rawHandler.execute(editor, c, dataContext);
            }
        });
    }

    private void updateEditor(@NotNull final Editor editor, final PsiFile psiFile) {
        //TODO configurable
        if(psiFile == null){

        }else {
            particleContainerManager.update(editor, psiFile);
        }
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
    public void loadState(@NotNull PowerMode3 state) {
        LOGGER.info("previous state found -- setting up...");

        XmlSerializerUtil.copyBean(state, this);
        loadConfigData();
    }

    @com.intellij.util.xmlb.annotations.Transient
    public boolean isConfigLoaded = false;

    // for use with experimental editor onCreated in particleManager
//    @com.intellij.util.xmlb.annotations.Transient
//    public boolean isLoading = false;

    public void loadConfigData(){
        boolean DO_BACKGROUND_LOAD = true;
//        boolean DO_BACKGROUND_LOAD = false;

        if(DO_BACKGROUND_LOAD) {
            Task.Backgroundable bgTask = new Task.Backgroundable(null, "Zeranthium Setup...",
                    false, null) {
//            Task.Modal bgTask = new Task.Modal(null, "Zeranthium Setup...",
//                    false) {
                @Override
                public void run(@NotNull ProgressIndicator progressIndicator) {
                    loadConfigDataAsync(progressIndicator);
                }
//            @Override
//            public boolean shouldStartInBackground() {
//                return true;
//            }
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

            setUpdateProgress(progressIndicator, "mandala", 0.2);
            Mandala2Config.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.MANDALA)));
            setUpdateProgress(progressIndicator, "lizard", 0.3);
            LizardConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.LIZARD)));
            setUpdateProgress(progressIndicator, "linker", 0.4);
            LinkerConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.LINKER)));
            setUpdateProgress(progressIndicator, "Droste", 0.5);
            DrosteConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.DROSTE)));
            setUpdateProgress(progressIndicator, "Copypaste", 0.6);
            CopyPasteVoidConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.COPYPASTEVOID)));

            setUpdateProgress(progressIndicator, "Sounds", 0.8);
            SoundConfig.setSoundData(this.deserializeSoundData(pathDataMap.get(ConfigType.SOUND)));
            MusicTriggerConfig.setSoundData(this.deserializeSoundData(pathDataMap.get(ConfigType.MUSIC_TRIGGER)));


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

            Mandala2Config.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.MANDALA)));
            LizardConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.LIZARD)));
            LinkerConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.LINKER)));
            DrosteConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.DROSTE)));
            CopyPasteVoidConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(pathDataMap.get(ConfigType.COPYPASTEVOID)));

            SoundConfig.setSoundData(this.deserializeSoundData(pathDataMap.get(ConfigType.SOUND)));
            MusicTriggerConfig.setSoundData(this.deserializeSoundData(pathDataMap.get(ConfigType.MUSIC_TRIGGER)));
        }
        this.isConfigLoaded = true;

//        if(!this.isConfigLoaded) {
//            LightningAltConfig.setSparkData(this.deserializeSpriteData(sparkDataStringArrays));
//            Mandala2Config.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(mandalaDataStringArrays, 120));
//            LizardConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(lizardDataStringArrays, 60));
//            LinkerConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(linkerDataStringArrays, 60));
//            DrosteConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(drosteDataStringArrays, 120));
//            CopyPasteVoidConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(copyPasteVoidDataStringArrays, 60));
//
//            SoundConfig.setSoundData(this.deserializeSoundData(soundDataStringArrays));
//            MusicTriggerConfig.setSoundData(this.deserializeSoundData(musicTriggerSoundDataStringArrays));
//        }
//        this.isConfigLoaded = true;
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



    @Override
    public void noStateLoaded() {
        LOGGER.info("NO State loaded previously");
        this.setParticleRGB(JBColor.darkGray.getRGB());


        loadConfigData();
    }

    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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



    private void setupExtraEditorActions(){
        final EditorActionManager actionManager = EditorActionManager.getInstance();


        String[] caret_movement = new String[]{
                IdeActions.ACTION_EDITOR_PASTE};

        for(String s : caret_movement){
            EditorActionHandler origHandler = actionManager.getActionHandler(s);

            MyPasteHandler myPasteHandler = new MyPasteHandler(origHandler);

            actionManager.setActionHandler(s, myPasteHandler);
        }
    }

    private void setupCursorMovementAction(){
        final EditorActionManager actionManager = EditorActionManager.getInstance();


        String[] caret_movement = new String[]{
                IdeActions.ACTION_EDITOR_MOVE_CARET_DOWN,
                IdeActions.ACTION_EDITOR_MOVE_CARET_UP,
                IdeActions.ACTION_EDITOR_MOVE_CARET_LEFT,
                IdeActions.ACTION_EDITOR_MOVE_CARET_RIGHT};

        for(String s : caret_movement){
            EditorActionHandler origHandler = actionManager.getActionHandler(s);
            EditorActionHandler h1 = new EditorActionHandler() {
                @Override
                protected void doExecute(@NotNull Editor editor, @Nullable Caret caret, DataContext dataContext) {
                    super.doExecute(editor, caret, dataContext);



                    origHandler.execute(editor,caret,dataContext);
                    LOGGER.info(s);
                    VisualPosition visualPosition = editor.getCaretModel().getVisualPosition();
                    Point point = editor.visualPositionToXY(visualPosition);
                    ScrollingModel scrollingModel = editor.getScrollingModel();
                    point.x = point.x - scrollingModel.getHorizontalScrollOffset();
                    point.y = point.y - scrollingModel.getVerticalScrollOffset();

                    ParticleSpriteDroste.cursorX = point.x;
                    ParticleSpriteDroste.cursorY = point.y;
                }
            };
            actionManager.setActionHandler(s, h1);
        }


    }
}

