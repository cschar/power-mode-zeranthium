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
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.editor.actionSystem.TypedAction;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


//https://www.jetbrains.org/intellij/sdk/docs/basics/persisting_state_of_components.html#defining-the-storage-location
@State(
        name = "PowerMode3",
        storages = {@Storage(value = "$APP_CONFIG$/power.mode3.xml")}
        //storages = {@Storage(com.cschar.pmode3.xml")}
)
public class PowerMode3 implements BaseComponent,
        PersistentStateComponent<PowerMode3.State> {
    //https://www.jetbrains.org/intellij/sdk/docs/basics/persisting_state_of_components.html#implementing-the-persistentstatecomponent-interface

    @com.intellij.util.xmlb.annotations.Transient
    private ParticleContainerManager particleContainerManager;

    private boolean enabled = true;

    public static PowerMode3 getInstance() {
        return ApplicationManager.getApplication().getComponent(PowerMode3.class);
    }

    @Override
    public void initComponent() {

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
                updateEditor(editor);
                rawHandler.execute(editor, c, dataContext);
            }
        });
    }

    private void updateEditor(@NotNull final Editor editor) {
        //TODO configurable
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
        return "PowerMode";
    }


//    @Nullable
//    @Override
//    public PowerMode3 getState() {
//        return this;
//    }
//
//    @Override
//    public void loadState(PowerMode3 state) {
//        XmlSerializerUtil.copyBean(state, this);
//    }

    public boolean isEnabled() {
        return enabled;
    }


    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    static class State {
        public String value;
        public int lifetime=15;
    }

    State myState;


    @Nullable
    @Override
    public PowerMode3.State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull PowerMode3.State state) {
        myState = state;
    }
}

