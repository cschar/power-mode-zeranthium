// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.cschar.pmode3.uitest.pages;

import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.data.RemoteComponent;
import com.intellij.remoterobot.fixtures.CommonContainerFixture;
import com.intellij.remoterobot.fixtures.ComponentFixture;
import com.intellij.remoterobot.fixtures.DefaultXpath;
import com.intellij.remoterobot.fixtures.FixtureName;
import org.jetbrains.annotations.NotNull;

import static com.intellij.remoterobot.search.locators.Locators.byXpath;
//import static com.intellij.remoterobot.utils.UtilsKt.hasAnyComponent;


@DefaultXpath(by = "FlatWelcomeFrame type", xpath = "//div[@class='FlatWelcomeFrame']")
@FixtureName(name = "Welcome Frame")
public class WelcomeFrameFixture extends CommonContainerFixture {
    public WelcomeFrameFixture(@NotNull RemoteRobot remoteRobot, @NotNull RemoteComponent remoteComponent) {
        super(remoteRobot, remoteComponent);
    }

    public ComponentFixture getDialog() {
        System.out.println("getting dialog");
        return find(ComponentFixture.class, byXpath("//div[@class='DialogRootPane']"));
    }

    public ComponentFixture getTextPane() {
        return find(
                ComponentFixture.class,
                byXpath("//div[@class='JTextPane']")
        );
    }

    //        val ok: ComponentFixture
//        get() = find(byXpath("//div[@class='JButton' and @text='OK']"))
    ComponentFixture ok;
    public ComponentFixture getOk(){
        return find(
                ComponentFixture.class,
                byXpath("//div[@class='JButton' and @text='OK']")
        );
    }

    public ComponentFixture createNewProjectLink() {
        return welcomeFrameLink("New Project");
    }

    public ComponentFixture importProjectLink() {
        return welcomeFrameLink("Get from VCS");
    }

    private ComponentFixture welcomeFrameLink(String text) {

        try{
            return find(ComponentFixture.class, byXpath("//div[@class='JBOptionButton' and @text='" + text + "']"));
        }catch(Exception e){
            return find(
                    ComponentFixture.class,
                    byXpath("//div[@class='NonOpaquePanel'][./div[@text='" + text + "']]//div[@class='JButton']")
            );
        }

//        if (hasAnyComponent(this, byXpath("//div[@class='NewRecentProjectPanel']"))) {
//            return find(ComponentFixture.class, byXpath("//div[@class='JBOptionButton' and @text='" + text + "']"));
//        }
//        return find(
//                ComponentFixture.class,
//                byXpath("//div[@class='NonOpaquePanel'][./div[@text='" + text + "']]//div[@class='JButton']")
//        );
    }
}
