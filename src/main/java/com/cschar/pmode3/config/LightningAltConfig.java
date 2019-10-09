package com.cschar.pmode3.config;

import com.cschar.pmode3.ParticleSpriteLightningAlt;
import com.cschar.pmode3.PowerMode3;
import com.cschar.pmode3.config.common.ui.CustomPathCellHighlighterRenderer;
import com.cschar.pmode3.config.common.ui.JTableButtonMouseListener;
import com.cschar.pmode3.config.common.ui.JTableButtonRenderer;
import com.cschar.pmode3.config.common.SpriteData;
import com.cschar.pmode3.config.common.ui.ZeranthiumColors;
import com.intellij.openapi.fileChooser.*;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.ArrayList;

public class LightningAltConfig extends JPanel{


    JCheckBox sparksEnabled;
    JComponent sparkConfigPanel;

    PowerMode3 settings;

    JTextField maxAlphaTextField;
    JTextField chancePerKeyPressTextField;

    JPanel firstCol;
    JPanel secondCol;

    public LightningAltConfig(PowerMode3 settings){



        this.settings = settings;

        this.setMaximumSize(new Dimension(1000,300));
        this.setLayout(new GridLayout(0,2)); //as many rows as necessary
        firstCol = new JPanel();
        firstCol.setLayout(new BoxLayout(firstCol, BoxLayout.PAGE_AXIS));
        this.add(firstCol);

        secondCol = new JPanel();
        secondCol.setLayout(new BoxLayout(secondCol, BoxLayout.Y_AXIS));
        JPanel headerPanel = new JPanel();
        JLabel headerLabel = new JLabel("Lightning Alt Options");
        headerLabel.setFont(new Font ("Arial", Font.BOLD, 20));
        headerPanel.add(headerLabel);
        headerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        headerPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        headerPanel.setMaximumSize(new Dimension(300,100));

        secondCol.add(headerPanel);
        this.add(secondCol);


        JPanel maxAlpha = new JPanel();
        maxAlpha.setLayout(new FlowLayout(FlowLayout.RIGHT));
        maxAlpha.add(new JLabel("Max Alpha: (0.1 - 1.0)"));
        this.maxAlphaTextField = new JTextField("");
        this.maxAlphaTextField.setMinimumSize(new Dimension(50,20));
        maxAlpha.add(maxAlphaTextField);
        maxAlpha.setAlignmentX( Component.RIGHT_ALIGNMENT);//0.0
        maxAlpha.setMaximumSize(new Dimension(500, 50));
        maxAlpha.setLayout(new FlowLayout(FlowLayout.RIGHT));
        secondCol.add(maxAlpha);


        this.chancePerKeyPressTextField = new JTextField();
        JLabel chancePerKeyPressLabel = new JLabel("Chance per keypress (0-100)");
        JPanel chancePerKeyPressPanel = new JPanel();
        chancePerKeyPressPanel.add(chancePerKeyPressLabel);
        chancePerKeyPressPanel.add(chancePerKeyPressTextField);
        chancePerKeyPressPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        chancePerKeyPressPanel.setAlignmentX( Component.RIGHT_ALIGNMENT);//0.0
        chancePerKeyPressPanel.setMaximumSize(new Dimension(400, 50));
        chancePerKeyPressPanel.setBackground(ZeranthiumColors.specialOption2);
        secondCol.add(chancePerKeyPressPanel);



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



        sparkConfigPanel = createSparkConfigTable();
        firstCol.add(sparkConfigPanel);


        secondCol.setOpaque(true);

    }

    public JComponent createSparkConfigTable(){

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
                table.getRowHeight() * LightningAltConfig.sparkData.size()));
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
        colModel.getColumn(1).setPreferredWidth(80);
        colModel.getColumn(2).setPreferredWidth(60);
        colModel.getColumn(3).setPreferredWidth(80);
        colModel.getColumn(4).setPreferredWidth(100);
        colModel.getColumn(5).setPreferredWidth(50);



        //make table transparent
        table.setOpaque(false);
        table.setShowGrid(false);
        table.getTableHeader().setOpaque(false);

        ((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setOpaque(false);
//        ((DefaultTableCellRenderer)table.getDefaultRenderer(Integer.class)).setOpaque(false);
        ((DefaultTableCellRenderer)table.getDefaultRenderer(String.class)).setOpaque(false);
//        ((DefaultTableCellRenderer)table.getDefaultRenderer(ImageIcon.class)).setOpaque(false);


        TableCellRenderer buttonRenderer = new JTableButtonRenderer();
        table.getColumn("set path (Single PNG)").setCellRenderer(buttonRenderer);
        table.getColumn("reset").setCellRenderer(buttonRenderer);
        table.addMouseListener(new JTableButtonMouseListener(table));

        TableCellRenderer pathRenderer = new CustomPathCellHighlighterRenderer(LightningAltConfig.sparkData);
        table.getColumn("path").setCellRenderer(pathRenderer);



        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());


        return sp;
//        return table;

    }


    public void loadValues(){


        this.chancePerKeyPressTextField.setText(String.valueOf(Config.getIntProperty(settings, PowerMode3.ConfigType.LIGHTNING_ALT,"chancePerKeyPress", 10)));
        this.maxAlphaTextField.setText(String.valueOf(Config.getFloatProperty(settings, PowerMode3.ConfigType.LIGHTNING_ALT,"maxAlpha", 0.5f)));
        this.sparksEnabled.setSelected(Config.getBoolProperty(settings, PowerMode3.ConfigType.LIGHTNING_ALT,"sparksEnabled", true));

        //sparkData is loaded on settings instantiation
    }

    public void saveValues() throws ConfigurationException {


        int chancePerKeyPress = Config.getJTextFieldWithinBoundsInt(this.chancePerKeyPressTextField,
                0, 100,
                "chance particle spawns per keypress");
        settings.setSpriteTypeProperty(PowerMode3.ConfigType.LIGHTNING_ALT, "chancePerKeyPress",
                String.valueOf(chancePerKeyPress));


        settings.setSpriteTypeProperty(PowerMode3.ConfigType.LIGHTNING_ALT, "sparksEnabled", String.valueOf(sparksEnabled.isSelected()));

        float maxAlpha = Config.getJTextFieldWithinBoundsFloat(this.maxAlphaTextField,
                0.0f, 1.0f, "Max alpha");
        settings.setSpriteTypeProperty(PowerMode3.ConfigType.LIGHTNING_ALT, "maxAlpha",
                String.valueOf(maxAlpha));


        ParticleSpriteLightningAlt.sparkData = sparkData;
        settings.setSerializedSpriteData(LightningAltConfig.sparkData, PowerMode3.ConfigType.LIGHTNING_ALT);
//        settings.setSerializedSparkData(SparksTableModel.data);
    }


    public static int CHANCE_PER_KEY_PRESS(PowerMode3 settings){
        return Config.getIntProperty(settings, PowerMode3.ConfigType.LIGHTNING_ALT, "chancePerKeyPress");
    }

    public static float MAX_ALPHA(PowerMode3 settings){
        return Config.getFloatProperty(settings, PowerMode3.ConfigType.LIGHTNING_ALT, "maxAlpha", 0.5f);
    }

    public static boolean SPARKS_ENABLED(PowerMode3 settings){
        return Config.getBoolProperty(settings, PowerMode3.ConfigType.LIGHTNING_ALT, "sparksEnabled");
    }

    static ArrayList<SpriteData> sparkData;

    public static void setSparkData(ArrayList<SpriteData> data){
        sparkData = data;
        ParticleSpriteLightningAlt.sparkData = data;
    }
}


class SparksTableModel extends AbstractTableModel {

    static ArrayList<SpriteData> data = LightningAltConfig.sparkData;



    public static final String[] columnNames = new String[]{
            "preview",
            "enabled?",
            "scale",
            "weight",
//            "weighted amount (1-100)",

            "set path (Single PNG)",
            "path",
            "reset"

    };





    private final Class[] columnClasses = new Class[]{
            ImageIcon.class,
            Boolean.class,
            Float.class,
            Integer.class,
            JButton.class,
            String.class,
            JButton.class
    };

    @Override
    public int getRowCount() {



        return data.size();
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
        switch (column) {
            case 0:
            case 5:
                return false;
            default:
                return true;
        }
    }


    @Override
    public Object getValueAt(int row, int column) {

//https://stackoverflow.com/questions/13833688/adding-jbutton-to-jtable
//        ImageIcon.class, Boolean.class, Integer.class, Boolean.class, String.class
//
        SpriteData d = data.get(row);

        switch (column) {
            case 0:
//                return previewIcon;
                return d.previewIcon;
            case 1:
                return d.enabled;
            case 2:
                return d.scale;
            case 3:
                return d.val1;
            case 4:
                final JButton button = new JButton("Set path");
                button.addActionListener(arg0 -> {


                    FileChooserDescriptor fd = new FileChooserDescriptor(true,false,false,false,false,false);
                    fd.setForcedToUseIdeaFileChooser(true);
                    FileChooserDialog fcDialog = FileChooserFactory.getInstance().createFileChooser(fd, null, null);


                    VirtualFile[] vfs = fcDialog.choose(null);
                    if(vfs.length != 0){
                        d.customPath = vfs[0].getPath();
                        d.setImage(vfs[0].getPath(), false);

                        this.fireTableDataChanged();
                    }

                });
                return button;
            case 5:
                return d.customPath;
            case 6:
                final JButton resetButton = new JButton("reset");
                resetButton.addActionListener(arg0 -> {
                    d.setImage(d.defaultPath, true);
                    d.customPath = "";
                    d.customPathValid = false;

                    this.fireTableDataChanged();
                });
                return resetButton;


        }

        throw new IllegalArgumentException();
    }



    @Override
    public void setValueAt(Object value, int row, int column) {

//        ImageIcon.class, Boolean.class, Integer.class, Boolean.class, String.class

        SpriteData d = data.get(row);

        switch (column) {
            case 0:  //image preview col
                return;
            case 1:  //enabled
                d.enabled = (Boolean) value;
                return;
            case 2:
                float f = (Float) value;
                f = Math.max(0.0f, f);
                f = Math.min(f,2.0f);
                d.scale = (Float) f;
                return;
            case 3: //round robin number, 1-->100
                int v = (Integer) value;
                v = Math.max(1, v);
                v = Math.min(v,100);
                d.val1 = v;
                return;
            case 4:   //button clicked
                return;
            case 5:   // custom path
                return;
            case 6:    //reset button clicked
                return;


        }

        throw new IllegalArgumentException();
    }

}
