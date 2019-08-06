package dk.lost_world;

import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import java.util.Calendar;


import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

class DummyWindow {
    private JLabel zomLabel;
    private JButton refreshToolWindowButton;
    private JButton hideToolWindowButton;
    private JLabel currentDate;
    private JLabel currentTime;
    private JLabel timeZone;
    private JPanel myToolWindowContent;
    private JButton button1;
    private JButton button2Button;

    public DummyWindow(ToolWindow toolWindow) {

        //hideToolWindowButton.addActionListener(e -> toolWindow.hide(null));

        //refreshToolWindowButton.addActionListener(e -> currentDateTime());

        //this.currentDateTime();

        button1.addActionListener(e -> clickButton1());
        button2Button.addActionListener(e -> clickButton2());

        zomLabel.setText("test string set in code");
        zomLabel.setIcon(new ImageIcon(getClass().getResource("/Time-icon.png")));
    }

    public void clickButton2(){
//        final StatusBar statusBar = WindowManager.getInstance().get;

        JComponent jc = new SpriteA().getContent();

        JBPopupFactory.getInstance().createBalloonBuilder(jc)
                .setFadeoutTime(3000)
                .createBalloon().show(
                    RelativePoint.getCenterOf(button1),
                    Balloon.Position.atRight
        );
    }

    public void clickButton1(){
//        final StatusBar statusBar = WindowManager.getInstance().get;

        JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(
                "Hybris Host URL '",
                MessageType.ERROR,
                null
        ).setFadeoutTime(3000)
                .createBalloon().show(
                                RelativePoint.getCenterOf(button1),
//                RelativePoint.getCenterOf(statusBar.getComponent()),
                Balloon.Position.atRight
        );
    }


    public JPanel getContent() {
        return myToolWindowContent;
    }
}