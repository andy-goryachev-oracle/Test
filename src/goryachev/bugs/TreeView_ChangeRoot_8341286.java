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
 * https://bugs.openjdk.org/browse/JDK-8341286
 */
public class TreeView_ChangeRoot_8341286 extends Application {
    TreeView<Entry> tree;
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
        
        Scene scene = new Scene(p, 600, 300);
        stage.setScene(scene);
        stage.show();

        changeRoot();
    }

    void changeRoot() {
        // FIX
        // either this line or the next one produces visual artifacts
        TreeItem<Entry> root = new TreeItem<>(new Entry(null));
        //TreeItem<Entry> root = new TreeItem<>(null);
        // this line works ok
        //TreeItem<Entry> root = new TreeItem<>(new Entry("Root"));

        int sz = new Random().nextInt(10);
        for (int i = 0; i < sz; i++) {
            root.getChildren().add(new TreeItem<>(new Entry("V" + seq++)));
        }
        tree.setRoot(root);
    }
    
    static class Entry {
        private final String text;

        public Entry(String s) {
            this.text = s;
        }
        
        @Override
        public String toString() {
            return text;
        }
    }
}