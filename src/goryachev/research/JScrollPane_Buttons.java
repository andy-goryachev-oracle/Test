/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
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
package goryachev.research;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Panel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

/**
 *
 */
public class JScrollPane_Buttons {
    public static void start() {
        EventQueue.invokeLater(() -> {
            new JScrollPane_Buttons().startEDT();
        });
    }
    
    void startEDT() {
        Panel left = new Panel(new FlowLayout());
        left.add(new JButton("A"));
        left.add(new JButton("B"));
        
        Panel right = new Panel(new FlowLayout());
        right.add(new JButton("A"));
        right.add(new JButton("B"));
        
        Panel p = new Panel(new FlowLayout());
        p.add(left);
        p.add(new JScrollPane(right));
        
        JFrame f = new JFrame();
        f.add(p);
        f.setVisible(true);
    }
}
