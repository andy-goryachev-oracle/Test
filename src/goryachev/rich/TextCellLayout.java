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

import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.shape.PathElement;
import javafx.scene.text.HitInfo;
import javafx.scene.text.TextFlow;
import goryachev.rich.impl.Markers;
import goryachev.rich.util.NewAPI;
import goryachev.rich.util.Util;

/**
 * Manages TextCells in the visible area, surrounded by a number of cells before and after the visible area,
 * for the purposes of layout, estimating the average paragraph height, and relative navigation.
 */
public class TextCellLayout {
    private final ArrayList<TextCell> cells = new ArrayList<>(32);
    private final double flowWidth;
    private final double flowHeight;
    private final int lineCount;
    private final Origin origin;
    private int visible;
    private int bottomCount;
    private double unwrappedWidth;
    private double topHeight;
    private double bottomHeight;
    
    public TextCellLayout(VFlow f) {
        this.flowWidth = f.getWidth();
        this.flowHeight = f.getHeight();
        this.origin = f.getOrigin();
        this.lineCount = f.lineCount();
    }

    public String toString() {
        return
            "TextCellLayout{" +
            "unwrapped=" + getUnwrappedWidth() +
            ", topCount=" + topCount() +
            ", visible=" + getVisibleCellCount() +
            ", bottomCount=" + bottomCount +
            ", lineCount=" + lineCount +
            ", topHeight=" + topHeight +
            ", bottomHeight=" + bottomHeight +
            ", average=" + averageHeight() +
            ", estMax=" + estimatedMax() +
            ", origin=" + origin +
            "}";
    }

    public boolean isValid(VFlow f) {
        return
            (f.getWidth() == flowWidth) &&
            (f.getHeight() == flowHeight) &&
            (f.topCellIndex() == origin.index());
    }

    public void addCell(TextCell box) {
        cells.add(box);
    }
    
    public void setUnwrappedWidth(double w) {
        unwrappedWidth = w;
    }
    
    public double getUnwrappedWidth() {
        // TODO add line number section width + any other gutters widths
        return unwrappedWidth;
    }

    public int getVisibleCellCount() {
        return visible;
    }
    
    public void setVisibleCount(int n) {
        visible = n;
    }
    
    protected TextCell lastCell() {
        int sz = cells.size();
        if(sz > 0) {
            return cells.get(sz - 1);
        }
        return null;
    }

    public Marker getTextPosition(double screenX, double screenY, Markers markers) {
        for(TextCell cell: cells) {
            Region r = cell.getContent();
            Point2D p = r.screenToLocal(screenX, screenY);
            if (p == null) {
                return markers.newMarker(cell.getLineIndex(), cell.getTextLength(), false);
            }
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
                } else {
                    return markers.newMarker(cell.getLineIndex(), 0, true);
                }
            }
        }

        TextCell cell = lastCell();
        if (cell == null) {
            return Marker.ZERO;
        }

        Region r = cell.getContent();
        int ix = 0;
        if (r instanceof TextFlow f) {
            ix = Math.max(0, NewAPI.getTextLength(f) - 1);
        }
        return markers.newMarker(cell.getLineIndex(), ix, false);
    }

    /** returns the cell contained in this layout, or null */
    public TextCell getCell(int modelIndex) {
        int ix = modelIndex - origin.index();
        if(ix < 0) {
            if((ix + topCount()) > 0) {
                // cells in the top part come after bottom part, and in reverse order
                return cells.get(bottomCount + topCount() + ix);
            }
        } else if(ix < bottomCount) {
            // cells in the normal (bottom) part
            return cells.get(ix);
        }
        return null;
    }
    
    /** returns a visible cell, or null */
    public TextCell getVisibleCell(int modelIndex) {
        int ix = modelIndex - origin.index();
        if((ix >= 0) && (ix < visible)) {
            return cells.get(ix);
        }
        return null;
    }
    
    /** returns a TextCell from the visible or bottom margin parts, or null */
    public TextCell getCellAt(int ix) {
        if(ix < bottomCount) {
            return cells.get(ix);
        }
        return null;
    }

    public CaretSize getCaretSize(Region parent, Marker m) {
        if (m != null) {
            int ix = m.getLineIndex();
            TextCell cell = getCell(ix);
            if (cell != null) {
                int charIndex = m.getCharIndex();
                boolean leading = m.isLeading();
                PathElement[] p = cell.getCaretShape(charIndex, leading);
                return Util.translateCaretSize(parent, cell.getContent(), p);
            }
        }
        return null;
    }

    public void removeNodesFrom(VFlow f) {
        ObservableList<Node> cs = f.getChildren();
        for (int i = getVisibleCellCount() - 1; i >= 0; --i) {
            TextCell cell = cells.get(i);
            cs.remove(cell.getContent());
        }
    }

    public void setBottomCount(int ix) {
        bottomCount = ix;
    }

    public int bottomCount() {
        return bottomCount;
    }

    public void setBottomHeight(double h) {
        bottomHeight = h;
    }

    public double bottomHeight() {
        return bottomHeight;
    }

    public int topCount() {
        return cells.size() - bottomCount;
    }

    public void setTopHeight(double h) {
        topHeight = h;
    }

    public double topHeight() {
        return topHeight;
    }

    public double averageHeight() {
        return (topHeight + bottomHeight) / (topCount() + bottomCount);
    }

    public double estimatedMax() {
        return (lineCount - topCount() - bottomCount) * averageHeight() + topHeight + bottomHeight;
    }

    private int binarySearch(double pos, int high, int low) {
        while (low <= high) {
            // TODO might be a problem for 2B-rows models
            int mid = (low + high) >>> 1;
            TextCell c = getCell(mid);
            if(c == null) {
                c = getCell(mid);
                System.err.println("ERR");
            }
            int cmp = compare(c, pos);
            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        // should not happen
        System.err.println("UNEXPECTED binarySearchTop");
        return low;
    }
    
    private static int compare(TextCell c, double offset) {
        double off = c.getOffset();
        if(offset < off) {
            return 1;
        } else if(off >= off + c.getPreferredHeight()) {
            return -1;
        }
        return 0;
    }

    /** creates a new Origin from the absolute position [0.0 ... (1.0-normalized.visible.amount)] */
    // TODO handle hit-the-rail conditions
    public Origin fromAbsolutePosition(double pos) {
        Origin p = fromAbsolutePositionPixels(pos);
        System.err.println("fromAbsolutePosition(pos=" + pos + ") -> " + p); 
        return p;
    }
    public Origin fromAbsolutePositionIndexes(double pos) { // FIX
        int low = origin.index() - topCount();
        int high = origin.index() + bottomCount;
        double top = low / (double)lineCount;
        double btm = high / (double)lineCount;
        int ix = (int)(pos * lineCount);
        return new Origin(ix, 0.0);
    }
    public Origin fromAbsolutePositionPixelsSimple(double pos) { // FIX
        double av = averageHeight();
        double max = estimatedMax();
        double estPos = pos * max;
        int ix = (int)(pos * lineCount);
        double offset = (max * pos) - (ix * av);
        return new Origin(ix, offset);
    }
    public Origin fromAbsolutePositionPixels(double pos) { // FIX
        int topIx = origin.index() - topCount();
        int btmIx = origin.index() + bottomCount;
        int ix = (int)(pos * lineCount);
        if ((ix >= topIx) && (ix < btmIx)) {
            // inside the layout
            double top = topIx / (double)lineCount;
            double btm = btmIx / (double)lineCount;
            
            double f = (pos - top) / (btm - top); // TODO watch for div0
            double off = f * (topHeight + bottomHeight);
            
            ix = binarySearch(off, btmIx - 1, topIx);
            TextCell c = getCell(ix);
            // TODO if top edge is at 0, the offset == estPos == pos.
            System.err.println("found off=" + off + ", cell{index=" + c.getLineIndex() + ", offset=" + c.getOffset() + "}}");
            return new Origin(c.getLineIndex(), off - c.getOffset());
        }
        return new Origin(ix, 0.0);
    }
}
