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
// This code borrows heavily from the following project, with permission from the author:
// https://github.com/andy-goryachev/FxEditor
package goryachev.apps.rich;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javafx.scene.Node;
import goryachev.rich.TextCell;
import goryachev.rich.model.StyleInfo;
import goryachev.rich.model.StyledOutput;
import goryachev.rich.model.StyledParagraph;
import goryachev.rich.model.StyledSegment;

public class SegmentStyledTextParagraph extends StyledParagraph {
    private ArrayList<Segment> segments;

    public SegmentStyledTextParagraph(int index) {
        super(index);
    }
    
    @Override
    public TextCell createTextCell() {
        int ix = getIndex();
        TextCell b = new TextCell(ix);
        if(segments == null) {
            // avoid zero height
            b.addSegment("");
        } else {
            for(Segment s: segments) {
                // TODO Segment.createNode()
                if(s instanceof TextSegment t) {
                    b.addSegment(t.text, t.style, t.css);
                } else if(s instanceof NodeSegment n) {
                    b.addInlineNode(n.generator.get());
                }
            }
        }
        return b;
    }
    
    public void export(int start, int end, StyledOutput out) throws IOException {
        if(segments == null) {
            out.append(StyledSegment.of(""));
        } else {
            int off = 0;
            int ct = size();
            for (int i = 0; i < ct; i++) {
                if (off >= end) {
                    return;
                }

                Segment seg = segments.get(i);
                String text = seg.getText();
                int len = (text == null ? 0: text.length());
                if (start <= off) {
                    int ix0 = Math.max(0, start - off);
                    int ix1 = Math.min(len, end - off);
                    StyledSegment ss = seg.createStyledSegment(ix0, ix1);
                    out.append(ss);
                }
                off += len;
            }
        }
    }

    @Override
    public String getText() {
        if (segments == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder(64);
        for (Segment s : segments) {
            sb.append(s.getText());
        }
        return sb.toString();
    }
    
    protected List<Segment> segments() {
        if(segments == null) {
            segments = new ArrayList<>();
        }
        return segments;
    }
    
    protected int size() {
        return segments == null ? 0 : segments.size();
    }

    public void addSegment(String text, String style, String[] css) {
        // TODO check for newlines/formfeed chars
        segments().add(new TextSegment(text, style, css));
    }
    
    public void addSegment(Supplier<Node> generator) {
        segments().add(new NodeSegment(generator));
    }

    /** base class */
    public static abstract class Segment {
        public abstract String getText();

        protected abstract StyledSegment createStyledSegment(int start, int end);
    }
    
    /** text segment */
    public static class TextSegment extends Segment {
        public final String text;
        public final String style;
        public final String[] css;

        public TextSegment(String text, String style, String[] css) {
            this.text = text;
            this.style = style;
            this.css = css;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        protected StyledSegment createStyledSegment(int start, int end) {
            int len = text.length();
            String s;
            if ((start <= 0) && (end >= len)) {
                s = text;
            } else {
                s = text.substring(Math.max(0, start), Math.min(end, len));
            }

            return StyledSegment.of(s, style, css);
        }
    }
    
    /** inline node segment */
    public static class NodeSegment extends Segment {
        public final Supplier<Node> generator;
        
        public NodeSegment(Supplier<Node> generator) {
            this.generator = generator;
        }
        
        @Override
        public String getText() {
            // must be one character
            return " ";
        }

        @Override
        protected StyledSegment createStyledSegment(int start, int end) {
            return StyledSegment.inlineNode(generator);
        }
    }
}
