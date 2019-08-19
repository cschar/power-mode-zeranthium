package com.cschar.pmode3.ui;

import com.cschar.pmode3.PowerMode3;
import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LizardConfigPanel extends JPanel {

    JPanel mainPanel;
    PowerMode3 settings;
    public LizardConfigPanel(PowerMode3 settings){
        this.settings = settings;
//        this.add(new JLabel("Lizard options"));

        //LIZARD
        //Build without GUI designer

        mainPanel = new JPanel();
        mainPanel.setMaximumSize(new Dimension(1000,300));
        mainPanel.setLayout(new GridLayout(0,2));
        JPanel firstCol = new JPanel();
        mainPanel.add(firstCol);

        JPanel secondCol = new JPanel();
        secondCol.setLayout(new BoxLayout(secondCol, BoxLayout.PAGE_AXIS));
        JLabel headerLabel = new JLabel("Lizard Options");
        headerLabel.setFont(new Font ("Arial", Font.BOLD, 20));
        secondCol.add(headerLabel);

        //Lizard color
        JPanel lizardColorPanel = new JPanel();
        JLabel lizardColorLabel = new JLabel("Lizard Color");
        lizardColorLabel.setOpaque(true); //to show background  https://stackoverflow.com/a/2380328/403403
        lizardColorPanel.add(lizardColorLabel);
        if(settings.getSpriteTypeProperty(PowerMode3.SpriteType.LIZARD, "lizardColor") == null){

            lizardColorLabel.setBackground(Color.GREEN);
            settings.setSpriteTypeProperty(PowerMode3.SpriteType.LIZARD, "lizardColor",
                    String.valueOf(Color.GREEN.getRGB()));
        }else{
            String colorRGB = settings.getSpriteTypeProperty(PowerMode3.SpriteType.LIZARD, "lizardColor");
            Color lizardColor = new Color(Integer.parseInt(colorRGB));
            lizardColorLabel.setBackground(lizardColor);
        };


        JButton lizardColorPickerButton = new JButton("Pick color");
        lizardColorPickerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(lizardColorPanel, "Choose lizard color",
                        JBColor.darkGray);

//                PowerMode3.getInstance().setSomePersistantState
                lizardColorLabel.setBackground(newColor);
                settings.setSpriteTypeProperty(PowerMode3.SpriteType.LIZARD, "lizardColor",
                        String.valueOf(newColor.getRGB()));
            }
        });
        lizardColorPanel.add(lizardColorPickerButton);
        secondCol.add(lizardColorPanel);

        //lizard density
        JPanel lop1 = new JPanel();
//        lop1.add(new JLabel("lizard density: "));
//        lop1.add(new JTextField("test"));

        secondCol.add(lop1);

        mainPanel.add(secondCol);


        this.add(this.mainPanel);
    }


}
