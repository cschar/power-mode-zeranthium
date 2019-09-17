package com.cschar.pmode3.config;

import com.cschar.pmode3.ParticleSpriteLinkerAnchor;
import com.cschar.pmode3.ParticleSpriteLizardAnchor;
import com.cschar.pmode3.PowerMode3;
import com.cschar.pmode3.config.common.CustomPathCellHighlighterRenderer;
import com.cschar.pmode3.config.common.JTableButtonMouseListener;
import com.cschar.pmode3.config.common.JTableButtonRenderer;
import com.cschar.pmode3.config.common.SpriteDataAnimated;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserFactory;
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

public class LinkerConfig extends JPanel {

    JPanel mainPanel;
    PowerMode3 settings;

    public JTextField maxPsiAnchorDistanceTextField;
    public JTextField minPsiAnchorDistanceTextField;
    private JTextField chanceOfSpawnTextField;
    private JTextField maxAnchorsToUse;
    private JTextField chancePerKeyPressTextField;

    private JComponent linkerSpriteConfigPanel;

    public static ArrayList<SpriteDataAnimated> spriteDataAnimated;

    public LinkerConfig(PowerMode3 settings){
        this.settings = settings;

//        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
//        this.setMaximumSize(new Dimension(1000,300));
        this.setLayout(new GridLayout(2,0)); //as many rows as necessary

        mainPanel = new JPanel();
        mainPanel.setMaximumSize(new Dimension(1000,500));
        mainPanel.setLayout(new GridLayout(0,2));
        JPanel firstCol = new JPanel();
        firstCol.setLayout(new BoxLayout(firstCol, BoxLayout.PAGE_AXIS));
        mainPanel.add(firstCol);

        JPanel secondCol = new JPanel();
        secondCol.setLayout(new BoxLayout(secondCol, BoxLayout.Y_AXIS));
        JPanel headerPanel = new JPanel();
        JLabel headerLabel = new JLabel("LinkerI Options");
        headerLabel.setFont(new Font ("Arial", Font.BOLD, 20));
        headerPanel.add(headerLabel);
        headerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        headerPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        headerPanel.setMaximumSize(new Dimension(300,100));
//        headerPanel.setBackground(Color.ORANGE);
//        secondCol.add(headerLabel);
        secondCol.add(headerPanel);
        mainPanel.add(secondCol);

//        JPanel lizardColorPanel = Config.getColorPickerPanel("Lizard Color", PowerMode3.SpriteType.LIZARD, settings, Color.GREEN);
//        secondCol.add(lizardColorPanel);


        JPanel maxPsi = new JPanel();
//        maxPsi.setLayout(new FlowLayout(FlowLayout.RIGHT));
        maxPsi.add(new JLabel("Max Psi Anchor Scan Distance: "));
        this.maxPsiAnchorDistanceTextField = new JTextField("");
        this.maxPsiAnchorDistanceTextField.setMinimumSize(new Dimension(50,20));
        maxPsi.add(maxPsiAnchorDistanceTextField);
        maxPsi.setAlignmentX( Component.RIGHT_ALIGNMENT);//0.0
        maxPsi.setMaximumSize(new Dimension(500, 50));
        maxPsi.setLayout(new FlowLayout(FlowLayout.RIGHT));
        secondCol.add(maxPsi);



        JPanel minPsi = new JPanel();
        minPsi.add(new JLabel("Min Psi Anchor Scan Distance: "));
        this.minPsiAnchorDistanceTextField = new JTextField("");
        this.minPsiAnchorDistanceTextField.setMinimumSize(new Dimension(50,20));
        minPsi.add(minPsiAnchorDistanceTextField);
        minPsi.setLayout(new FlowLayout(FlowLayout.RIGHT));
        minPsi.setAlignmentX( Component.RIGHT_ALIGNMENT);//0.0
        minPsi.setMaximumSize(new Dimension(500, 50));
//        minPsi.setBackground(Color.YELLOW);
        secondCol.add(minPsi);

        this.chancePerKeyPressTextField = new JTextField();
        JLabel chancePerKeyPressLabel = new JLabel("Chance per keypress (0-100)");
        JPanel chancePerKeyPressPanel = new JPanel();
        chancePerKeyPressPanel.add(chancePerKeyPressLabel);
        chancePerKeyPressPanel.add(chancePerKeyPressTextField);
        chancePerKeyPressPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        chancePerKeyPressPanel.setAlignmentX( Component.RIGHT_ALIGNMENT);//0.0
        chancePerKeyPressPanel.setMaximumSize(new Dimension(400, 50));
        chancePerKeyPressPanel.setBackground(Color.lightGray);
        secondCol.add(chancePerKeyPressPanel);

        this.chanceOfSpawnTextField = new JTextField();
        JLabel chanceOfSpawnLabel = new JLabel("Chance of Link per anchor (0-100)");
        JPanel chancePanel = new JPanel();
        chancePanel.add(chanceOfSpawnLabel);
        chancePanel.add(chanceOfSpawnTextField);
        chancePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        chancePanel.setAlignmentX( Component.RIGHT_ALIGNMENT);//0.0
        chancePanel.setMaximumSize(new Dimension(400, 50));
//        chancePanel.setBackground(Color.lightGray);
        secondCol.add(chancePanel);

        this.maxAnchorsToUse = new JTextField();
//        this.maxAnchorsToUse.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JLabel maxAnchorsLabel = new JLabel("Max Anchors to Use (1-100)");
        JPanel maxAnchorsPanel = new JPanel();
        maxAnchorsPanel.add(maxAnchorsLabel);
        maxAnchorsPanel.add(maxAnchorsToUse);
        maxAnchorsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        maxAnchorsPanel.setAlignmentX( Component.RIGHT_ALIGNMENT);//0.0
        maxAnchorsPanel.setMaximumSize(new Dimension(500, 50));
//        maxAnchorsPanel.setBackground(Color.yellow);
        secondCol.add(maxAnchorsPanel);

        System.out.println("Initialized 1 ");

        linkerSpriteConfigPanel = createConfigTable();
//        firstCol.add(linkerSpriteConfigPanel);


        this.add(mainPanel);
        this.add(linkerSpriteConfigPanel);

        this.loadValues();

    }

    public JPanel getConfigPanel(){
        return this;
    }

    public JComponent createConfigTable(){

        JBTable table = new JBTable();


        table.setRowHeight(60);
        LinkerTableModel tableModel = new LinkerTableModel();
        table.setModel(tableModel);

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

        TableCellRenderer pathRenderer = new CustomPathCellHighlighterRenderer(LinkerConfig.spriteDataAnimated);
        table.getColumn("path").setCellRenderer(pathRenderer);


        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());
        System.out.println("Initialized");
        return sp;
    }


    public void loadValues(){
        this.maxPsiAnchorDistanceTextField.setText(String.valueOf(Config.getIntProperty(settings, PowerMode3.SpriteType.LINKER,"maxPsiSearchDistance", 300)));
        this.minPsiAnchorDistanceTextField.setText(String.valueOf(Config.getIntProperty(settings, PowerMode3.SpriteType.LINKER,"minPsiSearchDistance", 100)));

        this.chancePerKeyPressTextField.setText(String.valueOf(Config.getIntProperty(settings, PowerMode3.SpriteType.LINKER,"chancePerKeyPress", 100)));
        this.chanceOfSpawnTextField.setText(String.valueOf(Config.getIntProperty(settings, PowerMode3.SpriteType.LINKER,"chanceOfSpawn", 100)));

        this.maxAnchorsToUse.setText(String.valueOf(Config.getIntProperty(settings, PowerMode3.SpriteType.LINKER,"maxAnchorsToUse", 10)));

    }

    public void saveValues(int maxPsiSearchLimit) throws ConfigurationException {

        int minLizardPsiDistance = Config.getJTextFieldWithinBoundsInt(this.minPsiAnchorDistanceTextField,
                0, maxPsiSearchLimit,
                "Min Distance to Psi Anchors will use when spawning lizards (cannot be greater than max defined at top)");
        settings.setSpriteTypeProperty(PowerMode3.SpriteType.LINKER, "minPsiSearchDistance",
                String.valueOf(minLizardPsiDistance));

        int maxLizardPsiDistance = Config.getJTextFieldWithinBoundsInt(this.maxPsiAnchorDistanceTextField,
                0, maxPsiSearchLimit,
                "Max Distance to Psi Anchors will use when spawning lizards (cannot be greater than max defined at top)");
        settings.setSpriteTypeProperty(PowerMode3.SpriteType.LINKER, "maxPsiSearchDistance",
                String.valueOf(maxLizardPsiDistance));

        int chancePerKeyPress = Config.getJTextFieldWithinBoundsInt(this.chancePerKeyPressTextField,
                0, 100,
                "chance lizards spawn per keypress");
        settings.setSpriteTypeProperty(PowerMode3.SpriteType.LINKER, "chancePerKeyPress",
                String.valueOf(chancePerKeyPress));

        int chanceOfSpawn = Config.getJTextFieldWithinBoundsInt(this.chanceOfSpawnTextField,
                0, 100,
                "chance lizard spawns for anchor");
        settings.setSpriteTypeProperty(PowerMode3.SpriteType.LINKER, "chanceOfSpawn",
                String.valueOf(chanceOfSpawn));

        int maxAnchorsToUse = Config.getJTextFieldWithinBoundsInt(this.maxAnchorsToUse,
                1, 100,
                "max anchors to use when spawning lizards");
        settings.setSpriteTypeProperty(PowerMode3.SpriteType.LINKER, "maxAnchorsToUse",
                String.valueOf(maxAnchorsToUse));

        settings.setSerializedSpriteDataAnimated(LinkerConfig.spriteDataAnimated, PowerMode3.SpriteType.LINKER);
    }



    public static int MAX_PSI_SEARCH(PowerMode3 settings) {
        return Config.getIntProperty(settings, PowerMode3.SpriteType.LINKER,"maxPsiSearchDistance");
    }
    public static int MIN_PSI_SEARCH(PowerMode3 settings) {
        return Config.getIntProperty(settings, PowerMode3.SpriteType.LINKER,"minPsiSearchDistance");
    }

    public static int CHANCE_OF_SPAWN(PowerMode3 settings){
        return Config.getIntProperty(settings, PowerMode3.SpriteType.LINKER, "chanceOfSpawn");
    }

    public static int MAX_ANCHORS_TO_USE(PowerMode3 settings){
        return Config.getIntProperty(settings, PowerMode3.SpriteType.LINKER, "maxAnchorsToUse");
    }

    public static int CHANCE_PER_KEY_PRESS(PowerMode3 settings){
        return Config.getIntProperty(settings, PowerMode3.SpriteType.LINKER, "chancePerKeyPress");
    }



    public static void setSpriteDataAnimated(ArrayList<SpriteDataAnimated> data){
        spriteDataAnimated = data;
        ParticleSpriteLinkerAnchor.spriteDataAnimated = data;
    }
}


class LinkerTableModel extends AbstractTableModel {

    static ArrayList<SpriteDataAnimated> data = LinkerConfig.spriteDataAnimated;



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
                        System.out.println(vfs[0]);
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
                    System.out.println("RESET");
                    d.setImageAnimated(d.defaultPath, true);
                    d.customPath = "";
                    d.customPathValid = false;
                    d.scale = 1.0f;
                    d.weightedAmount = 20;


                    this.fireTableDataChanged();
                });
                return resetButton;
            case 7:
                return d.alpha;
//                SpriteDataAnimated d = data.get(row);
//                return new OtherCol(d.maxNumParticles, d.isCyclic);
            case 8: // weight --> offset
                return d.weightedAmount;
            case 9: // maxParticles --> repeatEvery
                return d.maxNumParticles;

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
                v0 = Math.min(v0,10);
                d.weightedAmount = v0;
            case 9:
                int v1 = (Integer) value;
                v1 = Math.max(1, v1);
                v1 = Math.min(v1,10);
                d.maxNumParticles = v1;

        }

        throw new IllegalArgumentException();
    }

}