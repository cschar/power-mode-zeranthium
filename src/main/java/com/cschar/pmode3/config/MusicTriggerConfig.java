package com.cschar.pmode3.config;

import com.cschar.pmode3.PowerMode3;
import com.cschar.pmode3.Sound;
import com.cschar.pmode3.config.common.SoundData;
import com.cschar.pmode3.config.common.ui.*;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.keymap.KeymapManager;
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


public class MusicTriggerConfig extends JPanel {


    public static ArrayList<SoundData> soundData;

    PowerMode3 settings;

    public MusicTriggerConfig(PowerMode3 settings){
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

        JLabel headerLabel = new JLabel("Music Trigger Options");
        headerLabel.setFont(new Font ("Arial", Font.BOLD, 20));
        headerLabel.setAlignmentX( Component.RIGHT_ALIGNMENT);//0.0
        firstRow.add(headerLabel);

//        JTable configTable = createConfigTable();
//        tableModel = (MusicTriggerConfigTableModel) configTable.getModel();

        JComponent configTable = createConfigTable();
        firstRow.add(configTable);

//        this.add(configTable);
        this.add(firstRow);




    }

    public JComponent createConfigTable(){

        JBTable table = new JBTable();


        table.setRowHeight(60);

        table.setModel(new MusicTriggerConfigTableModel());

        table.setCellSelectionEnabled(false);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(false);

        table.setPreferredScrollableViewportSize(new Dimension(400,
                table.getRowHeight() * soundData.size()));
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane sp = ScrollPaneFactory.createScrollPane(table);
        sp.setOpaque(true);
        TableColumnModel colModel=table.getColumnModel();

        colModel.getColumn(0).setPreferredWidth(60); //preview
        colModel.getColumn(1).setWidth(150);  //hotkey text

        colModel.getColumn(2).setPreferredWidth(80); //set path
        colModel.getColumn(3).setPreferredWidth(100);  //path string
        colModel.getColumn(4).setWidth(60);  // reset




        //make table transparent
        table.setOpaque(false);
        table.setShowGrid(false);
        table.getTableHeader().setOpaque(false);

        ((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setOpaque(false);
        ((DefaultTableCellRenderer)table.getDefaultRenderer(String.class)).setOpaque(false);


        TableCellRenderer buttonRenderer = new JTableButtonRenderer();
        TableCellRenderer playerPreviewButtonRenderer = new JTableSoundButtonRenderer(MusicTriggerConfigTableModel.soundsPlaying);
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

        settings.setSerializedSoundData(MusicTriggerConfig.soundData, PowerMode3.ConfigType.MUSIC_TRIGGER);

    }

    public static void setSoundData(ArrayList<SoundData> data){
        soundData = data;
    }
}


