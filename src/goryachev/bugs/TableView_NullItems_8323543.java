package goryachev.bugs;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8323543
 */
public class TableView_NullItems_8323543 extends Application {
    @Override
    public void start(Stage stage) {
        TableView<String> tableView = new TableView<>();

        TableColumn<String, String> col = new TableColumn<>("123");
        col.setCellValueFactory(e -> new SimpleStringProperty(e.getValue()));
        tableView.getColumns().add(col);

        tableView.getItems().addAll("1", "2", "3");

        stage.setScene(new Scene(tableView));
        stage.show();

        tableView.setItems(null);
    }
}