package com.cschar.pmode3.config;

import com.cschar.pmode3.ParticleSpriteLightningAlt;
import com.cschar.pmode3.PowerMode3;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Mandala2Config extends JPanel{


    JPanel firstCol;
    JPanel secondCol;

    JCheckBox sparksEnabled;
    JComponent mandalaRingConfigPanel;

    PowerMode3 settings;

    public Mandala2Config(PowerMode3 settings){
        this.settings = settings;

        this.setMaximumSize(new Dimension(1000,300));
        this.setLayout(new GridLayout(0,2)); //as many rows as necessary
        firstCol = new JPanel();
        firstCol.setLayout(new BoxLayout(firstCol, BoxLayout.PAGE_AXIS));
        this.add(firstCol);

        secondCol = new JPanel();
        secondCol.setLayout(new BoxLayout(secondCol, BoxLayout.Y_AXIS));
        JPanel headerPanel = new JPanel();
        JLabel headerLabel = new JLabel("Mandala Options");
        headerLabel.setFont(new Font ("Arial", Font.BOLD, 20));
        headerPanel.add(headerLabel);
        headerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        headerPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        headerPanel.setMaximumSize(new Dimension(300,100));

        secondCol.add(headerPanel);
        this.add(secondCol);



        mandalaRingConfigPanel = createConfigTable();
        firstCol.add(mandalaRingConfigPanel);


        secondCol.setOpaque(true);

    }

    public JComponent createConfigTable(){

        JBTable table = new JBTable();


        table.setRowHeight(200);
        table.setModel(new MandalaRingTableModel());

//        table.setShowGrid(false);
        table.setBounds(30, 40, 400, 300);
        table.setCellSelectionEnabled(false);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(false);
//        table.setBorder(JBUI.Borders.empty(10));
        table.setPreferredScrollableViewportSize(new Dimension(400,
                table.getRowHeight() * LightningAltConfig.sparkData.length));
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
//        table.setBackground(Color.yellow);
//        table.setOpaque(true);
        // adding it to JScrollPane
        JScrollPane sp = ScrollPaneFactory.createScrollPane(table);
//        sp.setMaximumSize(new Dimension(470,350));

        sp.setOpaque(true);
//        sp.setAlignmentX(Component.RIGHT_ALIGNMENT);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        TableColumnModel colModel=table.getColumnModel();

        colModel.getColumn(0).setPreferredWidth(80);
        colModel.getColumn(1).setPreferredWidth(80);
        colModel.getColumn(2).setPreferredWidth(60);
        colModel.getColumn(3).setPreferredWidth(80);
        colModel.getColumn(4).setPreferredWidth(100);
        colModel.getColumn(5).setPreferredWidth(50);



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

        TableCellRenderer pathRenderer = new CustomPathCellHighlighterRenderer();
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

//
//
//        ParticleSpriteLightningAlt.sparkData = sparkData;
//        settings.setSerializedSparkData(sparkData);

    }


    static SparkData[] sparkData;

    public static void setSparkData(SparkData[] data){
        sparkData = data;
        ParticleSpriteLightningAlt.sparkData = data;
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

    static SparkData[] data = LightningAltConfig.sparkData;



    public static final String[] columnNames = new String[]{
            "preview",
            "enabled?",
            "scale",
            "speed",
            "set path",
            "path",
            "reset"

    };





    private final Class[] columnClasses = new Class[]{
            ImageIcon.class,
            Boolean.class,
            Integer.class,
            Integer.class,
            JButton.class,
            String.class,
            JButton.class
    };

    @Override
    public int getRowCount() {

        return data.length;
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
//                return previewIcon;
                return data[row].previewIcon;
            case 1:
                return data[row].enabled;
            case 2:
                return data[row].scale;
            case 3:
                return data[row].weightedAmount;
            case 4:
                final JButton button = new JButton("Set path");
                button.addActionListener(arg0 -> {


                    FileChooserDescriptor fd = new FileChooserDescriptor(true,false,false,false,false,false);
                    FileChooserDialog fcDialog = FileChooserFactory.getInstance().createFileChooser(fd, null, null);


                    VirtualFile[] vfs = fcDialog.choose(null);
                    if(vfs.length != 0){
                        data[row].customPath = vfs[0].getPath();
                        data[row].setImage(vfs[0].getPath(), false);

                        this.fireTableDataChanged();
                    }

                });
                return button;
            case 5:
                return data[row].customPath;
            case 6:
                final JButton resetButton = new JButton("reset");
                resetButton.addActionListener(arg0 -> {
                    System.out.println("RESET");
                    data[row].setImage(data[row].defaultPath, true);
                    data[row].customPath = "";
                    data[row].customPathValid = false;

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
                data[row].enabled = (Boolean) value;
                return;
            case 2:
                float f = (Float) value;
                f = Math.max(0.0f, f);
                f = Math.min(f,2.0f);
                data[row].scale = (Float) f;
                return;
            case 3: //round robin number, 1-->100
                int v = (Integer) value;
                v = Math.max(1, v);
                v = Math.min(v,100);
                data[row].weightedAmount = v;
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
