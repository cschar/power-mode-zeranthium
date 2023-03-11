package com.cschar.pmode3.config;

import com.cschar.pmode3.PowerMode3;
import com.cschar.pmode3.Sound;
import com.cschar.pmode3.config.common.SoundData;
import com.cschar.pmode3.config.common.ui.*;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserFactory;
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





public class SoundConfig extends JPanel {


    public static ArrayList<SoundData> soundData;

    PowerMode3 settings;

    public SoundConfig(PowerMode3 settings){
        this.settings = settings;


        this.setMaximumSize(new Dimension(1000,300));
        this.setLayout(new GridLayout(1,2));
//        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

//        this.setLayout(new GridLayout(2,0));
        JPanel firstRow = new JPanel();
        firstRow.setMaximumSize(new Dimension(1000,300));
//        firstRow.setBackground(Color.YELLOW);
        firstRow.setOpaque(true);
        firstRow.setLayout(new BoxLayout(firstRow, BoxLayout.PAGE_AXIS));

        JLabel headerLabel = new JLabel("Basic Sound Options");
        headerLabel.setFont(new Font ("Arial", Font.BOLD, 20));
        headerLabel.setAlignmentX( Component.RIGHT_ALIGNMENT);//0.0
        firstRow.add(headerLabel);
        JLabel headerSubLabel = new JLabel("Will play on keypress");
        headerSubLabel.setFont(new Font ("Arial", Font.BOLD, 14));
        headerSubLabel.setAlignmentX( Component.RIGHT_ALIGNMENT);//0.0
        firstRow.add(headerSubLabel);


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

        JScrollPane sp = ScrollPaneFactory.createScrollPane(table);
        sp.setOpaque(true);
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
        TableCellRenderer playerPreviewButtonRenderer = new JTableSoundButtonRenderer(SoundConfigTableModel.soundsPlaying);

        table.getColumn("preview").setCellRenderer(playerPreviewButtonRenderer);

        table.getColumn(SoundConfigTableModel.columnNames[3]).setCellRenderer(buttonRenderer); // set path
        table.getColumn("reset").setCellRenderer(buttonRenderer);
        table.addMouseListener(new JTableButtonMouseListener(table));


        TableCellRenderer pathRenderer = new CustomPathCellHighlighterRenderer(SoundConfig.soundData);
        table.getColumn("path").setCellRenderer(pathRenderer);


        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());
        return sp;
    }

    public void loadValues(){
        //Done in plugin setup since config panel may not be launched before typing action
    }

    public void saveValues() {
        settings.setSerializedSoundData(SoundConfig.soundData, PowerMode3.ConfigType.SOUND);
    }

    public static void setSoundData(ArrayList<SoundData> data){
        soundData = data;
    }







    public void loadJSONConfig(JSONObject configData, Path parentPath) throws JSONException {

        JSONArray tableData = configData.getJSONArray("tableData");

        for(int i =0; i<tableData.length(); i++){
            JSONObject spriteDataRow = tableData.getJSONObject(i);
            SoundConfig.soundData.set(i, consumeJSONConfig(spriteDataRow, i, parentPath));
        }//table data will change on scroll

    }


    //consume config according to how SoundConfig allows custom changes
    private static SoundData consumeJSONConfig(JSONObject jo, int indexToReplace, Path parentPath) throws JSONException {

        SoundData sd = new SoundData(
                SoundConfig.soundData.get(indexToReplace).enabled,
                jo.getInt("weight"),
                SoundConfig.soundData.get(indexToReplace).defaultPath,
                parentPath.resolve(jo.getString("customPath")).toString()
                );

        return sd;
    }
}


class SoundConfigTableModel extends AbstractTableModel {

    static ArrayList<SoundData> data = SoundConfig.soundData;
    public static Sound[] soundsPlaying = new Sound[data.size()];


    public static final String[] columnNames = new String[]{
            "preview",
            "enabled?",
            "weight",
//            "weighted amount (1-100)",

            "set path (MP3)",
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
                return JTableSoundButtonRenderer.getPreviewPlaySoundButton(this, soundsPlaying, d, row,column);
//                final JButton previewButton = new JButton("Preview");
//                previewButton.addActionListener(arg0 -> {
//                    Sound s;
//                    if(d.customPathValid){
//                        s = new Sound(d.customPath, false);
//                    }else{
//                        s = new Sound(d.defaultPath, true);
//                    }
//                    s.play();
////                    Sound s1 = new Sound(d.getPath(), !d.customPathValid); s1.play();
//                });
//                return previewButton;
            case 1:
                return d.enabled;
            case 2:
                return d.val1;
            case 3:
                final JButton button = new JButton("Set path");
                button.addActionListener(arg0 -> {


                    FileChooserDescriptor fd = new FileChooserDescriptor(true,false,false,false,false,false);
                    fd = new SoundFileChooserDescriptor(fd);

                    FileChooserDialog fcDialog = FileChooserFactory.getInstance().createFileChooser(fd, null, null);


                    VirtualFile[] vfs = fcDialog.choose(null);

                    if(vfs.length != 0){
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