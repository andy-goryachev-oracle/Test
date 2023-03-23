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
import java.util.HashSet;
import goryachev.rich.TextCell;
import goryachev.rich.TextPos;

/**
 * Editable Styled Text Model based on a collection of styled segments.
 * 
 * This model is suitable for relatively small
 */
public class EditableRichTextModel2 extends EditableStyledTextModelBase {
    private final ArrayList<RParagraph> paragraphs = new ArrayList<>();
    // TODO reduce memory consumption, later
    private final HashSet<StyleAttrs> styles = new HashSet<>();
    
    public EditableRichTextModel2() {
        paragraphs.add(new RParagraph());
        registerDataFormatHandler(new PlainTextFormatHandler());
    }
    
    @Override
    public int getParagraphCount() {
        return paragraphs.size();
    }
    
    @Override
    public String getPlainText(int index) {
        return paragraphs.get(index).getPlainText();
    }

    @Override
    public StyledParagraph getParagraph(int index) {
        RParagraph p = paragraphs.get(index);

        return new StyledParagraph(index) {
            @Override
            public String getText() {
                return p.getPlainText();
            }

            @Override
            public TextCell createTextCell() {
                // TODO empty paragraph?
                TextCell c = new TextCell(index);
                for(RSegment seg: p) {
                    String text = seg.text();
                    String style = seg.attrs().getStyle();
                    c.addSegment(text, style, null);
                }
                return c;
            }
        };
    }

    @Override
    protected int insertTextSegment(int index, int offset, StyledSegment segment) {        
        String text = segment.getText();
        // TODO may need to resolve the styles
        StyleAttrs a = getStyleAttrs(segment);

        RParagraph par = paragraphs.get(index);
        par.insertText(offset, text, a);
        return text.length();
    }
    
    protected StyleAttrs getStyleAttrs(StyledSegment segment) {
        StyleAttrs a = segment.getStyleAttrs();
        if(a == null) {
            // TODO convert
            a = new StyleAttrs();
        }
        // TODO pass through styles hash set to save on memory
        return a;
    }

    @Override
    protected void insertLineBreak(int index, int offset) {
        if(index >= getParagraphCount()) {
            paragraphs.add(new RParagraph());
        } else {
            RParagraph par = paragraphs.get(index);
            RParagraph par2 = par.insertLineBreak(offset);
            paragraphs.add(index + 1, par2);
        }
    }

    @Override
    protected void removeRegion(TextPos start, TextPos end) {
        int ix = start.index();
        RParagraph par = paragraphs.get(ix);

        if (ix == end.index()) {
            par.removeRegion(start.offset(), end.offset());
        } else {
            RParagraph last = paragraphs.get(end.index());
            last.removeRegion(0, end.offset());
            
            par.removeRegion(start.offset(), -1);
            par.append(last);

            int ct = end.index() - ix;
            ix++;
            for (int i = 0; i < ct; i++) {
                paragraphs.remove(ix);
            }
        }
    }

    @Override
    protected void insertParagraph(int index, StyledSegment segment) {
        // TODO
    }

    @Override
    protected void exportSegments(int index, int startOffset, int endOffset, StyledOutput out) {
        // TODO
    }

    @Override
    public void applyStyle(TextPos start, TextPos end, StyleAttrs attrs) {
        // TODO
    }

    @Override
    public void removeStyle(TextPos start, TextPos end, StyleAttrs attrs) {
        // TODO
    }

    @Override
    public StyleAttrs getStyledAttrs(TextPos pos) {
        // TODO
        return null;
    }

    /**
     * Model rich text segment.
     */
    protected static class RSegment {
        private String text;
        private final StyleAttrs attrs;
        
        public RSegment(String text, StyleAttrs attrs) {
            this.text = text;
            this.attrs = attrs;
        }
        
        public String text() {
            return text;
        }
        
        public StyleAttrs attrs() {
            return attrs;
        }
        
        public int length() {
            return text.length();
        }

        // TODO unit test
        public void removeRegion(int start, int end) {
            int len = text.length();
            if (end < 0) {
                text = text.substring(start);
            } else {
                if (start == 0) {
                    text = text.substring(end);
                } else {
                    text = text.substring(0, start) + text.substring(end, len);
                }
            }
        }
        
        public void append(String s) {
            text = text + s;
        }
    }

    /**
     * Model paragraph is a list of RSegments.
     */
    protected static class RParagraph extends ArrayList<RSegment> {
        public String getPlainText() {
            StringBuilder sb = new StringBuilder();
            for(RSegment s: this) {
                sb.append(s.text());
            }
            return sb.toString();
        }
        
        public int length() {
            return getPlainText().length();
        }

        public void insertText(int offset, String text, StyleAttrs attrs) {
            int off = 0;
            int ct = size();
            for (int i = 0; i < ct; i++) {
                if (offset == off) {
                    // insert at the beginning
                    insert(i, text, attrs);
                    return;
                } else {
                    RSegment seg = get(i);
                    int len = seg.length();
                    if ((offset > off) && (offset <= off + len)) {
                        // split segment
                        StyleAttrs a = seg.attrs();
                        String toSplit = seg.text();
                        int ix = offset - off;

                        String s1 = toSplit.substring(0, ix);
                        set(i++, new RSegment(s1, a));
                        if (insert(i, text, attrs)) {
                            i++;
                        }
                        if (ix < toSplit.length()) {
                            String s2 = toSplit.substring(ix);
                            insert(i, s2, a);
                        }
                        return;
                    }

                    off += len;
                }
            }

            // insert at the end
            insert(ct, text, attrs);
        }

        /**
         * Inserts a new segment, or appends to the previous segment if style is the same.
         * Returns true if a segment has been added.
         */
        private boolean insert(int ix, String text, StyleAttrs a) {
            if (ix > 0) {
                RSegment prev = get(ix - 1);
                if (a.equals(prev.attrs())) {
                    // combine
                    prev.append(text);
                    return false;
                }
            }

            RSegment seg = new RSegment(text, a);
            if (ix < size()) {
                add(ix, seg);
            } else {
                add(seg);
            }
            return true;
        }

        /** trims this paragraph and returns the remainder to be inserted next */
        public RParagraph insertLineBreak(int offset) {
            int off = 0;
            RParagraph next = new RParagraph();
            int i;
            int ct = size();
            for (i = 0; i < ct; i++) {
                RSegment seg = get(i);
                int len = seg.length();
                if (offset < (off + len)) {
                    if (offset != off) {
                        // split segment
                        StyleAttrs a = seg.attrs();
                        String toSplit = seg.text();
                        int ix = offset - off;
                        String s1 = toSplit.substring(0, ix);
                        String s2 = toSplit.substring(ix);
                        set(i, new RSegment(s1, a));

                        next.add(new RSegment(s2, a));
                        i++;
                    }
                    break;
                }
                off += len;
            }

            // move remaining segments to the next paragraph
            while (i < size()) {
                RSegment seg = remove(i);
                next.add(seg);
            }

            return next;
        }

        public void append(RParagraph p) {
            addAll(p);
        }

        /** end = -1 means remove from start to the end of paragraph */
        public void removeRegion(int start, int end) {
            int ix0 = -1;
            int off0 = 0;
            int off = 0;
            int ct = size();

            // find start segment
            int i = 0;
            for (; i < ct; i++) {
                RSegment seg = get(i);
                int len = seg.length();
                if (start < (off + len)) {
                    ix0 = i;
                    off0 = start - off;
                    break;
                }
                off += len;
            }

            if (ix0 < 0) {
                // start not found
                throw new Error("start=" + start + " len=" + length());
            }

            // find end segment
            int ix1 = -1;
            int off1 = -1;
            if (end >= 0) {
                for (; i < ct; i++) {
                    RSegment seg = get(i);
                    int len = seg.length();
                    if (end < (off + len)) {
                        ix1 = i;
                        off1 = end - off;
                        break;
                    }
                    off += len;
                }
            }

            if (ix0 == ix1) {
                // same segment
                RSegment seg = get(ix0);
                seg.removeRegion(off0, off1);
            } else {
                // spans multiple segments
                // first segment
                if (off0 > 0) {
                    RSegment seg = get(ix0);
                    seg.removeRegion(off0, -1);
                    ix0++;
                }
                // last segment
                if(ix1 < 0) {
                    ix1 = ct;
                } else {
                    RSegment seg = get(ix1);
                    seg.removeRegion(0, off1);
                }
                // remove in-between segments
                removeRange(ix0, ix1);
            }
        }
    }
}
