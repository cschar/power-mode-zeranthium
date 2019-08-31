package com.cschar.pmode3.config;

import com.cschar.pmode3.ParticleSpriteLightningAlt;
import com.cschar.pmode3.PowerMode3;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class LightningAltConfig extends BaseConfig {


    JCheckBox sparksEnabled;
    JComponent sparkConfigPanel;

    public LightningAltConfig(PowerMode3 settings){
        super(settings, PowerMode3.SpriteType.LIGHTNING_ALT, "Lightning Alt Options");


        JPanel sparksEnabledPanel = new JPanel();
        sparksEnabled = new JCheckBox("Sparks Enabled?");
        sparksEnabledPanel.add(sparksEnabled);
        sparksEnabledPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        sparksEnabledPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sparksEnabledPanel.setMaximumSize(new Dimension(500, 50));
//        sparksEnabledPanel.setBackground(Color.ORANGE);
        sparksEnabledPanel.setOpaque(true);
//        firstCol.setBackground(Color.GREEN);
        firstCol.setOpaque(true);
        firstCol.add(sparksEnabledPanel);



        sparkConfigPanel = createSparkConfig();
        firstCol.add(sparkConfigPanel);

//        secondCol.setBackground(Color.yellow);
        secondCol.setOpaque(true);

    }

    public JComponent createSparkConfig(){
//        JPanel sparkTable = new JPanel();
//        sparkTable.setLayout(new GridLayout(0,6)); //as many rows as necessary
//        sparkTable.setMaximumSize(new Dimension(400,500));
//        sparkTable.setBackground(Color.GRAY);
//        sparkTable.setOpaque(true);
//        // #, enabled, # of RR, image preview, custom path?, path
//        JPanel empty = new JPanel();
//        sparkTable.add(empty);
//        JCheckBox isRandom = new JCheckBox("is random?");
//        sparkTable.add(isRandom);
//        JCheckBox isRoundRobin = new JCheckBox("is RoundRObin?");
//        sparkTable.add(isRoundRobin);
//        JLabel preview = new JLabel("preview");
//        sparkTable.add(preview);
//        JLabel useCustom = new JLabel("use custom?");
//        sparkTable.add(useCustom);
//        JLabel useCustomPath = new JLabel("custom path:");
//        sparkTable.add(useCustomPath);
//
//        for(int i = 0; i < 10; i++){
//            for(int j = 0; j < 6; j++){
//                sparkTable.add(new JLabel(String.format("row %d-%d",i,j)));
//            }
//        }
//        return sparkTable;




        JBTable table = new JBTable();


        table.setRowHeight(60);
        table.setModel(new SparksTableModel());

//        table.setShowGrid(false);
        table.setBounds(30, 40, 400, 300);
        table.setCellSelectionEnabled(false);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(false);
//        table.setBorder(JBUI.Borders.empty(10));
        table.setPreferredScrollableViewportSize(new Dimension(400,
                table.getRowHeight() * LightningAltConfig.sparkData.length));
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
//        table.setBackground(Color.yellow);
//        table.setOpaque(true);
        // adding it to JScrollPane
        JScrollPane sp = ScrollPaneFactory.createScrollPane(table);
//        sp.setMaximumSize(new Dimension(470,350));

        sp.setOpaque(true);
//        sp.setAlignmentX(Component.RIGHT_ALIGNMENT);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        TableColumnModel colModel=table.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(80);
        colModel.getColumn(1).setPreferredWidth(120);
        colModel.getColumn(2).setPreferredWidth(300);


        //make table transparent
        table.setOpaque(false);
        table.setShowGrid(false);
        table.getTableHeader().setOpaque(false);

        ((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setOpaque(false);
//        ((DefaultTableCellRenderer)table.getDefaultRenderer(Integer.class)).setOpaque(false);
        ((DefaultTableCellRenderer)table.getDefaultRenderer(String.class)).setOpaque(false);
        ((DefaultTableCellRenderer)table.getDefaultRenderer(ImageIcon.class)).setOpaque(false);

        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());


        return sp;
//        return table;

    }


    public void loadValues(){
        super.loadValues();
        this.maxAlphaTextField.setText(String.valueOf(Config.getFloatProperty(settings, spriteType,"maxAlpha", 0.5f)));
        this.sparksEnabled.setSelected(Config.getBoolProperty(settings, PowerMode3.SpriteType.LIGHTNING_ALT,"sparksEnabled", true));

        //sparkData is loaded on settings instantiation
    }

    public void saveValues() throws ConfigurationException {
        super.saveValues();

        settings.setSpriteTypeProperty(PowerMode3.SpriteType.LIGHTNING_ALT, "sparksEnabled", String.valueOf(sparksEnabled.isSelected()));


        ParticleSpriteLightningAlt.sparkData = sparkData;
        settings.setSerializedSparkData(sparkData);
//        settings.setSerializedSparkData(SparksTableModel.data);
    }


    public static int CHANCE_PER_KEY_PRESS(PowerMode3 settings){
        return Config.getIntProperty(settings, PowerMode3.SpriteType.LIGHTNING_ALT, "chancePerKeyPress");
    }

    public static float MAX_ALPHA(PowerMode3 settings){
        return Config.getFloatProperty(settings, PowerMode3.SpriteType.LIGHTNING_ALT, "maxAlpha", 0.5f);
    }

    public static boolean SPARKS_ENABLED(PowerMode3 settings){
        return Config.getBoolProperty(settings, PowerMode3.SpriteType.LIGHTNING_ALT, "sparksEnabled");
    }

    static SparkData[] sparkData;

    public static void setSparkData(SparkData[] data){
        sparkData = data;
        ParticleSpriteLightningAlt.sparkData = data;
    }
}


class SparksTableModel extends AbstractTableModel {

    static SparkData[] data = LightningAltConfig.sparkData;


    private final String[] columnNames = new String[]{
            "preview",
            "enabled?",
            "weighted amount (1-100)",
//            "custom?",
//            "path"
    };




    private final Class[] columnClasses = new Class[]{
            ImageIcon.class, Boolean.class, Integer.class, Boolean.class, String.class
    };

    @Override
    public int getRowCount() {
        return data.length;
    }


    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnClasses[columnIndex];
    }


    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }


    @Override
    public Object getValueAt(int row, int column) {


//        ImageIcon.class, Boolean.class, Integer.class, Boolean.class, String.class



        switch (column) {
            case 0:
                return data[row].previewIcon;
            case 1:
                return data[row].enabled;
            case 2:
                return data[row].roundRobinAmount;
            case 3:
                return data[row].customPathEnabled;
            case 4:
                return data[row].path;
        }

        throw new IllegalArgumentException();
    }


    @Override
    public void setValueAt(Object value, int row, int column) {

//        ImageIcon.class, Boolean.class, Integer.class, Boolean.class, String.class


        switch (column) {
            case 0:  //image preview col
                return;
            case 1:  //enabled
                data[row].enabled = (Boolean) value;
                return;
            case 2: //round robin number, 1-->100
                int v = (Integer) value;
                v = Math.max(1, v);
                v = Math.min(v,100);
                data[row].roundRobinAmount = v;
                return;
            case 3:
                //custom path enabled
                data[row].customPathEnabled = (Boolean) value;
                return;
            case 4:  //new path entered
                return;


        }

        throw new IllegalArgumentException();
    }

}
