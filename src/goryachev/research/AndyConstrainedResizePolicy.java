package goryachev.research;

import java.util.List;
import javafx.scene.control.ConstrainedColumnResize;
import javafx.scene.control.ResizeFeaturesBase;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;
import javafx.util.Callback;

/**
 * TODO to be moved to ConstrainedColumnResize
 */
public class AndyConstrainedResizePolicy extends ConstrainedColumnResize {
    private final ResizeMode mode;

    public AndyConstrainedResizePolicy(ResizeMode m) {
        this.mode = m;
    }
    
    @Override
    public boolean constrainedResize(boolean firstRun,
        ResizeFeaturesBase rf,
        double contentWidth,
        List<? extends TableColumnBase<?,?>> visibleLeafColumns) {
        
        double tableWidth = rf.getContentWidth();
        if (tableWidth == 0.0) { 
            return false;
        }
        
        ResizeHelper h = new ResizeHelper(rf, contentWidth, visibleLeafColumns, mode);
        
        // phase 1: do a resize pass (possibly multiple in case one or more constraints have been hit)
        double sumWidths = h.sumWidths();
        double delta = tableWidth - sumWidths;

        if (Math.abs(delta) > EPSILON) {
            boolean needResize;
            do {
                needResize = h.resizeColumnsFromPref(delta, firstRun);
                if(needResize) System.out.println("*** another pass"); // FIX
            } while (needResize);
        }

        try
        {
            TableColumnBase<?,?> column = rf.getColumn();
            if (column == null) {
                return false;
            }

            // phase2: resize the specified column
            return h.resizeColumn(column);

        } finally {
            h.applySizes();
            
            System.out.println(h.dump()); // FIX
        }
    }
    
    public static TablePolicy forTable(ResizeMode m) {
        return new TablePolicy(m);
    }
    
    public static TreeTablePolicy forTreeTable(ResizeMode m) {
        return new TreeTablePolicy(m);
    }

    public static class TablePolicy
        extends AndyConstrainedResizePolicy
        implements Callback<TableView.ResizeFeatures,Boolean> {

        public TablePolicy(ResizeMode m) {
            super(m);
        }

        @Override
        public Boolean call(TableView.ResizeFeatures rf) {
            List<? extends TableColumnBase<?,?>> visibleLeafColumns = rf.getTable().getVisibleLeafColumns();
            boolean firstRun = isFirstRun(rf.getTable());
            boolean result = constrainedResize(firstRun, rf, rf.getContentWidth(), visibleLeafColumns);
            setFirstRun(rf.getTable(), !firstRun ? false : !result);
            return result;
        }
    }

    public static class TreeTablePolicy
        extends AndyConstrainedResizePolicy
        implements Callback<TreeTableView.ResizeFeatures,Boolean> {
        
        public TreeTablePolicy(ResizeMode m) {
            super(m);
        }

        @Override
        public Boolean call(TreeTableView.ResizeFeatures rf) {
            List<? extends TableColumnBase<?,?>> visibleLeafColumns = rf.getTable().getVisibleLeafColumns();
            boolean firstRun = isFirstRun(rf.getTable());
            boolean result = constrainedResize(firstRun, rf, rf.getContentWidth(), visibleLeafColumns);
            setFirstRun(rf.getTable(), !firstRun ? false : !result);
            return result;
        }
    }
}
