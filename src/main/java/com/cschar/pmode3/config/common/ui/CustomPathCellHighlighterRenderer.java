package com.cschar.pmode3.config.common.ui;

import com.cschar.pmode3.config.common.PathData;
import com.cschar.pmode3.config.common.SpriteData;
import com.intellij.ui.JBColor;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

public class CustomPathCellHighlighterRenderer extends JLabel implements TableCellRenderer {
    ArrayList<PathData> data;
    public CustomPathCellHighlighterRenderer(ArrayList<? extends PathData> data) {
        this.data = (ArrayList<PathData>) data;
        setOpaque(true); // Or color won't be displayed!
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        String val = (String)value;
        Color c;

        if (this.data.get(row).customPathValid) {
//            c = Color.WHITE;
            c = new JBColor(Color.LIGHT_GRAY, Color.GRAY);
            setText(row + " -- " + val);
        }else if(!this.data.get(row).customPath.equals("")){
            c = new JBColor(Color.RED, new Color(126, 1, 0));
            setText(row + " -- " +  "!!Error loading path!!: " + val);
        }else{
            c = JBColor.WHITE;
//            c = Color.CYAN;
            setText(row + " -- ");
        }

        setBackground(c);

        return this;
    }
}
