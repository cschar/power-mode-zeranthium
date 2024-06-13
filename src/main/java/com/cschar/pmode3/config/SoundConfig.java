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


    /**
     *  only used for loading custom config packs
     *
     * @exampleConfig
     *  "SOUND": {
     *       "tableData": [
     *         {"weight": 20, "customPath": "./SOUND/sound1.mp3"},
     *         {"weight": 20, "customPath": "./SOUND/sound2.mp3"},
     *         {"weight": 20, "customPath": "./SOUND/sound3.mp3"},
     *         {"weight": 20, "customPath": "./SOUND/sound4.mp3"}
     *       ]
     *     }
     */
    public void loadJSONConfig(JSONObject configData, Path parentPath) throws JSONException {

        JSONArray tableData = configData.getJSONArray("tableData");

        for(int i =0; i<tableData.length(); i++){
            JSONObject spriteDataRow = tableData.getJSONObject(i);
            SoundData sd = overrideWithCustomConfig(spriteDataRow, i, parentPath);
            SoundConfig.soundData.set(i, sd);
        }//table data will change on scroll

    }


    /**
     * only allow specific fields to be overridden
     */
    private static SoundData overrideWithCustomConfig(JSONObject jo, int indexToReplace, Path parentPath) throws JSONException {

        SoundData sd = new SoundData(
                //default
                SoundConfig.soundData.get(indexToReplace).enabled,
                // allow override
                jo.getInt("weight"),
                //default
                SoundConfig.soundData.get(indexToReplace).defaultPath,
                //allow override
                parentPath.resolve(jo.getString("customPath")).toString()
                );

        return sd;
    }
}


