/*
 * Copyright (c) 2026, Oracle and/or its affiliates. All rights reserved.
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
package goryachev.research.runner;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Iterator;
import java.util.Set;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;

/**
 * in memory java file manager
 */
public class InMemoryJavaFileManager implements JavaFileManager {
    private final StandardJavaFileManager fm;
    private final URL url;
    
    public InMemoryJavaFileManager(StandardJavaFileManager fm, String sourceName) throws Exception {
        this.fm = fm;
        URLStreamHandler sf = new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL u) throws IOException {
                p("openConnection " + u);
                return null;
            }
        };
        this.url = URL.of(URI.create(createUrl(sourceName)), sf);
    }

    public static String createUrl(String name) {
        return "java-input:///" + name.replace('.', '/') + Kind.SOURCE.extension;
    }
    
    private void p(String s) {
        if (true) {
            System.out.println(s);
        }
    }

    @Override
    public int isSupportedOption(String option) {
        var v = fm.isSupportedOption(option);
        p("isSupportedOption " + option + " " + v);
        return v;
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
        var v = fm.getClassLoader(location);
        p("getClassLoader " + location + " " + v);

        return new URLClassLoader(new URL[] { url } , v) {
            @Override
            public InputStream getResourceAsStream(String name) {
                p("CL: getResourceAsStream " + name);
                var v = super.getResourceAsStream(name);
                return v;
            }
        };
    }

    @Override
    public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse) throws IOException {
        var v = fm.list(location, packageName, kinds, recurse);
        p("list " + location + " " + v);
        return v;
    }

    @Override
    public String inferBinaryName(Location location, JavaFileObject file) {
        var v = fm.inferBinaryName(location, file);
        p("inferBinaryName " + location + " file=" + file + " " + v);
        return v;
    }

    @Override
    public boolean isSameFile(FileObject a, FileObject b) {
        var v = fm.isSameFile(a, b);
        p("isSameFile " + a + " " + b + " " + v);
        return v;
    }

    @Override
    public boolean handleOption(String current, Iterator<String> remaining) {
        var v = fm.handleOption(current, remaining);
        p("handleOption " + current + " " + v);
        return v;
    }

    @Override
    public boolean hasLocation(Location location) {
        var v = fm.hasLocation(location);
        p("hasLocation " + location + " " + v);
        return v;
    }

    @Override
    public JavaFileObject getJavaFileForInput(Location location, String className, Kind kind) throws IOException {
        var v = fm.getJavaFileForInput(location, className, kind);
        p("getJavaFileForInput " + location + " " + v);
        return v;
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) throws IOException {
        System.out.println("getJavaFileForOutput " + location + " className=" + className + " kind=" + kind + " sibling=" + sibling);
        if (location.isOutputLocation()) {
            // TODO location/class name/sibling + store in a hashtable?
            return new InMemoryJavaFileObject(className, kind);
        }
        var v = fm.getJavaFileForOutput(location, className, kind, sibling);
        // TODO
        // getJavaFileForOutput CLASS_OUTPUT className=CompilerTest kind=CLASS sibling=goryachev.research.TestRunner$JavaSourceFromString[java-input:///CompilerTest.java] SimpleFileObject[/Users/angorya/Projects/Test3/Test/CompilerTest.class]
        p("getJavaFileForOutput " + location + " className=" + className + " kind=" + kind + " sibling=" + sibling + " " + v);
        return v;
    }

    @Override
    public FileObject getFileForInput(Location location, String packageName, String relativeName) throws IOException {
        var v = fm.getFileForInput(location, packageName, relativeName);
        p("getFileForInput " + location + " " + v);
        return v;
    }

    @Override
    public FileObject getFileForOutput(Location location, String packageName, String relativeName, FileObject sibling) throws IOException {
        var v = fm.getFileForOutput(location, packageName, relativeName, sibling);
        p("getFileForOutput " + location + " packageName=" + packageName + " relativeName=" + relativeName + " sibling=" + sibling + " " + v);
        return v;
    }

    @Override
    public void flush() throws IOException {
        fm.flush();
    }

    @Override
    public void close() throws IOException {
        fm.close();
    }
    
    @Override
    public Iterable<Set<Location>> listLocationsForModules(Location location) throws IOException {
        var v = fm.listLocationsForModules(location);
        p("listLocationsForModules " + location + " " + v);
        return v;
    }
    
    @Override
    public String inferModuleName(Location location) throws IOException {
        var v = fm.inferModuleName(location);
        p("inferModuleName " + location + " " + v);
        return v;
    }
}