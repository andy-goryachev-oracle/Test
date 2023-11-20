package goryachev.bugs;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
When a nested column is dragged, the column drag header (blue) is positioned wrong. 
It appears at the very left of the table and not where the column is. 

Drag any nested column, verify that the column drag header appears at the beginning of the table. 
 */
public class TableView_NestedColumnDrag_8320444 extends Application {
    @Override
    public void start(Stage primaryStage) {
        TableView<String> tableView = new TableView<>();
        tableView.setFixedCellSize(24);

        tableView.setEditable(true);
        tableView.setItems(FXCollections.observableArrayList("a", "b", "c", "d", "e", "f", "g", "h", "i"));

        TableColumn<String, String> col1 = new TableColumn<>("checkbox");
        col1.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));

        TableColumn<String, String> col2 = new TableColumn<>("text");
        col2.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));

        TableColumn<String, String> col3 = new TableColumn<>("text2");
        col3.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));

        TableColumn<String, String> col4 = new TableColumn<>("text3");
        col4.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));

        tableView.getColumns().addAll(col1, col2, col3, col4);

        TableColumn<String, String> col5 = new TableColumn<>("text3687645");
        col5.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));

        TableColumn<String, String> col6 = new TableColumn<>("text3687645");
        col6.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));

        TableColumn<String, String> col7 = new TableColumn<>("text3687645");
        col7.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));

        TableColumn<String, String> col8 = new TableColumn<>("text3687645");
        col8.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));

        TableColumn<String, String> col9 = new TableColumn<>("text3687645");
        col9.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));

        col7.getColumns().addAll(col8, col9);

        col5.getColumns().addAll(col6, col7);

        tableView.getColumns().add(col5);

        BorderPane pane = new BorderPane(tableView);

        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}