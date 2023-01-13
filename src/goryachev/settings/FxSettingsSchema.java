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
// This code borrows heavily from the following project, with permission from the author:
// https://github.com/andy-goryachev/FxDock
package goryachev.settings;

import java.util.ArrayList;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Constants and methods used to persist settings.
 */
public class FxSettingsSchema {
    private static final String PREFIX = "FX.";
    private static final char SEP = ',';
    private static final char ESC = '\\';
    
    private static final String WINDOW_NORMAL = "N";
    private static final String WINDOW_ICONIFIED = "I";
    private static final String WINDOW_MAXIMIZED = "M";
    private static final String WINDOW_FULLSCREEN = "F";

    public static void storeWindow(WindowMonitor m, Window w) {
        ArrayList<Object> a = new ArrayList<>();
        a.add(m.getX());
        a.add(m.getY());
        a.add(m.getWidth());
        a.add(m.getHeight());
        if(w instanceof Stage s) {
            if(s.isIconified()) {
                a.add(WINDOW_ICONIFIED);
            } else if(s.isMaximized()) {
                a.add(WINDOW_MAXIMIZED);
            } else if(s.isFullScreen()) {
                a.add(WINDOW_FULLSCREEN);
            } else {
                a.add(WINDOW_NORMAL);
            }
        }
        FxSettings.set(PREFIX + m.getID(), toString(a));
    }
    
    public static void restoreWindow(WindowMonitor m, Window w) {
        // TODO
    }
    
    public static void storeNode(WindowMonitor m, Node n) {
        // TODO
    }

    public static void restoreNode(WindowMonitor m, Node n) {
        // TODO
    }

    private static String toString(ArrayList<Object> items) {
        StringBuilder sb = new StringBuilder();
        boolean sep = false;
        for (Object x: items) {
            if (sep) {
                sb.append(SEP);
            } else {
                sep = true;
            }
            sb.append(encode(x));
        }
        return sb.toString();
    }
    
    private static String encode(Object x) {
        if(x == null) {
            return "";
        }

        String s = x.toString();
        int len = s.length();
        int i = 0;
        for( ; i<len; i++) {
            char c = s.charAt(i);
            if(c == SEP) {
                break;
            } else if(c == ESC) {
                break;
            } else if(c < 0x20) {
                break;
            }
        }
        
        if(i == len) {
            return s;
        }
        
        StringBuilder sb = new StringBuilder(len + 8);
        sb.append(s, 0, i);
        for( ;i<len; i++) {
            char c = s.charAt(i);
            if(c == SEP) {
                escape(sb, c);
            } else if(c == ESC) {
                escape(sb, c);
            } else if(c < 0x20) {
                escape(sb, c);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static void escape(StringBuilder sb, char c) {
        sb.append(ESC);
        sb.append(nibble(c >> 4));
        sb.append(nibble(c));
    }

    private static char nibble(int ix) {
        return "0123456789abcdef".charAt(ix & 0x0f);
    }
}
