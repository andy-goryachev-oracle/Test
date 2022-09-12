package goryachev.research;

import java.util.List;
import javafx.scene.control.ConstrainedColumnResize;
import javafx.scene.control.ResizeFeaturesBase;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;
import javafx.util.Callback;

/**
 * Constrained columns resize algorithm which:
 * - honors minimal, preferred, and maximum widths
 * - unconditionally suppresses the horizontal scroll bar
 * 
 * @since 20
 */
// TODO to be moved to ConstrainedColumnResize
public class AndyConstrainedResizePolicy extends ConstrainedColumnResize {
    private final ResizeMode mode;

    public AndyConstrainedResizePolicy(ResizeMode m) {
        this.mode = m;
    }
    
    @Override
    public boolean constrainedResize(ResizeFeaturesBase rf,
        List<? extends TableColumnBase<?,?>> visibleLeafColumns) {
        
        double contentWidth = rf.getContentWidth();
        if (contentWidth == 0.0) { 
            return false;
        }
        
        ResizeHelper h = new ResizeHelper(rf, contentWidth, visibleLeafColumns, mode);
        h.resizeToContentWidth();
        
        boolean rv;
        TableColumnBase<?,?> column = rf.getColumn();
        if (column == null) {
            rv = false;
        } else {
            rv = h.resizeColumn(column);
        }

        h.applySizes();
        System.out.println(h.dump()); // FIX
        return rv;
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
            return constrainedResize(rf, visibleLeafColumns);
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
            return constrainedResize(rf, visibleLeafColumns);
        }
    }
}
