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
package goryachev.rich;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.AccessibleRole;
import javafx.scene.control.Control;

public class RichTextArea extends Control {
    private ObjectProperty<StyledTextModel> model;
    private final ReadOnlyIntegerWrapper currentLine = new ReadOnlyIntegerWrapper(-1);

    public RichTextArea() {
        getStyleClass().add("rich-text-area");
        getStyleClass().add("text-input");
        setAccessibleRole(AccessibleRole.TEXT_AREA);
        setSkin(new RichTextAreaSkin(this));
    }

    public void setModel(StyledTextModel m) {
        model.set(m);
    }

    public StyledTextModel getModel() {
        return model.get();
    }

    public ObjectProperty<StyledTextModel> modelProperty() {
        if (model == null) {
            model = new SimpleObjectProperty<>(this, "model") {
                @Override
                protected void invalidated() {
                    updateModel();
                }
            };
        }
        return model;
    }
    
    public int getCurrentLine() {
        return currentLine.get();
    }
    
    public void moveCurrentLine(int n) {
        // TODO clip
    }
    
    public ReadOnlyIntegerProperty currentLineProperty() {
        return currentLine.getReadOnlyProperty();
    }
    
    protected void updateModel() {
        // TODO
    }
}
