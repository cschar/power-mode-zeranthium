package com.cschar.pmode3.config;

import com.cschar.pmode3.ParticleSpriteMultiLayer;
import com.cschar.pmode3.ParticleSpriteMultiLayerChance;
import com.cschar.pmode3.PowerMode3;
import com.cschar.pmode3.config.common.SpriteDataAnimated;
import com.cschar.pmode3.config.common.ui.*;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.table.JBTable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EventObject;

public class MultiLayerChanceConfig extends BaseConfigPanel{


    JPanel firstRow;
    JComponent spriteConfigPanel;

    static ArrayList<SpriteDataAnimated> spriteDataAnimated;
    PowerMode3 settings;

    public final static int PREVIEW_SIZE = 80;

    public MultiLayerChanceConfig(PowerMode3 settings){
        this.settings = settings;

        this.setMaximumSize(new Dimension(1000,300));
        this.setLayout(new GridLayout(1,1)); //as many rows as necessary

        firstRow = new JPanel();
        firstRow.setMaximumSize(new Dimension(1000,500));
        firstRow.setLayout(new BoxLayout(firstRow, BoxLayout.Y_AXIS));
        this.add(firstRow);
        this.setupHeaderPanel("Multi Layer Chance Options", spriteDataAnimated);
        firstRow.add(headerPanel);
        spriteConfigPanel = createConfigTable();
        firstRow.add(spriteConfigPanel);
        firstRow.setOpaque(true);
    }


    public JComponent createConfigTable(){

        JBTable table = new JBTable();


        table.setRowHeight(PREVIEW_SIZE);
        table.setModel(new MultiLayerChanceTableModel(this));

        table.setCellSelectionEnabled(false);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(false);

        table.setPreferredScrollableViewportSize(new Dimension(400,
                table.getRowHeight() * 4));
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

        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        TableColumnModel colModel=table.getColumnModel();

        colModel.getColumn(0).setWidth(PREVIEW_SIZE); //preview
        colModel.getColumn(1).setPreferredWidth(70);  //enabled
        colModel.getColumn(1).setWidth(50);  //enabled

        colModel.getColumn(2).setPreferredWidth(50);  //scale
        colModel.getColumn(3).setPreferredWidth(80);  //speed rate
        colModel.getColumn(4).setPreferredWidth(100);  //set path
        colModel.getColumn(5).setPreferredWidth(50);  // path
        colModel.getColumn(6).setWidth(70);  //reset



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

        TableCellRenderer pathRenderer = new CustomPathCellHighlighterRenderer(spriteDataAnimated);
        table.getColumn("path").setCellRenderer(pathRenderer);



        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());
        return sp;
    }


    public void loadValues(){

//        this.moveWithCaret.setSelected(Config.getBoolProperty(settings, PowerMode3.ConfigType.MULTI_LAYER,"moveWithCaretEnabled", true));
//        this.moveSpeedTextField.setText(String.valueOf(Config.getFloatProperty(settings, PowerMode3.ConfigType.MULTI_LAYER,"moveSpeed", 0.1f)));

    }

    public void saveValues() throws ConfigurationException {

        settings.setSerializedSpriteDataAnimated(spriteDataAnimated, PowerMode3.ConfigType.MULTI_LAYER_CHANCE);
    }


//    static ArrayList<SpriteDataAnimated> spriteDataAnimated;

    public static void setSpriteDataAnimated(ArrayList<SpriteDataAnimated> data){
        spriteDataAnimated = data;
        ParticleSpriteMultiLayerChance.spriteDataAnimated = data;
    }



    public void loadJSONConfig(JSONObject configData, Path parentPath) throws JSONException {


        JSONArray tableData = configData.getJSONArray("tableData");

        int count = 0;
        for(int i =0; i<tableData.length(); i++){
            JSONObject jo = tableData.getJSONObject(i);


            //TODO make method more robust for all missing key values
            // put jo.has (x ) in a part loadJSONConfig method in parent class or something
            SpriteDataAnimated sd =  new SpriteDataAnimated(
                    PREVIEW_SIZE,
                    jo.getBoolean("enabled"),
                    (float) jo.getDouble("scale"),
                    jo.getInt("speedRate"),
                    spriteDataAnimated.get(i).defaultPath,
                    parentPath.resolve(jo.getString("customPath")).toString(),
                    jo.getBoolean("isCyclic"),
                    jo.has("val2") ? Math.max(1, Math.min(jo.getInt("val2"),10)) : 1,
                    (float) jo.getDouble("alpha"),
                    1);

            spriteDataAnimated.set(i, sd);
            count += 1;
        }

        for(int i = count; i < spriteDataAnimated.size(); i++){
            spriteDataAnimated.get(i).enabled = false;
        }
    }

}




class MultiLayerChanceTableModel extends AbstractConfigTableModel {

    static ArrayList<SpriteDataAnimated> data = MultiLayerChanceConfig.spriteDataAnimated;



    public static final String[] columnNames = new String[]{
            "preview",
            "enabled?",
            "scale",
            "speed rate",
            "set path",
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
            JButton.class,
    };

    public MultiLayerChanceTableModel(BaseConfigPanel config) {
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


        switch (column) {
            case 0:
//                return null;
//                return previewIcon;
                return data.get(row).previewIcon;
            case 1:
                return data.get(row).enabled;
            case 2:
                return data.get(row).scale;
            case 3:
                return data.get(row).speedRate;
            case 4:
                return this.getSetPathButton(data.get(row), data);
            case 5:
                return data.get(row).customPath;
            case 6:
                return this.getResetButton(data.get(row), data);
//            case 7:
//                SpriteDataAnimated d = data.get(row);
//                return new OtherColMultiLayer(d.val2, d.isCyclic);
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
                data.get(row).enabled = (Boolean) value;
                return;
            case 2:
                float v0 = (Float) value;
                v0 = Math.max(0.0f, v0);
                v0 = Math.min(v0, 2.0f);
                data.get(row).scale = v0;
                return;
            case 3:
                int v = (Integer) value;
                v = Math.max(1, v);
                v = Math.min(v,10);
                data.get(row).speedRate = v;
                return;
            case 4:   //button clicked
                return;
            case 5:   // custom path
                return;
            case 6:    //reset button clicked
                return;
//            case 7:    // other settings
//
//                OtherColMultiLayer c = (OtherColMultiLayer) value;
//                data.get(row).isCyclic = c.isCyclic;
//                data.get(row).val2 = c.numParticles;
//                return;


        }

        throw new IllegalArgumentException();
    }

}
