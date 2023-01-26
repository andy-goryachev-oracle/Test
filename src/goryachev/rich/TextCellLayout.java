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
            (f.getTopLineIndex() == origin.index());
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

    public TextCell getCell(int modelIndex) {
        int ix = modelIndex - origin.index();
        if ((ix >= 0) && (ix < cells.size())) {
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

    /** creates a new Origin from the absolute position in pixels */
    public Origin fromAbsolutePixels(double pos) {
        Origin p = fromAbsolutePixels2(pos);
        System.err.println("fromAbsolutePixels(pos=" + pos + ") -> " + p); 
        return p;
    }
    public Origin fromAbsolutePixels2(double pos) { // FIX
        double av = averageHeight();
        double top = origin.estPos();
        if (!Double.isNaN(top)) {
            if (pos >= top) {
                double p = pos - top;
                if (p < topHeight) {
                    // TODO binary search in top cells
                    return find(pos, p, true);
                }

                p -= topHeight;
                if (p < bottomHeight) {
                    // binary search in bottom cells
                    return find(pos, p, false);
                }
            }
        }

        // outside of the layout
        int ix = (int)Math.round(pos / av);
        return new Origin(ix, 0.0, pos);
    }

    private Origin find(double pos, double off, boolean top) {
        int low;
        int high;
        if (top) {
            low = bottomCount;
            high = cells.size() - 1;
        } else {
            low = 0;
            high = bottomCount - 1;
        }
        
        int ix = binarySearch(off, top, high, low);
        TextCell c = cells.get(ix);
        // TODO if top edge is at 0, the offset == estPos == pos.
        return new Origin(c.getLineIndex(), off - c.getOffset(), pos);
    }
    
    private int binarySearch(double pos, boolean top, int high, int low) {
        while (low <= high) {
            int mid = (low + high) >>> 1;
            TextCell c = cells.get(mid);
            int cmp = compare(c, pos);
            if (cmp < 0) {
                low = mid + (top ? -1 : 1);
            } else if (cmp > 0) {
                high = mid + (top ? 1 : - 1);
            } else {
                return mid;
            }
        }
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
}
