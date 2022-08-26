package goryachev.apps;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 */
public class JDK_8137244_Table extends Application {
    protected TableView<String> table;
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<String,String> column = new TableColumn<>("Column1");
        column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue()));
        table.getColumns().add(column);

        HBox buttonsBox = new HBox();
        Button fillTable = new Button("fill table");
        fillTable.setOnAction(e -> fillTable());
        Button clearTable = new Button("clear table");
        clearTable.setOnAction(e -> table.getItems().clear());
        buttonsBox.getChildren().addAll(fillTable, clearTable);

        VBox vb = new VBox();
        vb.getChildren().addAll(buttonsBox, table);
        Scene scene = new Scene(vb);
        stage.setScene(scene);
        stage.setTitle("JDK-8137244 " + System.getProperty("java.version"));
        stage.show();
    }

    protected void fillTable() {
        table.getItems().addAll(
            "100",
            "101",
            "102",
            "103",
            "104"
            );
    }

    protected static class Pojo {
        String value;

        public Pojo(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
