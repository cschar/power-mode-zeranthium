package com.cschar.pmode3;

import com.cschar.pmode3.actionHandlers.MyPasteHandler;
import com.cschar.pmode3.actionHandlers.MySpecialActionHandler;
import com.cschar.pmode3.config.SpecialActionSoundConfig;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

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
public class PowerMode3StartupActivity implements StartupActivity {
    private static final Logger LOGGER = Logger.getLogger(PowerMode3StartupActivity.class.getName());

    @Override
    public void runActivity(@NotNull Project project) {
        //force lazy loading of service
        PowerMode3.getInstance();

        setupActionEditorKeys();
    }

    public void setupActionEditorKeys() {
        LOGGER.info("setting actionEditorKeys...");

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
