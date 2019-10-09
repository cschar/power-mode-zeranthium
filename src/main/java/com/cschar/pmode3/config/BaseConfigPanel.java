package com.cschar.pmode3.config;

import com.cschar.pmode3.PowerMode3;
import com.cschar.pmode3.config.common.SpriteDataAnimated;
import com.cschar.pmode3.config.common.ui.ZeranthiumColors;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


public abstract class BaseConfigPanel extends JPanel {

    public JPanel headerPanel;
    public JLabel headerSizeLabel;



    void setupHeaderPanel(String title, ArrayList<SpriteDataAnimated> spriteDataAnimated){
        headerPanel = new JPanel();
        JLabel headerLabel = new JLabel(title);
        headerLabel.setFont(new Font ("Arial", Font.BOLD, 20));
        headerPanel.add(headerLabel);


        headerSizeLabel = new JLabel();
        calculateSize(spriteDataAnimated, this.headerSizeLabel);
        headerSizeLabel.setBackground(ZeranthiumColors.specialOption3);
        headerSizeLabel.setOpaque(true);
        headerSizeLabel.setBorder(JBUI.Borders.empty(5));

        headerPanel.add(headerSizeLabel);
        headerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        headerPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        headerPanel.setMaximumSize(new Dimension(500,100));
    }

    public static void calculateSize(ArrayList<SpriteDataAnimated> spriteDataAnimated, JLabel headerSizeLabel){
        double totalSizeMB = 0;
        for(SpriteDataAnimated d : spriteDataAnimated){
            totalSizeMB += d.getAssetSizeMB();
        }
        headerSizeLabel.setText(String.format("Mem: %.2f MB", totalSizeMB));
        headerSizeLabel.revalidate(); //doesnt work because static , only revalidating last panel method was used on
    }
}
