// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.cschar.pmode3.uitest;

import com.cschar.pmode3.uitest.pages.WelcomeFrame;
import com.cschar.pmode3.uitest.pages.WelcomeFrameFixture;
import com.cschar.pmode3.uitest.utils.RemoteRobotExtension;
import com.cschar.pmode3.uitest.utils.StepsLogger;
import com.intellij.remoterobot.RemoteRobot;
import com.intellij.remoterobot.data.RemoteComponent;
import com.intellij.remoterobot.fixtures.CommonContainerFixture;
import com.intellij.remoterobot.fixtures.ComponentFixture;
import com.intellij.remoterobot.fixtures.DefaultXpath;
import org.jetbrains.annotations.NotNull;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Duration;

import static com.intellij.remoterobot.fixtures.dataExtractor.TextDataPredicatesKt.startsWith;
import static com.intellij.remoterobot.search.locators.Locators.byXpath;

@ExtendWith(RemoteRobotExtension.class)
@Disabled
public class SayHelloJavaTest {
    @BeforeAll
    public static void initLogging() {
        StepsLogger.init();
    }

    @Test
    void checkSayHello(final RemoteRobot remoteRobot) {
        final WelcomeFrame welcomeFrame = remoteRobot.find(WelcomeFrame.class, Duration.ofSeconds(10));
        System.out.println("asserting..");
        assert (welcomeFrame.hasText(startsWith("IntelliJ IDEA")));

        if (!welcomeFrame.hasText("Say Hello")) {
            welcomeFrame.getMoreActions().click();
            welcomeFrame.getHeavyWeightPopup().findText("Say Hello").click();
        } else {
            welcomeFrame.findText("Say Hello").click();
        }

        System.out.println("Found Say hello text...");

        //  Kotlin code I have tried to replace by using Java below...
//        final SayHelloKotlinTest.HelloWorldDialog helloDialog = remoteRobot.find(SayHelloKotlinTest.HelloWorldDialog.class);
//        assert (helloDialog.getTextPane().hasText("Hello World22!"));
//        helloDialog.getOk().click();
//        assert (welcomeFrame.hasText(startsWith("IntelliJ IDEA")));

        //Find the component via its @DefaultXPath annotation....
//        HelloWorldDialog myHelloWorldDialog = remoteRobot.find(HelloWorldDialog.class, Duration.ofSeconds(5));
//       // HelloWorldDialog myHelloWorldDialog = remoteRobot.find(HelloWorldDialog.class);
//        assert( myHelloWorldDialog.getTextPane().hasText("Hello World22!")) : "cant find texthello 22";
//        myHelloWorldDialog.getOk().click();
//        assert (welcomeFrame.hasText(startsWith("IntelliJ IDEA")));

        WelcomeFrameFixture ff = remoteRobot.find(WelcomeFrameFixture.class, Duration.ofSeconds(5));
        ComponentFixture f = ff.getDialog();
        assert(f.hasText("Hello")) : "The title of dialog should be 'Hello' ";

        assert( ff.getTextPane().hasText("Hello World22!")) : "dialog should have text hello World22";
        ff.getOk().click();
        assert (welcomeFrame.hasText(startsWith("IntelliJ IDEA")));


    }


   //kotlin @DefaultXpath("title Hello", "//div[@title='Hello' and @class='MyDialog']")
   //java   @DefaultXpath(by = "FlatWelcomeFrame type", xpath = "//div[@class='FlatWelcomeFrame']")
    //this works.. it finds element of type 'MyDailog'
//    @DefaultXpath(by = "MyDialog type", xpath = "//div[@title='Hello' and @class='MyDialog']")

//    @DefaultXpath(by = "Hello title", xpath = "//div[@title='Hello' and @class='MyDialog']")
    @DefaultXpath(by = "MyDialog type", xpath = "//div[@class='MyDialog']")
    class HelloWorldDialog extends CommonContainerFixture {
        public HelloWorldDialog(@NotNull RemoteRobot remoteRobot, @NotNull RemoteComponent remoteComponent) {
            super(remoteRobot, remoteComponent);
        }

       // Create New Project
       public ComponentFixture createNewProjectLink() {
           return find(ComponentFixture.class, byXpath("//div[@text='Create New Project' and @class='ActionLink']"));
       }

       // Import Project
       public ComponentFixture importProjectLink() {
           return find(ComponentFixture.class, byXpath("//div[@text='Import Project' and @class='ActionLink']"));
       }
        // kotlin code
       //        val textPane: ComponentFixture
       //        get() = find(byXpath("//div[@class='JTextPane']"))
       // java equivalent
        ComponentFixture textPane;
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

    }
}
