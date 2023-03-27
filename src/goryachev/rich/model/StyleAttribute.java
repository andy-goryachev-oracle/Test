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

/**
 * Style Attribute provides a way to specify style in the RichTextArea.
 */
public abstract class StyleAttribute {
    /**
     * Builds a direct style give this attribute.
     * This method must append a valid CSS style followed by a semicolon, for example:
     * {@code "-fx-font-weight:bold;"}
     * This method must silently ignore any errors or values of incorrect type.
     * 
     * @param sb StringBuilder to append the style to
     * @param value attribute value
     */
    public abstract void buildStyle(StringBuilder sb, Object value);

    private final String name;
    private final Class<?> type;

    public StyleAttribute(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Returns the class corresponding to the attribute value.
     * The value must be Serializable.
     */
    public final Class<?> getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        int h = StyleAttribute.class.hashCode();
        h = 31 * h + name.hashCode();
        h = 31 * h + type.hashCode();
        return h;
    }

    @Override
    public boolean equals(Object x) {
        if (x == this) {
            return true;
        } else if (x instanceof StyleAttribute a) {
            return (type == a.type) && (name.equals(a.name));
        }
        return false;
    }
}
