package goryachev.research;

import java.util.List;
import javafx.scene.control.ConstrainedColumnResize;
import javafx.scene.control.ResizeFeaturesBase;
import javafx.scene.control.TableColumnBase;

/**
 * Let's see it this works.
 */
public class AndyConstrainedResizePolicy extends ConstrainedColumnResize {
    
    @Override
    public boolean constrainedResize(ResizeFeaturesBase rf,
        double contentWidth,
        List<? extends TableColumnBase<?,?>> visibleLeafColumns) {
        
        double tableWidth = rf.getContentWidth();
        if (tableWidth == 0.0) { 
            return false;
        }
        
        double colWidth = 0.0;
        for (TableColumnBase<?,?> col: visibleLeafColumns) {
            colWidth += col.getWidth();
        }
        
        if (Math.abs(colWidth - tableWidth) > EPSILON) {
            double actualDelta = tableWidth - colWidth;
            resizeColumns(rf, visibleLeafColumns, actualDelta);
        }

        TableColumnBase<?,?> column = rf.getColumn();
        if (column == null) {
            return false;
        }

        // resize the specific column

        double delta = rf.getDelta();
        boolean isShrinking = delta < 0;

        // need to find the last leaf column of the given column - it is this
        // column that we actually resize from. If this column is a leaf, then we
        // use it.
        TableColumnBase<?,?> leafColumn = column;
        while (leafColumn.getColumns().size() > 0) {
            leafColumn = leafColumn.getColumns().get(leafColumn.getColumns().size() - 1);
        }

        int colPos = visibleLeafColumns.indexOf(leafColumn);
        int endColPos = visibleLeafColumns.size() - 1;

        double remainingDelta = delta;
        while (endColPos > colPos && remainingDelta != 0) {
            TableColumnBase<?,?> resizingCol = visibleLeafColumns.get(endColPos);
            endColPos--;

            // if the column width is fixed, break out and try the next column
            if (!resizingCol.isResizable()) {
                continue;
            }

            // for convenience we discern between the shrinking and growing columns
            TableColumnBase<?,?> shrinkingCol = isShrinking ? leafColumn : resizingCol;
            TableColumnBase<?,?> growingCol = !isShrinking ? leafColumn : resizingCol;

            if (growingCol.getWidth() > growingCol.getPrefWidth()) {
                // growingCol is willing to be generous in this case - it goes
                // off to find a potentially better candidate to grow
                List<? extends TableColumnBase> seq = visibleLeafColumns.subList(colPos + 1, endColPos + 1);
                for (int i = seq.size() - 1; i >= 0; i--) {
                    TableColumnBase<?,?> c = seq.get(i);
                    if (c.getWidth() < c.getPrefWidth()) {
                        growingCol = c;
                        break;
                    }
                }
            }

            double sdiff = Math.min(Math.abs(remainingDelta), shrinkingCol.getWidth() - shrinkingCol.getMinWidth());
            double delta1 = resize(rf, shrinkingCol, -sdiff);
            double delta2 = resize(rf, growingCol, sdiff);
            remainingDelta += isShrinking ? sdiff : -sdiff;
        }
        // TODO EPSILON?
        return remainingDelta == 0;
    }
    
    @Override // FIX remove override
    protected double resizeColumns(ResizeFeaturesBase rf, List<? extends TableColumnBase<?,?>> columns, double delta) {
        // FIX remove
        int columnCount = columns.size();
        double colDelta = delta / columnCount;
        int col = 0;

        // we maintain a count of the amount of delta remaining to ensure that
        // the column resize operation accurately reflects the location of the
        // mouse pointer. Every time this value is not 0, the UI is a teeny bit
        // more inaccurate whilst the user continues to resize.
        double remainingDelta = delta;

        boolean isClean = true;
        for (TableColumnBase<?,?> c: columns) {
            col++;

            // resize each child column
            
            // TODO if a column hit its constraint (min when shrining, max if expanding) - stop, needAnotherPass = true;
            
            double leftOverDelta = resize(rf, c, colDelta);

            // calculate the remaining delta if the was anything left over in
            // the last resize operation
            remainingDelta = remainingDelta - colDelta + leftOverDelta;

            if (leftOverDelta != 0) {
                isClean = false;
                // and recalculate the distribution of the remaining delta for
                // the remaining siblings.
                colDelta = remainingDelta / (columnCount - col);
            }
        }

        // see isClean above for why this is done
        return isClean ? 0.0 : remainingDelta;
    }
    
    @Override // FIX remove override
    protected double resize(ResizeFeaturesBase rf, TableColumnBase column, double delta) {
        if (delta == 0) {
            return 0.0;
        }
        if (!column.isResizable()) {
            return delta;
        }

        boolean isShrinking = delta < 0;
        List<TableColumnBase<?,?>> resizingChildren = getResizableChildren(column, isShrinking);

        if (resizingChildren.size() > 0) {
            return resizeColumns(rf, resizingChildren, delta);
        } else {
            double w = column.getWidth() + delta;
            if (w > column.getMaxWidth()) {
                rf.setColumnWidth(column, column.getMaxWidth());
                return w - column.getMaxWidth();
            } else if (w < column.getMinWidth()) {
                rf.setColumnWidth(column, column.getMinWidth());
                return w - column.getMinWidth();
            } else {
                rf.setColumnWidth(column, w);
                return 0.0F;
            }
        }
    }
}
