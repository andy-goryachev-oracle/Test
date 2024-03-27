package goryachev.bugs;

import java.util.List;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ConstrainedColumnResizeBase;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * https://bugs.openjdk.org/browse/JDK-8329186
 */
public class TableView_DragColumn_8329186 extends Application {
    @Override
    public void start(Stage primaryStage) {
        final TableView<String> tableView = new TableView<>();
        tableView.setColumnResizePolicy(new UserDefinedResizePolicy());

        final TableColumn<String, String> col1 = new TableColumn<>("Column 1");
        col1.setPrefWidth(160);
        final TableColumn<String, String> col2 = new TableColumn<>("Column 2");
        col2.setPrefWidth(240);
        final TableColumn<String, String> col3 = new TableColumn<>("Column 3");
        col3.setPrefWidth(400);
        tableView.getColumns().addAll(col1, col2, col3);

        primaryStage.setScene(new Scene(tableView, 800, 400));
        primaryStage.show();
    }

    private static class UserDefinedResizePolicy extends ConstrainedColumnResizeBase
        implements Callback<TableView.ResizeFeatures, Boolean> {
        double[] weights = new double[] { 0.2, 0.3, 0.5 };
        double initialColumnWidth = -1;

        @SuppressWarnings("unchecked")
        @Override
        public Boolean call(TableView.ResizeFeatures rf) {
            System.out.println("w=" + rf.getContentWidth() + " d=" + rf.getDelta() + " c=" + rf.getColumn());
            
            List<? extends TableColumnBase<?, ?>> visibleLeafColumns = rf.getTable().getVisibleLeafColumns();
            TableColumn<String, String> column = rf.getColumn();
            if (column != null) {
                if (initialColumnWidth < 0) {
                    initialColumnWidth = column.getWidth();
                }
                double columnWeight = (initialColumnWidth + rf.getDelta()) / rf.getContentWidth();
                int index = rf.getTable().getColumns().indexOf(column);
                double oldColumnWeight = weights[index];
                weights[index] = columnWeight;
                weights[index + 1] -= (columnWeight - oldColumnWeight);
                double t = 0;
                for (int i = 0; i < 3; i++) {
                    double w = weights[i] * rf.getContentWidth();
                    t += w;
                    System.out.println(i + " " + w);
                    rf.setColumnWidth(visibleLeafColumns.get(i), w);
                }
                System.out.println("total=" + t);

                // Problem: we don't know when drag event stops
                // if (drag has finished) { initialColumnWidth = -1; }
                return true;
            }
            return false;
        }
    }
}