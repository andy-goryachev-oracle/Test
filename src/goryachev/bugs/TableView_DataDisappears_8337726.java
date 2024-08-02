package goryachev.bugs;

import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * https://bugs.openjdk.org/browse/JDK-8337726
 */
public class TableView_DataDisappears_8337726 extends Application {

    @Override
    public void start(Stage primaryStage) {

        StackPane root = new StackPane();

        TableView<Map> tableView = new TableView<>();
        int size = 10;
        for (int i = 0; i < size; i++) {
            TableColumn<Map, String> tableColumn = new TableColumn<>(i + "");
            int finalI = i;
            tableColumn.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Map, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(
                        TableColumn.CellDataFeatures<Map, String> mapStringCellDataFeatures) {
                        return new SimpleStringProperty(finalI + "");
                    }
                });
            tableView.getColumns().add(tableColumn);

        }
        for (int i = 0; i < 10; i++) {
            Map<String, String> map = new HashMap<>();
            tableView.getItems().add(map);
            for (int j = 0; j < size; j++) {
                map.put(j + "", j + "");
            }

        }
        root.getChildren().add(tableView);
        tableView.itemsProperty().addListener((ch) -> {
            System.out.println(ch);
        });
        
        //Adjust the height according to your screen resolution to reproduce the bug.
        Scene scene = new Scene(root, 250, 290);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Popup at Screen Corner");
        primaryStage.show();
    }
}