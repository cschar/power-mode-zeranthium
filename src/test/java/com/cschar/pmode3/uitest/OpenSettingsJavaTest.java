// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.cschar.pmode3.uitest;


import com.cschar.pmode3.services.GitPackLoaderService;
import com.cschar.pmode3.uitest.pages.DialogFixture;
import com.cschar.pmode3.uitest.pages.IdeaFrame;
import com.cschar.pmode3.uitest.pages.WelcomeFrame;
import com.cschar.pmode3.uitest.steps.JavaExampleSteps;
import com.cschar.pmode3.uitest.utils.RemoteRobotExtension;
import com.cschar.pmode3.uitest.utils.StepsLogger;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.fixtures.*;
import com.intellij.remoterobot.search.locators.Locator;
import com.intellij.remoterobot.search.locators.XpathLocator;
import com.intellij.remoterobot.utils.Keyboard;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Duration;

import static com.intellij.remoterobot.search.locators.Locators.byXpath;
import static com.intellij.remoterobot.stepsProcessing.StepWorkerKt.step;
import static com.intellij.remoterobot.utils.RepeatUtilsKt.waitFor;
import static java.awt.event.KeyEvent.*;
import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;

//import org.junit.Test;

@ExtendWith(RemoteRobotExtension.class)
@EnabledIfEnvironmentVariable(named = "TEST_TYPE", matches = "UI")
//@Disabled
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OpenSettingsJavaTest {

    private final RemoteRobot remoteRobot = new RemoteRobot("http://127.0.0.1:8082");
    private final JavaExampleSteps sharedSteps = new JavaExampleSteps(remoteRobot);
    private final Keyboard keyboard = new Keyboard(remoteRobot);
    private final int LONG_WAIT_5s = 5;

    @BeforeAll
    public static void initLogging() {
        StepsLogger.init();
    }

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

//    @AfterAll
//    static void closeProject(final RemoteRobot remoteRobot) {
//        JavaExampleSteps sharedSteps = new JavaExampleSteps(remoteRobot);
//        sharedSteps.closeProject(remoteRobot);
//    }

    @Test
    //    @Video
    @Order(20)
    void test_opens_second_project(final RemoteRobot remoteRobot) {
        assert 1 == 1;
    }

    @Test
//    @Video
    @Order(1)
    void test_opens_project(final RemoteRobot remoteRobot) {
//    public void test_1_opens_project() {
//        JavaExampleSteps sharedSteps = new JavaExampleSteps(remoteRobot);
        System.out.println("starting..");


        //
        // Create New Project
        //
//        final WelcomeFrame welcomeFrame = remoteRobot.find(WelcomeFrame.class,
//                                                           Duration.ofSeconds(30));
//        welcomeFrame.getCreateNewProjectLink().click();
//
//        final DialogFixture newProjectDialog = welcomeFrame.find(DialogFixture.class,
//                byXpath("//div[@class='MyDialog']"),
//                Duration.ofSeconds(30));
//
////        newProjectDialog.find(JListFixture.class, byXpath("//div[@class='JBList']")).clickItem("New Project", true);
//        System.out.println("clicking Java button");
//        newProjectDialog.findText("Java").click();
//
//        System.out.println("clicking Create button");
//        newProjectDialog.button("Create").click();

        sharedSteps.createNewCommandLineProject();
        sharedSteps.closeTipOfTheDay();

        //
        // Let Project Load
        //
        System.out.println("waiting for IDE to load after creating project...");

        final IdeaFrame idea = remoteRobot.find(IdeaFrame.class, ofSeconds(120));
        System.out.println("found Ideaframe, waiting to exit dumb mode...");
        waitFor(ofMinutes(5), () -> !idea.isDumbMode());

        System.out.println("opening settings...");

        if (remoteRobot.isMac()) {
            keyboard.hotKey(VK_META, VK_COMMA);
//            keyboard.hotKey(VK_SHIFT, VK_META, VK_A);
//            keyboard.enterText("Setting");
//            actionMenuItem(remoteRobot, "Settings...").click();
//            keyboard.enter();
        } else {
            keyboard.hotKey(VK_ALT, VK_CONTROL, VK_S);
//            keyboard.enterText("Setting");
//            keyboard.enter();
//            actionMenu(remoteRobot, "File").click();
//            actionMenuItem(remoteRobot, "Settings...").click();
        }

        System.out.println("looking for settings dialog...");

        //MacOS is preferences?!!??!
        final DialogFixture settingsDialog = remoteRobot.find(DialogFixture.class,
                DialogFixture.byTitle("Settings"), Duration.ofSeconds(120));


        System.out.println("Closing settings...");

        settingsDialog.find(ComponentFixture.class,
                byXpath("//div[@text.key='button.cancel']"),
                Duration.ofSeconds(5)).click();


        //Don't close so next test case can run

//        System.out.println("Closing the project...");
//        sharedSteps.closeProject();


    }

    @Test
    @Order(2)
//    @Disabled
    void createDownloadAndCancel_ReopeningShows_ReadyStatus(final RemoteRobot remoteRobot) {
//        sharedSteps.createNewCommandLineProject();
//        sharedSteps.closeTipOfTheDay();



        final IdeaFrame idea = remoteRobot.find(IdeaFrame.class, ofSeconds(10));
        waitFor(ofMinutes(5), () -> !idea.isDumbMode());

        step("open settings in the project", () -> {
            if (remoteRobot.isMac()) {
                keyboard.hotKey(VK_META, VK_COMMA);

//                keyboard.hotKey(VK_SHIFT, VK_META, VK_A);
//                actionMenuItem(remoteRobot, "Settings...").click();
//                keyboard.enter();
            } else {
                keyboard.hotKey(VK_ALT, VK_CONTROL, VK_S);
//                actionMenu(remoteRobot, "File").click();
//                actionMenuItem(remoteRobot, "Settings...").click();
            }
        });

        step("click settings sub menu", () -> {

            final DialogFixture settingsDialog = remoteRobot.find(DialogFixture.class,
                    DialogFixture.byTitle("Settings"), Duration.ofSeconds(3));

            settingsDialog.find(  JTreeFixture.class,
                    byXpath("//div[@class='MyTree']"),
                    Duration.ofSeconds(3)).clickRow(0);


            settingsDialog.find(ComponentFixture.class,
                    byXpath("//div[@text='Power Mode - Zeranthium']"),
                    Duration.ofSeconds(3)).click();


            settingsDialog.find(ComponentFixture.class,
//                    byXpath("//div[@accessiblename.key='icon.nodes.nodePlaceholder.tooltip' and @class='JLabel']"),
//                    byXpath("//div[@text='packs']"),
                    byXpath("//div[@text='|']"),
                    Duration.ofSeconds((3))).click();

            settingsDialog.find(ComponentFixture.class,
                    byXpath("//div[@text='zeranthium-extras-vol1']"),
                    Duration.ofSeconds(3)).click();


            //can do assert here
            settingsDialog.find(ComponentFixture.class,
                    byXpath("//div[@text='Ready']"),
                    Duration.ofSeconds(5)).click();

            settingsDialog.find(ComponentFixture.class,
                    byXpath("//div[@text='DOWNLOAD']"),
                    Duration.ofSeconds(5)).click();

            //assert here
            settingsDialog.find(JLabelFixture.class,
                    byXpath("//div[@text='Downloading...']"),
                    Duration.ofSeconds(5)).click();


            settingsDialog.find(ComponentFixture.class,
                    byXpath("//div[@text.key='button.cancel']"),
                    Duration.ofSeconds(5)).click();

//            GitPackLoaderService gitService = ApplicationManager.getApplication().getService(GitPackLoaderService.class);
//
//            assert(gitService.runningMonitors.size() == 1);

            idea.find(ComponentFixture.class,
//                    byXpath("//div[@mytext='Cloning zeranthium-extras-vol1...']"),
                    //2023.1 elements...
                    byXpath("//div[@accessiblename='Cloning zeranthium-extras-vol1...' and @class='TextPanel' and @text='Cloning zeranthium-extras-vol1...']"),
                    Duration.ofSeconds(LONG_WAIT_5s)).click();



            idea.find(ComponentFixture.class,
                    byXpath("//div[@myicon='stop.svg']"),
                    Duration.ofSeconds(2)).click();

            //incase we click it too fast... try to click again
            try {
                idea.find(ComponentFixture.class,
                        byXpath("//div[@myicon='stop.svg']"),
                        Duration.ofSeconds(5)).click();
            }catch (Exception e){

            }
//            waitFor(ofSeconds(3), () -> gitService.runningMonitors.size() == 0);
//            assert(gitService.runningMonitors.size() == 0);


//            actionMenu(remoteRobot, "File").click();
//            actionMenuItem(remoteRobot, "Settings...").click();

            step("open settings in the project", () -> {
                if (remoteRobot.isMac()) {
                    keyboard.hotKey(VK_META, VK_COMMA);
                } else {
                    keyboard.hotKey(VK_ALT, VK_CONTROL, VK_S);
                }
            });

            final DialogFixture settingsRentry = remoteRobot.find(DialogFixture.class,
                    DialogFixture.byTitle("Settings"), Duration.ofSeconds(3));

            settingsRentry.find(  JTreeFixture.class,
                    byXpath("//div[@class='MyTree']"),
                    Duration.ofSeconds(LONG_WAIT_5s)).clickRow(0);

            settingsRentry.find(ComponentFixture.class,
                    byXpath("//div[@text='Power Mode - Zeranthium']"),
                    Duration.ofSeconds(LONG_WAIT_5s)).click();

            settingsRentry.find(ComponentFixture.class,
//                    byXpath("//div[@text='packs']"),
                    byXpath("//div[@text='|']"),
                    Duration.ofSeconds((LONG_WAIT_5s))).click();

            settingsRentry.find(ComponentFixture.class,
                    byXpath("//div[@text='zeranthium-extras-vol1']"),
                    Duration.ofSeconds(LONG_WAIT_5s)).click();

            //should be ready since we cancelled downloading state
            assert(settingsRentry.find(JLabelFixture.class,
                    byXpath("//div[@text='Ready']"),
                    Duration.ofSeconds(LONG_WAIT_5s)).hasText("Ready"));

            //exit settings menu
            settingsRentry.find(ComponentFixture.class,
                    byXpath("//div[@text.key='button.cancel']"),
                    Duration.ofSeconds(LONG_WAIT_5s)).click();
        });

    }


    @Test
    @Order(3)
    @Disabled()
    void createDownload_ReopeningShows_DownloadLabel(final RemoteRobot remoteRobot) {

        //This frame is an open project,not the home screen
        final IdeaFrame idea = remoteRobot.find(IdeaFrame.class, ofSeconds(10));
        waitFor(ofMinutes(5), () -> !idea.isDumbMode());

        step("open settings in the project", () -> {
            if (remoteRobot.isMac()) {
                keyboard.hotKey(VK_META, VK_COMMA);
            } else {
                keyboard.hotKey(VK_ALT, VK_CONTROL, VK_S);
            }
        });

        step("start download in settings sub menu", () -> {

            final DialogFixture settingsDialog = remoteRobot.find(DialogFixture.class,
                    DialogFixture.byTitle("Settings"), Duration.ofSeconds(3));

            settingsDialog.find(JTreeFixture.class,
                    byXpath("//div[@class='MyTree']")).clickRow(0);


            settingsDialog.find(ComponentFixture.class,
                    byXpath("//div[@text='Power Mode - Zeranthium']"), Duration.ofSeconds(3)).click();


            settingsDialog.find(ComponentFixture.class,
                    byXpath("//div[@text='packs']"),
//                    byXpath("//div[@accessiblename.key='icon.nodes.nodePlaceholder.tooltip' and @class='JLabel']"),
                    Duration.ofSeconds(LONG_WAIT_5s)).click();

            settingsDialog.find(ComponentFixture.class,
                    byXpath("//div[@text='zeranthium-extras-vol1']"),
                    Duration.ofSeconds(LONG_WAIT_5s)).click();


            //can do assert here
            settingsDialog.find(ComponentFixture.class,
                    byXpath("//div[@text='Ready']"),
                    Duration.ofSeconds(LONG_WAIT_5s)).click();

            settingsDialog.find(ComponentFixture.class,
                    byXpath("//div[@text='DOWNLOAD']"),
                    Duration.ofSeconds(LONG_WAIT_5s)).click();

            //assert here
            settingsDialog.find(JLabelFixture.class,
                    byXpath("//div[@text='Downloading...']"),
                    Duration.ofSeconds(LONG_WAIT_5s)).click();


            settingsDialog.find(ComponentFixture.class,
                    byXpath("//div[@text.key='button.cancel']"),
                    Duration.ofSeconds(LONG_WAIT_5s)).click();
        });

        step("re-enter download menu, ensure downloading label is correct", () -> {

//            GitPackLoaderService gitService = ApplicationManager.getApplication().getService(GitPackLoaderService.class);
//            waitFor(ofSeconds(3), () -> gitService.runningMonitors.size() == 1);
//            assert(gitService.runningMonitors.size() == 1);


//            actionMenu(remoteRobot, "File").click();
//            actionMenuItem(remoteRobot, "Settings...").click();
            step("open settings in the project", () -> {
                if (remoteRobot.isMac()) {
                    keyboard.hotKey(VK_META, VK_COMMA);
                } else {
                    keyboard.hotKey(VK_ALT, VK_CONTROL, VK_S);
                }
            });


            final DialogFixture settingsRentry = remoteRobot.find(DialogFixture.class,
                    DialogFixture.byTitle("Settings"), Duration.ofSeconds(LONG_WAIT_5s));
            settingsRentry.find(  JTreeFixture.class,
                    byXpath("//div[@class='MyTree']"), Duration.ofSeconds(LONG_WAIT_5s)).clickRow(0);
            settingsRentry.find(ComponentFixture.class,
                    byXpath("//div[@text='Power Mode - Zeranthium']"), Duration.ofSeconds(LONG_WAIT_5s)).click();
            settingsRentry.find(ComponentFixture.class,
                    byXpath("//div[@text='packs']"),
//                    byXpath("//div[@accessiblename.key='icon.nodes.nodePlaceholder.tooltip' and @class='JLabel']"),
                    Duration.ofSeconds(LONG_WAIT_5s)).click();
            settingsRentry.find(ComponentFixture.class,
                    byXpath("//div[@text='zeranthium-extras-vol1']"),
                    Duration.ofSeconds(LONG_WAIT_5s)).click();

            //should be ready since we cancelled downloading state
            assert(settingsRentry.find(JLabelFixture.class,
                    byXpath("//div[@text='Downloading...']"),
                    Duration.ofSeconds(1)).hasText("Downloading..."));

            settingsRentry.find(ComponentFixture.class,
                    byXpath("//div[@text.key='button.cancel']"),
                    Duration.ofSeconds(2)).click();

        });

        step("cancel download", () -> {
            idea.find(ComponentFixture.class,
                    byXpath("//div[@mytext='Cloning zeranthium-extras-vol1...']"),
                    Duration.ofSeconds(5)).click();

            idea.find(ComponentFixture.class,
                    byXpath("//div[@myicon='stop.svg']"),
                    Duration.ofSeconds(2)).click();
        });


    }




}
