package goryachev.bugs;

import java.util.Base64;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class TableColumnHeader_SortArrow_8385778 extends Application {

    private static final String CSS_TEXT =
        ".table-view .column-header-background .column-header {\n" +
        " -fx-size: 2em;\n" +
        " -fx-padding: 0.35em 0 0.35em 2em;\n" +
        "}\n" +
        ".table-view .column-header-background .column-header > .label {\n" +
        " -fx-padding: 0;\n" +
        "}\n";

    private static String buildDataCss(String cssText) {
        String encoded = Base64.getEncoder().encodeToString(cssText.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return "data:text/css;base64," + encoded;
    }

    @Override
    public void start(Stage stage) {
        TableView<String[]> table = new TableView<>();
        table.getStylesheets().add(buildDataCss(CSS_TEXT));

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

        StackPane root = new StackPane(table);
        root.setPrefSize(460, 260);

        Scene scene = new Scene(root);
        stage.setTitle("Sort Arrow Padding Test — click a header to sort");
        stage.setScene(scene);
        stage.show();
    }
}