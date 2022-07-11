// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.cschar.pmode3.uitest;


import com.cschar.pmode3.services.GitPackLoaderService;
import com.cschar.pmode3.uitest.pages.DialogFixture;
import com.cschar.pmode3.uitest.pages.IdeaFrame;
import com.cschar.pmode3.uitest.steps.JavaExampleSteps;
import com.cschar.pmode3.uitest.utils.RemoteRobotExtension;
import com.cschar.pmode3.uitest.utils.StepsLogger;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.fixtures.*;
import com.intellij.remoterobot.search.locators.Locator;
import com.intellij.remoterobot.utils.Keyboard;
import com.intellij.ui.components.GradientViewport;

import com.intellij.ui.components.JBTabbedPane;
import org.assertj.swing.fixture.JTabbedPaneFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Duration;

import static com.cschar.pmode3.uitest.pages.ActionMenuFixtureKt.actionMenu;
import static com.cschar.pmode3.uitest.pages.ActionMenuFixtureKt.actionMenuItem;
import static com.intellij.remoterobot.search.locators.Locators.byXpath;
import static com.intellij.remoterobot.stepsProcessing.StepWorkerKt.step;
import static com.intellij.remoterobot.utils.RepeatUtilsKt.waitFor;
import static java.awt.event.KeyEvent.*;
import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;

@ExtendWith(RemoteRobotExtension.class)
@EnabledIfEnvironmentVariable(named = "TEST_TYPE", matches = "UI")
@Disabled
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

    @Test
    void createDownloadAndCancel_ReopeningShows_ReadyStatus(final RemoteRobot remoteRobot) {
//        sharedSteps.createNewCommandLineProject();
//        sharedSteps.closeTipOfTheDay();



        final IdeaFrame idea = remoteRobot.find(IdeaFrame.class, ofSeconds(10));
        waitFor(ofMinutes(5), () -> !idea.isDumbMode());

        step("open settings the project", () -> {
            if (remoteRobot.isMac()) {
                keyboard.hotKey(VK_SHIFT, VK_META, VK_A);
                actionMenuItem(remoteRobot, "Settings...").click();
                keyboard.enter();
            } else {
                actionMenu(remoteRobot, "File").click();
                actionMenuItem(remoteRobot, "Settings...").click();
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
                    byXpath("//div[@text='packs']"),
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
                    byXpath("//div[@mytext='Cloning zeranthium-extras-vol1...']"),
                    Duration.ofSeconds(LONG_WAIT_5s)).click();

            idea.find(ComponentFixture.class,
                    byXpath("//div[@myicon='stop.svg']"),
                    Duration.ofSeconds(2)).click();

//            waitFor(ofSeconds(3), () -> gitService.runningMonitors.size() == 0);
//            assert(gitService.runningMonitors.size() == 0);


            actionMenu(remoteRobot, "File").click();
            actionMenuItem(remoteRobot, "Settings...").click();


            final DialogFixture settingsRentry = remoteRobot.find(DialogFixture.class,
                    DialogFixture.byTitle("Settings"), Duration.ofSeconds(3));

            settingsRentry.find(  JTreeFixture.class,
                    byXpath("//div[@class='MyTree']"),
                    Duration.ofSeconds(LONG_WAIT_5s)).clickRow(0);

            settingsRentry.find(ComponentFixture.class,
                    byXpath("//div[@text='Power Mode - Zeranthium']"),
                    Duration.ofSeconds(LONG_WAIT_5s)).click();

            settingsRentry.find(ComponentFixture.class,
                    byXpath("//div[@text='packs']"),
//                    byXpath("//div[@accessiblename.key='icon.nodes.nodePlaceholder.tooltip' and @class='JLabel']"),
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
    void createDownload_ReopeningShows_DownloadLabel(final RemoteRobot remoteRobot) {

        final IdeaFrame idea = remoteRobot.find(IdeaFrame.class, ofSeconds(10));
        waitFor(ofMinutes(5), () -> !idea.isDumbMode());

        step("open settings the project", () -> {
            if (remoteRobot.isMac()) {
                keyboard.hotKey(VK_SHIFT, VK_META, VK_A);
                actionMenuItem(remoteRobot, "Settings...").click();
                keyboard.enter();
            } else {
                actionMenu(remoteRobot, "File").click();
                actionMenuItem(remoteRobot, "Settings...").click();
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


            actionMenu(remoteRobot, "File").click();
            actionMenuItem(remoteRobot, "Settings...").click();
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
