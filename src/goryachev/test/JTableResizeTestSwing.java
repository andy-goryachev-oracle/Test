/*
 * Copyright (c) 2022, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package goryachev.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

/**
 *
 */
public class JTableResizeTestSwing {
    enum Mode {
        AUTO_RESIZE_OFF(JTable.AUTO_RESIZE_OFF),
        AUTO_RESIZE_NEXT_COLUMN(JTable.AUTO_RESIZE_NEXT_COLUMN),
        AUTO_RESIZE_SUBSEQUENT_COLUMNS(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS),
        AUTO_RESIZE_LAST_COLUMN(JTable.AUTO_RESIZE_LAST_COLUMN),
        AUTO_RESIZE_ALL_COLUMNS(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        private final int value;
        Mode(int v) { this.value = v; }
        public int getValue() { return value; }
    }
    
    enum Demo {
        ALL("min, pref, max"),
        MAX("middle columns with max set")
        ;
        
        private final String text;
        Demo(String text) { this.text = text; }
        public String toString() { return text; }
    }
    
    protected static JPanel content;
    protected static JComponent current;
    
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> start());
    }
    
    protected static void start() {
        JComboBox<Demo> demoSelector = new JComboBox<>(Demo.values());
        JComboBox<Mode> resizeSelector = new JComboBox<>(Mode.values());
        
        JPanel tb = new JPanel();
        tb.add(new JLabel("Data: "));
        tb.add(demoSelector);
        tb.add(new JLabel(" Policy: "));
        tb.add(resizeSelector);
        
        JTable t = new JTable(100, 6);
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(true);
        t.setGridColor(Color.LIGHT_GRAY);
        
        conf(t, 0, 100, -1, -1);
        conf(t, 1, -1, -1, 20);
        conf(t, 4, -1, 500, -1);
        
        JScrollPane scroll = new JScrollPane(t, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll, new JPanel());
        split.setContinuousLayout(true);
        
        content = new JPanel(new BorderLayout());
//        content.add(split, BorderLayout.CENTER);
        content.add(tb, BorderLayout.NORTH);
        
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.getContentPane().add(content);
        f.setSize(900, 300);
        f.setTitle("JTable Test " + System.getProperty("java.version"));
        f.setVisible(true);
        
        demoSelector.setSelectedItem(null);
        demoSelector.addItemListener((ev) -> {
           Demo d = (Demo)demoSelector.getSelectedItem();
           updateTable(d);
        });
        
        demoSelector.setSelectedIndex(0);
        
        resizeSelector.setSelectedItem(null);
        resizeSelector.addItemListener((ev) -> {
            Mode m = (Mode)resizeSelector.getSelectedItem();
            t.setAutoResizeMode(m.getValue());
        });
        resizeSelector.setSelectedItem(
           Mode.AUTO_RESIZE_OFF
//      Mode.AUTO_RESIZE_NEXT_COLUMN
//      Mode.AUTO_RESIZE_SUBSEQUENT_COLUMNS
//      Mode.AUTO_RESIZE_LAST_COLUMN
//      Mode.AUTO_RESIZE_ALL_COLUMNS
            );
    }
    
    protected static void updateTable(Demo d) {
        JTable t = new JTable(100, 6);
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(true);
        t.setGridColor(Color.LIGHT_GRAY);
        
        switch(d) {
        case ALL:
            conf(t, 0, 100, -1, -1);
            conf(t, 1, -1, -1, 20);
            conf(t, 4, -1, 500, -1);
            break;
        case MAX:
            conf(t, 1, -1, -1, 20);
            conf(t, 2, -1, -1, 30);
            conf(t, 3, -1, -1, 40);
            conf(t, 4, -1, -1, 50);
            break;
        default:
            throw new Error("?" + d);
        }
        
        JScrollPane scroll = new JScrollPane(t, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll, new JPanel());
        split.setContinuousLayout(true);

        if (current != null) {
            content.remove(current);
        }
        content.add(split, BorderLayout.CENTER);
        content.validate();
        content.repaint();

        current = split;
    }
    
    protected static void conf(JTable t, int col, int min, int pref, int max) {
        TableColumn c = t.getTableHeader().getColumnModel().getColumn(col);
        
        if(min >= 0) {
            c.setMinWidth(min);
        }
        
        if(pref >= 0) {
            c.setPreferredWidth(pref);
        }

        if(max >= 0) {
            c.setMaxWidth(max);
        }
    }
}
