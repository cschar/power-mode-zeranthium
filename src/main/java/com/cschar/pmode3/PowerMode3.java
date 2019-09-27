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
import com.intellij.openapi.components.BaseComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.actionSystem.*;
import com.intellij.psi.PsiFile;
import com.intellij.ui.JBColor;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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


    @com.intellij.util.xmlb.annotations.Transient
    public boolean isConfigLoaded = false;



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
        put("sprite"+ ConfigType.LINKER + "Enabled", "true");
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




                //java.lang.ClassCastException: class com.intellij.ide.actions.PasteAction cannot be cast to class com.intellij.openapi.editor.actionSystem.EditorAction (com.intellij.ide.actions.PasteAction and com.intellij.openapi.editor.actionSystem.EditorAction are in unnamed module of
                //actionManager.setActionHandler(IdeActions.ACTION_PASTE, h1);

//        EditorActionHandler h = new EditorActionHandler() {
//            @Override
//            public boolean isEnabled(Editor editor, DataContext dataContext) {
//                return super.isEnabled(editor, dataContext);
//            }
//        };
//        PasteHandler ph = new MyPasteActionHandler(h);
//        actionManager.setActionHandler(IdeActions.ACTION_PASTE, ph);


//        EditorFactory.getInstance().getEventMulticaster().addCaretListener();
//        EditorFactory.getInstance().getEventMulticaster().addEditorMouseMotionListener();
//        EditorFactory.getInstance().getEventMulticaster().addSelectionListener();


                //https://upsource.jetbrains.com/idea-ce/file/idea-ce-fb93870b390e3a0e4b2b3c15363651714a4f963b/platform/platform-api/src/com/intellij/openapi/editor/actionSystem/EditorActionManager.java
//        EditorActionHandler commentblockhandler = actionManager.getActionHandler(IdeActions.ACTION_COMMENT_BLOCK);


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

                //https://intellij-support.jetbrains.com/hc/en-us/community/posts/206150409-How-do-I-get-to-PSI-from-a-document-or-editor-
                //ALternative way to get Psifile directly from editor
//                final EditorEx editor = (EditorEx) CommonDataKeys.EDITOR.getData(e.getDataContext());
//                if (editor != null) {
//                    final Project project = editor.getProject();
//                    if (project != null) {
//                        final PsiFile psiFile = PsiManager.getInstance(project).findFile(editor.getVirtualFile());
//                        if (psiFile != null) {
//                            final CaretModel caretModel = editor.getCaretModel();
//                            PsiElement element = psiFile.findElementAt(caretModel.getOffset());
//                        }
//                    }
//                }

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
        LOGGER.info("prevous state found -- setting up...");

        XmlSerializerUtil.copyBean(state, this);

//        loadConfigData();
    }

    public void loadConfigData(){
        if(!this.isConfigLoaded) {
            LightningAltConfig.setSparkData(this.deserializeSpriteData(sparkDataStringArrays));
            Mandala2Config.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(mandalaDataStringArrays, 120));
            LizardConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(lizardDataStringArrays, 60));
            LinkerConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(linkerDataStringArrays, 60));
            DrosteConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(drosteDataStringArrays, 120));
            CopyPasteVoidConfig.setSpriteDataAnimated(this.deserializeSpriteDataAnimated(copyPasteVoidDataStringArrays, 60));

            SoundConfig.setSoundData(this.deserializeSoundData(soundDataStringArrays));
            MusicTriggerConfig.setSoundData(this.deserializeSoundData(musicTriggerSoundDataStringArrays));
        }
        this.isConfigLoaded = true;
    }


    public ArrayList<SpriteData> deserializeSpriteData(ArrayList<String[]> target){
        ArrayList<SpriteData> sd = new ArrayList<SpriteData>();
        for(String[] s: target){
            sd.add(new SpriteData(Boolean.parseBoolean(s[0]), Float.parseFloat(s[1]), Integer.parseInt(s[2]),
                     s[3], s[4]));
        }
        return sd;
    }

    public void setSerializedSparkData(ArrayList<SpriteData> sparkData){
        ArrayList<String[]> serialized = new ArrayList<>();
        for( SpriteData d: sparkData){
               serialized.add(new String[]{String.valueOf(d.enabled), String.valueOf(d.scale), String.valueOf(d.val1),
                       String.valueOf(d.defaultPath), String.valueOf(d.customPath)});
        }
        this.sparkDataStringArrays = serialized;
    }

    public ArrayList<SpriteDataAnimated> deserializeSpriteDataAnimated(ArrayList<String[]> target, int previewSize){
        LOGGER.info("deserializing sprite data animated");
        ArrayList<SpriteDataAnimated> sd = new ArrayList<SpriteDataAnimated>();
        for(String[] s: target){
            //TODO : jsut serialize the previewSize lol
            sd.add(new SpriteDataAnimated(previewSize, Boolean.parseBoolean(s[0]),
                                          Float.parseFloat(s[1]),
                                          Integer.parseInt(s[2]),
                                          s[3], s[4],
                                          Boolean.parseBoolean(s[5]),
                                          Integer.parseInt(s[6]),
                                          Float.parseFloat(s[7]),
                                          Integer.parseInt(s[8])));
        }
        return sd;
    }

    public void setSerializedSpriteDataAnimated(ArrayList<SpriteDataAnimated> spriteData, ConfigType type){
//        try {
            ArrayList<String[]> serialized = new ArrayList<>();
            for (SpriteDataAnimated d : spriteData) {
                serialized.add(new String[]{String.valueOf(d.enabled), String.valueOf(d.scale), String.valueOf(d.speedRate),
                        String.valueOf(d.defaultPath), String.valueOf(d.customPath),
                        String.valueOf(d.isCyclic), String.valueOf(d.val2),
                        String.valueOf(d.alpha), String.valueOf(d.val1)});
            }
            if (type == ConfigType.MANDALA) {
                this.mandalaDataStringArrays = serialized;
            } else if (type == ConfigType.LIZARD) {
                this.lizardDataStringArrays = serialized;
            } else if (type == ConfigType.LINKER) {
                this.linkerDataStringArrays = serialized;
            }else if( type == ConfigType.DROSTE){
                this.drosteDataStringArrays = serialized;
            }else if( type == ConfigType.COPYPASTEVOID){
                this.copyPasteVoidDataStringArrays = serialized;
            }
//        }
    }

    public void setSerializedSoundData(ArrayList<SoundData> soundData, ConfigType type){
        ArrayList<String[]> serialized = new ArrayList<>();
        for (SoundData d : soundData) {
            serialized.add(new String[]{String.valueOf(d.enabled), String.valueOf(d.val1),
                    String.valueOf(d.defaultPath), String.valueOf(d.customPath)});
        }
        if (type == ConfigType.SOUND) {
            this.soundDataStringArrays = serialized;
        }else if(type == ConfigType.MUSIC_TRIGGER){
            this.musicTriggerSoundDataStringArrays = serialized;
        }
    }

    public ArrayList<SoundData> deserializeSoundData(ArrayList<String[]> target){
        ArrayList<SoundData> sd = new ArrayList<>();
        for(String[] s: target){
            sd.add(new SoundData(Boolean.parseBoolean(s[0]), Integer.parseInt(s[1]), s[2], s[3]));
        }
        return sd;
    }

    @Override
    public void noStateLoaded() {
        LOGGER.info("NO State loaded previously");
        this.setParticleRGB(JBColor.darkGray.getRGB());


//        loadConfigData();
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

