package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8340344
 */
public class TreeView_Misaligned_8340344 extends Application {

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(createTreeView());
        Scene scene = new Scene(root, 300, 250);
        primaryStage.setTitle("TreeView Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private TreeView createTreeView() {
        TreeItem<String> rootItem = new TreeItem<>("Root");
        TreeItem<String> item1 = new TreeItem<>("Item 1");
        TreeItem<String> item2 = new TreeItem<>("Item 2");
        TreeItem<String> item3 = new TreeItem<>("Item 3");
        TreeItem<String> item4 = new TreeItem<>("Item 1");
        item2.getChildren().add(item3);
        rootItem.getChildren().addAll(item1, item2, item4);

        TreeView<String> treeView = new TreeView<>(rootItem);
        rootItem.setExpanded(true);

        treeView.setShowRoot(false);
        treeView.setCellFactory(tv -> new TreeCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setGraphic(new Label("W"));
                }
            }
        });
        return treeView;
    }
}