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
 */
public interface StyledSegment {
    /**
     * Returns true if this segment is a text segment.
     */
    public boolean isText();
    
    /**
     * Returns true if this segment is a paragraph which contains a single Node.
     */
    public boolean isParagraph();
    
    /**
     * Returns true if this segment is a line break.
     */
    public boolean isLineBreak();
    
    /**
     * Returns the text associated with this segment.
     * Must be one character for inline nodes, must be null for node paragraphs.
     */
    public String getText();

    /**
     * Specifies a direct style string that will be set on a node that represents this segment.
     * The style can be null.
     */
    public String getDirectStyle();
    
    /**
     * Specifies the CSS style names that will be set on a node that represents this segment.
     * The array can be null.
     */
    public String[] getStyles();
    
    /**
     * This method must return a non-null instance when {@link isText()} is false, or null 
     * when {@link isText()} is true.
     */
    public Supplier<Node> getNodeGenerator();

    /**
     * This method must return StyleAttrs (or null) for this segment.
     * Keep in mind that the actual attributes and values might depend on the view that generated the segment,
     * unless the model itself maintains attributes independently of the view.
     */
    public StyleAttrs getStyleAttrs();
    

    /** A styled segment that represents a line break */
    public static final StyledSegment LINE_BREAK = new StringStyledSegment(null, null, null) {
        @Override
        public boolean isLineBreak() {
            return true;
        }

        @Override
        public boolean isText() {
            return false;
        }
    };

    /** 
     * Creates a StyleSegment from a non-null text and non-null attributes.
     * Important: text must not contain any characters < 0x20, except for TAB.
     */
    public static StyledSegment of(String text, StyleAttrs attrs) {
        String style = attrs.getStyle();

        return new StyledSegment() {
            @Override
            public boolean isText() {
                return true;
            }
            
            @Override
            public boolean isParagraph() {
                return false;
            }
            
            @Override
            public boolean isLineBreak() {
                return false;
            }
            
            @Override
            public String getText() {
                return text;
            }
            
            @Override
            public String[] getStyles() {
                return null;
            }
            
            @Override
            public Supplier<Node> getNodeGenerator() {
                return null;
            }
            
            @Override
            public String getDirectStyle() {
                return style;
            }

            @Override
            public StyleAttrs getStyleAttrs() {
                return attrs;
            }
        };
    }
}
