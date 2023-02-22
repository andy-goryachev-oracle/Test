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
package goryachev.apps;

import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

/**
 * Poor man replacement of my FX hacks.
 */
public class FX {
    public static Menu menu(MenuBar mb, String text) {
        Menu m = new Menu(text);
        mb.getMenus().add(m);
        return m;
    }

    public static MenuItem item(MenuBar mb, String text, Runnable action) {
        MenuItem mi = new MenuItem(text);
        mi.setOnAction((ev) -> action.run());
        lastMenu(mb).getItems().add(mi);
        return mi;
    }
    
    private static Menu lastMenu(MenuBar mb) {
        int ct = mb.getMenus().size();
        return mb.getMenus().get(ct - 1);
    }

    public static SeparatorMenuItem separator(MenuBar mb) {
        SeparatorMenuItem m = new SeparatorMenuItem();
        lastMenu(mb).getItems().add(m);
        return m;
    }

    public static void setStyle(Node n, String name, String value) {
        String oldStyle = n.getStyle();
        String newStyle = changeStyle(oldStyle, name, value);
        n.setStyle(newStyle);
    }
    
    private static String changeStyle(String s, String name, String value) {
        if (s == null) {
            return name + ":" + value + "; ";
        } else {
            String token = name + ":";
            // does not understand "name : value" with spaces
            int ix = s.indexOf(token);
            if(ix < 0) {
                return s + " " + token + value + "; ";
            } else {
                int ixe = s.indexOf(";", ix);
                if(ixe < 0) {
                    return s.substring(0, ix) + token + value + "; ";
                } else {
                    return s.substring(0, ix) + token + value + s.substring(ixe);
                }
            }
        }
    }
}
