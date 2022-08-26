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

import java.awt.Color;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

/**
 *
 */
public class JTableTest {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> start());
    }
    
    protected static void start() {
        JTable t = new JTable(100, 6);
        
        int rm = 
//            JTable.AUTO_RESIZE_OFF;
//            JTable.AUTO_RESIZE_NEXT_COLUMN;
//            JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS;
//            JTable.AUTO_RESIZE_LAST_COLUMN;
            JTable.AUTO_RESIZE_ALL_COLUMNS;
        t.setAutoResizeMode(rm);
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(true);
        t.setGridColor(Color.LIGHT_GRAY);
        
        TableColumn tc = t.getTableHeader().getColumnModel().getColumn(0);
        tc.setMinWidth(100);
        t.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(20);
        
        JScrollPane scroll = new JScrollPane(t, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll, new JPanel());
        split.setContinuousLayout(true);
        
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.getContentPane().add(split);
        f.setSize(900, 300);
        f.setTitle("JTable Test " + System.getProperty("java.version"));
        f.setVisible(true);
    }
}
