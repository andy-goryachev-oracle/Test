package goryachev.research;

import java.util.List;
import javafx.scene.control.ConstrainedColumnResize;
import javafx.scene.control.ResizeFeaturesBase;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;
import javafx.util.Callback;

/**
 * FIX describe
 */
public class AndyConstrainedResizePolicy extends ConstrainedColumnResize {
    private final ResizeMode mode;

    public AndyConstrainedResizePolicy(ResizeMode m) {
        this.mode = m;
    }
    
    @Override
    public boolean constrainedResize(ResizeFeaturesBase rf,
        double contentWidth,
        List<? extends TableColumnBase<?,?>> visibleLeafColumns) {
        
        double tableWidth = rf.getContentWidth();
        if (tableWidth == 0.0) { 
            return false;
        }
        
        ResizeHelper h = new ResizeHelper(rf, contentWidth, visibleLeafColumns);
        
        // phase 1: do a resize pass (possibly multiple in case one or more constraints have been hit)
        double sumWidths = h.sumWidths();
        double delta = tableWidth - sumWidths;

        if (Math.abs(delta) > EPSILON) {
            boolean needResize;
            do {
                needResize = h.resizeColumnsFromPref(delta);
                if(needResize) System.out.println("*** another pass"); // FIX
            } while (needResize);
        }

        try
        {
            TableColumnBase<?,?> column = rf.getColumn();
            if (column == null) {
                return false;
            }
        } finally {
            h.applySizes();
        }

        // phase2: resize the specified column
/*
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
        */
        return false;
    }
    
    public static TablePolicy forTable(ResizeMode m) {
        return new TablePolicy(m);
    }

    public static class TablePolicy
        extends AndyConstrainedResizePolicy
        implements Callback<TableView.ResizeFeatures,Boolean> {
        
        public TablePolicy(ResizeMode m) {
            super(m);
        }

        @Override
        public Boolean call(TableView.ResizeFeatures f) {
            List<? extends TableColumnBase<?,?>> visibleLeafColumns = f.getTable().getVisibleLeafColumns();
            return constrainedResize(f, f.getContentWidth(), visibleLeafColumns);
        }
    }
}
