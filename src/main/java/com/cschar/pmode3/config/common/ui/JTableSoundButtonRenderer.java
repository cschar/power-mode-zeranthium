package com.cschar.pmode3.config.common.ui;

import com.cschar.pmode3.Sound;
import com.cschar.pmode3.config.common.SoundData;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class JTableSoundButtonRenderer implements TableCellRenderer {

    Sound[] soundsPlaying;

    public JTableSoundButtonRenderer(Sound[] soundsPlaying){
        this.soundsPlaying = soundsPlaying;


    }
    @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JButton button = (JButton)value;
        if(soundsPlaying[row] != null){
            button.setBackground(Color.green);
            button.setText("Playing");
        }else{
            button.setBackground(Color.lightGray);
            button.setText("Preview");
        }

        button.setBorder(JBUI.Borders.empty(20,2));

        return button;
    }

    public static JButton getPreviewPlaySoundButton(AbstractTableModel table, Sound[] soundsPlaying, SoundData d, int row, int column){
        final JButton previewButton = new JButton("Preview");
        if(soundsPlaying[row] == null) {
            previewButton.addActionListener(arg0 -> {

                Sound s;
                if (d.customPathValid) {
                    s = new Sound(d.customPath, false);
                } else {
                    s = new Sound(d.defaultPath, true);
                }
                soundsPlaying[row] = s;

                table.fireTableCellUpdated(row, column);
                s.play(new Sound.SoundPlayCallback() {
                    @Override
                    public void call() {
                        soundsPlaying[row] = null;
                        table.fireTableCellUpdated(row,column);
                    }
                });
            });
        }else{
            //a sound is already playing!
            previewButton.addActionListener(arg0 -> {
                soundsPlaying[row].stop();
                soundsPlaying[row] = null;
                table.fireTableDataChanged();
            });

        }
        return previewButton;
    }
}
