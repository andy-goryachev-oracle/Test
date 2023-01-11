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
// This implementation borrows heavily (with permission from the author) from
// https://github.com/andy-goryachev/FxDock
package goryachev.settings;

import java.io.File;
import java.util.ArrayList;
import java.util.WeakHashMap;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.stage.Window;

/**
 * This facility coordinates saving UI settings to and from persistent media.
 */
public class FxSettings {
    private static ISettingsProvider provider;
    private static WeakHashMap<Window,WindowMonitor> monitors = new WeakHashMap<>(4);

    public static void useDirectory(String dir) {
        File d = new File(System.getProperty("user.home"), dir);
        useProvider(new FxSettingsFileProvider(d));
    }

    public static synchronized void useProvider(ISettingsProvider p) {
        if(provider != null) {
            throw new IllegalArgumentException("provider is already set");
        }
        
        provider = p;

        Window.getWindows().addListener((ListChangeListener.Change<? extends Window> ch) -> {
            while (ch.next()) {
                if (ch.wasAdded()) {
                    for(Window w: ch.getAddedSubList()) {
                        handleWindowOpening(w);
                    }
                } else if (ch.wasRemoved()) {
                    for(Window w: ch.getRemoved()) {
                        handleWindowClosing(w);
                    }
                }
            }
        });
    }
    
    private static WindowMonitor getWindowMonitor(Window w) {
        WindowMonitor m = monitors.get(w);
        if(m == null) {
            String id = w.getClass().getSimpleName(); // TODO +instance number
            m = new WindowMonitor(w, id);
            monitors.put(w, m);
        }
        return m;
    }

    private static void handleWindowOpening(Window w) {
        WindowMonitor m = getWindowMonitor(w);
        FxSettingsSchema.restoreWindow(m, w);
        
        Node p = w.getScene().getRoot();
        FxSettingsSchema.restoreNode(m, p);
    }

    private static void handleWindowClosing(Window w) {
        WindowMonitor m = getWindowMonitor(w);
        FxSettingsSchema.storeWindow(m, w);
        
        Node p = w.getScene().getRoot();
        FxSettingsSchema.storeNode(m, p);
    }

    public static void set(String key, String value) {
        provider.set(key, value);
    }
}
