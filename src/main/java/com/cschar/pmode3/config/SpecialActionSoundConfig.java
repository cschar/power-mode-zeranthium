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

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.ArrayList;


public class SpecialActionSoundConfig extends JPanel {


    public static ArrayList<SoundData> soundData;

    PowerMode3 settings;

    public enum KEYS {
        COPY,
        PASTE,
        BACKSPACE,
        DELETE,
        ENTER
    }

    public static int PREVIEW_SIZE = 50;

    public SpecialActionSoundConfig(PowerMode3 settings){
        this.settings = settings;


        this.setMaximumSize(new Dimension(1000,200));
        this.setLayout(new GridLayout(1,0));
//        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

//        this.setLayout(new GridLayout(2,0));
        JPanel firstRow = new JPanel();
        firstRow.setMaximumSize(new Dimension(1000,200));
//        firstRow.setBackground(Color.YELLOW);
        firstRow.setOpaque(true);
        firstRow.setLayout(new BoxLayout(firstRow, BoxLayout.PAGE_AXIS));

        JLabel headerLabel = new JLabel("Special Action Sound Options");
        headerLabel.setFont(new Font ("Arial", Font.BOLD, 20));
        headerLabel.setAlignmentX( Component.RIGHT_ALIGNMENT);//0.0
        firstRow.add(headerLabel);


        JComponent configTable = createConfigTable();
        firstRow.add(configTable);

//        this.add(configTable);
        this.add(firstRow);




    }

    public JComponent createConfigTable(){

        JBTable table = new JBTable();


        table.setRowHeight(PREVIEW_SIZE);

        table.setModel(new SpecialActionSoundConfigTableModel());

        table.setCellSelectionEnabled(false);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(false);

        table.setPreferredScrollableViewportSize(new Dimension(400,
                table.getRowHeight() * soundData.size() + 10));
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane sp = ScrollPaneFactory.createScrollPane(table);
        sp.setOpaque(true);
        TableColumnModel colModel=table.getColumnModel();

        colModel.getColumn(0).setWidth(PREVIEW_SIZE + 60); //preview
        colModel.getColumn(1).setPreferredWidth(60); //enabled
        colModel.getColumn(2).setWidth(150);  //hotkey text

        colModel.getColumn(3).setPreferredWidth(80); //set path
        colModel.getColumn(4).setPreferredWidth(100);  //path string
        colModel.getColumn(5).setWidth(60);  // reset




        //make table transparent
        table.setOpaque(false);
        table.setShowGrid(false);
        table.getTableHeader().setOpaque(false);

        ((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setOpaque(false);
        ((DefaultTableCellRenderer)table.getDefaultRenderer(String.class)).setOpaque(false);


        TableCellRenderer buttonRenderer = new JTableButtonRenderer();
        TableCellRenderer playerPreviewButtonRenderer = new JTableSoundButtonRenderer(SpecialActionSoundConfigTableModel.soundsPlaying);
        table.getColumn("preview").setCellRenderer(playerPreviewButtonRenderer);

        table.getColumn(MusicTriggerConfigTableModel.columnNames[2]).setCellRenderer(buttonRenderer);
        table.getColumn("reset").setCellRenderer(buttonRenderer);
        table.addMouseListener(new JTableButtonMouseListener(table));


        TableCellRenderer pathRenderer = new CustomPathCellHighlighterRenderer(soundData);
        table.getColumn("path").setCellRenderer(pathRenderer);


        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());
        return sp;
    }

    public void loadValues(){

//        this.soundEnabled.setSelected(Config.getBoolProperty(settings, PowerMode3.ConfigType.SOUND,"soundEnabled", true));
    }

    public void saveValues() {

//        settings.setSpriteTypeProperty(PowerMode3.ConfigType.SOUND, "soundEnabled", String.valueOf(soundEnabled.isSelected()));

        settings.setSerializedSoundData(soundData, PowerMode3.ConfigType.SPECIAL_ACTION_SOUND);

    }

    public static void setSoundData(ArrayList<SoundData> data){
        soundData = data;
    }
}


class SpecialActionSoundConfigTableModel extends AbstractTableModel {

    static ArrayList<SoundData> data = SpecialActionSoundConfig.soundData;
    public static Sound[] soundsPlaying = new Sound[data.size()];



    public static final String[] columnNames = new String[]{
            "preview",
            "enabled?",
            "key",
//            "weighted amount (1-100)",

            "set path (MP3)",
            "path",
            "reset"

    };








    private final Class[] columnClasses = new Class[]{
            JButton.class,
            Boolean.class,
            String.class,
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
            case 2:
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
            case 1:
                return data.get(row).enabled;
            case 2:
                String s="";
                if(row == SpecialActionSoundConfig.KEYS.COPY.ordinal()) { s="COPY"; }
                else if(row == SpecialActionSoundConfig.KEYS.PASTE.ordinal()){ s="PASTE";}
                else if(row == SpecialActionSoundConfig.KEYS.BACKSPACE.ordinal()){ s="BACKSPACE";}
                else if(row == SpecialActionSoundConfig.KEYS.DELETE.ordinal()){ s="DELETE";}
                else if(row == SpecialActionSoundConfig.KEYS.ENTER.ordinal()){ s="ENTER";}

                return "<html> <b> key: </b> " + s + "</html>";

            case 3:
                final JButton button = new JButton("Set path");
                button.addActionListener(arg0 -> {

                    FileChooserDescriptor fd = new FileChooserDescriptor(true,false,false,false,false,false);
                    fd = new SoundFileChooserDescriptor(fd);
//                    fd.setForcedToUseIdeaFileChooser(true);

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

                    d.customPath = "";
                    d.customPathValid = false;


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
                d.enabled = (Boolean) value;  // is set in hotkey map settings
                return;
            case 2:   //button clicked
                return;
            case 3:   // custom path
                return;
            case 4:    //reset button clicked
                return;
            case 5:    //reset button clicked
                return;

        }

        throw new IllegalArgumentException();
    }


}