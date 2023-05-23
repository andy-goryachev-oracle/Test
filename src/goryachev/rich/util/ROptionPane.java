/*
 * Copyright (c) 2023, Oracle and/or its affiliates. All rights reserved.
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
package goryachev.rich.util;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 *
 */
public class ROptionPane extends GridPane {
    private int row;
    private int column;
    private static final Insets MARGIN = new Insets(2, 4, 2, 4);
    
    public ROptionPane() {
        // no such thing
        // https://stackoverflow.com/questions/20454021/how-to-set-padding-between-columns-of-a-javafx-gridpane
        // setVGap(2);
    }
    
    public void label(String text) {
        add(new Label(text));
    }
    
    public void option(Node n) {
        add(n);
    }

    public void add(Node n) {
        add(n, column, row++);
        setMargin(n, MARGIN);
        setFillHeight(n, Boolean.TRUE);
        setFillWidth(n, Boolean.TRUE);
    }
}
