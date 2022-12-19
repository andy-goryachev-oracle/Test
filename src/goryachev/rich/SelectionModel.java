/*
 * Copyright (c) 2022, Oracle and/or its affiliates. All rights reserved.
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
// this code borrows heavily from the following project, with permission from the author:
// https://github.com/andy-goryachev/FxEditor
package goryachev.rich;

import javafx.beans.property.ReadOnlyProperty;

public interface SelectionModel {
    /**
     * Sets anchor.
     */
    public void setAnchor(Marker anchor);

    /**
     * clears the selection
     */
    public void clear();

    /**
     * clears existing selection, if any, and sets a new selection
     */
    public void setSelection(Marker anchor, Marker caret);

    /**
     * clears existing selection if any,
     * then selects from the anchor point to the current position.
     */
    public void clearAndExtendLastSegment(Marker pos);
    
    /**
     * Selection segment property.  The value can be null.
     */
    public ReadOnlyProperty<SelectionSegment> selectionSegmentProperty();

    /**
     * returns current selection segment, or null.
     */
    public SelectionSegment getSelectionSegment();
}
