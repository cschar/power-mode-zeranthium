package com.cschar.pmode3.config;

import com.cschar.pmode3.ParticleSpritePasteShape;
import com.cschar.pmode3.PowerMode3;
import com.cschar.pmode3.config.common.SpriteDataAnimated;
import com.cschar.pmode3.config.common.ui.AbstractConfigTableModel;
import com.cschar.pmode3.config.common.ui.CustomPathCellHighlighterRenderer;
import com.cschar.pmode3.config.common.ui.JTableButtonMouseListener;
import com.cschar.pmode3.config.common.ui.JTableButtonRenderer;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.table.JBTable;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.nio.file.Path;
import java.util.ArrayList;

public class CopyPasteVoidConfig extends BaseConfigJPanel {


    PowerMode3 settings;


    private JTextField fadeAmountTextField;
    private JCheckBox fadeColorEnabledCheckBox;

    private static Color fadeColor = Color.GREEN;
    public static ArrayList<SpriteDataAnimated> spriteDataAnimated;

    public CopyPasteVoidConfig(PowerMode3 settings){
        this.settings = settings;


        this.setMaximumSize(new Dimension(1000,300));
        this.setLayout(new GridLayout(1,2));
        JPanel firstCol = new JPanel();
        firstCol.setLayout(new BoxLayout(firstCol, BoxLayout.PAGE_AXIS));
        this.add(firstCol);

        JPanel secondCol = new JPanel();
        secondCol.setLayout(new BoxLayout(secondCol, BoxLayout.Y_AXIS));

        this.setupHeaderPanel("CopyPasteVoid Options", spriteDataAnimated);
        secondCol.add(headerPanel);
        this.add(secondCol);

        this.fadeAmountTextField = new JTextField();
        JPanel wobbleConfig = Config.populateTextFieldPanel(this.fadeAmountTextField, "Fade amount (0.01 - 0.1)");
        secondCol.add(wobbleConfig);

        JPanel fadeColorJPanel = new JPanel();
        this.fadeColorEnabledCheckBox = new JCheckBox("is enabled?", true);
        fadeColorJPanel.add(fadeColorEnabledCheckBox);
        JPanel fadeColorPanel = Config.getColorPickerPanel("fade Color", PowerMode3.ConfigType.COPYPASTEVOID, settings, fadeColor);
        fadeColorJPanel.add(fadeColorPanel);
        fadeColorJPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        fadeColorJPanel.setAlignmentX(RIGHT_ALIGNMENT);//0.0
        fadeColorJPanel.setMaximumSize(new Dimension(500, 50));
        secondCol.add(fadeColorJPanel);

//        JPanel lizardColorPanel = Config.getColorPickerPanel("Lizard Color", PowerMode3.SpriteType.LIZARD, settings, Color.GREEN);
//        secondCol.add(lizardColorPanel);




        JComponent j = createConfigTable();
        firstCol.add(j);

        this.loadValues();

    }



    public JComponent createConfigTable(){

        JBTable table = new JBTable();


        table.setRowHeight(60);
        table.setModel(new CopyPasteVoidTableModel(this));

        table.setCellSelectionEnabled(false);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(false);

        table.setPreferredScrollableViewportSize(new Dimension(400,
                table.getRowHeight() * 2));
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
//        table.setBackground(Color.yellow);
//        table.setOpaque(true);
        // adding it to JScrollPane
        JScrollPane sp = ScrollPaneFactory.createScrollPane(table);
//        sp.setMaximumSize(new Dimension(470,350));

        sp.setOpaque(true);
//        sp.setAlignmentX(Component.RIGHT_ALIGNMENT);
//        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
//        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        TableColumnModel colModel=table.getColumnModel();

        colModel.getColumn(0).setPreferredWidth(60); //preview
        colModel.getColumn(1).setPreferredWidth(70);  //enabled
        colModel.getColumn(1).setWidth(50);  //enabled

        colModel.getColumn(2).setPreferredWidth(50);  //scale
        colModel.getColumn(3).setPreferredWidth(80);  //speed rate
        colModel.getColumn(4).setPreferredWidth(100);  //set path
        colModel.getColumn(5).setPreferredWidth(50);  // path
        colModel.getColumn(6).setPreferredWidth(70);  //reset
        colModel.getColumn(6).setWidth(60);  //reset
        colModel.getColumn(7).setPreferredWidth(60);  //alpha
        colModel.getColumn(7).setWidth(60);


        //make table transparent
        table.setOpaque(false);
        table.setShowGrid(false);
        table.getTableHeader().setOpaque(false);

        ((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setOpaque(false);
        ((DefaultTableCellRenderer)table.getDefaultRenderer(String.class)).setOpaque(false);


        TableCellRenderer buttonRenderer = new JTableButtonRenderer();
        table.getColumn("set path").setCellRenderer(buttonRenderer);
        table.getColumn("reset").setCellRenderer(buttonRenderer);
        table.addMouseListener(new JTableButtonMouseListener(table));


        TableCellRenderer pathRenderer = new CustomPathCellHighlighterRenderer(CopyPasteVoidConfig.spriteDataAnimated);
        table.getColumn("path").setCellRenderer(pathRenderer);


        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());
        return sp;
    }

    /**
     * @exampleConfig
     *      //TODO
     */
    private void loadJSONConfig() {
        // placeholder
    }


    public void loadValues(){
        this.fadeColorEnabledCheckBox.setSelected(Config.getBoolProperty(settings, PowerMode3.ConfigType.COPYPASTEVOID,"fadeColorEnabled", true));
        this.fadeAmountTextField.setText(String.valueOf(Config.getFloatProperty(settings, PowerMode3.ConfigType.COPYPASTEVOID,"fadeAmount", 0.05f)));

    }

    public void saveValues() throws ConfigurationException {


        settings.setSpriteTypeProperty(PowerMode3.ConfigType.COPYPASTEVOID, "fadeColorEnabled", String.valueOf(fadeColorEnabledCheckBox.isSelected()));

        float fadeAmount = Config.getJTextFieldWithinBoundsFloat(this.fadeAmountTextField,
                0.01f, 0.1f,"fadeAmount");
        settings.setSpriteTypeProperty(PowerMode3.ConfigType.COPYPASTEVOID, "fadeAmount",
                String.valueOf(fadeAmount));

        settings.setSerializedSDAJsonInfo(CopyPasteVoidConfig.spriteDataAnimated, PowerMode3.ConfigType.COPYPASTEVOID);
    }



    public static float FADE_AMOUNT(PowerMode3 settings){
        return Config.getFloatProperty(settings, PowerMode3.ConfigType.COPYPASTEVOID, "fadeAmount",0.05f);
    }

    public static Color FADE_COLOR(PowerMode3 settings){
        return Config.getColorProperty(settings, PowerMode3.ConfigType.COPYPASTEVOID, "fade Color", fadeColor);
    }

    public static boolean FADE_ENABLED(PowerMode3 settings){
        return Config.getBoolProperty(settings, PowerMode3.ConfigType.COPYPASTEVOID, "fadeColorEnabled", true);
    }



    public static void setSpriteDataAnimated(ArrayList<SpriteDataAnimated> data){
        spriteDataAnimated = data;
        ParticleSpritePasteShape.spriteDataAnimated = data;
    }
}


class CopyPasteVoidTableModel extends AbstractConfigTableModel {

    static ArrayList<SpriteDataAnimated> data = CopyPasteVoidConfig.spriteDataAnimated;



    public static final String[] columnNames = new String[]{
            "preview",
            "enabled?",
            "scale",
            "weight",
//            "weighted amount (1-100)",

            "set path",
            "path",
            "reset",
            "alpha",
            "center"

    };





    private final Class[] columnClasses = new Class[]{
            ImageIcon.class,
            Boolean.class,
            Float.class,
            Integer.class,
            JButton.class,
            String.class,
            JButton.class,
            Float.class,
            Boolean.class
    };

    public CopyPasteVoidTableModel(BaseConfigJPanel config) {
        super(config);
    }

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
        SpriteDataAnimated d = data.get(row);
        
        switch (column) {
            case 0:
//                return null;
//                return previewIcon;
                return d.previewIcon;
            case 1:
                return d.enabled;
            case 2:
                return d.scale;
            case 3:
                return d.val1;
            case 4:
                return this.getSetPathButton(d, data);
            case 5:
                return data.get(row).customPath;
            case 6:
                return this.getResetButton(d, data);
            case 7:
                return d.alpha;
//                SpriteDataAnimated d = data.get(row);
//                return new OtherCol(d.maxNumParticles, d.isCyclic);
            case 8:
                return d.isCyclic;
        }

        throw new IllegalArgumentException();
    }



    @Override
    public void setValueAt(Object value, int row, int column) {

//        ImageIcon.class, Boolean.class, Integer.class, Boolean.class, String.class

        SpriteDataAnimated d = data.get(row);

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
                d.scale = f;
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
            case 7:    // other settings

                float alpha = (Float) value;
                alpha = Math.max(0.0f, alpha);
                alpha = Math.min(alpha,1.0f);
                data.get(row).alpha = alpha;
                return;
            case 8:
                d.isCyclic = (Boolean) value;
                return;

        }

        throw new IllegalArgumentException();
    }

}