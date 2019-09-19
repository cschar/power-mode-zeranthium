package com.cschar.pmode3.config;

import com.cschar.pmode3.PowerMode3;
import com.cschar.pmode3.Sound;
import com.cschar.pmode3.config.common.*;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserFactory;
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





public class SoundConfig extends JPanel {

    public static ArrayList<SoundData> soundData;

    PowerMode3 settings;

    public SoundConfig(PowerMode3 settings){
        this.settings = settings;


        this.setMaximumSize(new Dimension(1000,300));
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

//        this.setLayout(new GridLayout(2,0));
        JPanel firstRow = new JPanel();
//        firstRow.setMinimumSize(new Dimension(1000,50));
        firstRow.setBackground(Color.YELLOW);
        firstRow.setOpaque(true);
        firstRow.setLayout(new BoxLayout(firstRow, BoxLayout.PAGE_AXIS));
        firstRow.add(new JLabel("H"));
        this.add(firstRow);

        JComponent configTable = createConfigTable();
        this.add(configTable);
    }

    public JComponent createConfigTable(){

        JBTable table = new JBTable();


        table.setRowHeight(60);
        SoundConfigTableModel tableModel = new SoundConfigTableModel();
        table.setModel(tableModel);

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

        colModel.getColumn(0).setPreferredWidth(60); //preview
        colModel.getColumn(1).setPreferredWidth(70);  //enabled
        colModel.getColumn(1).setWidth(50);  //enabled

        colModel.getColumn(2).setPreferredWidth(80);  //weight amount
        colModel.getColumn(3).setPreferredWidth(100);  //set path
        colModel.getColumn(4).setPreferredWidth(50);  // path
        colModel.getColumn(5).setWidth(60);  //reset



        //make table transparent
        table.setOpaque(false);
        table.setShowGrid(false);
        table.getTableHeader().setOpaque(false);

        ((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setOpaque(false);
        ((DefaultTableCellRenderer)table.getDefaultRenderer(String.class)).setOpaque(false);


        TableCellRenderer buttonRenderer = new JTableButtonRenderer();
        table.getColumn("preview").setCellRenderer(buttonRenderer);
        table.getColumn("set path").setCellRenderer(buttonRenderer);
        table.getColumn("reset").setCellRenderer(buttonRenderer);
        table.addMouseListener(new JTableButtonMouseListener(table));


        TableCellRenderer pathRenderer = new CustomPathCellHighlighterRenderer(SoundConfigTableModel.data);
        table.getColumn("path").setCellRenderer(pathRenderer);


        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());
        return sp;
    }
}


class SoundConfigTableModel extends AbstractTableModel {

    static ArrayList<SoundData> data = new ArrayList<SoundData>(){
        {
            add(new SoundData(true,20,"/sounds/h1.mp3",""));
            add(new SoundData(true,20,"/sounds/h2.mp3",""));
            add(new SoundData(true,20,"/sounds/h3.mp3",""));
            add(new SoundData(true,20,"/sounds/h4.mp3",""));

        }};



    public static final String[] columnNames = new String[]{
            "preview",
            "enabled?",
            "weight",
//            "weighted amount (1-100)",

            "set path",
            "path",
            "reset"

    };





    //TODO: https://sites.google.com/site/piotrwendykier/software/jtransforms
    // add a previewImage with FFT ?
    private final Class[] columnClasses = new Class[]{
            JButton.class,
            Boolean.class,
            Integer.class,
            JButton.class,
            String.class,
            JButton.class,
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
            case 4:
                return false;
            default:
                return true;
        }
    }


    @Override
    public Object getValueAt(int row, int column) {

//https://stackoverflow.com/questions/13833688/adding-jbutton-to-jtable
//        ImageIcon.class, Boolean.class, Integer.class, Boolean.class, String.class
//
        SoundData d = data.get(row);

        switch (column) {
            case 0:
                final JButton previewButton = new JButton("Preview");
                previewButton.addActionListener(arg0 -> {

                    Sound s;
                    if(d.customPathValid){
                        s = new Sound(d.customPath, false);
                    }else{
                        s = new Sound(d.defaultPath, true);
                    }
                    s.play();

//                    Sound s1 = new Sound(d.getPath(), !d.customPathValid); s1.play();

                });
                return previewButton;
            case 1:
                return d.enabled;
            case 2:
                return d.val1;
            case 3:
                final JButton button = new JButton("Set path");
                button.addActionListener(arg0 -> {


                    FileChooserDescriptor fd = new FileChooserDescriptor(true,false,false,false,false,false);
                    FileChooserDialog fcDialog = FileChooserFactory.getInstance().createFileChooser(fd, null, null);


                    VirtualFile[] vfs = fcDialog.choose(null);

                    if(vfs.length != 0){
                        System.out.println(vfs[0]);

                        d.setValidMP3Path(vfs[0]);
                        this.fireTableDataChanged();
                    }

                });
                return button;
            case 4:
                return data.get(row).customPath;
            case 5:
                final JButton resetButton = new JButton("reset");
                resetButton.addActionListener(arg0 -> {
                    System.out.println("RESET");
//                    d.setImageAnimated(d.defaultPath, true);
                    d.customPath = "";
                    d.customPathValid = false;
                    d.val1 = 20;


                    this.fireTableDataChanged();
                });
                return resetButton;

        }

        throw new IllegalArgumentException();
    }



    @Override
    public void setValueAt(Object value, int row, int column) {

//        ImageIcon.class, Boolean.class, Integer.class, Boolean.class, String.class

        SoundData d = data.get(row);

        switch (column) {
            case 0:  //sound preview button clicked
                return;
            case 1:  //enabled
                d.enabled = (Boolean) value;
                return;
            case 2: //val1 number (type chance)
                int v = (Integer) value;
                v = Math.max(1, v);
                v = Math.min(v,100);
                d.val1 = v;
                return;
            case 3:   //button clicked
                return;
            case 4:   // custom path
                return;
            case 5:    //reset button clicked
                return;

        }

        throw new IllegalArgumentException();
    }

}