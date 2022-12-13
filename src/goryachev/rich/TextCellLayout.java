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

import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.layout.Region;
import javafx.scene.text.HitInfo;
import javafx.scene.text.TextFlow;

import goryachev.rich.impl.Markers;
import goryachev.rich.util.NewAPI;

/**
 * Represents text cells layed out in a visible area.
 */
public class TextCellLayout {
    private final VFlow flow;
    private final ArrayList<TextCell> cells = new ArrayList<>(32);
    private double width;
    private double height;
    private int topLineIndex;
    private double unwrappedWidth;
    
    public TextCellLayout(VFlow flow) {
        this.flow = flow;
    }

    public boolean isValid(VFlow f) {
        return
            (f.getWidth() == width) &&
            (f.getHeight() == height) &&
            (f.getTopLineIndex() == topLineIndex);
    }

    public void addBox(TextCell box) {
        cells.add(box);
    }
    
    public void setUnwrappedWidth(double w) {
        unwrappedWidth = w;
    }
    
    protected TextCell getLastCell() {
        int sz = cells.size();
        if(sz > 0) {
            return cells.get(sz - 1);
        }
        return null;
    }

    // TODO perhaps we need a new API in TextFlow
    public Marker getTextPosition(double screenX, double screenY, Markers markers) {
        for(TextCell cell: cells) {
            Region r = cell.getContent();
            Point2D p = r.screenToLocal(screenX, screenY);
            Insets pad = r.getPadding();
            double y = p.getY() - pad.getTop();
            if(y < 0) {
                return markers.newMarker(cell.getLineIndex(), 0, true);
            } else if(y < cell.getPreferredHeight()) {
                // TODO move this to TextCell?
                if(r instanceof TextFlow t) {
                    double x = p.getX() - pad.getLeft();
                    HitInfo h = t.hitTest(new Point2D(x, y));
                    if(h != null) {
                        return markers.newMarker(cell.getLineIndex(), h.getCharIndex(), h.isLeading());
                    }
                }
            }
        }
        
        TextCell cell = getLastCell();
        if(cell == null) {
            return Marker.ZERO;
        }
        
        Region r = cell.getContent();
        int ix = 0;
        if(r instanceof TextFlow f) {
            ix = Math.max(0, NewAPI.getText(f).length() - 1);
        }
        return markers.newMarker(cell.getLineIndex(), ix, false);
    }
}
