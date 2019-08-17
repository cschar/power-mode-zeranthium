package com.cschar.pmode3;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.components.BaseComponent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.editor.actionSystem.TypedAction;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import org.jetbrains.annotations.NotNull;

public class MyBaseComponent implements BaseComponent {
    //http://www.jetbrains.org/intellij/sdk/docs/basics/persisting_state_of_components.html
    //http://www.jetbrains.org/intellij/sdk/docs/basics/plugin_structure/plugin_components.html#plugin-components-lifecycle

    @Override
    public void initComponent() {

        final EditorActionManager editorActionManager = EditorActionManager.getInstance();
        final EditorFactory editorFactory = EditorFactory.getInstance();

        final TypedAction typedAction = editorActionManager.getTypedAction();
        final TypedActionHandler rawHandler = typedAction.getRawHandler();

        typedAction.setupRawHandler(new TypedActionHandler() {
            @Override
            public void execute(@NotNull final Editor editor, final char c, @NotNull final DataContext dataContext) {
                myUpdateEditor(editor);
                rawHandler.execute(editor, c, dataContext);
                rawHandler.execute(editor, 'C', dataContext);
            }
        });
    }

    private void myUpdateEditor(@NotNull final Editor editor) {
        //TODO configurable
        System.out.println("OUT");
        //particleContainerManager.update(editor);
    }


    @Override
    public void disposeComponent() {
        //particleContainerManager.dispose();
        //particleContainerManager = null;
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "PowerMode";
    }
}
