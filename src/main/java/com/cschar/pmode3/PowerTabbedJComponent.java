package com.cschar.pmode3;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.VerticalSeparatorComponent;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.util.ui.JBUI;
import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public class PowerTabbedJComponent extends JPanel{

    public JPanel getSettingsColumn(JBColor color){
        JPanel firstCol = new JPanel();
        firstCol.setLayout(new BoxLayout(firstCol, BoxLayout.Y_AXIS));
        firstCol.setBackground(color);
        firstCol.setMaximumSize(new Dimension(300,600));

        JCheckBox checkBox = new JCheckBox("setting 1 checked?");
        firstCol.add(checkBox);

        JCheckBox checkBox2 = new JCheckBox("setting 1 checked?");
        firstCol.add(checkBox2);

        JCheckBox checkBox3 = new JCheckBox("setting 1 checked?");
        firstCol.add(checkBox2);

        return firstCol;
    }

    public JPanel makeTopSettings(){
        JBColor color = JBColor.lightGray;
        JPanel topSettings = new JPanel();

        JPanel col1 = new JPanel();
        col1.setLayout(new BoxLayout(col1, BoxLayout.Y_AXIS));
        col1.setBorder(JBUI.Borders.empty(10, 10, 50, 10));
        col1.setBackground(JBColor.CYAN);

        JCheckBox isEnabledCheckBox = new JCheckBox("is enabled?");
        col1.add(isEnabledCheckBox);
        col1.add(new VerticalSeparatorComponent());
        JLabel toggleHotkeyInfolabel = new JLabel("Toggle Hotkey: CTRL+SHIFT+Z..");
        col1.add(toggleHotkeyInfolabel);

        JPanel col2 = new JPanel();
        col2.setLayout(new BoxLayout(col2, BoxLayout.Y_AXIS));
        col2.setBorder(JBUI.Borders.empty(10, 10, 50, 10));
        col2.setBackground(JBColor.darkGray);


        JPanel shakePanel = new JPanel();
        shakePanel.setLayout(new BoxLayout(shakePanel, BoxLayout.X_AXIS));
        JLabel shakeDistanceLbl = new JLabel("shake dist:");
        JTextField shakeDistance = new JTextField();
        shakeDistance.setName("name");
        shakePanel.add(shakeDistanceLbl);
        shakePanel.add(shakeDistance);

        col2.add(shakePanel);

        JPanel lifeTimePanel = new JPanel();
        lifeTimePanel.setLayout(new BoxLayout(lifeTimePanel, BoxLayout.X_AXIS));
        JLabel lifeTimeLbl = new JLabel("Lifetime:");
        JTextField lifeTimeField = new JTextField();
        lifeTimePanel.add(lifeTimeLbl);
        lifeTimePanel.add(lifeTimeField);

        col2.add(lifeTimePanel);

        JPanel maxSearchDistPanel = new JPanel();
        maxSearchDistPanel.setLayout(new BoxLayout(maxSearchDistPanel, BoxLayout.X_AXIS));
        JLabel searchDistLbl = new JLabel("max anchor search distance from caret:");
        JTextField searchDistField = new JTextField();
        maxSearchDistPanel.add(searchDistLbl);
        maxSearchDistPanel.add(searchDistField);

        col2.add(maxSearchDistPanel);


        JPanel col3 = new JPanel();
        col3.setLayout(new BoxLayout(col3, BoxLayout.Y_AXIS));
        col3.setBorder(JBUI.Borders.empty(10, 10, 50, 10));
        col3.setBackground(JBColor.GREEN);

        JButton anchorConfigButton = new JButton("click it");
        col3.add(anchorConfigButton);

        topSettings.add(col1);
        topSettings.add(col2);
        topSettings.add(col3);

        return topSettings;
    }

    public JCheckBox setCheckBox(JPanel parent, String title, JCheckBox checkbox){
        checkbox = new JCheckBox(title);
        parent.add(checkbox);

        return checkbox;
    }



    public JPanel makeBotSettings(){
        JPanel botSettings = new JPanel();

        JPanel col1 = new JPanel();
        col1.setLayout(new BoxLayout(col1, BoxLayout.Y_AXIS));
        col1.setBorder(JBUI.Borders.empty(10, 100, 50, 10));
        col1.setBackground(JBColor.CYAN);

        for(int i =0; i<4; i++) {
            setCheckBox(col1, "enable setting" + String.valueOf(i) , null);
        }


        JPanel col2 = new JPanel();
        col2.setLayout(new BoxLayout(col2, BoxLayout.Y_AXIS));
        col2.setBorder(JBUI.Borders.empty(10, 10, 50, 10));
        col2.setBackground(JBColor.GREEN);

        for(int i =0; i<4; i++) {
            setCheckBox(col2, "enable setting" + String.valueOf(i) , null);
        }


        botSettings.add(col2);

        JPanel col3 = new JPanel();
        col3.setLayout(new BoxLayout(col3, BoxLayout.Y_AXIS));
        col3.setBorder(JBUI.Borders.empty(10, 10, 50, 10));
        col3.setBackground(JBColor.GREEN);

        col3.add(new JLabel(".."));
        for(int i =0; i<3; i++) {
            setCheckBox(col3, "enable setting" + String.valueOf(i) , null);
        }

        JPanel col4 = new JPanel();
        col4.setLayout(new BoxLayout(col4, BoxLayout.Y_AXIS));
        col4.setBorder(JBUI.Borders.empty(10, 10, 50, 10));
        col4.setBackground(JBColor.DARK_GRAY);

        col4.add(new JLabel("empty.."));
        for(int i =0; i<2; i++) {
            setCheckBox(col4, " setting" + String.valueOf(i) , null);
        }


        botSettings.add(col1);
        botSettings.add(col2);
        botSettings.add(col3);
        botSettings.add(col4);
        return botSettings;
    }

   public PowerTabbedJComponent(){

       JBTabbedPane settingsTabbedPane = new JBTabbedPane(JTabbedPane.LEFT);
       settingsTabbedPane.setOpaque(false);

//       JPanel panel1 = new JPanel();
//       panel1.setBackground(JBColor.orange);
//       panel1.setBorder(JBUI.Borders.empty(2, 2, 200, 2));
//       panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));
//       ImageIcon sliderIcon = new ImageIcon(this.getClass().getResource("/icons/bar_small.png"));
//       settingsTabbedPane.addTab("Particle Settings", sliderIcon, panel1);
////       JLabel labTab1 = new JLabel("Tab #1");
////       labTab1.setUI(new VerticalLabelUI(false)); // true/false to make it upwards/downwards
////       settingsTabbedPane.setTabComponentAt(0, labTab1); // For component1
//
//       panel1.add(getSettingsColumn(JBColor.green));
//       panel1.add(getSettingsColumn((JBColor) JBColor.cyan));



       JPanel panel2 = new JPanel();
       panel2.setBackground(JBColor.getHSBColor(206,88,75));
       panel2.setBorder(JBUI.Borders.empty(2, 2, 200, 2));
       panel2.setLayout(new BoxLayout(panel2, BoxLayout.PAGE_AXIS));
       ImageIcon sliderIcon2 = new ImageIcon(this.getClass().getResource("/icons/bar_small.png"));
       settingsTabbedPane.addTab("Particle 2", sliderIcon2, panel2);

       panel2.add(makeTopSettings());
       panel2.add(makeBotSettings());

       this.setMaximumSize(new Dimension(1000,500));
       this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
       this.add(settingsTabbedPane);


   };


}

