package com.cschar.pmode3.config;

import com.cschar.pmode3.Sound;
import com.cschar.pmode3.config.common.SoundData;
import com.cschar.pmode3.config.common.ui.JTableSoundButtonRenderer;
import com.cschar.pmode3.config.common.ui.SoundFileChooserDescriptor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class SoundConfigTableModel extends AbstractTableModel {

    public static ArrayList<SoundData> data = SoundConfig.soundData;
    public static Sound[] soundsPlaying = new Sound[data.size()];
    private static final Logger LOGGER = Logger.getInstance(SoundConfigTableModel.class);
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
                return JTableSoundButtonRenderer.getPreviewPlaySoundButton(this, soundsPlaying, d, row, column);
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


                    FileChooserDescriptor fd = new FileChooserDescriptor(true, false, false, false, false, false);
                    fd = new SoundFileChooserDescriptor(fd);

                    FileChooserDialog fcDialog = FileChooserFactory.getInstance().createFileChooser(fd, null, null);


                    VirtualFile[] vfs = fcDialog.choose(null);

                    if (vfs.length != 0) {
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
                v = Math.min(v, 100);
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
