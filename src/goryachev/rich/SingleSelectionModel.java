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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * This SelectionModel supports a single selection segment.
 */
public class SingleSelectionModel implements SelectionModel {
    private Marker anchor;
    private Marker caret;
    private ReadOnlyObjectWrapper<TextPos> anchorPosition = new ReadOnlyObjectWrapper<>();
    private ReadOnlyObjectWrapper<TextPos> caretPosition = new ReadOnlyObjectWrapper<>();
    private final ChangeListener<TextPos> listener;

    public SingleSelectionModel() {
        this.listener = (src,old,val) -> {
            if(isAnchor(src)) {
                anchorPosition.set(val);
            } else {
                caretPosition.set(val);
            }
        };
    }
    
    @Override
    public void clear() {
        setSelection(null, null);
    }

    @Override
    public void setSelection(Marker an, Marker ca) {
        //System.err.println("setSelection a=" + an + " caret=" + ca); // FIX
        // the downside of having two properties instead of a single selection segment is that
        // a change in selection would trigger one or two events
        if(anchor != null) {
            anchor.textPosProperty().removeListener(listener);
        }
        anchor = an;
        if(anchor != null) {
            anchor.textPosProperty().addListener(listener);
            anchorPosition.set(anchor.getTextPos());
        } else {
            anchorPosition.set(null);
        }
        
        if(caret != null) {
            caret.textPosProperty().removeListener(listener);
        }
        caret = ca;
        if(caret != null) {
            caret.textPosProperty().addListener(listener);
            caretPosition.set(caret.getTextPos());
        } else {
            caretPosition.set(null);
        }
    }

    @Override
    public void extendSelection(Marker pos) {
        Marker a = anchor;
        if (a == null) {
            a = pos;
        }
        setSelection(a, pos);
    }

    @Override
    public ReadOnlyProperty<TextPos> anchorPositionProperty() {
        return anchorPosition.getReadOnlyProperty();
    }

    @Override
    public ReadOnlyProperty<TextPos> caretPositionProperty() {
        return caretPosition;
    }
    
    private boolean isAnchor(ObservableValue<? extends TextPos> src) {
        if(anchor != null) {
            return anchor.textPosProperty() == src;
        } else if(caret != null) {
            return caret.textPosProperty() != src;
        }
        return false;
    }
}
