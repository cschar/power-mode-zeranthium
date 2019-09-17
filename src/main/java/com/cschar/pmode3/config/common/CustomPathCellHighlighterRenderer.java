package com.cschar.pmode3.config.common;

import com.cschar.pmode3.config.common.SpriteData;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

public class CustomPathCellHighlighterRenderer extends JLabel implements TableCellRenderer {
    ArrayList<SpriteData> data;
    public CustomPathCellHighlighterRenderer(ArrayList<? extends SpriteData> data) {
        this.data = (ArrayList<SpriteData>) data;
        setOpaque(true); // Or color won't be displayed!
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        String val = (String)value;
        Color c;

        if (this.data.get(row).customPathValid) {
//            c = Color.WHITE;
            c = Color.lightGray;
            setText(row + " -- " + val);
        }else if(this.data.get(row).customPath != ""){
            c = Color.RED;
            setText(row + " -- " +  "!!Error loading path!!: " + val);
        }else{
            c = Color.WHITE;
//            c = Color.CYAN;
            setText(row + " -- ");
        }

        setBackground(c);

        return this;
    }
}