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

import java.util.function.Supplier;
import javafx.scene.Node;

/**
 * Data structure used to modify the styled text model.
 * 
 * Represents:
 * 1. a single text segment with direct style and/or style names
 * 2. an inline Node
 * 3. a paragraph containing a single Node
 *
 * TODO
 * The main issue with StyledText (or, specifically, its styles) is that a lookup is needed to
 * convert styles to text attributes for the purposes of export.  Similarly, it might be difficult to
 * import external styles and attributes into those supported by the model.
 * 
 * UNLESS, specific attributes are used (colors, fonts, font attributes, paragraph attributes), and a mapping
 * is provided between StyledText attributes and the (default) RichTextArea CSS.
 * This might require a StyleSheet + supported subset of -fx- properties.
 * 
 * TODO in addition to is*(), add getType() returning an enum { TEXT, PARAGRAPH, INLINE_NODE, LINE_BREAK }
 * TODO perhaps add guarded/unguarded factory methods (of(), ofGuarded()) that check for <0x20, or specify that
 * text must not include those characters.
 */
public abstract class StyledSegment {
    /**
     * Returns true if this segment is a text segment.
     */
    public boolean isText() { return false; }
    
    /**
     * Returns true if this segment is a paragraph which contains a single Node.
     */
    public boolean isParagraph() { return false; }
    
    /**
     * Returns true if this segment is a line break.
     */
    public boolean isLineBreak() { return false; }
    
    /**
     * Returns the text associated with this segment.
     * Must be one character for inline nodes, must be null for node paragraphs.
     */
    public String getText() { return null; }

    /**
     * Specifies a direct style string that will be set on a node that represents this segment.
     * The style can be null.
     */
    public String getDirectStyle() { return null; }
    
    /**
     * Specifies the CSS style names that will be set on a node that represents this segment.
     * The array can be null.
     */
    public String[] getStyles() { return null; }
    
    /**
     * This method must return a non-null instance when {@link isText()} is false, or null 
     * when {@link isText()} is true.
     */
    public Supplier<Node> getNodeGenerator() { return null; }

    /**
     * This method must return StyleAttrs (or null) for this segment.
     * Keep in mind that the actual attributes and values might depend on the view that generated the segment,
     * unless the model itself maintains attributes independently of the view.
     */
    public StyleAttrs getStyleAttrs() { return null; }
    

    /** A styled segment that represents a line break */
    public static final StyledSegment LINE_BREAK = new StyledSegment() {
        @Override
        public boolean isLineBreak() {
            return true;
        }

        @Override
        public String toString() {
            return "LINE_BREAK";
        }
    };

    /** 
     * Creates a StyleSegment from a non-null text and non-null attributes.
     * Important: text must not contain any characters < 0x20, except for TAB.
     */
    // TODO attrs must be R/O
    public static StyledSegment of(String text, StyleAttrs attrs) {
        return new StyledSegment() {
            private String style; 

            @Override
            public boolean isText() {
                return true;
            }
            
            @Override
            public String getText() {
                return text;
            }
            
            @Override
            public String getDirectStyle() {
                if(style == null) {
                    style = attrs.getStyle();
                }
                return style;
            }

            @Override
            public StyleAttrs getStyleAttrs() {
                return attrs;
            }
            
            @Override
            public String toString() {
                return "StyledSegment{text=" + getText() + ", attrs=" + attrs + "}";
            }
        };
    }
    
    /** 
     * Creates a StyleSegment from a non-null plain text.
     * Important: text must not contain any characters < 0x20, except for TAB.
     */
    public static StyledSegment of(String text) {
        return new StyledSegment() {
            private String style; 

            @Override
            public boolean isText() {
                return true;
            }
            
            @Override
            public String getText() {
                return text;
            }
            
            @Override
            public String toString() {
                return "StyledSegment{text=" + getText() + "}";
            }
        };
    }
    
    /** 
     * Creates a StyleSegment from a non-null text with direct and stylesheet styles.
     * Important: text must not contain any characters < 0x20, except for TAB.
     */
    public static StyledSegment of(String text, String direct, String[] css) {
        return new StyledSegment() {
            @Override
            public boolean isText() {
                return true;
            }

            @Override
            public String getText() {
                return text;
            }

            @Override
            public String getDirectStyle() {
                return direct;
            }

            @Override
            public String[] getStyles() {
                return css;
            }

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder(32);
                sb.append("StyledSegment{text=").append(text);
                sb.append(", direct=").append(direct);
                if (css != null) {
                    sb.append(", css=[");
                    boolean sep = false;
                    for (String s : css) {
                        if (sep) {
                            sb.append(',');
                        } else {
                            sep = true;
                        }
                        sb.append(s);
                    }
                    sb.append("]");
                }
                sb.append("}");
                return sb.toString();
            }
        };
    }
}
