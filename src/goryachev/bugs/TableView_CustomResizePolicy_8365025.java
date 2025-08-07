package goryachev.bugs;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.scene.control.ConstrainedColumnResizeBase;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;

/**
 * https://bugs.openjdk.org/browse/JDK-8365025
 */
public class TableView_CustomResizePolicy_8365025 extends Application {

    private static final int NUM_FIXED_COLUMNS = 4;

    // 0: fails on macOS, Windows
    // 1 to 6, fails on Windows with fractional scale 1.25, 1.50, 1.75
    // 7 works for any fractional scale
    private static final int TEST_OPTION = 0;

    @Override
    public void start(Stage stage) throws IOException {

        TableView<SimpleTableRow> tableView = createTable();

        final StackPane parent = new StackPane(tableView);
        Scene scene = new Scene(parent, 300, 240);
        stage.setScene(scene);
        stage.show();
        stage.titleProperty().bind(Bindings.format("%.2f", stage.renderScaleXProperty()));
    }

    private TableView<SimpleTableRow> createTable() {
        TableView<SimpleTableRow> tableView = new TableView<>();

        TableColumn<SimpleTableRow, String> cn = new TableColumn<>("Name");
        cn.setResizable(true);
        cn.setCellValueFactory(data -> data.getValue().cn);
        cn.setMinWidth(80);

        TableColumn<SimpleTableRow, Number> c1 = new TableColumn<>("C1");
        c1.setResizable(false);
        c1.setCellValueFactory(data -> data.getValue().c1);
        c1.setMinWidth(40);

        TableColumn<SimpleTableRow, Number> c2 = new TableColumn<>("C2");
        c2.setResizable(false);
        c2.setCellValueFactory(data -> data.getValue().c2);
        c2.setMinWidth(40);

        TableColumn<SimpleTableRow, Number> c3 = new TableColumn<>("C3");
        c3.setResizable(false);
        c3.setCellValueFactory(data -> data.getValue().c3);
        c3.setMinWidth(40);

        TableColumn<SimpleTableRow, Number> c4 = new TableColumn<>("C4");
        c4.setResizable(false);
        c4.setCellValueFactory(data -> data.getValue().c4);
        c4.setMinWidth(40);

        for (int i = 0; i < 100; i++) {
            tableView.getItems().add(new SimpleTableRow());
        }

        tableView.getColumns().addAll(cn, c1, c2, c3, c4);
        tableView.setColumnResizePolicy(new UserDefinedResizePolicy());
        return tableView;
    }

    private static class UserDefinedResizePolicy 
        extends ConstrainedColumnResizeBase // this is the FIX
        implements Callback<TableView.ResizeFeatures, Boolean> {

        private TableView<?> table;

        @SuppressWarnings("unchecked")
        @Override
        public Boolean call(TableView.ResizeFeatures rf) {
            table = rf.getTable();
            List<? extends TableColumnBase<?,?>> visibleLeafColumns = table.getVisibleLeafColumns();
            TableColumnBase<?, ?> nameColumn = visibleLeafColumns.getFirst();
            rf.setColumnWidth(nameColumn, nameColumn.getWidth() + rf.getDelta());
            double widthLeft = rf.getContentWidth() - nameColumn.getWidth();
            double w = calculateWidth(widthLeft, TEST_OPTION);
            for (int i = 1; i <= NUM_FIXED_COLUMNS; i++) {
                rf.setColumnWidth(visibleLeafColumns.get(i), Math.min(widthLeft, w));
                widthLeft -= w;
            }
            return true;
        }

        private double calculateWidth(double widthLeft, int option) {
            final double width = widthLeft / NUM_FIXED_COLUMNS;
            return switch (option) {
                case 0 -> width;
                case 1 -> Math.floor(width);
                case 2 -> Math.ceil(width);
                case 3 -> table.snapPositionX(width);
                case 5 -> table.snapSpaceX(width);
                case 6 -> table.snapSizeX(width);
                case 7 -> snapPortionX(width);
                default -> throw new IllegalStateException("Unexpected value: " + option);
            };
        }

        // Adapted from Region::snapPortionX
        private double snapPortionX(double value) {
            double scaleX = getSnapScaleX(table.getScene());
            return floor(value, scaleX);
        }

        // Adapted from ScaledMath::floor
        private double floor(double value, double scale) {
            double d = value * scale;
            if (Double.isInfinite(d)) {  // Avoids returning NaN for high magnitude inputs
                return value;
            }
            return Math.floor(d + Math.ulp(d)) / scale;
        }

        // from Region::getSnapScaleX
        private static double getSnapScaleX(Scene scene) {
            if (scene == null) return 1.0;
            Window window = scene.getWindow();
            if (window == null) return 1.0;
            return window.getRenderScaleX();
        }
    }

    private static class SimpleTableRow {
        private final StringProperty cn = new SimpleStringProperty();
        private final DoubleProperty c1 = new SimpleDoubleProperty();
        private final DoubleProperty c2 = new SimpleDoubleProperty();
        private final DoubleProperty c3 = new SimpleDoubleProperty();
        private final DoubleProperty c4 = new SimpleDoubleProperty();

        public SimpleTableRow() {
            Random random = new Random();
            cn.set("Name " + random.nextInt(100));
            c1.set(random.nextDouble() * 100);
            c2.set(random.nextDouble() * 100);
            c3.set(random.nextDouble() * 100);
            c4.set(random.nextDouble() * 100);
        }
    }
}