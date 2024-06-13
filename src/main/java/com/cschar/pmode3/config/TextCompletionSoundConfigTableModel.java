package com.cschar.pmode3.config;

import com.cschar.pmode3.Sound;
import com.cschar.pmode3.config.common.SoundData;
import com.cschar.pmode3.config.common.ui.AbstractConfigTableModel;
import com.cschar.pmode3.config.common.ui.JTableSoundButtonRenderer;
import com.cschar.pmode3.config.common.ui.SoundFileChooserDescriptor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import java.util.List;

public class TextCompletionSoundConfigTableModel extends AbstractConfigTableModel {

    public static Sound[] soundsPlaying;
    public static List<SoundData> soundData;
    public TextCompletionSoundConfigTableModel(BaseConfigJPanel config, List<SoundData> sd){
        super(config);
        soundsPlaying = new Sound[sd.size()];
        soundData = sd;
    }

//    public static ArrayList<SoundData> data = SpecialActionSoundConfig.soundData;

    private static final Logger LOGGER = Logger.getInstance(TextCompletionSoundConfigTableModel.class);
    public static void emptySounds(){
        if(soundsPlaying != null){
            for(Sound s : soundsPlaying){
                try {
                    s.stop();
                }catch (Exception e){
                    LOGGER.debug("sound already null");
//                    LOGGER.warn("tried closing sound", e);
                }
            }
            soundsPlaying = null;
        }
    }

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
        try {
            return soundData.size();
        } catch (Exception e) {
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
        SoundData d = soundData.get(row);

        switch (column) {
            case 0:
                return JTableSoundButtonRenderer.getPreviewPlaySoundButton(this, soundsPlaying, d, row, column);
            case 1:
                return soundData.get(row).enabled;
            case 2:
                return soundData.get(row).soundExtra1;
            case 3:
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
            case 4:
                return soundData.get(row).customPath;
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

        SoundData d = soundData.get(row);

        switch (column) {
            case 0:  //sound preview button clicked
                return;
            case 1:  //enabled
                d.enabled = (Boolean) value;  // is set in hotkey map settings
                return;
            case 2:   //text to complete

                // Limit to length 50
                String v = (String) value;
                if (v.length() > 50){
                    v = v.substring(0,50);
                }
                d.soundExtra1 = v;
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
