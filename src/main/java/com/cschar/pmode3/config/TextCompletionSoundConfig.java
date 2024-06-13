package com.cschar.pmode3.config;

import com.cschar.pmode3.PowerMode3;
import com.cschar.pmode3.config.common.SoundData;
import com.cschar.pmode3.config.common.ui.CustomPathCellHighlighterRenderer;
import com.cschar.pmode3.config.common.ui.JTableButtonMouseListener;
import com.cschar.pmode3.config.common.ui.JTableButtonRenderer;
import com.cschar.pmode3.config.common.ui.JTableSoundButtonRenderer;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.ArrayList;

public class TextCompletionSoundConfig extends BaseConfigJPanel {

    public static ArrayList<SoundData> soundData;

    PowerMode3 settings;

    public static int PREVIEW_SIZE = 50;

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

    public void loadValues(){

//        this.soundEnabled.setSelected(Config.getBoolProperty(settings, PowerMode3.ConfigType.SOUND,"soundEnabled", true));
    }

    public void saveValues() {

        //TEXT_COMPLETION_SOUND
        settings.setSerializedSoundData(soundData, PowerMode3.ConfigType.TEXT_COMPLETION_SOUND);

    }

    public static void setSoundData(ArrayList<SoundData> data){
        soundData = data;
    }
}


