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

import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 * Handles mouse events.
 */
public class MouseHandler {
    private final RichTextArea control;
    
    public MouseHandler(RichTextArea control) {
        this.control = control;
    }
    
    public void register(RichTextArea control, VFlow f) {
        f.addEventFilter(MouseEvent.MOUSE_CLICKED, this::handleMouseClicked);
        f.addEventFilter(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);
        f.addEventFilter(MouseEvent.MOUSE_RELEASED, this::handleMouseReleased);
        f.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::handleMouseDragged);
        f.addEventFilter(ScrollEvent.ANY, this::handleScrollEvent);
    }
    
    protected void handleMouseClicked(MouseEvent ev) {
        // TODO
    }
    
    protected void handleMousePressed(MouseEvent ev) {
        // TODO
        if(ev.isPopupTrigger()) {
            // TODO something about clearing the selection if hapepened outside of said selection?
            return;
        }
        
        // TODO Marker pos = getTextPosition(ev);
        // TODO control.setSuppressBlink(true);
        
        if(ev.isShiftDown()) {
            // TODO clear selection, add selection from the anchor point to the current position
        } else {
            // TODO clear, setAnchor(pos), setSelection(pos)
        }
        
        control.requestFocus();
    }
    
    protected void handleMouseReleased(MouseEvent ev) {
        // TODO
    }
    
    protected void handleMouseDragged(MouseEvent ev) {
        // TODO
    }
    
    protected void handleScrollEvent(ScrollEvent ev) {
        // TODO
    }
}
