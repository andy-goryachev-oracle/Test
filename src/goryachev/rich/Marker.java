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
// this code borrows heavily from the following project, with permission from the author:
// https://github.com/andy-goryachev/FxEditor
package goryachev.rich;

import goryachev.rich.impl.Markers;

/**
 * Tracks position in the text document.
 * 
 * TODO control maintains a weak list of these markers - what is someone creates a binding to one of the
 * marker's property?  will it break the binding?
 * 
 * Another altenative is to create an immutable position (similar to HitInfo, but w/o String text) and
 * have a single property in the Marker.
 * 
 * TODO three properties or a single immutable TextPos?
 */
public class Marker implements Comparable<Marker> {
    public static final Marker ZERO = new Marker(true);
    private int lineIndex;
    private int charIndex;
    private boolean leading;
    
    private Marker() {
    }
    
    private Marker(boolean leading) {
        this.leading = leading;
    }
    
    public static Marker create(Markers owner, int lineIndex, int charIndex, boolean leading) {
        if(owner == null) {
            throw new IllegalArgumentException("must specify the owner");
        }
        
        Marker m = new Marker();
        m.lineIndex = lineIndex;
        m.charIndex = charIndex;
        m.leading = leading;
        return m;
    }

    @Override
    public int compareTo(Marker m) {
        int d = lineIndex - m.lineIndex;
        if(d == 0) {
            d = getLineOffset() - m.getLineOffset();
            if(d == 0) {
                if(leading != m.leading) {
                    return leading ? -1 : 1;
                }
            }
        }
        return d;
    }
    
    @Override
    public int hashCode() {
        int h = Marker.class.hashCode();
        h = h * 31 + lineIndex;
        h = h * 31 + charIndex;
        h = h * 31 + Boolean.hashCode(leading);
        return h;
    }
    
    public int getLineIndex() {
        return lineIndex;
    }
    
    public int getCharIndex() {
        return charIndex;
    }
    
    public boolean isLeading() {
        return leading;
    }
    
    public int getLineOffset() {
        return leading ? charIndex : (charIndex + 1);
    }
}
