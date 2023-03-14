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
import javafx.scene.paint.Color;
import goryachev.rich.util.NewAPI;

/**
 * Map of style attributes.
 */
public class StyleAttrs {
    public static enum Attr {
        BOLD(Boolean.class),
        FONT_FAMILY(String.class),
        FONT_SIZE(Double.class),
        ITALIC(Boolean.class),
        STRIKE_THROUGH(Boolean.class),
        TEXT_COLOR(Color.class),
        UNDERLINE(Boolean.class),
        ;
        public final Class<?> type;
        Attr(Class<?> type) { this.type = type; }
    }
    
    private final HashMap<Attr,Object> attributes = new HashMap<>();
    private String style;
    
    public StyleAttrs() {
    }
    
    public void set(Attr a, boolean value) {
        set(a, value ? Boolean.TRUE : null);
    }

    public void set(Attr a, Object value) {
        if (value == null) {
            attributes.remove(a);
        } else if (value.getClass().isAssignableFrom(a.type)) {
            attributes.put(a, value);
        } else {
            throw new IllegalArgumentException(a + " requires value of type " + a.type);
        }
        style = null;
    }

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
        for(Attr a: attributes.keySet()) {
            Object v = attributes.get(a);
            switch(a) {
            case BOLD:
                sb.append("-fx-font-weight:bold; ");
                break;
            case FONT_FAMILY:
                sb.append("-fx-font-family:").append(v).append("; ");
                break;
            case FONT_SIZE:
                int n = (int)Math.round(100.0 * (Double)v);
                sb.append("-fx-font-size:").append(n).append("%; ");
                break;
            case ITALIC:
                sb.append("-fx-font-style:italic; ");
                break;
            case STRIKE_THROUGH:
                sb.append("-fx-strikethrough:true; ");
                break;
            case TEXT_COLOR:
                String color = NewAPI.toColorString((Color)v);
                sb.append("-fx-fill:").append(color).append("; ");
                break;
            case UNDERLINE:
                sb.append("-fx-underline:true; ");
                break;
            default:
                // silently ignore
            }
        }
        return sb.toString();
    }
}
