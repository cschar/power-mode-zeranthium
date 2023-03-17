package com.cschar.pmode3;

import com.cschar.pmode3.actionHandlers.MyPasteHandler;
import com.cschar.pmode3.actionHandlers.MySpecialActionHandler;
import com.cschar.pmode3.config.SpecialActionSoundConfig;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.project.Project;
//import com.intellij.openapi.startup.ProjectActivity;
import com.intellij.openapi.startup.ProjectActivity;
import com.intellij.openapi.startup.StartupActivity;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.diagnostic.Logger;
//import java.util.logging.Logger;

/**
 *
 * <p> This class solves an error when we try and initialize actionEditorHandler stuff in the initializeComponent method...
 *
 *  Error was:
 *  Should be called at least in the state COMPONENTS_LOADED, the current state is: CONFIGURATION_STORE_INITIALIZED
 *  Current violators count: 1
 *
 * alternative way to add this in the initializeComponent() method of the Component:
 * //        ApplicationManager.getApplication().invokeLater(() -> {
 * //            ApplicationManager.getApplication().executeOnPooledThread(this::setupActionEditorKeys);
 * //        });
 *
 *
 *    this class is triggered by adding it as a definiton in plugin.xml
 *         <extensions defaultExtensionNs="com.intellij">
 *              ...
 *              <postStartupActivity implementation="com.cschar.pmode3.PowerMode3StartupActivity"/>
 *            </extensions>
 *
 */
public class PowerMode3StartupActivity implements ProjectActivity {
    private static final Logger LOGGER = Logger.getInstance(PowerMode3StartupActivity.class);



//    In EAP
    @Nullable
    @Override
    public Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        LOGGER.debug(" ====== Loading Startup Activity.. ======= ");
        PowerMode3 p = PowerMode3.getInstance();
        ApplicationManager.getApplication().getService(PowerMode3.class);

        p.startup1 = this;
        setupActionEditorKeys();
        return null;
    }

    private EditorActionHandler pasteHandler;
    private EditorActionHandler copyHandler;
    private EditorActionHandler deleteHandler;
    private EditorActionHandler backspaceHandler;
    private EditorActionHandler enterHandler;

    public void teardownActionEditorKeys() {
        LOGGER.debug("unsetting actionEditorKeys...");

        final EditorActionManager actionManager = EditorActionManager.getInstance();

        actionManager.setActionHandler(IdeActions.ACTION_EDITOR_COPY, copyHandler);
        actionManager.setActionHandler(IdeActions.ACTION_EDITOR_PASTE, pasteHandler);
        actionManager.setActionHandler(IdeActions.ACTION_EDITOR_DELETE, deleteHandler);
        actionManager.setActionHandler(IdeActions.ACTION_EDITOR_BACKSPACE, backspaceHandler);
        actionManager.setActionHandler(IdeActions.ACTION_EDITOR_ENTER, enterHandler);


    }



    public void setupActionEditorKeys() {
        LOGGER.debug("setting actionEditorKeys...");

        final EditorActionManager actionManager = EditorActionManager.getInstance();
        MySpecialActionHandler h1;
        EditorActionHandler origHandler;

        //COPYPASTEVOID
        pasteHandler = actionManager.getActionHandler(IdeActions.ACTION_EDITOR_PASTE);
        MyPasteHandler myPasteHandler = new MyPasteHandler(pasteHandler);
        actionManager.setActionHandler(IdeActions.ACTION_EDITOR_PASTE, myPasteHandler);

        //SPECIAL_ACTION_SOUND
        //Paste reuses Copypastevoid above
        origHandler = actionManager.getActionHandler(IdeActions.ACTION_EDITOR_PASTE);
        h1 = new MySpecialActionHandler(origHandler, SpecialActionSoundConfig.KEYS.PASTE);
        actionManager.setActionHandler(IdeActions.ACTION_EDITOR_PASTE, h1);

        copyHandler = actionManager.getActionHandler(IdeActions.ACTION_EDITOR_COPY);
        h1 = new MySpecialActionHandler(copyHandler, SpecialActionSoundConfig.KEYS.COPY);
        actionManager.setActionHandler(IdeActions.ACTION_EDITOR_COPY, h1);



        deleteHandler = actionManager.getActionHandler(IdeActions.ACTION_EDITOR_DELETE);
        h1 = new MySpecialActionHandler(origHandler, SpecialActionSoundConfig.KEYS.DELETE);
        actionManager.setActionHandler(IdeActions.ACTION_EDITOR_DELETE, h1);

        backspaceHandler = actionManager.getActionHandler(IdeActions.ACTION_EDITOR_BACKSPACE);
        h1 = new MySpecialActionHandler(origHandler, SpecialActionSoundConfig.KEYS.BACKSPACE);
        actionManager.setActionHandler(IdeActions.ACTION_EDITOR_BACKSPACE, h1);

        enterHandler = actionManager.getActionHandler(IdeActions.ACTION_EDITOR_ENTER);
        h1 = new MySpecialActionHandler(origHandler, SpecialActionSoundConfig.KEYS.ENTER);
        actionManager.setActionHandler(IdeActions.ACTION_EDITOR_ENTER, h1);


    }


}
