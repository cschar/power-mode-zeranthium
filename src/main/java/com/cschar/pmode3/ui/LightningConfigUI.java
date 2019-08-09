package com.cschar.pmode3.ui;

import com.cschar.pmode3.PowerMode3;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LightningConfigUI {
    private JPanel mainLightningPanel;
    public JPanel imagePreviewPanel;
    public JLabel imagePreviewLabel;
    private JTextField textField1;
    private JTextField textField2;

    public LightningConfigUI(PowerMode3 settings){

    }


    public JComponent getComponent() {
        return this.mainLightningPanel;
    }

    private void createUIComponents() {
        this.mainLightningPanel = new JPanel();

        imagePreviewPanel = new JPanel();
//        imagePreviewPanel.setBorder(JBUI.Borders.empty(10, 10, 10, 10));
//        this.mainLightningPanel.setPreferredSize(new Dimension(500,300));
    }
}
