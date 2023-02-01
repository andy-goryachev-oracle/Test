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
// This code borrows heavily from the following project, with permission from the author:
// https://github.com/andy-goryachev/FxDock
package goryachev.settings;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.stage.PopupWindow;
import javafx.stage.Window;

/**
 * This facility coordinates saving UI settings to and from persistent media.
 * All the calls, excepr useProvider(), are expected to happen in an FX application thread.
 * 
 * TODO handle i/o errors - set handler?
 */
public class FxSettings {
    private static ISettingsProvider provider;
    private static final WeakHashMap<Window,WindowMonitor> monitors = new WeakHashMap<>(4);
    private static final AtomicBoolean save = new AtomicBoolean();
    private static Thread saveThread;

    public static void useDirectory(String dir) {
        File d = new File(System.getProperty("user.home"), dir);
        useProvider(new FxSettingsFileProvider(d));
    }

    /** call this in Application.init() */
    public static synchronized void useProvider(ISettingsProvider p) {
        if(provider != null) {
            throw new IllegalArgumentException("provider is already set");
        }
        
        provider = p;

        // TODO once, in FX thread - later?
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
        
        try {
            provider.load();
        } catch (IOException e) {
            throw new Error(e);
        }
    }
    
    public static void setName(Window w, String name) {
        // TODO
    }
    
    private static WindowMonitor getWindowMonitor(Window w) {
        WindowMonitor m = monitors.get(w);
        if(m == null) {
            String id = createID(w);
            m = new WindowMonitor(w, id);
            monitors.put(w, m);
        }
        return m;
    }
    
    private static String createID(Window win) {
        // TODO use name provided by setName
        String prefix = win.getClass().getSimpleName() + ".";
        
        HashSet<String> ids = new HashSet<>();
        for(Window w: Window.getWindows()) {
            if(w == win) {
                continue;
            }
            WindowMonitor m = monitors.get(w);
            String id = m.getID();
            if(id.startsWith(prefix)) {
                ids.add(id);
            }
        }
        
        for(int i=0; i<100_000; i++) {
            String id = prefix + i;
            if(!ids.contains(id)) {
                return id;
            }
        }

        // safeguard measure
        throw new Error("cannot create id: too many windows?");
    }

    private static void handleWindowOpening(Window w) {
        if (w instanceof PopupWindow) {
            return;
        }

        WindowMonitor m = getWindowMonitor(w);
        FxSettingsSchema.restoreWindow(m, w);

        Node p = w.getScene().getRoot();
        FxSettingsSchema.restoreNode(m, p);
    }

    private static void handleWindowClosing(Window w) {
        if (w instanceof PopupWindow) {
            return;
        }

        WindowMonitor m = getWindowMonitor(w);
        FxSettingsSchema.storeWindow(m, w);
        
        Node p = w.getScene().getRoot();
        FxSettingsSchema.storeNode(m, p);
    }

    public static void set(String key, String value) {
        provider.set(key, value);
        triggerSave();
    }
    
    public static void setStream(String key, SStream s) {
        provider.set(key, s);
        triggerSave();
    }
    
    public static String get(String key) {
        return provider.get(key);
    }

    public static int getInt(String key, int defaultValue) {
        String v = get(key);
        if (v != null) {
            try {
                return Integer.parseInt(v);
            } catch (NumberFormatException e) { }
        }
        return defaultValue;
    }
    
    public static SStream getStream(String key) {
        return provider.getSStream(key);
    }
    
    private static synchronized void triggerSave() {
        save.set(true);
        
        if(saveThread == null) {
            saveThread = new Thread("saving settings") {
                @Override
                public void run() {
                    do {
                        try {
                            sleep(50);
                        } catch (InterruptedException e) { }
                        
                        if(save.getAndSet(false)) {
                            try {
                                provider.save();
                            } catch(IOException e) {
                                // TODO handle error
                            }
                        }
                    } while(save.get());
                }
            };
            saveThread.start();
        }
    }
}
