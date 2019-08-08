package com.cschar.pmode3.ui;

import javax.swing.*;
import java.awt.*;

public class LightningConfigUI {
    private JCheckBox checkBox1;
    private JPanel mainLightningPanel;
    private JRadioButton radioButton1;
    private JRadioButton radioButton2;
    private JRadioButton radioButton3;
    public JPanel infoPanel;

    public LightningConfigUI(){

    }


    public JComponent getComponent() {
        return this.mainLightningPanel;
    }

    private void createUIComponents() {
        this.mainLightningPanel = new JPanel();
//        this.mainLightningPanel.setPreferredSize(new Dimension(500,300));
    }
}
