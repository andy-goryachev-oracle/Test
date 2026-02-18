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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import javax.tools.SimpleJavaFileObject;
import javax.tools.JavaFileObject.Kind;

/**
 *
 */
// in memory
    public class InMemoryJavaFileObject extends SimpleJavaFileObject {

        private ByteArrayOutputStream out;
        
        public InMemoryJavaFileObject(String name, Kind kind) {
            super(URI.create("java-output:///" + name.replace('.', '/') + kind.extension), kind);
        }

        @Override
        public InputStream openInputStream() throws IOException {
            p("openInputStream");
            // TODO
            return null;
        }

        @Override
        public OutputStream openOutputStream() throws IOException {
            p("openOutputStream");
            out = new ByteArrayOutputStream();
            return out;
        }

        @Override
        public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
            p("openReader");
            // TODO
            return null;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            p("getCharContent");
            // TODO
            return null;
        }

        @Override
        public Writer openWriter() throws IOException {
            p("openWriter");
            // TODO
            return null;
        }

//        @Override
//        public long getLastModified() {
//            return 0;
//        }
//
//        @Override
//        public boolean delete() {
//            return false;
//        }

//        @Override
//        public NestingKind getNestingKind() {
//            // TODO
//            return null;
//        }

//        @Override
//        public Modifier getAccessLevel() {
//            // TODO
//            return null;
//        }
        
        private void p(String s) {
            if (true) {
                System.out.println(s);
            }
        }
    }