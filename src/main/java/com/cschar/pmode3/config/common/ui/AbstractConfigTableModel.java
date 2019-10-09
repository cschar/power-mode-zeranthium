package com.cschar.pmode3.config.common.ui;

import com.cschar.pmode3.config.BaseConfigPanel;
import com.cschar.pmode3.config.DrosteConfig;
import com.cschar.pmode3.config.common.SpriteDataAnimated;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public abstract class AbstractConfigTableModel  extends AbstractTableModel {

    public BaseConfigPanel config;
    public AbstractConfigTableModel(BaseConfigPanel config){
        this.config = config;

    }

    public JButton getSetPathButton(SpriteDataAnimated d, ArrayList<SpriteDataAnimated> data){
        final JButton button = new JButton("Set path");
        button.addActionListener(arg0 -> {


            FileChooserDescriptor fd = new FileChooserDescriptor(false,true,false,false,false,false);
            FileChooserDialog fcDialog = FileChooserFactory.getInstance().createFileChooser(fd, null, null);


            VirtualFile[] vfs = fcDialog.choose(null);

            if(vfs.length != 0){

                d.customPath = vfs[0].getPath();
                d.setImageAnimated(vfs[0].getPath(), false);

                DrosteConfig.calculateSize(data, this.config.headerSizeLabel);
                this.fireTableDataChanged();
            }

        });
        return button;
    }

    public JButton getResetButton(SpriteDataAnimated d, ArrayList<SpriteDataAnimated> data){
        final JButton resetButton = new JButton("reset");
        resetButton.addActionListener(arg0 -> {

            d.setImageAnimated(d.defaultPath, true);
            d.customPath = "";
            d.customPathValid = false;

            DrosteConfig.calculateSize(data, this.config.headerSizeLabel);
            this.fireTableDataChanged();
        });
        return resetButton;
    }
}
