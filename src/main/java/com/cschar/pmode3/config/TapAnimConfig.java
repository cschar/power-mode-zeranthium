package com.cschar.pmode3.config;

import com.cschar.pmode3.ParticleSpriteDroste;
import com.cschar.pmode3.ParticleSpriteTapAnim;
import com.cschar.pmode3.PowerMode3;
import com.cschar.pmode3.config.common.SpriteDataAnimated;
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

public class TapAnimConfig extends JPanel {

    JPanel mainPanel;
    PowerMode3 settings;




    private JComponent spriteConfigPanel;

    public static ArrayList<SpriteDataAnimated> spriteDataAnimated;

    public final static int PREVIEW_SIZE = 120;

    public TapAnimConfig(PowerMode3 settings){
        this.settings = settings;


        this.setLayout(new GridLayout(1,1));



        JPanel firstRow =  new JPanel();
        firstRow.setLayout(new BoxLayout(firstRow, BoxLayout.PAGE_AXIS));
        firstRow.setMaximumSize(new Dimension(1000,300));

        JPanel headerPanel = new JPanel();
        JLabel headerLabel = new JLabel("Tap Anim Options");
        headerLabel.setFont(new Font ("Arial", Font.BOLD, 20));
        headerPanel.add(headerLabel);
        headerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        headerPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        headerPanel.setMaximumSize(new Dimension(300,100));
        firstRow.add(headerPanel);


        spriteConfigPanel = createConfigTable();
        firstRow.add(spriteConfigPanel);
        this.add(firstRow);


        this.loadValues();

    }



    public JComponent createConfigTable(){

        JBTable table = new JBTable();


        table.setRowHeight(PREVIEW_SIZE);
        table.setModel( new TapAnimTableModel());

        table.setCellSelectionEnabled(false);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);

        table.setPreferredScrollableViewportSize(new Dimension(400,
                table.getRowHeight() * 1));

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
        colModel.getColumn(1).setWidth(40);  //enabled

        colModel.getColumn(2).setWidth(50);  //scale
        colModel.getColumn(3).setWidth(120);  //speed rate
        colModel.getColumn(4).setWidth(100);  //set path
        colModel.getColumn(5).setWidth(50);  // path
        colModel.getColumn(6).setWidth(60);  //reset
        colModel.getColumn(7).setWidth(60); //alpha


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

        TableCellRenderer pathRenderer = new CustomPathCellHighlighterRenderer(TapAnimConfig.spriteDataAnimated);
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

        settings.setSerializedSpriteDataAnimated(TapAnimConfig.spriteDataAnimated, PowerMode3.ConfigType.TAP_ANIM);
    }



    public static void setSpriteDataAnimated(ArrayList<SpriteDataAnimated> data){
        spriteDataAnimated = data;
        ParticleSpriteTapAnim.spriteDataAnimated = data;
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
//                    1,
                    jo.getInt("xoffset"), //val2
                    (float) jo.getDouble("alpha"),
                    jo.getInt("yoffset")); //val1


            spriteDataAnimated.set(i, sd);
        }
    }
}


class TapAnimTableModel extends AbstractTableModel {

    static ArrayList<SpriteDataAnimated> data = TapAnimConfig.spriteDataAnimated;



    public static final String[] columnNames = new String[]{
            "preview",
            "enabled?",
            "scale",
            "steps per press",
//            "weighted amount (1-100)",

            "set path",
            "path",
            "reset",
            "alpha",
            "y offset (+/- 400)",
            "x offset (+/- 400)",
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
            Integer.class,
            Boolean.class
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
                final JButton button = new JButton("Set path");
                button.addActionListener(arg0 -> {


                    FileChooserDescriptor fd = new FileChooserDescriptor(false,true,false,false,false,false);
                    FileChooserDialog fcDialog = FileChooserFactory.getInstance().createFileChooser(fd, null, null);


                    VirtualFile[] vfs = fcDialog.choose(null);

                    if(vfs.length != 0){

                        d.customPath = vfs[0].getPath();
                        d.setImageAnimated(vfs[0].getPath(), false);


                        this.fireTableDataChanged();
                    }

                });
                return button;
            case 5:
                return data.get(row).customPath;
            case 6:
                final JButton resetButton = new JButton("reset");
                resetButton.addActionListener(arg0 -> {

                    d.setImageAnimated(d.defaultPath, true);
                    d.customPath = "";
                    d.customPathValid = false;
//                    d.scale = 1.0f;
//                    d.val1 = 20;


                    this.fireTableDataChanged();
                });
                return resetButton;
            case 7:
                return d.alpha;

            case 8: // weight --> yoffset
                return d.val1;
            case 9: // val2 --> xoffset
                return d.val2;
            case 10: // maxParticles --> repeatEvery
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
                v0 = Math.max(-400, v0);
                v0 = Math.min(v0,400);
                d.val1 = v0;
                return;
            case 9:    // expandOffset
                v0 = (Integer) value;
                v0 = Math.max(-400, v0);
                v0 = Math.min(v0,400);
                d.val2 = v0;
                return;
            case 10:
                d.isCyclic =  (Boolean) value;
                return;

        }

        throw new IllegalArgumentException();
    }

}