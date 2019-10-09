package com.cschar.pmode3.config;

import com.cschar.pmode3.ParticleSpriteDroste;
import com.cschar.pmode3.ParticleSpriteLinkerAnchor;
import com.cschar.pmode3.PowerMode3;
import com.cschar.pmode3.config.common.SpriteDataAnimated;
import com.cschar.pmode3.config.common.ui.AbstractConfigTableModel;
import com.cschar.pmode3.config.common.ui.CustomPathCellHighlighterRenderer;
import com.cschar.pmode3.config.common.ui.JTableButtonMouseListener;
import com.cschar.pmode3.config.common.ui.JTableButtonRenderer;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.table.JBTable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.nio.file.Path;
import java.util.ArrayList;

public class DrosteConfig extends BaseConfigPanel {

    JPanel mainPanel;
    PowerMode3 settings;



    private static Color originalTracerColor = Color.WHITE;
    private JComponent drosteSpriteConfigPanel;
    static ArrayList<SpriteDataAnimated> spriteDataAnimated;

    public final static int PREVIEW_SIZE = 120;

    public DrosteConfig(PowerMode3 settings){
        this.settings = settings;


        this.setLayout(new GridLayout(1,1));



        JPanel firstRow =  new JPanel();
        firstRow.setLayout(new BoxLayout(firstRow, BoxLayout.PAGE_AXIS));
        firstRow.setMaximumSize(new Dimension(1000,300));

        this.setupHeaderPanel("Droste Options", spriteDataAnimated);
        firstRow.add(headerPanel);


        drosteSpriteConfigPanel = createConfigTable();
        firstRow.add(drosteSpriteConfigPanel);
        this.add(firstRow);
//        this.add(drosteSpriteConfigPanel);

        this.loadValues();

    }



    public JComponent createConfigTable(){

        JBTable table = new JBTable();


        table.setRowHeight(PREVIEW_SIZE);
        table.setModel(new DrosteTableModel(this));

        table.setCellSelectionEnabled(false);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);

        table.setPreferredScrollableViewportSize(new Dimension(400,
                table.getRowHeight() * 2));

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

        colModel.getColumn(0).setWidth(PREVIEW_SIZE); //preview
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

        TableCellRenderer pathRenderer = new CustomPathCellHighlighterRenderer(DrosteConfig.spriteDataAnimated);
        table.getColumn("path").setCellRenderer(pathRenderer);


        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());
        return sp;
    }


    public void loadValues(){

//        ParticleSpriteLinkerAnchor.cyclicToggleEnabled = isCyclicEnabled.isSelected();

    }

    public void saveValues() throws ConfigurationException {

        settings.setSerializedSpriteDataAnimated(DrosteConfig.spriteDataAnimated, PowerMode3.ConfigType.DROSTE);
    }



    public static void setSpriteDataAnimated(ArrayList<SpriteDataAnimated> data){
        spriteDataAnimated = data;
        ParticleSpriteDroste.spriteDataAnimated = data;
    }

    public static void loadJSONConfig(JSONObject configData, Path parentPath) throws JSONException {

        JSONArray tableData = configData.getJSONArray("tableData");

        for(int i =0; i<tableData.length(); i++){
            JSONObject jo = tableData.getJSONObject(i);

            SpriteDataAnimated sd =  new SpriteDataAnimated(
                    PREVIEW_SIZE,
                    jo.getBoolean("enabled"),
                    (float) jo.getDouble("scale"),
                    jo.getInt("speedRate"),
                    spriteDataAnimated.get(i).defaultPath,
                    parentPath.resolve(jo.getString("customPath")).toString(),
                    jo.getBoolean("isCyclic"),
                    1,
//                    jo.getInt("val2"),
                    (float) jo.getDouble("alpha"),
                    jo.getInt("offset")); //val1

        //TODO take care of droste Only 1 at a time layer rule
            spriteDataAnimated.set(i, sd);
        }
    }
}


class DrosteTableModel extends AbstractConfigTableModel {

    static ArrayList<SpriteDataAnimated> data = DrosteConfig.spriteDataAnimated;



    public static final String[] columnNames = new String[]{
            "preview",
            "enabled?",
            "scale",
            "speed",
//            "weighted amount (1-100)",

            "set path",
            "path",
            "reset",
            "alpha",
            "offset (10-400)",
            "is cyclic"

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
            Integer.class,
            Boolean.class
    };

    public DrosteTableModel(BaseConfigPanel config) {
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
        
        SpriteDataAnimated d = data.get(row);
        
        switch (column) {
            case 0:
                return d.previewIcon;
            case 1:
                return d.enabled;
            case 2:
                return d.scale;
            case 3:
                return d.speedRate;
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
            case 8: // weight --> offset
                return d.val1;
            case 9: // maxParticles --> repeatEvery
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
                //set others to false, only allow one droste effect at a time
                boolean isSettingToTrue = (Boolean) value;
                for(int others = 0; others < data.size(); others++){
                    if(others != row && isSettingToTrue){
                        data.get(others).enabled = false;
                    };
                }
                this.fireTableDataChanged();
                d.enabled = isSettingToTrue;
                return;
            case 2:  //sprite scale
                float f = (Float) value;
                f = Math.max(0.0f, f);
                f = Math.min(f,2.0f);
                d.scale = f;
                ParticleSpriteDroste.needsUpdate = true;

                return;
            case 3: //speed rate
                int v = (Integer) value;
                v = Math.max(1, v);
                v = Math.min(v,10);
                d.speedRate = v;
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
            case 8:    // expandOffset
                int v0 = (Integer) value;
                v0 = Math.max(10, v0);
                v0 = Math.min(v0,400);
                d.val1 = v0;

                //Hacky way to update expensive computation
                ParticleSpriteDroste.needsUpdate = true;


                return;
            case 9:
//                int v1 = (Integer) value;
//                v1 = Math.max(1, v1);
//                v1 = Math.min(v1,100);
//                d.val2 = v1;
//                return;
                d.isCyclic =  (Boolean) value;
                return;

        }

        throw new IllegalArgumentException();
    }

}