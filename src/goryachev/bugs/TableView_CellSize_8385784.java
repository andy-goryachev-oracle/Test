package goryachev.bugs;

import java.util.Base64;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

// https://bugs.openjdk.org/browse/JDK-8385784
public class TableView_CellSize_8385784 extends Application {

    private static final String CSS_TEXT =
        """
        .table-view .column-header-background .column-header {
            -fx-background-color: #888888, white;
            -fx-background-insets: 0, 0 0 1 0;
            -fx-padding: 0;
        }
        .table-view .table-row-cell {
            -fx-background-color: #888888, white;
            -fx-background-insets: 0, 0 0 1 0;
            -fx-padding: 0;
        }
        .table-view.integer .column-header-background .column-header {
            -fx-cell-size: 2em;
        }
        .table-view.integer .table-row-cell {
            -fx-cell-size: 2em;
        }
        .table-view.fractional .column-header-background .column-header {
            -fx-cell-size: 1.98em;
        }
        .table-view.fractional .table-row-cell {
            -fx-cell-size: 1.67em;
        }
        """;

    private static String buildDataCss(String cssText) {
        String encoded = Base64.getEncoder().encodeToString(cssText.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return "data:text/css;base64," + encoded;
    }

    private static TableView<String[]> buildTable() {
        TableView<String[]> table = new TableView<>();
        table.getStylesheets().add(buildDataCss(CSS_TEXT));
        table.setColumnResizePolicy(table.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<String[], String> col1 = new TableColumn<>("Name");
        col1.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[0]));
        col1.setPrefWidth(200);
        col1.setSortable(true);

        TableColumn<String[], String> col2 = new TableColumn<>("Value");
        col2.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[1]));
        col2.setPrefWidth(200);
        col2.setSortable(true);

        table.getColumns().addAll(col1, col2);

        ObservableList<String[]> data = FXCollections.observableArrayList(
            new String[] { "Alice", "42" },
            new String[] { "Bob", "17" },
            new String[] { "Charlie", "99" },
            new String[] { "Diana", "5" },
            new String[] { "Eve", "73" });
        table.setItems(data);
        return table;
    }

    @Override
    public void start(Stage stage) {
        var intTable = buildTable();
        intTable.getStyleClass().add("integer");

        var fracTable = buildTable();
        fracTable.getStyleClass().add("fractional");

        HBox root = new HBox(10, intTable, fracTable);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
    }
}