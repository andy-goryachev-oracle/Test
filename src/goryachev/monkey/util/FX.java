/*
 * Copyright (c) 2022, 2023, Oracle and/or its affiliates. All rights reserved.
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
package goryachev.monkey.util;

import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;

/**
 * Shortcuts and convenience methods that should be a part of JavaFX.
 */
public class FX {
    public static Menu menu(MenuBar b, String text) {
        Menu m = new Menu(text);
        b.getMenus().add(m);
        return m;
    }

    public static MenuItem item(MenuBar b, String text, Runnable action) {
        MenuItem mi = new MenuItem(text);
        mi.setOnAction((ev) -> action.run());
        lastMenu(b).getItems().add(mi);
        return mi;
    }
    
    public static MenuItem item(MenuBar b, MenuItem mi) {
        lastMenu(b).getItems().add(mi);
        return mi;
    }
    
    public static MenuItem item(MenuBar b, String text) {
        MenuItem mi = new MenuItem(text);
        lastMenu(b).getItems().add(mi);
        return mi;
    }

    private static Menu lastMenu(MenuBar b) {
        List<Menu> ms = b.getMenus();
        return ms.get(ms.size() - 1);
    }
    
    public static SeparatorMenuItem separator(MenuBar b) {
        SeparatorMenuItem s = new SeparatorMenuItem();
        lastMenu(b).getItems().add(s);
        return s;
    }

    public static RadioMenuItem radio(MenuBar b, String text, KeyCombination accelerator, ToggleGroup g) {
        RadioMenuItem mi = new RadioMenuItem(text);
        mi.setAccelerator(accelerator);
        mi.setToggleGroup(g);
        lastMenu(b).getItems().add(mi);
        return mi;
    }

    public static void add(GridPane p, Node n, int col, int row) {
        p.getChildren().add(n);
        GridPane.setConstraints(n, col, row);
    }
    
    public static <T> void select(ComboBox<T> cb, T value) {
        cb.getSelectionModel().select(value);
    }
    
    public static <T> T getSelectedItem(ComboBox<T> cb) {
        return cb.getSelectionModel().getSelectedItem();
    }
}
