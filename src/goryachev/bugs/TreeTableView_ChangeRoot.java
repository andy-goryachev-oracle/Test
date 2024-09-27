package goryachev.bugs;

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
 * 
 */
public class TreeTableView_ChangeRoot extends Application {
    TreeTableView<Entry> tree;
    static long seq = 1;
    
    @Override
    public void start(Stage stage) {
        stage.setTitle("TreeView Selection Bug Demo");

        // Populate TreeView. Other than described in the bug report, 2 items are sufficient to reproduce the error.
        TreeItem<Entry> root = new TreeItem<>(new Entry());
//        root.setExpanded(true);
//        root.getChildren().addAll(
//            new TreeItem<>(new Entry()),
//            new TreeItem<>(new Entry())
//        );

        // Simple treeview configuration
        tree = new TreeTableView<>(root);
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
    }
    
    void changeRoot() {
        TreeItem<Entry> root = new TreeItem<>(new Entry());
        root.setExpanded(true);
        root.getChildren().addAll(
            new TreeItem<>(new Entry()),
            new TreeItem<>(new Entry()),
            new TreeItem<>(new Entry())
        );
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