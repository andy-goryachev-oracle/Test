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

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Constants and methods used to persist settings.
 */
public class FxSettingsSchema {
    private static final String PREFIX = "FX.";
    
    private static final String WINDOW_NORMAL = "N";
    private static final String WINDOW_ICONIFIED = "I";
    private static final String WINDOW_MAXIMIZED = "M";
    private static final String WINDOW_FULLSCREEN = "F";

    public static void storeWindow(WindowMonitor m, Window w) {
        SStream ss = SStream.writer();
        ss.add(m.getX());
        ss.add(m.getY());
        ss.add(m.getWidth());
        ss.add(m.getHeight());
        if(w instanceof Stage s) {
            if(s.isIconified()) {
                ss.add(WINDOW_ICONIFIED);
            } else if(s.isMaximized()) {
                ss.add(WINDOW_MAXIMIZED);
            } else if(s.isFullScreen()) {
                ss.add(WINDOW_FULLSCREEN);
            } else {
                ss.add(WINDOW_NORMAL);
            }
        }
        FxSettings.setStream(PREFIX + m.getID(), ss);
    }
    
    public static void restoreWindow(WindowMonitor m, Window win) {
        SStream ss = FxSettings.getStream(PREFIX + m.getID());
        if(ss == null) {
            return;
        }
        
        double x = ss.nextDouble(-1);
        double y = ss.nextDouble(-1);
        double w = ss.nextDouble(-1);
        double h = ss.nextDouble(-1);
        String t = ss.nextString(WINDOW_NORMAL);
        
        if((w > 0) && (h > 0)) {
            if(isValid(x, y)) {
                win.setX(x);
                win.setY(y);
            }
            
            if(win instanceof Stage s) {
                if(s.isResizable()) {
                    s.setWidth(w);
                    s.setHeight(h);
                }
                
                switch(t) {
                case WINDOW_FULLSCREEN:
                    s.setFullScreen(true);
                    break;
                case WINDOW_MAXIMIZED:
                    s.setMaximized(true);
                    break;
                // TODO iconified?
                }
            }
        }
    }
    
    private static boolean isValid(double x, double y) {
        for(Screen s: Screen.getScreens()) {
            Rectangle2D r = s.getVisualBounds();
            if(r.contains(x, y)) {
                return true;
            }
        }
        return false;
    }

    public static void storeNode(WindowMonitor m, Node n) {
        // TODO
    }

    public static void restoreNode(WindowMonitor m, Node n) {
        // TODO
    }
}
