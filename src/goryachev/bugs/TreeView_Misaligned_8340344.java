package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Node;
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
        Scene scene = new Scene(root, 300, 50);
        primaryStage.setTitle("TreeView Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private TreeView createTreeView() {
        TreeItem<String> rootItem = new TreeItem<>("Root");
        TreeItem<String> p1 = new TreeItem<>("P1");
        p1.getChildren().add(
            new TreeItem<>("c1")
        );
        TreeItem<String> p2 = new TreeItem<>("P2");
        p2.getChildren().add(
            new TreeItem<>("c2")
        );
        TreeItem<String> p3 = new TreeItem<>("P3");
        
        rootItem.getChildren().addAll(
            p1,
            p2,
            p3
        );

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
                    setGraphic(new Label("---"));
                }
                //setDisclosureNode(dn(item));
            }
        });
        return treeView;
    }

    private Node dn(String text) {
        return new Label(getText(text));
    }

    private String getText(String text) {
        if(text == null) {
            return "";
        } else if(text.contains("1")) {
            return "+";
        } else if(text.contains("2")) {
            return "++";
        }
        return "+++";
    }
}