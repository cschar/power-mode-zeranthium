package com.cschar.pmode3.config;

import com.cschar.pmode3.PowerMode3;
import com.intellij.openapi.options.ConfigurationException;

import javax.swing.*;
import java.awt.*;

public class MOMAConfig extends JPanel {


    PowerMode3 settings;

    private JCheckBox oneSquareEnabledCheckBox;
    private JCheckBox twoSquareEnabledCheckBox;
    private JCheckBox threeSquareEnabledCheckBox;

    private JCheckBox emitTopCheckBox;
    private JCheckBox emitBottomCheckBox;


    private JTextField chanceOfLightningTextField;
    private static Color colorOne = Color.yellow;
    private static Color colorTwo = Color.red;
    private static Color colorThree = Color.WHITE;

    public MOMAConfig(PowerMode3 settings){
        this.settings = settings;



        this.setMaximumSize(new Dimension(1000,300));
        this.setLayout(new GridLayout(0,2));
        JPanel firstCol = new JPanel();
        firstCol.setLayout(new BoxLayout(firstCol, BoxLayout.PAGE_AXIS));
        this.add(firstCol);

        JPanel secondCol = new JPanel();
        secondCol.setLayout(new BoxLayout(secondCol, BoxLayout.Y_AXIS));
        JPanel headerPanel = new JPanel();
        JLabel headerLabel = new JLabel("MOMA Options");
        headerLabel.setFont(new Font ("Arial", Font.BOLD, 20));
        headerPanel.add(headerLabel);
        headerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        headerPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        headerPanel.setMaximumSize(new Dimension(300,100));
        secondCol.add(headerPanel);
        this.add(secondCol);


        //save values so we can check if we need to reload sprites
        String colorRGBInner = settings.getSpriteTypeProperty(PowerMode3.ConfigType.LIGHTNING, "one Square Color");
        if(colorRGBInner != null){
            this.colorOne =  new Color(Integer.parseInt(colorRGBInner));
        }
        String colorRGBOuter = settings.getSpriteTypeProperty(PowerMode3.ConfigType.LIGHTNING, "two Square Color");
        if(colorRGBOuter != null){
            this.colorTwo =  new Color(Integer.parseInt(colorRGBOuter));
        }







        JPanel oneSquareJPanel = new JPanel();
        this.oneSquareEnabledCheckBox = new JCheckBox("is enabled?", true);
        oneSquareJPanel.add(oneSquareEnabledCheckBox);
        JPanel oneSquareColorPanel = Config.getColorPickerPanel("one Square Color", PowerMode3.ConfigType.MOMA, settings, this.colorOne);
        oneSquareJPanel.add(oneSquareColorPanel);
        oneSquareJPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        oneSquareJPanel.setAlignmentX( Component.RIGHT_ALIGNMENT);//0.0
        oneSquareJPanel.setMaximumSize(new Dimension(500, 50));
        secondCol.add(oneSquareJPanel);

        JPanel twoSquareJPanel = new JPanel();
        this.twoSquareEnabledCheckBox = new JCheckBox("is enabled?", true);
        twoSquareJPanel.add(twoSquareEnabledCheckBox);
        JPanel twoSquareColorPanel = Config.getColorPickerPanel("two Square Color", PowerMode3.ConfigType.MOMA, settings, this.colorTwo);
        twoSquareJPanel.add(twoSquareColorPanel);
        twoSquareJPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        twoSquareJPanel.setAlignmentX( Component.RIGHT_ALIGNMENT);//0.0
        twoSquareJPanel.setMaximumSize(new Dimension(500, 50));
        secondCol.add(twoSquareJPanel);

        JPanel threeSquareJPanel = new JPanel();
        this.threeSquareEnabledCheckBox = new JCheckBox("is enabled?", true);
        threeSquareJPanel.add(threeSquareEnabledCheckBox);
        JPanel threeSquareColorPanel = Config.getColorPickerPanel("three Square Color", PowerMode3.ConfigType.MOMA, settings, this.colorThree);
        threeSquareJPanel.add(threeSquareColorPanel);
        threeSquareJPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        threeSquareJPanel.setAlignmentX( Component.RIGHT_ALIGNMENT);//0.0
        threeSquareJPanel.setMaximumSize(new Dimension(500, 50));
        secondCol.add(threeSquareJPanel);


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


        this.oneSquareEnabledCheckBox.setSelected(Config.getBoolProperty(settings, PowerMode3.ConfigType.MOMA,"oneSquareEnabled", true));
        this.twoSquareEnabledCheckBox.setSelected(Config.getBoolProperty(settings, PowerMode3.ConfigType.MOMA,"twoSquareEnabled", true));
        this.threeSquareEnabledCheckBox.setSelected(Config.getBoolProperty(settings, PowerMode3.ConfigType.MOMA,"threeSquareEnabled", true));

        this.emitTopCheckBox.setSelected(Config.getBoolProperty(settings, PowerMode3.ConfigType.MOMA,"emitTopEnabled", true));
        this.emitBottomCheckBox.setSelected(Config.getBoolProperty(settings, PowerMode3.ConfigType.MOMA,"emitBottomEnabled", true));

    }

    public void saveValues() throws ConfigurationException {


        settings.setSpriteTypeProperty(PowerMode3.ConfigType.MOMA, "oneSquareEnabled", String.valueOf(oneSquareEnabledCheckBox.isSelected()));
        settings.setSpriteTypeProperty(PowerMode3.ConfigType.MOMA, "twoSquareEnabled", String.valueOf(twoSquareEnabledCheckBox.isSelected()));
        settings.setSpriteTypeProperty(PowerMode3.ConfigType.MOMA, "threeSquareEnabled", String.valueOf(twoSquareEnabledCheckBox.isSelected()));

        settings.setSpriteTypeProperty(PowerMode3.ConfigType.MOMA, "emitTopEnabled", String.valueOf(emitTopCheckBox.isSelected()));
        settings.setSpriteTypeProperty(PowerMode3.ConfigType.MOMA, "emitBottomEnabled", String.valueOf(emitBottomCheckBox.isSelected()));

    }




    public static boolean ONE_SQUARE_ENABLED(PowerMode3 settings){
        return Config.getBoolProperty(settings, PowerMode3.ConfigType.MOMA, "oneSquareEnabled");
    }

    public static boolean TWO_SQUARE_ENABLED(PowerMode3 settings){
        return Config.getBoolProperty(settings, PowerMode3.ConfigType.MOMA, "twoSquareEnabled");
    }

    public static boolean THREE_SQUARE_ENABLED(PowerMode3 settings){
        return Config.getBoolProperty(settings, PowerMode3.ConfigType.MOMA, "threeSquareEnabled");
    }

    public static Color ONE_SQUARE_COLOR(PowerMode3 settings){
        return Config.getColorProperty(settings, PowerMode3.ConfigType.MOMA, "one Square Color", colorOne);
//        String RGB = settings.getSpriteTypeProperty(PowerMode3.SpriteType.MOMA, "one Square Color");
//        return new Color(Integer.parseInt(RGB));
    }

    public static Color TWO_SQUARE_COLOR(PowerMode3 settings){
        return Config.getColorProperty(settings, PowerMode3.ConfigType.MOMA, "two Square Color", colorTwo);
//        String RGB = settings.getSpriteTypeProperty(PowerMode3.SpriteType.MOMA, "two Square Color");
//        return new Color(Integer.parseInt(RGB));

    }

    public static Color THREE_SQUARE_COLOR(PowerMode3 settings){
        return Config.getColorProperty(settings, PowerMode3.ConfigType.MOMA, "three Square Color", colorThree);
//        String RGB = settings.getSpriteTypeProperty(PowerMode3.SpriteType.MOMA, "three Square Color");
//        return new Color(Integer.parseInt(RGB));

    }

    public static boolean EMIT_TOP(PowerMode3 settings){
        return Config.getBoolProperty(settings, PowerMode3.ConfigType.MOMA, "emitTopEnabled");
    }

    public static boolean EMIT_BOTTOM(PowerMode3 settings){
        return Config.getBoolProperty(settings, PowerMode3.ConfigType.MOMA, "emitBottomEnabled");
    }

}
