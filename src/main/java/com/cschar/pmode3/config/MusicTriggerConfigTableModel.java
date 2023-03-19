package com.cschar.pmode3.config;

import com.cschar.pmode3.PowerMode3;
import com.cschar.pmode3.Sound;
import com.cschar.pmode3.config.common.SoundData;
import com.cschar.pmode3.config.common.ui.JTableSoundButtonRenderer;
import com.cschar.pmode3.config.common.ui.SoundFileChooserDescriptor;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.keymap.KeymapManager;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class MusicTriggerConfigTableModel extends AbstractTableModel {

    public static ArrayList<SoundData> data = MusicTriggerConfig.soundData;
    static int MAX_SOUNDS = 10;
    public static Sound[] soundsPlaying = new Sound[MAX_SOUNDS];
    private static final Logger LOGGER = Logger.getInstance(MusicTriggerConfigTableModel.class);
    //TODO: can put this in Pmode3abstracttablemodel then have soundconfigtablemodels use that class
    // also need soundconfigs to then inherit from baseConfigJPanel
    public static void emptySounds(){
        if(soundsPlaying != null){
            for(Sound s : soundsPlaying){
                try {
                    s.stop();
                    LOGGER.debug("stopped sound playing: " + s.getPath());
                }catch (Exception e){
//                    LOGGER.debug("sound already null");
//                    LOGGER.warn("tried closing sound", e);
                }
            }
            soundsPlaying = null;
        }
    }
    KeymapManager keymapManager;

    public MusicTriggerConfigTableModel() {
        keymapManager = KeymapManager.getInstance();
    }


    public static final String[] columnNames = new String[]{
            "preview",
            "hotkey",
//            "weighted amount (1-100)",

            "set path (MP3)",
            "path",
            "reset"

    };


    //TODO: https://sites.google.com/site/piotrwendykier/software/jtransforms
    // add a previewImage with FFT ?
    private final Class[] columnClasses = new Class[]{
            JButton.class,
            String.class,
            JButton.class,
            String.class,
            JButton.class,
    };

    @Override
    public int getRowCount() {
        try {
            return data.size();
        }catch (Exception e){
            LOGGER.debug("error getting row count");
            return 0;
        }
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
            case 1:
            case 3:
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
                //TODO play up to 5x , have a stop button beside to cut all off
                return JTableSoundButtonRenderer.getPreviewPlaySoundButton(this, soundsPlaying, d, row, column);
            case 1:
                Shortcut[] zShortcuts;
                if (row == 0) {
                    //defined in the META-INF/plugin.xml <action id=""> param
                    zShortcuts = keymapManager.getActiveKeymap()
                            .getShortcuts("com.cschar.pmode3.PowerModeZeranthiumTriggerSongA");
//                }else if(row == 1){
                } else {
                    zShortcuts = keymapManager.getActiveKeymap()
                            .getShortcuts("com.cschar.pmode3.PowerModeZeranthiumTriggerSongB");
                }
                StringBuffer sb = new StringBuffer();
                for (Shortcut sc : zShortcuts) {
                    sb.append(sc + "<br>");
                }
                return "<html> <b> Hotkey: </b> " + sb.toString() + "</html>";

            case 2:
                final JButton button = new JButton("Set path");
                button.addActionListener(arg0 -> {

                    FileChooserDescriptor fd = new FileChooserDescriptor(true, false, false, false, false, false);
                    fd = new SoundFileChooserDescriptor(fd);
//                    fd.setForcedToUseIdeaFileChooser(true);

                    FileChooserDialog fcDialog = FileChooserFactory.getInstance().createFileChooser(fd, null, null);
                    VirtualFile[] vfs = fcDialog.choose(null);

                    if (vfs.length != 0) {
                        d.setValidMP3Path(vfs[0]);
                        this.fireTableDataChanged();
                    }

                });
                return button;
            case 3:
                return data.get(row).customPath;
            case 4:
                final JButton resetButton = new JButton("reset");
                resetButton.addActionListener(arg0 -> {

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
//                d.enabled = (Boolean) value;  // is set in hotkey map settings
                return;
            case 2:   //button clicked
                return;
            case 3:   // custom path
                return;
            case 4:    //reset button clicked
                return;

        }

        throw new IllegalArgumentException();
    }


}
