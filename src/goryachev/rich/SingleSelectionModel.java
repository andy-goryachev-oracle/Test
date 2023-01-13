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
package goryachev.rich;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;

/**
 * This SelectionModel support a single selection segment.
 * 
 * TODO selectedItemProperty
 * TODO selectedIndexProperty
 * TODO selectedItemsProperty
 * TODO selectedIndexesProperty
 * 
 * TODO maybe separate selection controller and selection model.
 * anchor belongs to a controller, transient selection that gets updated during dragging operation 
 * is a part of the controller, and once the drag is finished, the selection model is updated with the resulting
 * selection segment.
 */
public class SingleSelectionModel implements SelectionModel {
    private final ReadOnlyObjectWrapper<SelectionSegment> selection = new ReadOnlyObjectWrapper<>();
    private Marker anchor;

    public SingleSelectionModel() {
    }
    
    public void setAnchor(Marker anchor) {
        this.anchor = anchor;
    }

    @Override
    public void clear() {
       selection.set(null);
    }

    @Override
    public void setSelection(Marker anchor, Marker caret) {
        SelectionSegment seg = new SelectionSegment(anchor, caret);
        selection.set(seg);
    }

    @Override
    public void clearAndExtendLastSegment(Marker pos) {
        if (anchor == null) {
            anchor = pos;
        }
        setSelection(anchor, pos);
    }

    @Override
    public ReadOnlyProperty<SelectionSegment> selectionSegmentProperty() {
        return selection.getReadOnlyProperty();
    }
    
    @Override
    public SelectionSegment getSelectionSegment() {
        return selection.get();
    }
}
