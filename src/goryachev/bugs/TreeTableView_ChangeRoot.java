package goryachev.bugs;

import java.util.Random;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8341281
 */
public class TreeTableView_ChangeRoot extends Application {
    TreeTableView<Entry> tree;
    static long seq = 1;
    
    @Override
    public void start(Stage stage) {
        stage.setTitle("TreeTableView Change Root");

        // Simple treeview configuration
        tree = new TreeTableView<>();
        tree.setShowRoot(true);

        {
            TreeTableColumn<Entry, String> c = new TreeTableColumn<>("Title");
            c.setCellValueFactory((v) -> {
                Entry en = v.getValue().getValue();
                return en == null ? null : en.title;
            });
            tree.getColumns().add(c);
            c.setMinWidth(150);
        }
        {
            TreeTableColumn<Entry, String> c = new TreeTableColumn<>("Text");
            c.setCellValueFactory((v) -> {
                Entry en = v.getValue().getValue();
                return en == null ? null : en.text;
            });
            tree.getColumns().add(c);
            c.setMinWidth(250);
        }
        
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
        // FIX
        // Breaks the tree table view
        TreeItem<Entry> root = new TreeItem<>();
        // and this does not
        //TreeItem<Entry> root = new TreeItem<>(new Entry());
        
        int sz = new Random().nextInt(10) + 1;
        for (int i = 0; i < sz; i++) {
            root.getChildren().add(new TreeItem<>(new Entry()));
        }
        tree.setRoot(root);
    }

    static class Entry {
        public final SimpleStringProperty title = new SimpleStringProperty();
        public final SimpleStringProperty text = new SimpleStringProperty();

        public Entry() {
            String s = String.valueOf(seq++);
            this.title.set(s);
            this.text.set("[" + s + "]");
        }
    }
}