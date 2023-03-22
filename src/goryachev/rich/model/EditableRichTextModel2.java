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
        // TODO
    }

    @Override
    protected void insertParagraph(int index, StyledSegment segment) {
        // TODO
    }

    @Override
    protected void removeRegion(TextPos start, TextPos end) {
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

        // for simplicity, this implementation does not coalesce segments which have the same style attributes
        public void insertText(int offset, String text, StyleAttrs attrs) {
            int off = 0;
            int ct = size();
            for (int i = 0; i < ct; i++) {
                if (offset == off) {
                    // insert at the beginning
                    add(i, new RSegment(text, attrs));
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
                        add(i++, new RSegment(text, attrs));
                        if (ix < toSplit.length()) {
                            String s2 = toSplit.substring(ix);
                            add(i++, new RSegment(s2, a));
                        }
                        return;
                    }

                    off += len;
                }
            }

            // insert at the end
            add(new RSegment(text, attrs));
        }
    }

    /**
     * Model rich text segment.
     */
    protected static record RSegment(String text, StyleAttrs attrs) {
        public int length() {
            return text.length();
        }
    }
}
