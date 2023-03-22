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
package goryachev.rich.model;

import java.util.ArrayList;

public class TStyledOutput implements StyledOutput {
    private ArrayList<Object> items = new ArrayList<>();

    public TStyledOutput() {
    }

    public Object[] getResult() {
        return items.toArray();
    }

    @Override
    public void append(StyledSegment seg) {
        System.out.println("TStyledOutput.append " + seg); // FIX
        if (seg.isText()) {
            String text = seg.getText();
            // TODO a) depends on view, b) may or may not have direct attributes, c) attributes are mutable
            StyleAttrs a = seg.getStyleAttrs();
            items.add(text);
            items.add(a);
        } else if (seg.isLineBreak()) {
            items.add("\n");
        } else {
            throw new Error("not yet supported: " + seg);
        }
    }

    @Override
    public Object getOutput() {
        return getResult();
    }
}
