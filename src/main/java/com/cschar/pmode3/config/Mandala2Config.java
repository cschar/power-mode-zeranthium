package com.cschar.pmode3.config;

import com.cschar.pmode3.ParticleSpriteMandalaRing;
import com.cschar.pmode3.PowerMode3;
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

public class Mandala2Config extends JPanel{


    JPanel firstRow;
    JPanel secondRow;

    JCheckBox sparksEnabled;
    JComponent mandalaRingConfigPanel;

    PowerMode3 settings;

    public Mandala2Config(PowerMode3 settings){
        this.settings = settings;

        this.setMaximumSize(new Dimension(1000,300));
        this.setLayout(new GridLayout(1,0)); //as many rows as necessary
        firstRow = new JPanel();
        firstRow.setMaximumSize(new Dimension(1000,300));
        firstRow.setLayout(new GridLayout(0,2)); //as many rows as necessary
        JPanel firstRowCol1 = new JPanel();
        firstRowCol1.setBackground(Color.yellow);
        firstRowCol1.setOpaque(true);
        firstRowCol1.setLayout(new BoxLayout(firstRowCol1, BoxLayout.Y_AXIS));
        JPanel firstRowCol2 = new JPanel();
        firstRowCol2.setLayout(new BoxLayout(firstRowCol2, BoxLayout.Y_AXIS));
        firstRowCol2.setBackground(Color.ORANGE);
        firstRowCol2.setOpaque(true);
        firstRow.add(firstRowCol1);
        firstRow.add(firstRowCol2);

//        firstRow.setLayout(new BoxLayout(firstRow, BoxLayout.PAGE_AXIS));


        secondRow = new JPanel();
        secondRow.setMaximumSize(new Dimension(1000,500));
        secondRow.setLayout(new BoxLayout(secondRow, BoxLayout.Y_AXIS));
        JPanel headerPanel = new JPanel();
        JLabel headerLabel = new JLabel("Mandala Options");
        headerLabel.setFont(new Font ("Arial", Font.BOLD, 20));
        headerPanel.add(headerLabel);
        headerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        headerPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        headerPanel.setMaximumSize(new Dimension(300,100));

//        firstRowCol2.add(headerPanel);

//        secondRow.add(headerPanel);
        this.add(secondRow);
//        this.add(firstRow);



        mandalaRingConfigPanel = createConfigTable();
        secondRow.add(headerPanel);
        secondRow.add(mandalaRingConfigPanel);


        secondRow.setOpaque(true);

    }

    public JComponent createConfigTable(){

        JBTable table = new JBTable();


        table.setRowHeight(120);
        table.setModel(new MandalaRingTableModel());

//        table.setShowGrid(false);
//        table.setBounds(30, 40, 400, 300);
        table.setCellSelectionEnabled(false);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(false);
//        table.setBorder(JBUI.Borders.empty(10));
//        table.setPreferredScrollableViewportSize(new Dimension(400,
//                table.getRowHeight() * LightningAltConfig.sparkData.length));
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
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        TableColumnModel colModel=table.getColumnModel();

        colModel.getColumn(0).setPreferredWidth(120);
        colModel.getColumn(1).setPreferredWidth(80);
        colModel.getColumn(2).setPreferredWidth(60);
        colModel.getColumn(3).setPreferredWidth(80);
        colModel.getColumn(4).setPreferredWidth(100);
        colModel.getColumn(5).setPreferredWidth(50);

        //TODO: column for "constant cycle"
        //TODO: column for "max particles"  (max rings per row)

        //make table transparent
        table.setOpaque(false);
        table.setShowGrid(false);
        table.getTableHeader().setOpaque(false);

        ((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setOpaque(false);
//        ((DefaultTableCellRenderer)table.getDefaultRenderer(Integer.class)).setOpaque(false);
        ((DefaultTableCellRenderer)table.getDefaultRenderer(String.class)).setOpaque(false);
//        ((DefaultTableCellRenderer)table.getDefaultRenderer(ImageIcon.class)).setOpaque(false);


        TableCellRenderer buttonRenderer = new JTableButtonRenderer();
        table.getColumn("set path").setCellRenderer(buttonRenderer);
        table.getColumn("reset").setCellRenderer(buttonRenderer);
        table.addMouseListener(new JTableButtonMouseListener(table));

        TableCellRenderer pathRenderer = new MandalaCustomPathCellHighlighterRenderer();
        table.getColumn("path").setCellRenderer(pathRenderer);



        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());


        return sp;
//        return table;

    }


    public void loadValues(){
        
        //sparkData is loaded on settings instantiation
    }

    public void saveValues() throws ConfigurationException {
        settings.setSerializedSpriteDataAnimated(Mandala2Config.mandalaData);
    }


    static ArrayList<SpriteDataAnimated> mandalaData;

    public static void setSpriteDataAnimated(ArrayList<SpriteDataAnimated> data){
        mandalaData = data;
        ParticleSpriteMandalaRing.mandalaRingData = data;
    }
}

class MandalaCustomPathCellHighlighterRenderer extends JLabel implements TableCellRenderer {

    public MandalaCustomPathCellHighlighterRenderer() {
        setOpaque(true); // Or color won't be displayed!
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        String val = (String)value;
        Color c;




        if (MandalaRingTableModel.data.get(row).customPathValid) {
//            c = Color.WHITE;
            c = Color.lightGray;
            setText(row + " -- " + val);
        }else if(MandalaRingTableModel.data.get(row).customPath != ""){
            c = Color.RED;
            setText(row + " -- " +  "!!Error loading path!!: " + val);
        }else{
            c = Color.WHITE;
//            c = Color.CYAN;
            setText(row + " -- ");
        }

        setBackground(c);

        return this;
    }
}

//class MandalaJTableButtonRenderer implements TableCellRenderer {
//    @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//        JButton button = (JButton)value;
//        button.setBackground(Color.lightGray);
//        button.setBorder(JBUI.Borders.empty(20,2));
//
//        return button;
//    }
//}


class MandalaRingTableModel extends AbstractTableModel {

    static ArrayList<SpriteDataAnimated> data = Mandala2Config.mandalaData;



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
                        System.out.println(vfs[0]);
                        data.get(row).customPath = vfs[0].getPath();
                        data.get(row).setImageAnimated(vfs[0].getPath(), false);


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
                    data.get(row).setImageAnimated(data.get(row).defaultPath, true);
                    data.get(row).customPath = "";
                    data.get(row).customPathValid = false;

                    this.fireTableDataChanged();
                });
                return resetButton;


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


        }

        throw new IllegalArgumentException();
    }

}
