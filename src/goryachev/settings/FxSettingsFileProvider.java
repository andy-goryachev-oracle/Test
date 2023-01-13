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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Settings provider stores settings as a single file in the specified directory.
 */
public class FxSettingsFileProvider implements ISettingsProvider {
    private final File file;
    private final Properties data = new Properties();

    public FxSettingsFileProvider(File dir) {
        file = new File(dir, "uisettings.properties");
    }
    
    @Override
    public void load() throws IOException {
        if (file.exists() && file.isFile()) {
            InputStream in = new FileInputStream(file);
            try {
                data.load(in);
            } finally {
                in.close();
            }
        }
    }

    @Override
    public void save() throws IOException {
        if(file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        
        FileOutputStream out = new FileOutputStream(file);
        try
        {
            // TODO writing to a string buffer will be quicker
            synchronized (data) {
                data.store(out, null);
            }
        } finally {
            out.close();
        }
    }

    @Override
    public void set(String key, String value) {
        System.out.println("SET " + key + "=" + value); // FIX
        synchronized (data) {
            if (value == null) {
                data.remove(key);
            } else {
                data.put(key, value);
            }
        }
    }

    @Override
    public String get(String key) {
        String v = get2(key);
        System.out.println("GET " + key + "=" + v); // FIX
        return v;
    }
    private String get2(String key) {
        synchronized (data) {
            return data.getProperty(key);
        }
    }
}
