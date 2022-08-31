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
package goryachev.research;

import java.util.List;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javafx.scene.control.ConstrainedColumnResize;
import javafx.scene.control.ResizeFeaturesBase;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;
import javafx.scene.control.ConstrainedColumnResize.TablePolicy;
import javafx.util.Callback;

/**
 * Constrained column resize policy lifted from JTable.
 */
public class JTableResize extends ConstrainedColumnResize {
    
    public enum ResizeMode {
        AUTO_RESIZE_NEXT_COLUMN,
        AUTO_RESIZE_SUBSEQUENT_COLUMNS,
        AUTO_RESIZE_LAST_COLUMN,
        AUTO_RESIZE_ALL_COLUMNS
    }
    
    public static class ForTable
        extends JTableResize
        implements Callback<TableView.ResizeFeatures,Boolean> {
        
        public ForTable(ResizeMode m) {
            super(m);
        }
    
        @Override
        public Boolean call(TableView.ResizeFeatures f) {
            List<? extends TableColumnBase<?,?>> visibleLeafColumns = f.getTable().getVisibleLeafColumns();
            return constrainedResize(f, f.getContentWidth(), visibleLeafColumns);
        }
        
        @Override
        public String toString() {
            return "new-constrained-resize";
        }
    }
    
    public static ForTable forTable(ResizeMode m) {
        return new ForTable(m);
    }
    
    private final ResizeMode autoResizeMode;
    
    public JTableResize(ResizeMode m) {
        autoResizeMode = m;
    }
    
    @Override
    public boolean constrainedResize(ResizeFeaturesBase rf,
        double contentWidth,
        List<? extends TableColumnBase<?,?>> visibleLeafColumns) {
        
        double tableWidth = rf.getContentWidth();
        if (tableWidth == 0.0) { 
            return false;
        }
        
        double totalColumnsWidth = getTotalColumnWidth(visibleLeafColumns);
        
        if (Math.abs(totalColumnsWidth - tableWidth) > EPSILON) {
            setWidthsFromPreferredWidths(rf, visibleLeafColumns, false);
        }
        
        TableColumnBase<?,?> resizingColumn = rf.getColumn();
        if (resizingColumn == null) {
            return false;
        }
        
        // A column has been resized and JTable may need to distribute
        // any overall delta to other columns, according to the resize mode.

        // need to find the last leaf column of the given column - it is this
        // column that we actually resize from. If this column is a leaf, then we
        // use it.
        while (resizingColumn.getColumns().size() > 0) {
            resizingColumn = resizingColumn.getColumns().get(resizingColumn.getColumns().size() - 1);
        }
        
        int columnIndex = visibleLeafColumns.indexOf(resizingColumn);
        double delta = tableWidth - totalColumnsWidth; // FIX computing twice
        accommodateDelta(rf, visibleLeafColumns, columnIndex, delta);
        delta = rf.getContentWidth() - getTotalColumnWidth(visibleLeafColumns);

        // If the delta cannot be completely accomodated, then the
        // resizing column will have to take any remainder. This means
        // that the column is not being allowed to take the requested
        // width. This happens under many circumstances: For example,
        // AUTO_RESIZE_NEXT_COLUMN specifies that any delta be distributed
        // to the column after the resizing column. If one were to attempt
        // to resize the last column of the table, there would be no
        // columns after it, and hence nowhere to distribute the delta.
        // It would then be given entirely back to the resizing column,
        // preventing it from changing size.
        if (delta != 0.0) {
            double w = resizingColumn.getWidth() + delta;
            rf.setColumnWidth(resizingColumn, w);
        }

        // At this point the JTable has to work out what preferred sizes
        // would have resulted in the layout the user has chosen.
        // Thereafter, during window resizing etc. it has to work off
        // the preferred sizes as usual - the idea being that, whatever
        // the user does, everything stays in synch and things don't jump
        // around.
        setWidthsFromPreferredWidths(rf, visibleLeafColumns, true);
        return true;
    }
    
    protected double getTotalColumnWidth(List<? extends TableColumnBase<?,?>> columns) {
        double w = 0.0;
        for (TableColumnBase<?,?> c: columns) {
            w += c.getWidth();
        }
        return w;
    }
    
    protected double sumPreferredWidths(List<? extends TableColumnBase<?,?>> columns) {
        double w = 0.0;
        for (TableColumnBase<?,?> c: columns) {
            w += c.getPrefWidth();
        }
        return w;
    }
    
    // Distribute delta over columns, as indicated by the autoresize mode.
    private void accommodateDelta(ResizeFeaturesBase rf,
                                  List<? extends TableColumnBase<?,?>> columns,
                                  int resizingColumnIndex,
                                  double delta) {
        int columnCount = columns.size(); // getColumnCount();
        int from = resizingColumnIndex;
        int to;

        // Use the mode to determine how to absorb the changes.
        switch(autoResizeMode) {
            case AUTO_RESIZE_NEXT_COLUMN:
                from = from + 1;
                to = Math.min(from + 1, columnCount); break;
            case AUTO_RESIZE_SUBSEQUENT_COLUMNS:
                from = from + 1;
                to = columnCount; break;
            case AUTO_RESIZE_LAST_COLUMN:
                from = columnCount - 1;
                to = from + 1; break;
            case AUTO_RESIZE_ALL_COLUMNS:
                from = 0;
                to = columnCount; break;
            default:
                return;
        }

        final int start = from;
        final int end = to;

        Resizable3 r = new Resizable3() {
            public int getElementCount() {
                return end - start;
            }

            public double getLowerBoundAt(int i) {
                return columns.get(i + start).getMinWidth();
            }

            public double getUpperBoundAt(int i) {
                return columns.get(i + start).getMaxWidth();
            }

            public double getMidPointAt(int i) {
                return columns.get(i + start).getWidth();
            }

            public void setSizeAt(double w, int i) {
                rf.setColumnWidth(columns.get(i + start), w);
            }
        };

        double totalWidth = 0.0;
        for(int i = from; i < to; i++) {
            TableColumnBase<?,?> c = columns.get(i);
            double w = c.getWidth();
            totalWidth = totalWidth + w;
        }

        adjustSizes(totalWidth + delta, r, false);
    }
    
    private void setWidthsFromPreferredWidths(ResizeFeaturesBase rf,
                                              List<? extends TableColumnBase<?,?>> columns,
                                              boolean inverse) {
        double target = !inverse ? rf.getContentWidth() : sumPreferredWidths(columns);

        Resizable3 r = new Resizable3() {
            public int getElementCount() {
                return columns.size();
            }

            public double getLowerBoundAt(int i) {
                return columns.get(i).getMinWidth();
            }

            public double getUpperBoundAt(int i) {
                return columns.get(i).getMaxWidth();
            }

            public double getMidPointAt(int i) {
                if (!inverse) {
                    return columns.get(i).getPrefWidth();
                } else {
                    return columns.get(i).getWidth();
                }
            }

            public void setSizeAt(double w, int i) {
                if (!inverse) {
                    rf.setColumnWidth(columns.get(i), w);
                } else {
                    columns.get(i).setPrefWidth(w);
                }
            }
        };

        adjustSizes(target, r, inverse);
    }

    private void adjustSizes(double target, Resizable3 r, boolean inverse) {
        int count = r.getElementCount();
        double totalPreferred = 0.0;
        for (int i = 0; i < count; i++) {
            totalPreferred += r.getMidPointAt(i);
        }
        
        Resizable2 s;
        if ((target < totalPreferred) == !inverse) {
            s = new Resizable2() {
                public int getElementCount()      { return r.getElementCount(); }
                public double getLowerBoundAt(int i) { return r.getLowerBoundAt(i); }
                public double getUpperBoundAt(int i) { return r.getMidPointAt(i); }
                public void setSizeAt(double newSize, int i) { r.setSizeAt(newSize, i); }
            };
        } else {
            s = new Resizable2() {
                public int getElementCount()      { return r.getElementCount(); }
                public double getLowerBoundAt(int i) { return r.getMidPointAt(i); }
                public double getUpperBoundAt(int i) { return r.getUpperBoundAt(i); }
                public void setSizeAt(double newSize, int i) { r.setSizeAt(newSize, i); }
            };
        }
        
        adjustSizes(target, s, !inverse);
    }
    
    private void adjustSizes(double target, Resizable2 r, boolean limitToRange) {
        double totalLowerBound = 0;
        double totalUpperBound = 0;
        for (int i = 0; i < r.getElementCount(); i++) {
            totalLowerBound += r.getLowerBoundAt(i);
            totalUpperBound += r.getUpperBoundAt(i);
        }

        if (limitToRange) {
            target = Math.min(Math.max(totalLowerBound, target), totalUpperBound);
        }

        for (int i = 0; i < r.getElementCount(); i++) {
            double lowerBound = r.getLowerBoundAt(i);
            double upperBound = r.getUpperBoundAt(i);
            // Check for zero. This happens when the distribution of the delta
            // finishes early due to a series of "fixed" entries at the end.
            // In this case, lowerBound == upperBound, for all subsequent terms.
            double newSize;
            if (totalLowerBound == totalUpperBound) {
                newSize = lowerBound;
            } else {
                double f = (double)(target - totalLowerBound) / (totalUpperBound - totalLowerBound);
                newSize = (int)Math.round(lowerBound + f * (upperBound - lowerBound));
                // We'd need to round manually in an all integer version.
                // size[i] = (int)(((totalUpperBound - target) * lowerBound +
                //     (target - totalLowerBound) * upperBound)/(totalUpperBound-totalLowerBound));
            }
            r.setSizeAt(newSize, i);
            target -= newSize;
            totalLowerBound -= lowerBound;
            totalUpperBound -= upperBound;
        }
    }
    
    protected interface Resizable2 {
        public int getElementCount();
        public double getLowerBoundAt(int i);
        public double getUpperBoundAt(int i);
        public void setSizeAt(double newSize, int i);
    }

    protected interface Resizable3 extends Resizable2 {
        public double getMidPointAt(int i);
    }
}
