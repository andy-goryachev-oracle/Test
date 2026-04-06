package goryachev.bugs;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/// https://bugs.openjdk.org/browse/JDK-8256142
public class TreeView_Focus_8256142 extends Application {

    @Override
    public void start(Stage stage) {
        Node n =
            treeTable();
            //tree();
        Scene scene = new Scene(new StackPane(n), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    private Node treeTable() {
        TreeTableView<Object> t = new TreeTableView<>();
        TreeItem<Object> root = new TreeItem<>("Root");
        t.setRoot(root);
        t.setShowRoot(false);
        
        TreeTableColumn<Object, String> c1 = new TreeTableColumn<>("C1");
        c1.setCellValueFactory((f) -> new SimpleStringProperty(f.getValue().getValue().toString()));
        t.getColumns().addAll(c1);

        // focus, then remove
        if(!true)
        {
            root.getChildren().add(new TreeItem<>("Covfefe"));
            t.getSelectionModel().select(0);
            root.getChildren().clear();
        }
        
        root.getChildren().add(new TreeItem<>("Foo"));
        root.getChildren().add(new TreeItem<>("Bar"));
        root.getChildren().add(new TreeItem<>("Baz"));
        return t;
    }
    
    private Node tree() {
        TreeView<String> treeView = new TreeView<>();
        TreeItem<String> root = new TreeItem<>("Root");
        treeView.setRoot(root);
        treeView.setShowRoot(false);

        // focus, then remove
        if(true)
        {
            root.getChildren().add(new TreeItem<>("Covfefe"));
            treeView.getSelectionModel().select(0);
            root.getChildren().clear();
        }
        
        root.getChildren().add(new TreeItem<>("Foo"));
        root.getChildren().add(new TreeItem<>("Bar"));
        root.getChildren().add(new TreeItem<>("Baz"));
        return treeView;
    }
}