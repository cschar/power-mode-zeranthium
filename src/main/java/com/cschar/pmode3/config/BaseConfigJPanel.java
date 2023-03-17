package com.cschar.pmode3.config;

import com.cschar.pmode3.config.common.SpriteDataAnimated;
import com.cschar.pmode3.config.common.ui.ZeranthiumColors;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


/**
 * base config panel used to build out other particle effect configs
 */
public abstract class BaseConfigPanel extends JPanel {

    public JPanel headerPanel;
    public JLabel headerSizeLabel;
    static int PREVIEW_SIZE = 60;



    void setupHeaderPanel(String title, ArrayList<SpriteDataAnimated> spriteDataAnimated){
        headerPanel = new JPanel();
        JLabel headerLabel = new JLabel(title);
        headerLabel.setFont(new Font ("Arial", Font.BOLD, 20));
        headerPanel.add(headerLabel);


        headerSizeLabel = new JLabel();
        //TODO: refactor to scan dir and calculate size
        // but DONT load into memory permanently
        // Only load if enabled at top at end
//        calculateAssetSizesMB(spriteDataAnimated, this.headerSizeLabel);
        headerSizeLabel.setBackground(ZeranthiumColors.specialOption3);
        headerSizeLabel.setOpaque(true);
        headerSizeLabel.setBorder(JBUI.Borders.empty(5));

        headerPanel.add(headerSizeLabel);
        headerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        headerPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        headerPanel.setMaximumSize(new Dimension(500,100));
    }

    public void calculateAssetSizesMB(ArrayList<SpriteDataAnimated> spriteDataAnimated){
        double totalSizeMB = 0;
        for(SpriteDataAnimated d : spriteDataAnimated){
            totalSizeMB += d.getAssetSizeMB();
        }
        headerSizeLabel.setText(String.format("Mem: %.2f MB", totalSizeMB));
        headerSizeLabel.revalidate(); //doesnt work because static , only revalidating last panel method was used on
    }
}
