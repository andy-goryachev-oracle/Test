package goryachev.apps;

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
public class JDK_8137244 extends Application {
    private TreeItem<Pojo> root;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        root = new TreeItem<>();
        TreeTableView<Pojo> table = new TreeTableView<>();
        table.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
        table.setShowRoot(false);
        TreeTableColumn<Pojo,String> column = new TreeTableColumn<>("Column1");
        column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getValue()));
        table.getColumns().add(column);
        table.setRoot(root);

        HBox buttonsBox = new HBox();
        Button fillTable = new Button("fill table");
        fillTable.setOnAction(e -> fillTable());
        Button clearTable = new Button("clear table");
        clearTable.setOnAction(e -> root.getChildren().clear());
        buttonsBox.getChildren().addAll(fillTable, clearTable);

        VBox vb = new VBox();
        vb.getChildren().addAll(buttonsBox, table);
        Scene scene = new Scene(vb);
        stage.setScene(scene);
        stage.setTitle("JDK-8137244 " + System.getProperty("java.version"));
        stage.show();
    }

    protected void fillTable() {
        root.getChildren().add(new TreeItem<>(new Pojo("100")));
        root.getChildren().add(new TreeItem<>(new Pojo("101")));
        root.getChildren().add(new TreeItem<>(new Pojo("102")));
        root.getChildren().add(new TreeItem<>(new Pojo("103")));
        root.getChildren().add(new TreeItem<>(new Pojo("104")));
        root.getChildren().add(new TreeItem<>(new Pojo("105")));
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
