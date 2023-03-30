package goryachev.bugs;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 */
public class JDK_8130747 extends Application {
    private TreeItem<Pojo> rootItem;

    @Override
    public void start(Stage stage) {
        VBox root = new VBox();

        rootItem = new TreeItem<>();
        TreeTableView<Pojo> table = new TreeTableView<>();
        table.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        table.setShowRoot(false);
        TreeTableColumn<Pojo, String> column = new TreeTableColumn<>("Column1");
        column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getValue()));
        table.getColumns().add(column);
        table.setRoot(rootItem);

        HBox buttonsBox = new HBox();
        Button fillTable = new Button("fill table");
        fillTable.setOnAction(e -> fillTable());
        Button clearTable = new Button("clear table");
        clearTable.setOnAction(e -> rootItem.getChildren().clear());
        buttonsBox.getChildren().addAll(fillTable, clearTable);

        root.getChildren().addAll(buttonsBox, table);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("JDK-8130747 " + System.getProperty("java.version"));
        stage.show();
    }

    private void fillTable() {
        rootItem.getChildren().add(new TreeItem<>(new Pojo("100")));
        rootItem.getChildren().add(new TreeItem<>(new Pojo("101")));
        rootItem.getChildren().add(new TreeItem<>(new Pojo("102")));
        rootItem.getChildren().add(new TreeItem<>(new Pojo("103")));
        rootItem.getChildren().add(new TreeItem<>(new Pojo("104")));
        rootItem.getChildren().add(new TreeItem<>(new Pojo("105")));
    }

    public static void main(String[] args) {
        launch(args);
    }

    private class Pojo {
        String value;

        public Pojo(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
