package com.cschar.pmode3.config;

import com.cschar.pmode3.ParticleSpriteLinkerAnchor;
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
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.nio.file.Path;
import java.util.ArrayList;

public class LinkerConfig extends BaseConfigJPanel {

    JPanel mainPanel;
    PowerMode3 settings;
    static int PREVIEW_SIZE = 60;

    public JTextField maxPsiAnchorDistanceTextField;
    public JTextField minPsiAnchorDistanceTextField;
    private JTextField chanceOfSpawnTextField;
    private JTextField maxAnchorsToUse;
    private JTextField chancePerKeyPressTextField;
    private JCheckBox tracerEnabledCheckBox;


    private JTextField maxLinksTextField;
    private JTextField distanceFromCenterTextField;
    private JTextField wobbleAmountTextField;

    private JTextField curve1AmountTextField;

    private JCheckBox isCyclicEnabled;
    private JTextField maxCycleParticlesTextField;

    private JCheckBox moveWithCaret;
    private JTextField moveSpeedTextField;



    private static Color originalTracerColor = ZeranthiumColors.originalTracerColor;


    private JComponent linkerSpriteConfigPanel;
    public static ArrayList<SpriteDataAnimated> spriteDataAnimated;
    JPanel firstRow;


    public LinkerConfig(PowerMode3 settings){
        this.settings = settings;

        //Copied from MultiLayer
        this.setMaximumSize(new Dimension(1000,700));
        this.setLayout(new GridLayout(2,1)); //as many rows as necessary

        firstRow = new JPanel();
        firstRow.setMaximumSize(new Dimension(1000,500));
        firstRow.setLayout(new BoxLayout(firstRow, BoxLayout.X_AXIS));

        this.setupHeaderPanel("Multi Layer Options", spriteDataAnimated);
        this.add(firstRow);

        //Populate First row

        JPanel firstCol = new JPanel();
        firstCol.setLayout(new BoxLayout(firstCol, BoxLayout.PAGE_AXIS));

        // -----------  First col config --------------------

        JPanel caretMovementPanel = new JPanel();
        caretMovementPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        caretMovementPanel.setAlignmentX(RIGHT_ALIGNMENT);
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
        JLabel maxLinksLabel = new JLabel("Max Links to Use (1-50)");
        JPanel maxLinksPanel = new JPanel();
        maxLinksPanel.add(maxLinksLabel);
        maxLinksPanel.add(this.maxLinksTextField);
        maxLinksPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        maxLinksPanel.setAlignmentX(RIGHT_ALIGNMENT);//0.0
        maxLinksPanel.setMaximumSize(new Dimension(500, 40));
        firstCol.add(maxLinksPanel);

        this.distanceFromCenterTextField = new JTextField();
        JLabel distanceFromCenterLabel = new JLabel("Cutoff Distance From Center px (1-500)");
        JPanel distanceFromCenterPanel = new JPanel();
        distanceFromCenterPanel.add(distanceFromCenterLabel);
        distanceFromCenterPanel.add(this.distanceFromCenterTextField);
        distanceFromCenterPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        distanceFromCenterPanel.setAlignmentX(RIGHT_ALIGNMENT);//0.0
        distanceFromCenterPanel.setMaximumSize(new Dimension(500, 40));
        firstCol.add(distanceFromCenterPanel);

        this.wobbleAmountTextField = new JTextField();
        JPanel wobbleConfig = Config.populateTextFieldPanel(this.wobbleAmountTextField, "Wobble amount (0-200)");
        firstCol.add(wobbleConfig);

        this.curve1AmountTextField = new JTextField();
        JPanel curve1Config = Config.populateTextFieldPanel(this.curve1AmountTextField, "curve amount (-100 - 400)");
        firstCol.add(curve1Config);

        firstRow.add(firstCol);

        JPanel secondCol = new JPanel();
        secondCol.setLayout(new BoxLayout(secondCol, BoxLayout.Y_AXIS));
        this.setupHeaderPanel("LinkerI Options", spriteDataAnimated);
        secondCol.add(headerPanel);
        firstRow.add(secondCol);


        // ------ Second Column Config ----------------

        JPanel maxPsi = new JPanel();
//        maxPsi.setLayout(new FlowLayout(FlowLayout.RIGHT));
        maxPsi.add(new JLabel("Max Psi Anchor Scan Distance: "));
        this.maxPsiAnchorDistanceTextField = new JTextField("");
        this.maxPsiAnchorDistanceTextField.setMinimumSize(new Dimension(50,20));
        maxPsi.add(maxPsiAnchorDistanceTextField);
        maxPsi.setAlignmentX(RIGHT_ALIGNMENT);//0.0
        maxPsi.setMaximumSize(new Dimension(500, 50));
        maxPsi.setLayout(new FlowLayout(FlowLayout.RIGHT));
        secondCol.add(maxPsi);



        JPanel minPsi = new JPanel();
        minPsi.add(new JLabel("Min Psi Anchor Scan Distance: "));
        this.minPsiAnchorDistanceTextField = new JTextField("");
        this.minPsiAnchorDistanceTextField.setMinimumSize(new Dimension(50,20));
        minPsi.add(minPsiAnchorDistanceTextField);
        minPsi.setLayout(new FlowLayout(FlowLayout.RIGHT));
        minPsi.setAlignmentX(RIGHT_ALIGNMENT);//0.0
        minPsi.setMaximumSize(new Dimension(500, 50));
//        minPsi.setBackground(Color.YELLOW);
        secondCol.add(minPsi);

        this.chancePerKeyPressTextField = new JTextField();


        JPanel tracerColorPanel = Config.getColorPickerPanel("tracer Color", PowerMode3.ConfigType.LINKER, settings, this.originalTracerColor);
        this.tracerEnabledCheckBox = new JCheckBox("is enabled?", true);
        JPanel tracerConfig = Config.populateEnabledColorPickerPanel(tracerColorPanel, tracerEnabledCheckBox);
        secondCol.add(tracerConfig);

        isCyclicEnabled = new JCheckBox("cyclic?");
        this.maxCycleParticlesTextField = new JTextField();
        JLabel maxCycleParticleLabel = new JLabel("Cycle Particles (1-5)");
        JPanel cyclicPanel = new JPanel();
        cyclicPanel.add(isCyclicEnabled);
        cyclicPanel.add(maxCycleParticleLabel);
        cyclicPanel.add(this.maxCycleParticlesTextField);
        cyclicPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        cyclicPanel.setAlignmentX(RIGHT_ALIGNMENT);//0.0
        cyclicPanel.setMaximumSize(new Dimension(500, 50));
        secondCol.add(cyclicPanel);

        this.chanceOfSpawnTextField = new JTextField();
        this.maxAnchorsToUse = new JTextField();




        linkerSpriteConfigPanel = createConfigTable();

        this.add(firstRow);
        this.add(linkerSpriteConfigPanel);

        this.loadValues();

    }


    public JComponent createConfigTable(){
        JBTable table = new JBTable();

        table.setRowHeight(PREVIEW_SIZE);
        table.setModel(new LinkerTableModel(this));

        table.setCellSelectionEnabled(false);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(false);

        table.setPreferredScrollableViewportSize(new Dimension(400,
                table.getRowHeight() * 3));
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

        colModel.getColumn(0).setPreferredWidth(PREVIEW_SIZE); //preview
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

        TableCellRenderer pathRenderer = new CustomPathCellHighlighterRenderer(LinkerConfig.spriteDataAnimated);
        table.getColumn("path").setCellRenderer(pathRenderer);


        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());
        return sp;
    }


    public void loadValues(){
        this.maxPsiAnchorDistanceTextField.setText(String.valueOf(Config.getIntProperty(settings, PowerMode3.ConfigType.LINKER,"maxPsiSearchDistance", 400)));
        this.minPsiAnchorDistanceTextField.setText(String.valueOf(Config.getIntProperty(settings, PowerMode3.ConfigType.LINKER,"minPsiSearchDistance", 50)));

        this.chancePerKeyPressTextField.setText(String.valueOf(Config.getIntProperty(settings, PowerMode3.ConfigType.LINKER,"chancePerKeyPress", 100)));
        this.chanceOfSpawnTextField.setText(String.valueOf(Config.getIntProperty(settings, PowerMode3.ConfigType.LINKER,"chanceOfSpawn", 100)));



        this.maxAnchorsToUse.setText(String.valueOf(Config.getIntProperty(settings, PowerMode3.ConfigType.LINKER,"maxAnchorsToUse", 10)));

        this.tracerEnabledCheckBox.setSelected(Config.getBoolProperty(settings, PowerMode3.ConfigType.LINKER,"tracerEnabled", true));

        this.moveWithCaret.setSelected(Config.getBoolProperty(settings, PowerMode3.ConfigType.LINKER,"moveWithCaretEnabled", true));
        this.moveSpeedTextField.setText(String.valueOf(Config.getFloatProperty(settings, PowerMode3.ConfigType.LINKER,"movespeed", 1.0f)));
        this.maxLinksTextField.setText(String.valueOf(Config.getIntProperty(settings, PowerMode3.ConfigType.LINKER,"maxLinks", 50)));
        this.distanceFromCenterTextField.setText(String.valueOf(Config.getIntProperty(settings, PowerMode3.ConfigType.LINKER,"distanceFromCenter", 150)));

        this.wobbleAmountTextField.setText(String.valueOf(Config.getIntProperty(settings, PowerMode3.ConfigType.LINKER,"wobbleAmount", 20)));
        this.curve1AmountTextField.setText(String.valueOf(Config.getIntProperty(settings, PowerMode3.ConfigType.LINKER,"curve1Amount", 0)));

        this.maxCycleParticlesTextField.setText(String.valueOf(Config.getIntProperty(settings, PowerMode3.ConfigType.LINKER,"maxCycleParticles", 3)));
        this.isCyclicEnabled.setSelected(Config.getBoolProperty(settings, PowerMode3.ConfigType.LINKER,"isCyclicEnabled", false));
        ParticleSpriteLinkerAnchor.cyclicToggleEnabled = isCyclicEnabled.isSelected();

    }

    public void saveValues(int maxPsiSearchLimit, boolean isSettingEnabled) throws ConfigurationException {
        ParticleSpriteLinkerAnchor.settingEnabled = isSettingEnabled;

        int minLizardPsiDistance = Config.getJTextFieldWithinBoundsInt(this.minPsiAnchorDistanceTextField,
                0, maxPsiSearchLimit,
                "Min Distance to Psi Anchors will use when spawning linkers (cannot be greater than max defined at top)");
        settings.setSpriteTypeProperty(PowerMode3.ConfigType.LINKER, "minPsiSearchDistance",
                String.valueOf(minLizardPsiDistance));

        int maxLizardPsiDistance = Config.getJTextFieldWithinBoundsInt(this.maxPsiAnchorDistanceTextField,
                0, maxPsiSearchLimit,
                "Max Distance to Psi Anchors will use when spawning linkers (cannot be greater than max defined at top)");
        settings.setSpriteTypeProperty(PowerMode3.ConfigType.LINKER, "maxPsiSearchDistance",
                String.valueOf(maxLizardPsiDistance));

        int chancePerKeyPress = Config.getJTextFieldWithinBoundsInt(this.chancePerKeyPressTextField,
                0, 100,
                "chance linkers spawn per keypress");
        settings.setSpriteTypeProperty(PowerMode3.ConfigType.LINKER, "chancePerKeyPress",
                String.valueOf(chancePerKeyPress));

        int chanceOfSpawn = Config.getJTextFieldWithinBoundsInt(this.chanceOfSpawnTextField,
                0, 100,
                "chance links spawns for anchor");
        settings.setSpriteTypeProperty(PowerMode3.ConfigType.LINKER, "chanceOfSpawn",
                String.valueOf(chanceOfSpawn));

        int maxAnchorsToUse = Config.getJTextFieldWithinBoundsInt(this.maxAnchorsToUse,
                1, 100,
                "max anchors to use when spawning lizards");
        settings.setSpriteTypeProperty(PowerMode3.ConfigType.LINKER, "maxAnchorsToUse",
                String.valueOf(maxAnchorsToUse));

        settings.setSpriteTypeProperty(PowerMode3.ConfigType.LINKER, "moveWithCaretEnabled", String.valueOf(moveWithCaret.isSelected()));
        settings.setSpriteTypeProperty(PowerMode3.ConfigType.LINKER, "movespeed",
                String.valueOf(Config.getJTextFieldWithinBoundsFloat(this.moveSpeedTextField,
                        0.01f, 1.0f,"movespeed")));


        int maxLinks = Config.getJTextFieldWithinBoundsInt(this.maxLinksTextField,
                10, 50,
                "max links on linkerI");
        settings.setSpriteTypeProperty(PowerMode3.ConfigType.LINKER, "maxLinks",
                String.valueOf(maxLinks));


        settings.setSpriteTypeProperty(PowerMode3.ConfigType.LINKER, "tracerEnabled", String.valueOf(tracerEnabledCheckBox.isSelected()));

        //Cyclic global garbage lol
        settings.setSpriteTypeProperty(PowerMode3.ConfigType.LINKER, "isCyclicEnabled", String.valueOf(isCyclicEnabled.isSelected()));
        ParticleSpriteLinkerAnchor.cyclicToggleEnabled = isCyclicEnabled.isSelected();
        int maxCycleParticles = Config.getJTextFieldWithinBoundsInt(this.maxCycleParticlesTextField,
                1, 5,
                "max cycle particles per anchor linker");
        settings.setSpriteTypeProperty(PowerMode3.ConfigType.LINKER, "maxCycleParticles",
                String.valueOf(maxCycleParticles));
        ParticleSpriteLinkerAnchor.MAX_CYCLE_PARTICLES = LinkerConfig.MAX_CYCLE_PARTICLES(settings);

        //TODO::: LoaderSaver class, init in constructor for config, iterate over lists in loadValues/saveValues
        //    -- load()  in loadValues()
        //    --- save(), in saveValues()
        //    --- get()  in static methods
        int distanceFromCenter = Config.getJTextFieldWithinBoundsInt(this.distanceFromCenterTextField,
                1, 500,
                "start distance from center");
        settings.setSpriteTypeProperty(PowerMode3.ConfigType.LINKER, "distanceFromCenter",
                String.valueOf(distanceFromCenter));

        int wobbleAmount = Config.getJTextFieldWithinBoundsInt(this.wobbleAmountTextField,
                0, 200,
                "wobble ");
        settings.setSpriteTypeProperty(PowerMode3.ConfigType.LINKER, "wobbleAmount",
                String.valueOf(wobbleAmount));

        int curve1Amount = Config.getJTextFieldWithinBoundsInt(this.curve1AmountTextField,
                -100, 400,
                "curve1 ");
        settings.setSpriteTypeProperty(PowerMode3.ConfigType.LINKER, "curve1Amount",
                String.valueOf(curve1Amount));

        settings.setSerializedSDAJsonInfo(LinkerConfig.spriteDataAnimated, PowerMode3.ConfigType.LINKER);
    }



    public static int MAX_PSI_SEARCH(PowerMode3 settings) {
        return Config.getIntProperty(settings, PowerMode3.ConfigType.LINKER,"maxPsiSearchDistance");
    }
    public static int MIN_PSI_SEARCH(PowerMode3 settings) {
        return Config.getIntProperty(settings, PowerMode3.ConfigType.LINKER,"minPsiSearchDistance");
    }

    public static int CHANCE_OF_SPAWN(PowerMode3 settings){
        return Config.getIntProperty(settings, PowerMode3.ConfigType.LINKER, "chanceOfSpawn");
    }

    public static int MAX_ANCHORS_TO_USE(PowerMode3 settings){
        return Config.getIntProperty(settings, PowerMode3.ConfigType.LINKER, "maxAnchorsToUse");
    }

    public static int CHANCE_PER_KEY_PRESS(PowerMode3 settings){
        return Config.getIntProperty(settings, PowerMode3.ConfigType.LINKER, "chancePerKeyPress");
    }

    public static boolean TRACER_ENABLED(PowerMode3 settings){
        return Config.getBoolProperty(settings, PowerMode3.ConfigType.LINKER, "tracerEnabled");
    }

    public static Color TRACER_COLOR(PowerMode3 settings){
        return Config.getColorProperty(settings, PowerMode3.ConfigType.LINKER, "tracer Color", originalTracerColor);
    }

    public static int MAX_LINKS(PowerMode3 settings){
        return Config.getIntProperty(settings, PowerMode3.ConfigType.LINKER, "maxLinks");
    }

    public static int DISTANCE_FROM_CENTER(PowerMode3 settings){
        return Config.getIntProperty(settings, PowerMode3.ConfigType.LINKER, "distanceFromCenter");
    }

    public static int WOBBLE_AMOUNT(PowerMode3 settings){
        return Config.getIntProperty(settings, PowerMode3.ConfigType.LINKER, "wobbleAmount");
    }

    public static int CURVE1_AMOUNT(PowerMode3 settings){
        return Config.getIntProperty(settings, PowerMode3.ConfigType.LINKER, "curve1Amount");
    }

    public static boolean IS_CYCLIC_ENABLED(PowerMode3 settings){
        return Config.getBoolProperty(settings, PowerMode3.ConfigType.LINKER, "isCyclicEnabled");
    }

    public static int MAX_CYCLE_PARTICLES(PowerMode3 settings){
        return Config.getIntProperty(settings, PowerMode3.ConfigType.LINKER, "maxCycleParticles");
    }

    public static boolean MOVE_WITH_CARET(PowerMode3 settings){
        return Config.getBoolProperty(settings, PowerMode3.ConfigType.LINKER, "moveWithCaretEnabled",true);
    }

    //TODO PUT THIS IN TABLE as val4 , make one row change all other rows ugh
    public static float CARET_MOVE_SPEED(PowerMode3 settings){
        return Config.getFloatProperty(settings, PowerMode3.ConfigType.LINKER, "movespeed", 0.1f);
    }



    public static void setSpriteDataAnimated(ArrayList<SpriteDataAnimated> data){
        spriteDataAnimated = data;
        ParticleSpriteLinkerAnchor.spriteDataAnimated = data;
    }

    /**
     *
     * @val1 Offset to start on links
     * @val2 Repeat every N links
     * @val3 unused
     *
     *
     * @exampleConfig
     * "LINKER":{
     *       "tracerEnabled": false,
     *       "isCyclicEnabled" false,
     *       "distanceToCenter": 290,
     *       "tableData":[
     *         {
     *           "defaultPath": "./linker1/",
     *           "val2": 100,
     *           "isCyclic": false,
     *           "alpha": 1,
     *           "val1": 1,
     *           "scale": 0.3,
     *           "enabled": true,
     *           "speedRate": 2
     *         }
     *       ]
     *     }
     *
     *
     */
    public void loadJSONConfig(JSONObject configData, Path parentPath) throws JSONException {
        PowerMode3 settings = PowerMode3.getInstance();

        if(configData.has("maxLinks")) {
            int maxLinks = configData.getInt("maxLinks");
            this.maxLinksTextField.setText(String.valueOf(maxLinks));
            settings.setSpriteTypeProperty(PowerMode3.ConfigType.LINKER, "maxLinks",
                    String.valueOf(maxLinks));
        }

        if(configData.has("tracerEnabled")) {
            boolean tracerEnabled = configData.getBoolean("tracerEnabled");
            this.tracerEnabledCheckBox.setSelected(tracerEnabled);
            settings.setSpriteTypeProperty(PowerMode3.ConfigType.LINKER, "tracerEnabled",
                    String.valueOf(tracerEnabled));
        }


        if(configData.has("distanceToCenter")) {
            int distanceFromCenter = configData.getInt("distanceToCenter");
            distanceFromCenter = Math.min(500, Math.max(distanceFromCenter, 1));
            this.distanceFromCenterTextField.setText(String.valueOf(distanceFromCenter));
            settings.setSpriteTypeProperty(PowerMode3.ConfigType.LINKER, "distanceFromCenter",
                    String.valueOf(distanceFromCenter));
        }

        if(configData.has("isCyclicEnabled")) {
            boolean cyclicEnabled = configData.getBoolean("isCyclicEnabled");
            this.isCyclicEnabled.setSelected(cyclicEnabled);
            settings.setSpriteTypeProperty(PowerMode3.ConfigType.LINKER, "isCyclicEnabled",
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
                    jo.getInt("val2"), //val2  //repeat every n links
                    (float) jo.getDouble("alpha"),
                    jo.getInt("val1"), //val1  //offset to start on links
                    0);


            spriteDataAnimated.set(i, sd);
        }
    }
}


class LinkerTableModel extends AbstractConfigTableModel {

    static ArrayList<SpriteDataAnimated> data = LinkerConfig.spriteDataAnimated;


    public static final String[] columnNames = new String[]{
            "preview",
            "enabled?",
            "scale",
            "speed",
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

    public LinkerTableModel(BaseConfigJPanel config) {
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