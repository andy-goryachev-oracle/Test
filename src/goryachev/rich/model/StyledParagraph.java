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
package goryachev.rich.model;

import goryachev.rich.TextCell;

/**
 * Represents a single styled text paragraph, a light weight item in a model.
 */
public abstract class StyledParagraph {
    /**
     * Returns plain text for the given paragraph.
     * This text should be the same as the result of the 'copy'
     * operation performed on the paragraph, and must not contain any line separators.
     * This method might return null if no text is associated with the paragraph.
     */
    public abstract String getText();

    /**
     * Creates Nodes which provide visual representation of the paragraph.
     * This method must create new Nodes each time, in order to support multiple RichTextArea instances
     * connected to the same model.
     * The nodes are not reused, and might be created repeatedly,
     * so the model must not keep strong references to these nodes.
     */
    public abstract TextCell createTextCell();

    private final int index;

    public StyledParagraph(int index) {
        this.index = index;
    }

    /**
     * Returns the model paragraph index.
     */
    public final int getIndex() {
        return index;
    }
}
