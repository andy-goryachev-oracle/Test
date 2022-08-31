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
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

/**
 *
 */
public class JTableResizeTest {
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
    
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> start());
    }
    
    protected static void start() {
        JComboBox<Mode> cb = new JComboBox<>(Mode.values());

        JPanel tb = new JPanel();
        tb.add(cb);
        
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
        
        JPanel p = new JPanel(new BorderLayout());
        p.add(split, BorderLayout.CENTER);
        p.add(tb, BorderLayout.NORTH);
        
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.getContentPane().add(p);
        f.setSize(900, 300);
        f.setTitle("JTable Test " + System.getProperty("java.version"));
        f.setVisible(true);
        
        cb.setSelectedItem(null);
        
        cb.addItemListener((ev) -> {
            Mode m = (Mode)cb.getSelectedItem();
            t.setAutoResizeMode(m.getValue());
        });
        
        cb.setSelectedItem(
      Mode.AUTO_RESIZE_OFF
//      Mode.AUTO_RESIZE_NEXT_COLUMN
//      Mode.AUTO_RESIZE_SUBSEQUENT_COLUMNS
//      Mode.AUTO_RESIZE_LAST_COLUMN
//      Mode.AUTO_RESIZE_ALL_COLUMNS
            );
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
