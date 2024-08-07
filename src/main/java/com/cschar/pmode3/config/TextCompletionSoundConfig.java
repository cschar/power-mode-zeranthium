package com.cschar.pmode3.config;

import com.cschar.pmode3.PowerMode3;
import com.cschar.pmode3.Sound;
import com.cschar.pmode3.ZJSONLoader;
import com.cschar.pmode3.config.common.SoundData;
import com.cschar.pmode3.config.common.ui.CustomPathCellHighlighterRenderer;
import com.cschar.pmode3.config.common.ui.JTableButtonMouseListener;
import com.cschar.pmode3.config.common.ui.JTableButtonRenderer;
import com.cschar.pmode3.config.common.ui.JTableSoundButtonRenderer;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.table.JBTable;
import com.intellij.util.SmartList;
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

public class TextCompletionSoundConfig extends BaseConfigJPanel {
    private static final Logger LOGGER = Logger.getInstance(TextCompletionSoundConfig.class);

    // Static so when JPanel is closed, this data still exists
    public static ArrayList<SoundData> soundData;

    PowerMode3 settings;

    public static int PREVIEW_SIZE = 50;

    public static int MAX_ROWS = 5;

    public TextCompletionSoundConfig(PowerMode3 settings){
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

        JLabel headerLabel = new JLabel("Text Completion Sound Options");
        headerLabel.setFont(new Font ("Arial", Font.BOLD, 20));
        headerLabel.setAlignmentX( Component.RIGHT_ALIGNMENT);//0.0
        firstRow.add(headerLabel);


        JComponent configTable = createConfigTable();
        firstRow.add(configTable);

//        this.add(configTable);
        this.add(firstRow);
    }

    // IncrementLadder(ladder int[], char c)
    // PlayCompleted(ladder int[])

    /**
     * increment ladder in place
     *
     * @param ladder    word ladder
     * @param c         character being typed
     * @param sounds    sound array to check
     *
     * @return array of whether to trigger sound
     */
    public static int[] incrementLadder(int[] ladder, char c, ArrayList<SoundData> sounds){
        int[] results = new int[ladder.length];
        c = Character.toLowerCase(c);

        for(int i =0; i < ladder.length; i++){
            if ( i >= sounds.size() ){
                break;
            }

            String word = sounds.get(i).soundExtra1;
            //TODO: put lowercase when loading
            word = word.toLowerCase();
            int curIdx = ladder[i];

            if(word.charAt(curIdx) == c){
                ladder[i]++;
                if(word.length() == ladder[i]){
                    results[i] = 1;

                    //reset, start at 0
                    ladder[i] = 0;
                    //if first char is match, start at 1
                    if (c == word.charAt(0)) {
                        ladder[i]++;
                    }
                }
            }else{
                //reset, start at 0
                ladder[i] = 0;
                //if first char is match, start at 1
                if (c == word.charAt(0)) {
                    ladder[i]++;
                }
            }
        }
        return results;
    }

    public static String getDefaultWord(int row){
        ZJSONLoader z = new ZJSONLoader();
        SmartList<String> defaults = z.getDefaultJSONTableConfigs().get(PowerMode3.ConfigType.TEXT_COMPLETION_SOUND);
        SoundData sd = SoundData.fromJsonObjectString(defaults.get(row));
        return sd.soundExtra1;
    }

    public JComponent createConfigTable(){

        JBTable table = new JBTable();
        table.setRowHeight(PREVIEW_SIZE);
        TextCompletionSoundConfigTableModel tableModel = new TextCompletionSoundConfigTableModel(this, soundData);
        table.setModel(tableModel);
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
        TableCellRenderer playerPreviewButtonRenderer = new JTableSoundButtonRenderer(tableModel.soundsPlaying);
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

    /**
     *  only used for loading custom config packs
     *
     * @exampleConfig
     *  "TEXT_COMPLETION_SOUND": {
            "tableData": [
                {"enabled": true,  "soundExtra1": "my_word_1",       "customPath": "./SOUND/sound1.mp3"},
                {"enabled": true,  "soundExtra1": spaced words 20",  "customPath": "./SOUND/sound2.mp3"},
                {"enabled": false, "soundExtra1": "functionName500", "customPath": "./SOUND/sound3.mp3"},
                {"enabled": true,  "soundExtra1": "import",          "customPath": "./SOUND/sound4.mp3"}
             ]
     *     }
     */
    public void loadJSONConfig(JSONObject configData, Path parentPath) throws JSONException {
        JSONArray tableData = configData.getJSONArray("tableData");

        for(int i =0; i<tableData.length(); i++){
            if (i >= MAX_ROWS) {
                LOGGER.debug("TextCompletionSound: cannot load more than " + MAX_ROWS + " entries... stopping");
                break;
            }

            JSONObject soundDataRow = tableData.getJSONObject(i);
            SoundData sd = overrideWithCustomConfig(soundDataRow, i, parentPath);
            TextCompletionSoundConfig.soundData.set(i, sd);

        }

    }

    /**
     * only allow specific fields to be overridden
     */
    private static SoundData overrideWithCustomConfig(JSONObject jo, int indexToReplace, Path parentPath) throws JSONException {

        String customWord = jo.has("soundExtra1") ? jo.getString("soundExtra1") : "";

        Boolean enabled = jo.has("enabled") && jo.getBoolean("enabled");

        String customPath = jo.has("customPath") ? jo.getString("customPath") : "";


        SoundData sd = new SoundData(
                //allow override
                enabled,
                // default
                SoundConfig.soundData.get(indexToReplace).val1,
                //default
                SoundConfig.soundData.get(indexToReplace).defaultPath,
                //allow override
                parentPath.resolve(customPath).toString(),
                //allow override
                customWord
        );

        return sd;
    }

    public void loadValues(){
        //Done in plugin setup since config panel may not be launched before typing action
    }

    public void saveValues() {
        //TEXT_COMPLETION_SOUND
        settings.setSerializedSoundData(soundData, PowerMode3.ConfigType.TEXT_COMPLETION_SOUND);
    }

    public static void setSoundData(ArrayList<SoundData> data){
        soundData = data;
    }
}


