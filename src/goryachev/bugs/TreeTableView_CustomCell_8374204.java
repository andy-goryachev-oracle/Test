package goryachev.bugs;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/// https://bugs.openjdk.org/browse/JDK-8374204
public class TreeTableView_CustomCell_8374204 extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        TreeTableView<Item> table = new TreeTableView<>();

        TreeTableColumn<Item, String> col1 = new TreeTableColumn<>("name");
        col1.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getValue().name));

        TreeTableColumn<Item, String> col2 = new TreeTableColumn<>("some data");
        col2.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getValue().someData));

        col2.setCellFactory(c -> new TreeTableCell<>() {
            private Label customLabel = new Label();

            {
                customLabel.setBackground(Background.fill(Color.GRAY));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                // FIX no idea why this happens
//                if (empty) {
//                    setGraphic(new Button("empty"));
//                } else {
//                    this.customLabel.setText(item != null ? item : "none");
//                    this.setGraphic(this.customLabel);
//                }
                if (empty) {
                    setText("empty");
                } else {
                    setText(item != null ? item : "none");
                }
            }
        });

        table.getColumns().addAll(col1, col2);

        TreeItem<Item> rootItem = new TreeItem<>(new Item("root", "root value"));
        rootItem.setExpanded(true);
        rootItem.getChildren().addAll(
            new TreeItem<>(new Item("A", null)),
            new TreeItem<>(new Item("B", null)),
            new TreeItem<>(new Item("C", null)),
            new TreeItem<>(new Item("D", null)));
        table.setRoot(rootItem);

        StackPane root = new StackPane();
        root.getChildren().add(table);
        stage.setScene(new Scene(root, 500, 80));
        stage.show();
    }

    record Item(String name, String someData) {
    }
}