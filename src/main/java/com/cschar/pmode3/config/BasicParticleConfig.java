package com.cschar.pmode3.config;

import com.cschar.pmode3.PowerMode3;
import com.intellij.openapi.options.ConfigurationException;

import javax.swing.*;
import java.awt.*;


public class BasicParticleConfig extends JPanel{

    PowerMode3 settings;
    JPanel firstCol;
    JPanel secondCol;
    private JTextField maxParticleSizeField;
    private JTextField numParticlesField;
    private JCheckBox emitTopCheckBox;
    private JCheckBox emitBottomCheckBox;


    public BasicParticleConfig(PowerMode3 settings){
        this.settings = settings;



        this.setMaximumSize(new Dimension(1000,300));
        this.setLayout(new GridLayout(0,2)); //as many rows as necessary
        firstCol = new JPanel();
        firstCol.setLayout(new BoxLayout(firstCol, BoxLayout.PAGE_AXIS));
        this.add(firstCol);

        secondCol = new JPanel();
        secondCol.setLayout(new BoxLayout(secondCol, BoxLayout.Y_AXIS));
        JPanel headerPanel = new JPanel();
        JLabel headerLabel = new JLabel("Basic Particle Options");
        headerLabel.setFont(new Font ("Arial", Font.BOLD, 20));
        headerPanel.add(headerLabel);
        headerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        headerPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        headerPanel.setMaximumSize(new Dimension(300,100));

        secondCol.add(headerPanel);
        this.add(secondCol);


        JPanel basicColorPanel = Config.getColorPickerPaneBuilder("basic particle Color",
                settings.BASIC_PARTICLE.basicColor, PowerMode3.ConfigType.BASIC_PARTICLE, settings);


        JPanel colorJPanel = new JPanel();
        colorJPanel.add(basicColorPanel);
        colorJPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        colorJPanel.setAlignmentX( Component.RIGHT_ALIGNMENT);//0.0
        colorJPanel.setMaximumSize(new Dimension(500, 50));
        secondCol.add(colorJPanel);


        this.maxParticleSizeField = new JTextField();
        JLabel infoLabel = new JLabel("max particle size (1-10)");
        JPanel maxSizePanel = new JPanel();
        maxSizePanel.add(infoLabel);
        maxSizePanel.add(maxParticleSizeField);
        maxSizePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        maxSizePanel.setAlignmentX( Component.RIGHT_ALIGNMENT);//0.0
        maxSizePanel.setMaximumSize(new Dimension(500, 50));

        secondCol.add(maxSizePanel);

        this.numParticlesField = new JTextField();
        infoLabel = new JLabel("num particles (1-10)");
        JPanel numParticlesPanel = new JPanel();
        numParticlesPanel.add(infoLabel);
        numParticlesPanel.add(numParticlesField);
        numParticlesPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        numParticlesPanel.setAlignmentX( Component.RIGHT_ALIGNMENT);//0.0
        numParticlesPanel.setMaximumSize(new Dimension(500, 50));

        secondCol.add(numParticlesPanel);


        //Col 1

        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        emitTopCheckBox = new JCheckBox("emit from top?", true);
        checkboxPanel.add(emitTopCheckBox);
        checkboxPanel.setMaximumSize(new Dimension(300, 50));
        checkboxPanel.setAlignmentX( Component.LEFT_ALIGNMENT );//0.0
//        checkboxPanel.setBackground(Color.cyan);
        firstCol.add(checkboxPanel);

        checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        emitBottomCheckBox = new JCheckBox("emit from bottom?", true);
        checkboxPanel.add(emitBottomCheckBox);
        checkboxPanel.setMaximumSize(new Dimension(300, 50));
//        checkboxPanel.setBackground(Color.cyan);
        checkboxPanel.setAlignmentX( Component.LEFT_ALIGNMENT );//0.0
        firstCol.add(checkboxPanel);


    }



    public void loadValues(){
        this.maxParticleSizeField.setText(String.valueOf(settings.BASIC_PARTICLE.maxParticleSize));
        this.numParticlesField.setText(String.valueOf(settings.BASIC_PARTICLE.numParticles));
        this.emitTopCheckBox.setSelected(settings.BASIC_PARTICLE.emitTop);
        this.emitBottomCheckBox.setSelected(settings.BASIC_PARTICLE.emitBot);
    }

    public void saveValues() throws ConfigurationException {

        settings.BASIC_PARTICLE.maxParticleSize =  Config.getJTextFieldWithinBoundsInt(this.maxParticleSizeField,
                1, 10,
                "max particle size");

        settings.BASIC_PARTICLE.numParticles  = Config.getJTextFieldWithinBoundsInt(this.numParticlesField,
                1, 10,
                "max number of particles");

        settings.BASIC_PARTICLE.emitTop = emitTopCheckBox.isSelected();
        settings.BASIC_PARTICLE.emitBot = emitBottomCheckBox.isSelected();

    }



}
