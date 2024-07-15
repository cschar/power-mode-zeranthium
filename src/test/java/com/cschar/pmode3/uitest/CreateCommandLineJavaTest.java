//// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
//
//package com.cschar.pmode3.uitest;
//
//import com.automation.remarks.junit5.Video;
//import com.cschar.pmode3.uitest.pages.IdeaFrame;
//import com.cschar.pmode3.uitest.steps.JavaExampleSteps;
//import com.cschar.pmode3.uitest.utils.RemoteRobotExtension;
//import com.cschar.pmode3.uitest.utils.StepsLogger;
//import com.intellij.remoterobot.RemoteRobot;
//import com.intellij.remoterobot.fixtures.*;
//import com.intellij.remoterobot.search.locators.Locator;
//import com.intellij.remoterobot.utils.Keyboard;
//import org.assertj.swing.core.MouseButton;
//import org.junit.Ignore;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//
//import java.time.Duration;
//
//import static com.intellij.remoterobot.search.locators.Locators.byXpath;
//import static com.intellij.remoterobot.stepsProcessing.StepWorkerKt.step;
//import static com.intellij.remoterobot.utils.RepeatUtilsKt.waitFor;
//import static com.intellij.remoterobot.utils.RepeatUtilsKt.waitForIgnoringError;
//import static java.awt.event.KeyEvent.*;
//import static java.time.Duration.ofMinutes;
//import static com.cschar.pmode3.uitest.pages.ActionMenuFixtureKt.actionMenu;
//import static com.cschar.pmode3.uitest.pages.ActionMenuFixtureKt.actionMenuItem;
//import static com.cschar.pmode3.uitest.pages.EditorKt.editor;
//import static java.time.Duration.ofSeconds;
//import static org.assertj.swing.timing.Pause.pause;
//
//
//@ExtendWith(RemoteRobotExtension.class)
//public class CreateCommandLineJavaTest {
//
//    private final RemoteRobot remoteRobot = new RemoteRobot("http://127.0.0.1:8082");
//    private final JavaExampleSteps sharedSteps = new JavaExampleSteps(remoteRobot);
//    private final Keyboard keyboard = new Keyboard(remoteRobot);
//
//    @BeforeAll
//    public static void initLogging() {
//        StepsLogger.init();
//    }
//
//    @BeforeEach
//    public void waitForIde() {
//        waitForIgnoringError(Duration.ofMinutes(3), ofSeconds(5), "Wait for Ide started", "Ide is not started", () -> remoteRobot.callJs("true"));
//    }
//
//    @AfterEach
//    public void closeProject(final RemoteRobot remoteRobot) {
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
//    }
//
//    @Test
////    @Video
//    void createCommandLineProject(final RemoteRobot remoteRobot) {
//        sharedSteps.createNewCommandLineProject();
//        sharedSteps.closeTipOfTheDay();
//
//        final IdeaFrame idea = remoteRobot.find(IdeaFrame.class, ofSeconds(10));
//        waitFor(ofMinutes(5), () -> !idea.isDumbMode());
//
//        step("Create Java file", () -> {
//            final ContainerFixture projectView = idea.getProjectViewTree();
//            if (!projectView.hasText("src")) {
//                projectView.findText(String.valueOf(idea.getProjectName())).doubleClick();
//                waitFor(() -> projectView.hasText("src"));
//            }
//            projectView.findText("src").click(MouseButton.RIGHT_BUTTON);
//            actionMenu(remoteRobot, "New").click();
//            actionMenuItem(remoteRobot, "Java Class").click();
//            keyboard.enterText("App");
//            keyboard.enter();
//        });
//
//        final TextEditorFixture editor = idea.textEditor(ofSeconds(2));
//
//        step("Write a code", () -> {
//            pause(ofSeconds(5).toMillis());
//            editor.getEditor().findText("App").click();
//            keyboard.key(VK_END);
//            keyboard.enter();
//            sharedSteps.autocomplete("main");
//            sharedSteps.autocomplete("sout");
//            keyboard.enterText("\"");
//            keyboard.enterText("Hello from UI test");
//        });
//
//        step("Launch the application", () -> {
//            waitFor(ofSeconds(20), () -> !editor
//                    .find(JButtonFixture.class, byXpath("//div[@class='TrafficLightButton']"))
//                    .hasText("Analyzing..."));
//            waitFor(ofSeconds(10), () -> editor.getGutter().getIcons().size() > 0);
//            final GutterIcon runIcon = editor.getGutter().getIcons()
//                    .stream()
//                    .filter((it) -> it.getDescription().contains("run.svg"))
//                    .findFirst()
//                    .orElseThrow(() -> {
//                        throw new IllegalStateException("No Run icon presents in the gutter");
//                    });
//            runIcon.click();
//            idea.find(CommonContainerFixture.class, byXpath("//div[@class='HeavyWeightWindow']"), ofSeconds(4))
//                    .button(byXpath("//div[@disabledicon='execute.svg']"), ofSeconds(4))
//                    .click();
//        });
//        step("Check console output", () -> {
//            final Locator locator = byXpath("//div[@class='ConsoleViewImpl']");
//            waitFor(ofMinutes(1), () -> idea.findAll(ContainerFixture.class, locator).size() > 0);
//            waitFor(ofMinutes(1), () -> idea.find(ComponentFixture.class, locator)
//                    .hasText("Hello from UI test"));
//        });
//    }
//}