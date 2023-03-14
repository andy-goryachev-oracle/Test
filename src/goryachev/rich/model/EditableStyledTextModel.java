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
 * TODO RTF format handler
 * TODO native format handler
 */
public class EditableStyledTextModel extends EditablePlainTextModel {
    private final ArrayList<StyledRun> runs = new ArrayList<>();

    public EditableStyledTextModel() {
        Marker m0 = newMarker(TextPos.ZERO);
        StyleAttrs a = new StyleAttrs();
        a.set(StyleAttrs.Attr.FONT_FAMILY, "System");
        a.set(StyleAttrs.Attr.FONT_SIZE, 1.0);
        
        runs.add(new StyledRun(m0, a));
    }
    
    @Override
    public StyledParagraph getParagraph(int index) {
        return new StyledParagraph(index) {
            @Override
            public String getText() {
                return getPlainText(index);
            }

            @Override
            public TextCell createTextCell() {
                String text = getText();
                TextCell c = new TextCell(index);
                StyleRunGenerator g = new StyleRunGenerator(text, 0, text.length());
                while(g.next()) {
                    c.addSegment(g.text(), g.style(), null);
                }
                return c;
            }
        };
    }
    
    @Override
    protected int insertTextSegment(int index, int offset, StyledSegment segment) {
        // TODO
        return super.insertTextSegment(index, offset, segment);
    }
    
    @Override
    protected void insertParagraph(int index, StyledSegment segment) {
        // no-op
    }

    @Override
    public void applyStyle(TextPos start, TextPos end, String direct, String[] css) {
        // TODO
    }

    @Override
    public void removeStyle(TextPos start, TextPos end, String direct, String[] css) {
        // TODO
    }
    
    @Override
    protected void exportSegments(int index, int start, int end, StyledOutput out) {
        // TODO
        super.exportSegments(index, start, end, out);
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
    
    /** generates styles */
    protected class StyleRunGenerator {
        private final String text;
        private int offset;
        private final int end;
        private String segment;
        private StyledRun currentRun;
        private int runIndex;
        
        public StyleRunGenerator(String text, int start, int end) {
            this.text = text;
            this.offset = start;
            this.end = end;
            runIndex = binarySearch(start);
        }
        
        private int binarySearch(int offset) {
            // TODO
            return 0;
        }
        
        /** returns remaining length in the currentRun or -1 if the next run is beyond this paragraph */
        private int remainingLength() {
            // assumes runIndex and currentRun are already set
            int ix = runIndex + 1;
            if(ix < runs.size()) {
                StyledRun r = runs.get(ix);
                TextPos p0 = currentRun.marker.getTextPos();
                TextPos p1 = r.marker.getTextPos();
                if(p1.index() == p0.index()) {
                    int len = p1.offset() - offset;
                    return len;
                }
            }
            return -1;
        }
        
        /**
         * prepares the next segment by setting text and style fields and returning true.
         * returns false when the end of text is reached. 
         */
        public boolean next() {
            if(offset < end) {
                int start = offset;
                currentRun = runs.get(runIndex);
                int len = remainingLength();
                if(len < 0) {
                    segment = text.substring(offset);
                    offset += segment.length();
                } else {
                    segment = text.substring(offset, offset + len);
                    offset += len;
                }
                return true;
            } else {
                return false;
            }
        }
        
        public String text() {
            return segment;
        }
        
        public String style() {
            return currentRun.attributes.getStyle();
        }
    }
}
