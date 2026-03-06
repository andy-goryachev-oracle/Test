package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/// https://bugs.openjdk.org/browse/JDK-8256142
public class TreeView_Focus_8256142 extends Application {

    @Override
    public void start(Stage stage) {
        TreeView<String> treeView = new TreeView<>();
        TreeItem<String> root = new TreeItem<>("Root");
        treeView.setRoot(root);
        treeView.setShowRoot(false);

        root.getChildren().add(new TreeItem<>("Foo"));
        root.getChildren().add(new TreeItem<>("Bar"));
        root.getChildren().add(new TreeItem<>("Baz"));

        var scene = new Scene(new StackPane(treeView), 640, 480);
        stage.setScene(scene);
        stage.show();
    }
}