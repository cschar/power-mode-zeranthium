package com.cschar.pmode3.config;

import com.cschar.pmode3.ParticleSpriteLantern;
import com.cschar.pmode3.ParticleSpriteLinkerAnchor;
import com.cschar.pmode3.PowerMode3;
import com.cschar.pmode3.config.common.SpriteDataAnimated;
import com.cschar.pmode3.config.common.ui.*;
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

public class LanternConfig extends BaseConfigPanel {

    JPanel topPanel;
    PowerMode3 settings;


    private JCheckBox tracerEnabledCheckBox;


    private JTextField maxLinksTextField;


    private JCheckBox isCyclicEnabled;
    private JTextField maxParticlesTextField;

    private JCheckBox moveWithCaret;
    private JTextField moveSpeedTextField;



    private static Color originalTracerColor = ZeranthiumColors.originalTracerColor;


    private JComponent spriteConfigPanel;
    static ArrayList<SpriteDataAnimated> spriteDataAnimated;


    public LanternConfig(PowerMode3 settings){
        this.settings = settings;

//        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
//        this.setMaximumSize(new Dimension(1000,300));
//        this.setLayout(new GridLayout(2,0)); //as many rows as necessary
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        topPanel = new JPanel();
        topPanel.setMaximumSize(new Dimension(1000,500));
        topPanel.setLayout(new GridLayout(1,2));
        JPanel firstCol = new JPanel();
        firstCol.setLayout(new BoxLayout(firstCol, BoxLayout.PAGE_AXIS));
        topPanel.add(firstCol);

        JPanel secondCol = new JPanel();
        secondCol.setLayout(new BoxLayout(secondCol, BoxLayout.Y_AXIS));
        this.setupHeaderPanel("Lantern Options", spriteDataAnimated);
        secondCol.add(headerPanel);
        topPanel.add(secondCol);

//        JPanel lizardColorPanel = Config.getColorPickerPanel("Lizard Color", PowerMode3.SpriteType.LIZARD, settings, Color.GREEN);
//        secondCol.add(lizardColorPanel);




        JPanel tracerColorPanel = Config.getColorPickerPanel("tracer Color", PowerMode3.ConfigType.LANTERN, settings, this.originalTracerColor);
        this.tracerEnabledCheckBox = new JCheckBox("is enabled?", true);
        JPanel tracerConfig = Config.populateEnabledColorPickerPanel(tracerColorPanel, tracerEnabledCheckBox);
        secondCol.add(tracerConfig);

        isCyclicEnabled = new JCheckBox("cyclic?");
        this.maxParticlesTextField = new JTextField();
        JLabel maxCycleParticleLabel = new JLabel("MAX Particles (1-50)");
        JPanel cyclicPanel = new JPanel();
        cyclicPanel.add(isCyclicEnabled);
        cyclicPanel.add(maxCycleParticleLabel);
        cyclicPanel.add(this.maxParticlesTextField);
        cyclicPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        cyclicPanel.setAlignmentX( Component.RIGHT_ALIGNMENT);//0.0
        cyclicPanel.setMaximumSize(new Dimension(500, 50));
        secondCol.add(cyclicPanel);



        //First col config

        JPanel caretMovementPanel = new JPanel();
        caretMovementPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        caretMovementPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        caretMovementPanel.setMaximumSize(new Dimension(500,100));
//        this.moveWithCaret = new JCheckBox("move with Caret?");
        this.moveWithCaret = new JCheckBox("move with caret/mouse?");
        caretMovementPanel.add(moveWithCaret);
        this.moveSpeedTextField = new JTextField();
//        this.moveSpeedTextField.setEditable(false);
        caretMovementPanel.add(Config.populateTextFieldPanel(this.moveSpeedTextField, "speed (0.01 - 1.0)"));
        caretMovementPanel.setOpaque(true);
        caretMovementPanel.setBackground(ZeranthiumColors.specialOption1);
        firstCol.add(caretMovementPanel);



        this.maxLinksTextField = new JTextField();
//        this.maxAnchorsToUse.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JLabel maxLinksLabel = new JLabel("Max Links to Use (5-100)");
        JPanel maxLinksPanel = new JPanel();
        maxLinksPanel.add(maxLinksLabel);
        maxLinksPanel.add(this.maxLinksTextField);
        maxLinksPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        maxLinksPanel.setAlignmentX( Component.RIGHT_ALIGNMENT);//0.0
        maxLinksPanel.setMaximumSize(new Dimension(500, 40));
        firstCol.add(maxLinksPanel);



        spriteConfigPanel = createConfigTable();


        this.add(topPanel);
        this.add(spriteConfigPanel);

        this.loadValues();

    }


    public JComponent createConfigTable(){

        JBTable table = new JBTable();


        table.setRowHeight(60);

        table.setModel(new LanternTableModel(this));

        table.setCellSelectionEnabled(false);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);

        table.setPreferredScrollableViewportSize(new Dimension(400,
                table.getRowHeight() * 3));

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

        TableCellRenderer pathRenderer = new CustomPathCellHighlighterRenderer(LanternConfig.spriteDataAnimated);
        table.getColumn("path").setCellRenderer(pathRenderer);


        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());
        return sp;
    }


    public void loadValues(){

        this.tracerEnabledCheckBox.setSelected(Config.getBoolProperty(settings, PowerMode3.ConfigType.LANTERN,"tracerEnabled", true));

        this.moveWithCaret.setSelected(Config.getBoolProperty(settings, PowerMode3.ConfigType.LANTERN,"moveWithCaretEnabled", true));
        this.moveSpeedTextField.setText(String.valueOf(Config.getFloatProperty(settings, PowerMode3.ConfigType.LANTERN,"movespeed", 1.0f)));
        this.maxLinksTextField.setText(String.valueOf(Config.getIntProperty(settings, PowerMode3.ConfigType.LANTERN,"maxLinks", 20)));

        this.maxParticlesTextField.setText(String.valueOf(Config.getIntProperty(settings, PowerMode3.ConfigType.LANTERN,"maxParticles", 10)));
        this.isCyclicEnabled.setSelected(Config.getBoolProperty(settings, PowerMode3.ConfigType.LANTERN,"isCyclicEnabled", false));


    }

    public void loadJSONConfig(JSONObject configData, Path parentPath) throws JSONException {
        PowerMode3 settings = PowerMode3.getInstance();

        if(configData.has("maxLinks")) {
            int maxLinks = configData.getInt("maxLinks");
            this.maxLinksTextField.setText(String.valueOf(maxLinks));
            settings.setSpriteTypeProperty(PowerMode3.ConfigType.LANTERN, "maxLinks",
                    String.valueOf(maxLinks));
        }

        if(configData.has("tracerEnabled")) {
            boolean tracerEnabled = configData.getBoolean("tracerEnabled");
            this.tracerEnabledCheckBox.setSelected(tracerEnabled);
            settings.setSpriteTypeProperty(PowerMode3.ConfigType.LANTERN, "tracerEnabled",
                    String.valueOf(tracerEnabled));
        }

        if(configData.has("isCyclicEnabled")) {
            boolean cyclicEnabled = configData.getBoolean("isCyclicEnabled");
            this.isCyclicEnabled.setSelected(cyclicEnabled);
            settings.setSpriteTypeProperty(PowerMode3.ConfigType.LANTERN, "isCyclicEnabled",
                    String.valueOf(cyclicEnabled));
        }


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
                    jo.getInt("val2"), //val2 //repeat every n links
                    (float) jo.getDouble("alpha"),
                    jo.getInt("val1")); //val1  //offset to start on links


            spriteDataAnimated.set(i, sd);
        }
    }

    public void saveValues() throws ConfigurationException {




        settings.setSpriteTypeProperty(PowerMode3.ConfigType.LANTERN, "moveWithCaretEnabled", String.valueOf(moveWithCaret.isSelected()));
        settings.setSpriteTypeProperty(PowerMode3.ConfigType.LANTERN, "movespeed",
                String.valueOf(Config.getJTextFieldWithinBoundsFloat(this.moveSpeedTextField,
                        0.01f, 1.0f,"movespeed")));


        int maxLinks = Config.getJTextFieldWithinBoundsInt(this.maxLinksTextField,
                5, 100,
                "max links on lantern");
        settings.setSpriteTypeProperty(PowerMode3.ConfigType.LANTERN, "maxLinks",
                String.valueOf(maxLinks));


        settings.setSpriteTypeProperty(PowerMode3.ConfigType.LANTERN, "tracerEnabled", String.valueOf(tracerEnabledCheckBox.isSelected()));

        //Cyclic global garbage lol
        settings.setSpriteTypeProperty(PowerMode3.ConfigType.LANTERN, "isCyclicEnabled", String.valueOf(isCyclicEnabled.isSelected()));
        ParticleSpriteLinkerAnchor.cyclicToggleEnabled = isCyclicEnabled.isSelected();
        int maxCycleParticles = Config.getJTextFieldWithinBoundsInt(this.maxParticlesTextField,
                1, 50,
                "max particles");
        settings.setSpriteTypeProperty(PowerMode3.ConfigType.LANTERN, "maxParticles",
                String.valueOf(maxCycleParticles));
        ParticleSpriteLinkerAnchor.MAX_CYCLE_PARTICLES = LinkerConfig.MAX_CYCLE_PARTICLES(settings);


        settings.setSerializedSpriteDataAnimated(LanternConfig.spriteDataAnimated, PowerMode3.ConfigType.LANTERN);
    }




    public static boolean TRACER_ENABLED(PowerMode3 settings){
        return Config.getBoolProperty(settings, PowerMode3.ConfigType.LANTERN, "tracerEnabled");
    }

    public static Color TRACER_COLOR(PowerMode3 settings){
        return Config.getColorProperty(settings, PowerMode3.ConfigType.LANTERN, "tracer Color", originalTracerColor);
    }

    public static int MAX_LINKS(PowerMode3 settings){
        return Config.getIntProperty(settings, PowerMode3.ConfigType.LANTERN, "maxLinks");
    }



    public static boolean IS_CYCLIC_ENABLED(PowerMode3 settings){
        return Config.getBoolProperty(settings, PowerMode3.ConfigType.LANTERN, "isCyclicEnabled");
    }

    public static int MAX_PARTICLES(PowerMode3 settings){
        return Config.getIntProperty(settings, PowerMode3.ConfigType.LANTERN, "maxParticles");
    }

    public static boolean MOVE_WITH_CARET(PowerMode3 settings){
        return Config.getBoolProperty(settings, PowerMode3.ConfigType.LANTERN, "moveWithCaretEnabled",true);
    }

    //TODO PUT THIS IN TABLE as val4 , make one row change all other rows ugh
    public static float CARET_MOVE_SPEED(PowerMode3 settings){
        return Config.getFloatProperty(settings, PowerMode3.ConfigType.LANTERN, "movespeed", 0.1f);
    }



    public static void setSpriteDataAnimated(ArrayList<SpriteDataAnimated> data){
        spriteDataAnimated = data;
        ParticleSpriteLantern.spriteDataAnimated = data;
    }
}


class LanternTableModel extends AbstractConfigTableModel {

    static ArrayList<SpriteDataAnimated> data = LanternConfig.spriteDataAnimated;



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
            "offset",
            "repeat every"

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
            Integer.class
    };

    public LanternTableModel(BaseConfigPanel config) {
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
                return d.val2;

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
            case 8:
                int v0 = (Integer) value;
                v0 = Math.max(0, v0);
                v0 = Math.min(v0,100);
                d.val1 = v0;
                return;
            case 9:
                int v1 = (Integer) value;
                v1 = Math.max(1, v1);
                v1 = Math.min(v1,100);
                d.val2 = v1;
                return;

        }

        throw new IllegalArgumentException();
    }

}