// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.cschar.pmode3.uitest;

import com.cschar.pmode3.uitest.pages.IdeaFrame;
import com.cschar.pmode3.uitest.steps.JavaExampleSteps;
import com.cschar.pmode3.uitest.utils.RemoteRobotExtension;
import com.cschar.pmode3.uitest.utils.StepsLogger;
import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.fixtures.ContainerFixture;
import com.intellij.remoterobot.utils.Keyboard;
import org.assertj.swing.core.MouseButton;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.ThreadLocalRandom;

import static com.intellij.remoterobot.search.locators.Locators.byXpath;
import static com.intellij.remoterobot.stepsProcessing.StepWorkerKt.step;
import static com.intellij.remoterobot.utils.RepeatUtilsKt.waitFor;
import static java.time.Duration.ofMinutes;
import static com.cschar.pmode3.uitest.pages.ActionMenuFixtureKt.actionMenu;
import static com.cschar.pmode3.uitest.pages.ActionMenuFixtureKt.actionMenuItem;
import static com.cschar.pmode3.uitest.pages.EditorKt.editor;

@ExtendWith(RemoteRobotExtension.class)
@Disabled
@EnabledIfEnvironmentVariable(named = "TEST_TYPE", matches = "UI")
public class WriteTextJavaTest {

    private final RemoteRobot remoteRobot = new RemoteRobot("http://127.0.0.1:8082");
    private final JavaExampleSteps sharedSteps = new JavaExampleSteps(remoteRobot);
    private final Keyboard keyboard = new Keyboard(remoteRobot);

    @BeforeAll
    public static void initLogging() {
        StepsLogger.init();
    }

    @AfterEach
    public void closeProject(final RemoteRobot remoteRobot) {
//        step("Close the project", () -> {
//            if (remoteRobot.isMac()) {
//                keyboard.hotKey(VK_SHIFT, VK_META, VK_A);
//                keyboard.enterText("Close Project");
//                keyboard.enter();
//            } else {
//                actionMenu(remoteRobot, "File").click();
//                actionMenuItem(remoteRobot, "Close Project").click();
//            }
//        });
    }

    @Test
    void writeSomeText(final RemoteRobot remoteRobot) {
//        sharedSteps.createNewCommandLineProject();
//        sharedSteps.closeTipOfTheDay();

        final IdeaFrame idea = remoteRobot.find(IdeaFrame.class);
        waitFor(ofMinutes(5), () -> !idea.isDumbMode());

        int r = ThreadLocalRandom.current().nextInt(1,100000);
        String appName = "App" + r;

        step("Create New Kotlin file", () -> {
            final ContainerFixture projectView = idea.getProjectViewTree();
            if (!projectView.hasText("src")) {
                projectView.findText(idea.getProjectName()).doubleClick();
                waitFor(() -> projectView.hasText("src"));
            }
            projectView.findText("src").click(MouseButton.RIGHT_BUTTON);
            actionMenu(remoteRobot, "New").click();
            actionMenuItem(remoteRobot, "Kotlin Class/File").click();


            keyboard.enterText(appName);
            keyboard.down();
            keyboard.enter();
        });

        final ContainerFixture editor = editor(idea, appName+".kt");

        step("Write a code", () -> {
            //simulate autocomplete step
//            keyboard.enterText("main");
//            keyboard.enter();

            sharedSteps.autocomplete("main");
            keyboard.enterText("println(\"Hello from UI test\");");
            keyboard.enter();


            for(int i =0; i<4; i++) {
                keyboard.enter();
            }

            keyboard.enterText("for(i in 1..5){");
            keyboard.enter();
            keyboard.enterText("println(i)");
            keyboard.enter();


        });

//        step("Launch the application", () -> {
//            editor.findText("main").click(MouseButton.RIGHT_BUTTON);
//            idea.find(ComponentFixture.class,
//                    byXpath("//div[@class='ActionMenuItem' and @disabledicon='execute.svg']")
//            ).click();
//        });
//        step("Check console output", () -> {
//            final Locator locator = byXpath("//div[@class='ConsoleViewImpl']");
//            waitFor(ofMinutes(1), () -> idea.findAll(ContainerFixture.class, locator).size() > 0);
//            waitFor(ofMinutes(1), () -> idea.find(ComponentFixture.class, locator)
//                    .hasText("Hello from UI test"));
//        });
    }
}
