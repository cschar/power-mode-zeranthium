package com.cschar.pmode3.services;
// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Action class to demonstrate how to interact with the IntelliJ Platform.
 * The only action this class performs is to provide the user with a popup dialog as feedback.
 * Typically this class is instantiated by the IntelliJ Platform framework based on declarations
 * in the plugin.xml file. But when added at runtime this class is instantiated by an action group.
 */
public class PopupDialogAction extends AnAction {

    /**
     * This default constructor is used by the IntelliJ Platform framework to instantiate this class based on plugin.xml
     * declarations. Only needed in {@link PopupDialogAction} class because a second constructor is overridden.
     *
     * @see AnAction#AnAction()
     */
    public PopupDialogAction() {
        super();
    }

    /**
     * This constructor is used to support dynamically added menu actions.
     * It sets the text, description to be displayed for the menu item.
     * Otherwise, the default AnAction constructor is used by the IntelliJ Platform.
     *
     * @param text        The text to be displayed as a menu item.
     * @param description The description of the menu item.
     * @param icon        The icon to be used with the menu item.
     */
    public PopupDialogAction(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon);
    }

    /**
     * Gives the user feedback when the dynamic action menu is chosen.
     * Pops a simple message dialog. See the psi_demo plugin for an
     * example of how to use {@link AnActionEvent} to access data.
     *
     * @param event Event received when the associated menu item is chosen.
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
//        // Using the event, create and show a dialog
//        Project currentProject = event.getProject();
//        StringBuilder dlgMsg = new StringBuilder(event.getPresentation().getText() + " Selected!");
//        String dlgTitle = event.getPresentation().getDescription();
//        // If an element is selected in the editor, add info about it.
//        Navigatable nav = event.getData(CommonDataKeys.NAVIGATABLE);
//        if (nav != null) {
//            dlgMsg.append(String.format("\nSelected Element: %s", nav.toString()));
//        }
//        Messages.showMessageDialog(currentProject, dlgMsg.toString(), dlgTitle, Messages.getInformationIcon());

        //https://plugins.jetbrains.com/docs/intellij/popups.html#introduction

        System.out.println("Creating custom panel");
        JComponent content = new MyJComponent("A custom panel");

        JBPopup popup = JBPopupFactory.getInstance().createComponentPopupBuilder(content, content).createPopup();

        if (popup.getSize() == null) {
            System.out.println("popup size is null");
        }else{
            System.out.println("popup size is" + popup.getSize().toString());
        }

        Dimension d = new Dimension();
        d.setSize(900,500);
        popup.setMinimumSize(d);
        popup.showInFocusCenter();
        popup.setUiVisible(true);
        popup.setCaption("A popup yeah");


        if (popup.getSize() == null) {
            System.out.println("==popup size is null");
        }else{
            System.out.println("==popup size is" + popup.getSize().toString());
        }

        System.out.println("Popup is visible? : " + popup.isVisible());

    }

    /**
     * Determines whether this menu item is available for the current context.
     * Requires a project to be open.
     *
     * @param e Event received when the associated group-id menu is chosen.
     */
    @Override
    public void update(AnActionEvent e) {
        // Set the availability based on whether a project is open
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }

}