package com.cschar.pmode3.config;

import com.cschar.pmode3.ParticleSpriteLockedLayer;
import com.cschar.pmode3.PowerMode3;
import com.cschar.pmode3.config.common.SpriteDataAnimated;
import com.cschar.pmode3.config.common.ui.CustomPathCellHighlighterRenderer;
import com.cschar.pmode3.config.common.ui.JTableButtonMouseListener;
import com.cschar.pmode3.config.common.ui.JTableButtonRenderer;
import com.cschar.pmode3.config.common.ui.ZeranthiumColors;
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
import javax.swing.table.*;
import java.awt.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EventObject;

public class LockedLayerConfig extends BaseConfigPanel {


    JPanel firstRow;
    JComponent configPanel;

    private JTextField gutterWidthTextField;

    PowerMode3 settings;
    static ArrayList<SpriteDataAnimated> spriteDataAnimated;
    public final static int PREVIEW_SIZE = 120;

    public LockedLayerConfig(PowerMode3 settings){
        this.settings = settings;

        this.setMaximumSize(new Dimension(1000,300));
        this.setLayout(new GridLayout(1,1)); //as many rows as necessary

        firstRow = new JPanel();
        firstRow.setMaximumSize(new Dimension(1000,500));
        firstRow.setLayout(new BoxLayout(firstRow, BoxLayout.Y_AXIS));
        this.setupHeaderPanel("Locked Layer Options", spriteDataAnimated);
        firstRow.add(headerPanel);

        this.add(firstRow);

        this.gutterWidthTextField = new JTextField();
        firstRow.add(Config.populateTextFieldPanel(this.gutterWidthTextField, "gutter offset (0 - 200)"));
        firstRow.setOpaque(true);

        configPanel = createConfigTable();
        firstRow.add(configPanel);



    }

    public JComponent createConfigTable(){

        JBTable table = new JBTable();


        table.setRowHeight(PREVIEW_SIZE);
        table.setModel(new LockedLayerTableModel());

        table.setCellSelectionEnabled(false);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(false);

        table.setPreferredScrollableViewportSize(new Dimension(400,
                table.getRowHeight()));
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane sp = ScrollPaneFactory.createScrollPane(table);


        sp.setOpaque(true);

        TableColumnModel colModel=table.getColumnModel();

        colModel.getColumn(0).setWidth(PREVIEW_SIZE); //preview
        colModel.getColumn(1).setWidth(50);  //enabled

        colModel.getColumn(2).setPreferredWidth(50);  //alpha
        colModel.getColumn(3).setPreferredWidth(80);  //speed rate
        colModel.getColumn(4).setPreferredWidth(100);  //set path
        colModel.getColumn(5).setPreferredWidth(50);  // path
        colModel.getColumn(6).setPreferredWidth(70);  //reset
        colModel.getColumn(6).setMaxWidth(70);  //reset
        colModel.getColumn(7).setMinWidth(200);


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

        LockedLayerOtherPanelCellEditorRenderer OtherPanelCellEditorRenderer = new LockedLayerOtherPanelCellEditorRenderer();
        table.getColumn("other").setCellRenderer(OtherPanelCellEditorRenderer);
        table.getColumn("other").setCellEditor(OtherPanelCellEditorRenderer);


        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());
        return sp;
    }


    public void loadValues(){
        //table loaded during plugin initialization
        this.gutterWidthTextField.setText(String.valueOf(Config.getIntProperty(settings, PowerMode3.ConfigType.LOCKED_LAYER,"gutterWidth", 50)));
    }

    public void saveValues() throws ConfigurationException {

        int gutterVal = Config.getJTextFieldWithinBoundsInt(this.gutterWidthTextField,0, 200,"gutterWidth");
        settings.setSpriteTypeProperty(PowerMode3.ConfigType.LOCKED_LAYER, "gutterWidth", String.valueOf(gutterVal));

        for(SpriteDataAnimated d: spriteDataAnimated){
            d.val1 = gutterVal;
        }

        settings.setSerializedSpriteDataAnimated(LockedLayerConfig.spriteDataAnimated, PowerMode3.ConfigType.LOCKED_LAYER);
    }

    public static void setSpriteDataAnimated(ArrayList<SpriteDataAnimated> data){
        spriteDataAnimated = data;
        ParticleSpriteLockedLayer.spriteDataAnimated = data;
    }


    public static void loadJSONConfig(JSONObject configData, Path parentPath) throws JSONException {

        JSONArray tableData = configData.getJSONArray("tableData");

        for(int i =0; i<tableData.length(); i++){
            JSONObject jo = tableData.getJSONObject(i);

            int screenPosition = jo.getInt("val2");
            screenPosition = Math.max(screenPosition, 0);
            screenPosition = Math.min(screenPosition, 4);

            SpriteDataAnimated sd =  new SpriteDataAnimated(
                    PREVIEW_SIZE,
                    jo.getBoolean("enabled"),
                1.0f,
//                    (float) jo.getDouble("scale"),
                    jo.getInt("speedRate"),
                    spriteDataAnimated.get(i).defaultPath,
                    parentPath.resolve(jo.getString("customPath")).toString(),
                    jo.getBoolean("isCyclic"),
                    screenPosition,
                    (float) jo.getDouble("alpha"),
                    60); // --> gutterWidth
//                    jo.getInt("val1")); // --> gutterWidth

            spriteDataAnimated.set(i, sd);
        }
    }

}


class LockedLayerOtherPanelCellEditorRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {

    private static final long serialVersionUID = 1L;
    private LockedLayerOtherColCellPanel renderer = new LockedLayerOtherColCellPanel();
    private LockedLayerOtherColCellPanel editor = new LockedLayerOtherColCellPanel();

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        renderer.setOtherCol((LockedLayerOtherCol) value);
        return renderer;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

        editor.setOtherCol((LockedLayerOtherCol) value);
        return editor;
    }

    @Override
    public Object getCellEditorValue() {
        return editor.getOtherCol();
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return false;
    }
}

//


class LockedLayerOtherCol {

    public int numParticles;
    public boolean isCyclic;

    public LockedLayerOtherCol(int numParticles, boolean isCyclic) {
        this.isCyclic = isCyclic;
        this.numParticles = numParticles;
    }
}

class LockedLayerOtherColCellPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private JCheckBox isCyclicCheckbox;
    private JTextField numParticles;

    LockedLayerOtherColCellPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));



        numParticles = new JTextField();
        numParticles.addActionListener( (event) -> {
            //limit input
            try {
                int v0 = Integer.parseInt(numParticles.getText());
                v0 = Math.max(0, v0);
                v0 = Math.min(v0, 4);
                numParticles.setText(String.valueOf(v0));
            }catch (NumberFormatException e){
                numParticles.setText("1");
            }
        });
        JLabel maxParticlesLabel = new JLabel("<html>Stretch -> 0 <br/> Top/Right -> 1  <br/>\n Top/Left -> 2 <br/>\n Bot/Right -> 3 <br/>\n Bot/Left -> 4 </html>");
        JPanel maxParticlesPanel = new JPanel();
        maxParticlesPanel.add(maxParticlesLabel);
        maxParticlesPanel.add(numParticles);
        maxParticlesPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        maxParticlesPanel.setAlignmentX( Component.RIGHT_ALIGNMENT);
//        maxParticlesPanel.setMaximumSize(new Dimension(300, 50));
        maxParticlesPanel.setBackground(ZeranthiumColors.specialOption2);
        maxParticlesPanel.setOpaque(true);

        isCyclicCheckbox = new JCheckBox("is cyclic");
//        isCyclicCheckbox.setEnabled(false);
        isCyclicCheckbox.setAlignmentX( Component.RIGHT_ALIGNMENT);

        this.add(isCyclicCheckbox);
        this.add(maxParticlesPanel);

    }


    public void setOtherCol(LockedLayerOtherCol otherCol) {
        this.isCyclicCheckbox.setSelected(otherCol.isCyclic);
        this.numParticles.setText(String.valueOf(otherCol.numParticles));
    }

    public LockedLayerOtherCol getOtherCol() {
        return new LockedLayerOtherCol(Integer.parseInt(this.numParticles.getText()), this.isCyclicCheckbox.isSelected());
    }
}


class LockedLayerTableModel extends AbstractTableModel {

    static ArrayList<SpriteDataAnimated> data = LockedLayerConfig.spriteDataAnimated;



    public static final String[] columnNames = new String[]{
            "preview",
            "enabled?",
            "alpha",
            "speed rate",
            "set path",
            "path",
            "reset",
            "other"

    };





    private final Class[] columnClasses = new Class[]{
            ImageIcon.class,
            Boolean.class,
            Float.class,
            Integer.class,
            JButton.class,
            String.class,
            JButton.class,
            LockedLayerOtherCol.class,

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


        switch (column) {
            case 0:
//                return null;
//                return previewIcon;
                return data.get(row).previewIcon;
            case 1:
                return data.get(row).enabled;
            case 2:
                return data.get(row).alpha;
            case 3:
                return data.get(row).speedRate;
            case 4:
                final JButton button = new JButton("Set path");
                button.addActionListener(arg0 -> {


                    FileChooserDescriptor fd = new FileChooserDescriptor(false,true,false,false,false,false);
                    FileChooserDialog fcDialog = FileChooserFactory.getInstance().createFileChooser(fd, null, null);


                    VirtualFile[] vfs = fcDialog.choose(null);

                    if(vfs.length != 0){

                        data.get(row).customPath = vfs[0].getPath();
                        data.get(row).setImageAnimated(vfs[0].getPath(), false);

                        LockedLayerConfig.calculateSize(data);
                        this.fireTableDataChanged();
                    }

                });
                return button;
            case 5:
                return data.get(row).customPath;
            case 6:
                final JButton resetButton = new JButton("reset");
                resetButton.addActionListener(arg0 -> {

                    SpriteDataAnimated d = data.get(row);
                    d.setImageAnimated(d.defaultPath, true);
                    d.customPath = "";
                    d.customPathValid = false;
                    d.scale = 1.0f;
                    d.speedRate = 2;

                    LockedLayerConfig.calculateSize(data);
                    this.fireTableDataChanged();
                });
                return resetButton;
            case 7:
                SpriteDataAnimated d = data.get(row);
                return new LockedLayerOtherCol(d.val2, d.isCyclic);
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
                v0 = Math.min(v0, 1.0f);
                data.get(row).alpha = v0;
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
            case 7:    // other settings

                LockedLayerOtherCol c = (LockedLayerOtherCol) value;
                data.get(row).isCyclic = c.isCyclic;
                data.get(row).val2 = c.numParticles;
                return;


        }

        throw new IllegalArgumentException();
    }

}
