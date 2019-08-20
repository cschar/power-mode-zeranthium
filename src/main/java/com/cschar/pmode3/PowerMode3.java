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


import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.BaseComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.editor.actionSystem.TypedAction;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.psi.PsiFile;
import com.intellij.ui.JBColor;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;


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

    @com.intellij.util.xmlb.annotations.Transient
    private ParticleContainerManager particleContainerManager;

    @com.intellij.util.xmlb.annotations.Transient
    Color particleColor;


    private int scrollBarPosition = 0;
//    public String scrollBarPosition2 = "0";

    private boolean enabled = true;
    private int shakeDistance = 5;
    private int numOfParticles = 7;
    private int lifetime = 200;
    private int particleSize = 3;
    private int maxPsiSearchDistance = 200;  //amount of total characters searched around caret for anchors

    private int particleRGB;




    public enum SpriteType{
        LIGHTNING,
        LIGHTNING_ALT,
        LIZARD,
        MOMA,
        VINE
    }

    //consider JSON https://stackabuse.com/reading-and-writing-json-in-java/ ??
    @com.intellij.util.xmlb.annotations.MapAnnotation  //this tells it to copy its inner values, wont serialize without it
    private Map<String,String> configMap = new HashMap<String,String>(){{
        put("basicParticleEnabled", "true");
        put("sprite"+ SpriteType.LIGHTNING + "Enabled", "false");
        put("sprite"+ SpriteType.LIGHTNING_ALT + "Enabled", "false");
        put("sprite"+ SpriteType.LIZARD + "Enabled", "false");


        put("sprite"+ SpriteType.MOMA+ "Enabled", "false");
        put(String.format("sprite%sEmitTop", SpriteType.MOMA), "true");
        put(String.format("sprite%sEmitBottom", SpriteType.MOMA), "true");

        put("sprite"+ SpriteType.VINE + "Enabled", "false");
    }};


    //good lord
    boolean[] getSpriteTypeDirections(SpriteType type){
        boolean[] directions = new boolean[]{
                Boolean.parseBoolean(configMap.get("sprite" + type + "EmitTop")),
                Boolean.parseBoolean(configMap.get("sprite" + type + "EmitBottom"))
        };
        return directions;
    }
    void setSpriteTypeDirections(SpriteType type, Boolean top, Boolean bottom){
        configMap.put(String.format("sprite%sEmitTop", type), top.toString());
        configMap.put(String.format("sprite%sEmitBottom", type), bottom.toString());
    }

    void setSpriteTypeEnabled(Boolean enabled, SpriteType type){
        configMap.put("sprite" + type + "Enabled", enabled.toString());
    }
    boolean getSpriteTypeEnabled(SpriteType type){
        return Boolean.parseBoolean(configMap.get("sprite"+type+"Enabled"));
    }

    //LOMBOK would help here... wouldnt have to do these pesky getter/setters
    void setBasicParticleEnabled(Boolean enabled){
        configMap.put("basicParticleEnabled", enabled.toString());
    }
    boolean getBasicParticleEnabled(){
        return Boolean.parseBoolean(configMap.get("basicParticleEnabled"));
    }

    public void setSpriteTypeProperty(SpriteType type, String property, String value){
        configMap.put(String.format("sprite%s_%s", type, property), value);
    }

    public String getSpriteTypeProperty(SpriteType type, String property){
        return configMap.get(String.format("sprite%s_%s", type, property));
    }










    public static PowerMode3 getInstance() {
        return ApplicationManager.getApplication().getComponent(PowerMode3.class);
    }

    @Override
    public void initializeComponent() {

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

    private void updateEditor(@NotNull final Editor editor, @NotNull final PsiFile psiFile) {
        //TODO configurable
        particleContainerManager.update(editor, psiFile);
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
        System.out.println("prevous state found -- setting up...");

//        Color c = new Color(particleRGB);
//        this.setParticleColor(c);
        XmlSerializerUtil.copyBean(state, this);

//        this.particleColor = JBColor.darkGray;
    }



    @Override
    public void noStateLoaded() {
//        this.setParticleColor(c);
        System.out.println("NO State loaded previously");
         this.setParticleRGB(JBColor.darkGray.getRGB());
    }

    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    public int getLifetime() { return lifetime; }
    public void setLifetime(int l) { lifetime=l;}
    public int getParticleSize() { return particleSize;}
    public void setParticleSize(int p) {particleSize=p;}

    public int getParticleRGB() {     return particleRGB; }
    public void setParticleRGB(int particleRGB) {
        this.particleRGB = particleRGB;
        this.particleColor = new JBColor(new Color(particleRGB), new Color(particleRGB));
    }

    public int getMaxPsiSearchDistance() {  return maxPsiSearchDistance; }
    public void setMaxPsiSearchDistance(int maxPsiSearchDistance) {this.maxPsiSearchDistance = maxPsiSearchDistance;}

    public int getNumOfParticles() {  return numOfParticles;   }
    public void setNumOfParticles(int numOfParticles) { this.numOfParticles = numOfParticles;  }
    public int getShakeDistance() {  return shakeDistance;  }
    public void setShakeDistance(int shakeDistance) {   this.shakeDistance = shakeDistance;  }

    public int getScrollBarPosition() {   return scrollBarPosition;  }
    public void setScrollBarPosition(int scrollBarPosition) {  this.scrollBarPosition = scrollBarPosition; }
}

