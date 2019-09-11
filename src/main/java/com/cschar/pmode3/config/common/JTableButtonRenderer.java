package com.cschar.pmode3.config.common;

import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class JTableButtonRenderer implements TableCellRenderer {
    @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JButton button = (JButton)value;
        button.setBackground(Color.lightGray);
        button.setBorder(JBUI.Borders.empty(20,2));

        return button;
    }
}
