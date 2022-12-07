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
package goryachev.rich.simple;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import goryachev.rich.StyledTextLine;
import goryachev.rich.StyledTextModel;

public class SimpleStyledTextModel implements StyledTextModel {
    private final ObservableList<StyledTextLine> lines = FXCollections.observableArrayList();

    public SimpleStyledTextModel() {
    }

    @Override
    public ObservableList<? extends StyledTextLine> getTextLines() {
        return lines;
    }

    public SimpleStyledTextModel addSegment(String text, String style, String... css) {
        if (lines.size() == 0) {
            lines.add(new SimpleStyledTextLine());
        }

        SimpleStyledTextLine t = (SimpleStyledTextLine)lines.get(lines.size() - 1);
        t.addSegment(text, style, css);
        return this;
    }

    public SimpleStyledTextModel nl() {
        return nl(1);
    }

    public SimpleStyledTextModel nl(int count) {
        for (int i = 0; i < count; i++) {
            lines.add(new SimpleStyledTextLine());
        }
        return this;
    }
}
