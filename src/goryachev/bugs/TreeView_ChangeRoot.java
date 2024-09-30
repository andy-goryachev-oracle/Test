package goryachev.bugs;

import java.util.Random;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * 
 */
public class TreeView_ChangeRoot extends Application {
    TreeView<String> tree;
    static long seq = 1;
    
    @Override
    public void start(Stage stage) {
        stage.setTitle("TreeView Change Root");

        // Simple treeview configuration
        tree = new TreeView<>();
        tree.setShowRoot(true);

        Button button = new Button("Change Root");
        button.setOnAction((ev) -> changeRoot());

        BorderPane p = new BorderPane();
        p.setTop(new HBox(button));
        p.setCenter(tree);
        
        Scene scene = new Scene(p, 600, 500);
        stage.setScene(scene);
        stage.show();

        changeRoot();
    }

    void changeRoot() {
        TreeItem<String> root = new TreeItem<>();

        int sz = new Random().nextInt(10);
        for (int i = 0; i < sz; i++) {
            root.getChildren().add(new TreeItem<>("V" + seq++));
        }
        tree.setRoot(root);
    }
}