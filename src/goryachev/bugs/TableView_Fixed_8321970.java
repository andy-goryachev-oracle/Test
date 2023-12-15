package goryachev.bugs;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8321970
 */
public class TableView_Fixed_8321970 extends Application {

    @Override
    public void start(Stage primaryStage) {
        final TableView<String> tableView = new TableView<>();
        final ObservableList<String> content = FXCollections.observableArrayList();
        for (int i = 0; i < 10; ++i) {
            content.add(Integer.toString(i));
        }
        tableView.setItems(content);
        tableView.setFixedCellSize(24);

        for (int i = 0; i < 10; ++i) {
            final TableColumn<String, String> tableColumn = new TableColumn<>(Integer.toString(i));
            tableColumn.setPrefWidth(20);
            tableColumn.setCellValueFactory(value -> new SimpleStringProperty("foo"));
            tableView.getColumns().add(tableColumn);
        }

        final Scene scene = new Scene(tableView, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setOnShown(e -> {
            for (int i = 0; i < 10; ++i) {
                final TableColumn<String, String> tableColumn = new TableColumn<>(Integer.toString(i + 10));
                tableColumn.setPrefWidth(20);
                tableColumn.setCellValueFactory(value -> new SimpleStringProperty("bar"));
                tableView.getColumns().add(tableColumn);
            }
        });
        primaryStage.show();
    }

}