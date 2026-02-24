package goryachev.bugs;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/// https://bugs.openjdk.org/browse/JDK-8378314
///
public class TreeTableView_DisclosureCss_8378314 extends Application {
    
    @Override
    public void start(Stage primaryStage) {

        TreeTableView<String> table = new TreeTableView<>();

        TreeTableColumn<String, String> column = new TreeTableColumn<>("Column");
        column.setPrefWidth(150);
        column.setCellValueFactory(f -> f.getValue().valueProperty());

        // TextArea for logging
        TextArea logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefHeight(500);

        // Cell factory with mouse click handler
        column.setCellFactory(col -> new TreeTableCell<>() {
            {
                setOnMouseClicked(event -> {
                    if (!isEmpty()) {
                        logArea.appendText("Clicked on: " + getItem() + " | clickCount=" + event.getClickCount() + "\n");
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
            }
        });

        table.getColumns().add(column);
        table.setPadding(new Insets(10));

        TreeItem<String> rootItem = new TreeItem<>("root");
        rootItem.setExpanded(true);
        rootItem.getChildren().addAll(
            new TreeItem<>("A"),
            new TreeItem<>("B"));
        table.setRoot(rootItem);

        VBox root = new VBox(10, table, logArea);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 750, 700);

//        scene.getStylesheets().add(
//            "data:text/css," +
//            """
//            .tree-table-row-cell {
//                -fx-padding: 0 0 0 -11;
//            }
//            .tree-table-row-cell > .tree-disclosure-node {
//                -fx-padding: 0 0 0 14; /* revert -11 + padding 3 */
//            }
//            """);

        primaryStage.setScene(scene);
        primaryStage.setTitle("TreeTable Mouse Test");
        primaryStage.show();
    }
}
