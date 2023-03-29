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

import java.util.HashMap;
import java.util.Set;
import javafx.scene.paint.Color;
import goryachev.rich.util.Util;

/**
 * Map of style attributes.
 */
// TODO it is probably a good idea to make this class immutable
public class StyleAttrs {
    public static final StyleAttribute BOLD = new StyleAttribute("BOLD", Boolean.class) {
        @Override
        public void buildStyle(StringBuilder sb, Object value) {
            if (Boolean.TRUE.equals(value)) {
                sb.append("-fx-font-weight:bold; ");
            }
        }
    };
    
    public static final StyleAttribute FONT_FAMILY = new StyleAttribute("FONT_FAMILY", String.class) {
        @Override
        public void buildStyle(StringBuilder sb, Object value) {
            sb.append("-fx-font-family:").append(value).append("; ");
        }
    };
    
    /** Font size, in percent, relative to the base font size. */
    public static final StyleAttribute FONT_SIZE = new StyleAttribute("FONT_SIZE", Integer.class) {
        @Override
        public void buildStyle(StringBuilder sb, Object value) {
            int n = (Integer)value;
            sb.append("-fx-font-size:").append(n).append("%; ");
        }
    };
    
    public static final StyleAttribute ITALIC = new StyleAttribute("ITALIC", Boolean.class) {
        @Override
        public void buildStyle(StringBuilder sb, Object value) {
            if (Boolean.TRUE.equals(value)) {
                sb.append("-fx-font-style:italic; ");
            }
        }
    };
    
    public static final StyleAttribute STRIKE_THROUGH = new StyleAttribute("STRIKE_THROUGH", Boolean.class) {
        @Override
        public void buildStyle(StringBuilder sb, Object value) {
            if (Boolean.TRUE.equals(value)) {
                sb.append("-fx-strikethrough:true; ");
            }
        }
    };
    
    public static final StyleAttribute TEXT_COLOR = new StyleAttribute("TEXT_COLOR", Color.class) {
        @Override
        public void buildStyle(StringBuilder sb, Object value) {
            if (value != null) {
                String color = Util.toCssColor((Color)value);
                sb.append("-fx-fill:").append(color).append("; ");
            }
        }
    };
    
    public static final StyleAttribute UNDERLINE = new StyleAttribute("UNDERLINE", Boolean.class) {
        @Override
        public void buildStyle(StringBuilder sb, Object value) {
            if (Boolean.TRUE.equals(value)) {
                sb.append("-fx-underline:true; ");
            }
        }
    };
    
    private final HashMap<StyleAttribute,Object> attributes;
    private transient String style;
    
    public StyleAttrs() {
        this.attributes = new HashMap<>();
    }
    
    public StyleAttrs(StyleAttrs a) {
        this.attributes = new HashMap<>(a.attributes);
    }

    public boolean equals(Object x) {
        if (x == this) {
            return true;
        } else if (x instanceof StyleAttrs s) {
            return attributes.equals(s.attributes);
        } else {
            return false;
        }
    }
    
    public int hashCode() {
        return attributes.hashCode() + (31 * StyleAttrs.class.hashCode());
    }

    public void set(StyleAttribute a, boolean value) {
        set(a, Boolean.valueOf(value));
    }

    public void set(StyleAttribute a, Object value) {
        if (value == null) {
            attributes.put(a, null);
        } else if (value.getClass().isAssignableFrom(a.getType())) {
            attributes.put(a, value);
        } else {
            throw new IllegalArgumentException(a + " requires value of type " + a.getType());
        }
        style = null;
    }
    
    public Object get(StyleAttribute a) {
        return attributes.get(a);
    }
    
    public Set<StyleAttribute> attributeSet() {
        return attributes.keySet();
    }

    /** returns a direct style string or null */
    public String getStyle() {
        if (style == null) {
            style = createStyleString();
        }
        return style;
    }

    private String createStyleString() {
        if (attributes.size() == 0) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder(32);
        for(StyleAttribute a: attributes.keySet()) {
            Object v = attributes.get(a);
            a.buildStyle(sb, v);
        }
        return sb.toString();
    }

    /** 
     * Creates a new StyleAttrs instance by combining the two attribute sets.
     * The new attributes override any existing ones.
     * This instance remains unchanged.
     */
    public StyleAttrs combine(StyleAttrs attrs) {
        StyleAttrs rv = new StyleAttrs(this);
        for(StyleAttribute a: attrs.attributeSet()) {
            Object v = attrs.get(a);
            rv.set(a, v);
        }
        return rv;
    }
    
    /** 
     * Applies the specified attributes to this instance.
     * The new attributes override any existing ones.
     */
    public void apply(StyleAttrs attrs) {
        for(StyleAttribute a: attrs.attributeSet()) {
            Object v = attrs.get(a);
            set(a, v);
        }
    }
    
    public StyleAttrs copy() {
        return new StyleAttrs(this);
    }

    public boolean getBoolean(StyleAttribute a) {
        Object v = attributes.get(a);
        return Boolean.TRUE.equals(v);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(32);
        sb.append("[");
        boolean sep = false;
        for (StyleAttribute a : attributeSet()) {
            if (sep) {
                sb.append(",");
            } else {
                sep = true;
            }
            Object v = get(a);
            sb.append(a);
            sb.append('=');
            sb.append(v);
        }
        sb.append("]");
        return sb.toString();
    }
    
    public Color getTextColor() {
        return (Color)get(TEXT_COLOR);
    }

    public boolean isBold() {
        return getBoolean(BOLD);
    }

    public boolean isItalic() {
        return getBoolean(ITALIC);
    }

    public boolean isUnderline() {
        return getBoolean(UNDERLINE);
    }

    public boolean isStrikeThrough() {
        return getBoolean(STRIKE_THROUGH);
    }
    
    public Integer getFontSize() {
        return (Integer)get(FONT_SIZE);
    }
    
    public String getFontFamily() {
        return (String)get(FONT_FAMILY);
    }
}
