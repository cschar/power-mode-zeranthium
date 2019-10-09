package com.cschar.pmode3.config;

import com.cschar.pmode3.ParticleSpriteMandala;
import com.cschar.pmode3.PowerMode3;
import com.cschar.pmode3.config.common.ui.CustomPathCellHighlighterRenderer;
import com.cschar.pmode3.config.common.ui.JTableButtonMouseListener;
import com.cschar.pmode3.config.common.ui.JTableButtonRenderer;
import com.cschar.pmode3.config.common.SpriteDataAnimated;
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

public class Mandala2Config extends BaseConfigPanel{


    JPanel firstRow;
    JComponent mandalaRingConfigPanel;

    private JCheckBox moveWithCaret;
    private JTextField moveSpeedTextField;
    static ArrayList<SpriteDataAnimated> spriteDataAnimated;
    PowerMode3 settings;

    public final static int PREVIEW_SIZE = 120;

    public Mandala2Config(PowerMode3 settings){
        this.settings = settings;

        this.setMaximumSize(new Dimension(1000,300));
        this.setLayout(new GridLayout(1,1)); //as many rows as necessary

        firstRow = new JPanel();
        firstRow.setMaximumSize(new Dimension(1000,500));
        firstRow.setLayout(new BoxLayout(firstRow, BoxLayout.Y_AXIS));

        this.setupHeaderPanel("Mandala Options", spriteDataAnimated);
//        headerPanel = new JPanel();
//        JLabel headerLabel = new JLabel("Mandala Options");
//        headerLabel.setFont(new Font ("Arial", Font.BOLD, 20));
//        headerPanel.add(headerLabel);
//
//
//        headerSizeLabel = new JLabel();
//        this.calculateSize();
//        headerSizeLabel.setBackground(ZeranthiumColors.specialOption3);
//        headerSizeLabel.setOpaque(true);
//
//        headerPanel.add(headerSizeLabel);
//
//        headerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
//        headerPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
//        headerPanel.setMaximumSize(new Dimension(500,100));

        this.add(firstRow);

        mandalaRingConfigPanel = createConfigTable();
        firstRow.add(headerPanel);
        JPanel caretMovementPanel = new JPanel();
        caretMovementPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        caretMovementPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        caretMovementPanel.setMaximumSize(new Dimension(500,100));
//        this.moveWithCaret = new JCheckBox("move with Caret?");
        this.moveWithCaret = new JCheckBox("move with caret/mouse?");
        caretMovementPanel.add(moveWithCaret);
        this.moveSpeedTextField = new JTextField();
        caretMovementPanel.add(Config.populateTextFieldPanel(this.moveSpeedTextField, "speed (0.001 - 1.0)"));
        caretMovementPanel.setBackground(ZeranthiumColors.specialOption1);

        firstRow.add(caretMovementPanel);


        firstRow.add(mandalaRingConfigPanel);


        firstRow.setOpaque(true);

    }


    public JComponent createConfigTable(){

        JBTable table = new JBTable();


        table.setRowHeight(PREVIEW_SIZE);
        table.setModel(new MandalaRingTableModel());

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
//        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        TableColumnModel colModel=table.getColumnModel();

        colModel.getColumn(0).setWidth(PREVIEW_SIZE); //preview
        colModel.getColumn(1).setPreferredWidth(70);  //enabled
        colModel.getColumn(1).setWidth(50);  //enabled

        colModel.getColumn(2).setPreferredWidth(50);  //scale
        colModel.getColumn(3).setPreferredWidth(80);  //speed rate
        colModel.getColumn(4).setPreferredWidth(100);  //set path
        colModel.getColumn(5).setPreferredWidth(50);  // path
        colModel.getColumn(6).setPreferredWidth(70);  //reset
        colModel.getColumn(6).setMaxWidth(70);  //reset
        colModel.getColumn(7).setPreferredWidth(400);  //other options
        colModel.getColumn(7).setMinWidth(300);


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

        OtherPanelCellEditorRenderer OtherPanelCellEditorRenderer = new OtherPanelCellEditorRenderer();
        table.getColumn("other").setCellRenderer(OtherPanelCellEditorRenderer);
        table.getColumn("other").setCellEditor(OtherPanelCellEditorRenderer);


        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());
        return sp;
    }


    public void loadValues(){

        this.moveWithCaret.setSelected(Config.getBoolProperty(settings, PowerMode3.ConfigType.MANDALA,"moveWithCaretEnabled", true));
        this.moveSpeedTextField.setText(String.valueOf(Config.getFloatProperty(settings, PowerMode3.ConfigType.MANDALA,"movespeed", 0.1f)));

    }

    public void saveValues(boolean isSettingEnabled) throws ConfigurationException {
        ParticleSpriteMandala.settingEnabled = isSettingEnabled; //to kill any lingering ones

        settings.setSpriteTypeProperty(PowerMode3.ConfigType.MANDALA, "moveWithCaretEnabled", String.valueOf(moveWithCaret.isSelected()));

        settings.setSpriteTypeProperty(PowerMode3.ConfigType.MANDALA, "movespeed",
                String.valueOf(Config.getJTextFieldWithinBoundsFloat(this.moveSpeedTextField,
                        0.001f, 1.0f,"movespeed")));

        settings.setSerializedSpriteDataAnimated(Mandala2Config.spriteDataAnimated, PowerMode3.ConfigType.MANDALA);
    }


//    static ArrayList<SpriteDataAnimated> spriteDataAnimated;

    public static void setSpriteDataAnimated(ArrayList<SpriteDataAnimated> data){
        spriteDataAnimated = data;
        ParticleSpriteMandala.mandalaRingData = data;
    }

    public static boolean MOVE_WITH_CARET(PowerMode3 settings){
        return Config.getBoolProperty(settings, PowerMode3.ConfigType.MANDALA, "moveWithCaretEnabled",true);
    }

    public static float CARET_MOVE_SPEED(PowerMode3 settings){
        return Config.getFloatProperty(settings, PowerMode3.ConfigType.MANDALA, "movespeed", 0.1f);
    }

    public static void loadJSONConfig(JSONObject configData, Path parentPath) throws JSONException {

        JSONArray tableData = configData.getJSONArray("tableData");

        int count = 0;
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
                    1);

            spriteDataAnimated.set(i, sd);
            count += 1;
        }

        for(int i = count; i < spriteDataAnimated.size(); i++){
            spriteDataAnimated.get(i).enabled = false;
        }
    }

}



class OtherPanelCellEditorRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {

    private static final long serialVersionUID = 1L;
    private OtherColCellPanelMandala renderer = new OtherColCellPanelMandala();
    private OtherColCellPanelMandala editor = new OtherColCellPanelMandala();

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        renderer.setOtherCol((OtherColMandala) value);
        return renderer;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

        editor.setOtherCol((OtherColMandala) value);
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


class OtherColMandala {

    public int numParticles;
    public boolean isCyclic;

    public OtherColMandala(int numParticles, boolean isCyclic) {
        this.isCyclic = isCyclic;
        this.numParticles = numParticles;
    }
}

class OtherColCellPanelMandala extends JPanel {

    private static final long serialVersionUID = 1L;

    private JCheckBox isCyclicCheckbox;
    private JTextField numParticles;

    OtherColCellPanelMandala() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));



        numParticles = new JTextField();
        numParticles.addActionListener( (event) -> {
            //limit input
            try {
                int v0 = Integer.parseInt(numParticles.getText());
                v0 = Math.max(1, v0);
                v0 = Math.min(v0, 10);
                numParticles.setText(String.valueOf(v0));
            }catch (NumberFormatException e){
                numParticles.setText("1");
            }
        });
        JLabel maxParticlesLabel = new JLabel("Max Particles (1-10)");
        JPanel maxParticlesPanel = new JPanel();
        maxParticlesPanel.add(maxParticlesLabel);
        maxParticlesPanel.add(numParticles);
        maxParticlesPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        maxParticlesPanel.setAlignmentX( Component.RIGHT_ALIGNMENT);
        maxParticlesPanel.setMaximumSize(new Dimension(300, 50));
        maxParticlesPanel.setBackground(ZeranthiumColors.specialOption2);
        maxParticlesPanel.setOpaque(true);

        isCyclicCheckbox = new JCheckBox("is cyclic");
        isCyclicCheckbox.setAlignmentX( Component.RIGHT_ALIGNMENT);

        this.add(isCyclicCheckbox);
        this.add(maxParticlesPanel);

    }


    public void setOtherCol(OtherColMandala otherColMandala) {
        this.isCyclicCheckbox.setSelected(otherColMandala.isCyclic);
        this.numParticles.setText(String.valueOf(otherColMandala.numParticles));
    }

    public OtherColMandala getOtherCol() {
        return new OtherColMandala(Integer.parseInt(this.numParticles.getText()), this.isCyclicCheckbox.isSelected());
    }
}


class MandalaRingTableModel extends AbstractTableModel {

    static ArrayList<SpriteDataAnimated> data = Mandala2Config.spriteDataAnimated;



    public static final String[] columnNames = new String[]{
            "preview",
            "enabled?",
            "scale",
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
            OtherColMandala.class,
//            OtherPanel.class
//            MandalaOtherCellData.class
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
                return data.get(row).scale;
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


                        Mandala2Config.calculateSize(data);
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

                    Mandala2Config.calculateSize(data);
                    this.fireTableDataChanged();
                });
                return resetButton;
            case 7:
                SpriteDataAnimated d = data.get(row);
                return new OtherColMandala(d.val2, d.isCyclic);
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
            case 7:    // other settings

                OtherColMandala c = (OtherColMandala) value;
                data.get(row).isCyclic = c.isCyclic;
                data.get(row).val2 = c.numParticles;
                return;


        }

        throw new IllegalArgumentException();
    }

}
