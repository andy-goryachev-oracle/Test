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
package goryachev.settings;

import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Stage does not keep track of its normal bounds when minimized, maximized, or switched to full screen.
 */
class WindowMonitor {
    private final String id;
    private double x;
    private double y;
    private double width;
    private double height;
    private double x2;
    private double y2;
    private double w2;
    private double h2;
    
    public WindowMonitor(Window w, String id) {
        this.id = id;
        
        w.xProperty().addListener((p) -> updateX(w));
        w.yProperty().addListener((p) -> updateY(w));
        w.widthProperty().addListener((p) -> updateWidth(w));
        w.heightProperty().addListener((p) -> updateHeight(w));
        
        if(w instanceof Stage s) {
            s.iconifiedProperty().addListener((p) -> updateIconified(s));
            s.maximizedProperty().addListener((p) -> updateMaximized(s));
            s.fullScreenProperty().addListener((p) -> updateFullScreen(s));
        }
    }
    
    public String getID() {
        return id;
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    public double getWidth() {
        return width;
    }
    
    public double getHeight() {
        return height;
    }
    
    private void updateX(Window w) {
        x2 = x;
        x = w.getX();
    }

    private void updateY(Window w) {
        y2 = y;
        y = w.getY();
    }
    
    private void updateWidth(Window w) {
        w2 = width;
        width = w.getWidth();
    }
    
    private void updateHeight(Window w) {
        h2 = height;
        height = w.getHeight();
    }

    private void updateIconified(Stage s) {
        if(s.isIconified()) {
            x = x2;
            y = y2;
        }
    }

    private void updateMaximized(Stage s) {
        if(s.isMaximized()) {
            x = x2;
            y = y2;
        }
    }
    
    private void updateFullScreen(Stage s) {
        if(s.isFullScreen()) {
            x = x2;
            y = y2;
            width = w2;
            height = h2;
        }
    }
}
