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
import goryachev.rich.Marker;
import goryachev.rich.TextCell;
import goryachev.rich.TextPos;

/**
 * Editable styled text model.
 * 
 * Extends the plain text editable model, adding styled runs, using an ordered
 * list of markers and a limited set of supported attributes.
 * 
 * TODO style segments or plain text + style runs?
 * TODO use styles (direct + css) or attributes (bold, italic, font size, text color)?
 * TODO RTF format handler
 * TODO native format handler
 */
public class EditableStyledTextModel extends EditablePlainTextModel {
    private final ArrayList<StyledRun> runs = new ArrayList<>();

    public EditableStyledTextModel() {
    }
    
    @Override
    public StyledParagraph getParagraph(int index) {
        return new StyledParagraph() {
            @Override
            public String getPlainText() {
                return EditableStyledTextModel.this.getPlainText(index);
            }

            @Override
            public int getIndex() {
                return index;
            }

            @Override
            public TextCell createTextCell() {
                String text = getPlainText();
                TextCell c = new TextCell(index);
                // TODO populate segments
                int start = 0;
                // next, styles
                //f.setStyle(STYLE);
                return c;
            }
        };
    }

    @Override
    public void applyStyle(TextPos start, TextPos end, String direct, String[] css) {
        // TODO
    }

    @Override
    public void removeStyle(TextPos start, TextPos end, String direct, String[] css) {
        // TODO
    }
    
    /** Represents a text run with a specific style */
    public static class StyledRun {
        public final Marker marker;
        public final StyleAttrs attributes;
        
        public StyledRun(Marker marker, StyleAttrs a) {
            this.marker = marker;
            this.attributes = a;
        }
    }
}
