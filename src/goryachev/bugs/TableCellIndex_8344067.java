package goryachev.bugs;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TableCellIndex_8344067 extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        TableView<String> tableView = new TableView<>();
        //tableView.setFixedCellSize(24);

        TableColumn<String, String> col = new TableColumn<>("A");
        col.setCellValueFactory(p -> new SimpleStringProperty(p.getValue()));
        tableView.getColumns().add(col);

        for (int i = 0; i < 60; i++) {
            tableView.getItems().add(String.valueOf(i));
        }
        String lastItem = "LASTITEM";
        tableView.getItems().add(lastItem);

        Button reSetItems = new Button("Re-Set items");
        reSetItems.setOnAction(e -> tableView.setItems(FXCollections.observableArrayList("0", lastItem)));
        Scene scene = new Scene(new VBox(5, tableView, new HBox(reSetItems)));

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}