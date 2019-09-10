package com.cschar.pmode3.config;

import com.cschar.pmode3.PowerMode3;
import com.intellij.openapi.options.ConfigurationException;

import javax.swing.*;
import java.awt.*;


public class BasicParticleConfig extends JPanel{






    PowerMode3 settings;


    private static Color originalBasicColor = Color.CYAN;

    JPanel firstCol;
    JPanel secondCol;

    private JTextField maxParticleSizeField;
    private JTextField numParticlesField;

    private JCheckBox emitTopCheckBox;
    private JCheckBox emitBottomCheckBox;

    enum K {
        MAX_PARTICLE_SIZE,
        NUM_PARTICLES,
        EMIT_TOP,
        EMIT_BOT
    }

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


        JPanel basicColorPanel = Config.getColorPickerPanel("basic particle Color", PowerMode3.SpriteType.BASIC_PARTICLE, settings, BasicParticleConfig.originalBasicColor);


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
        this.maxParticleSizeField.setText(String.valueOf(Config.getIntProperty(settings,
                PowerMode3.SpriteType.BASIC_PARTICLE, K.MAX_PARTICLE_SIZE.toString(), 5)));
        this.numParticlesField.setText(String.valueOf(Config.getIntProperty(settings,
                PowerMode3.SpriteType.BASIC_PARTICLE, K.NUM_PARTICLES.toString(), 7)));

        this.emitTopCheckBox.setSelected(Config.getBoolProperty(settings, PowerMode3.SpriteType.BASIC_PARTICLE, K.EMIT_TOP.name(), true));
        this.emitBottomCheckBox.setSelected(Config.getBoolProperty(settings, PowerMode3.SpriteType.BASIC_PARTICLE, K.EMIT_BOT.name(), true));
    }

    public void saveValues() throws ConfigurationException {

        int maxParticleSize = Config.getJTextFieldWithinBoundsInt(this.maxParticleSizeField,
                1, 10,
                "max particle size");
        settings.setSpriteTypeProperty(PowerMode3.SpriteType.BASIC_PARTICLE, K.MAX_PARTICLE_SIZE.toString(),
                String.valueOf(maxParticleSize));

        int numParticles = Config.getJTextFieldWithinBoundsInt(this.numParticlesField,
                1, 10,
                "max number of particles");
        settings.setSpriteTypeProperty(PowerMode3.SpriteType.BASIC_PARTICLE, K.NUM_PARTICLES.toString(),
                String.valueOf(numParticles));

        settings.setSpriteTypeProperty(PowerMode3.SpriteType.BASIC_PARTICLE, K.EMIT_TOP.name(), String.valueOf(emitTopCheckBox.isSelected()));
        settings.setSpriteTypeProperty(PowerMode3.SpriteType.BASIC_PARTICLE, K.EMIT_BOT.name(), String.valueOf(emitBottomCheckBox.isSelected()));
    }

    public static Color BASIC_PARTICLE_COLOR(PowerMode3 settings){
        return Config.getColorProperty(settings, PowerMode3.SpriteType.BASIC_PARTICLE, "basic particle Color", BasicParticleConfig.originalBasicColor);
    }

    public static int MAX_PARTICLE_SIZE(PowerMode3 settings){
        return Config.getIntProperty(settings, PowerMode3.SpriteType.BASIC_PARTICLE, K.MAX_PARTICLE_SIZE.toString(), 5);
    }

    public static boolean EMIT_TOP(PowerMode3 settings){
        return Config.getBoolProperty(settings, PowerMode3.SpriteType.BASIC_PARTICLE, K.EMIT_TOP.name());
    }

    public static boolean EMIT_BOTTOM(PowerMode3 settings){
        return Config.getBoolProperty(settings, PowerMode3.SpriteType.BASIC_PARTICLE, K.EMIT_BOT.name());
    }

    //TODO: test idea
    // replace with static public int that is initially
    // set in some static method called inside powermode3 onLoad?

    public static int NUM_PARTICLES(PowerMode3 settings){
        return Config.getIntProperty(settings, PowerMode3.SpriteType.BASIC_PARTICLE, K.NUM_PARTICLES.name());
    }

}
