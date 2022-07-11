// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.cschar.pmode3.uitest.pages

import com.intellij.remoterobot.RemoteRobot
import com.intellij.remoterobot.data.RemoteComponent
import com.intellij.remoterobot.fixtures.CommonContainerFixture
import com.intellij.remoterobot.fixtures.ComponentFixture
import com.intellij.remoterobot.fixtures.DefaultXpath
import com.intellij.remoterobot.fixtures.FixtureName
import com.intellij.remoterobot.search.locators.byXpath

// fun RemoteRobot.welcomeFrame(function: WelcomeFrame.() -> Unit) {
//    find(WelcomeFrame::class.java, Duration.ofSeconds(10)).apply(function)
// }

@FixtureName("Welcome Frame65765")
@DefaultXpath("ooogly", "//div[@class='FlatWelcomeFrame']")
class WelcomeFrame(remoteRobot: RemoteRobot, remoteComponent: RemoteComponent) :
    CommonContainerFixture(remoteRobot, remoteComponent) {
    val createNewProjectLink
        get() = actionLink(
            byXpath(
                "New Project",
                "//div[(@class='MainButton' and @text='New Project') or (@accessiblename='New Project' and @class='JButton')]"
            )
        )
    val moreActions
        get() = button(byXpath("More Actions", "//div[@accessiblename='More Actions' and @class='DropDownLink']"))

//    val moreActions2
//        get() = com.intellij.remoterobot.fixtures.

    val heavyWeightPopup
        get() = remoteRobot.find(ComponentFixture::class.java, byXpath("//div[@class='HeavyWeightWindow']"))
}
