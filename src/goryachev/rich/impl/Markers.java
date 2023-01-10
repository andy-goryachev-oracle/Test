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
// this code borrows heavily from the following project, with permission from the author:
// https://github.com/andy-goryachev/FxEditor
package goryachev.rich.impl;

import goryachev.rich.Marker;
import goryachev.rich.TextPos;
import goryachev.rich.util.WeakList;

/**
 * Manages Markers.
 */
public class Markers {
    private static final int LIMIT_MARKER_COUNT_SAFEGUARD = 1_000_000;
    private final WeakList<Marker> markers;
    
    public Markers(int size ) {
        markers = new WeakList<>(size);
    }
    
    public Marker newMarker(int lineIndex, int charIndex, boolean leading) {
        Marker m = Marker.create(this, new TextPos(lineIndex, charIndex, leading));
        markers.add(m);
        
        // safeguard
        if (markers.size() > LIMIT_MARKER_COUNT_SAFEGUARD) {
            markers.gc();
            if (markers.size() > LIMIT_MARKER_COUNT_SAFEGUARD) {
                throw new RuntimeException("too many markers");
            }
        }
        
        return m;
    }
}
