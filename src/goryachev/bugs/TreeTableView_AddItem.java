package goryachev.bugs;

import java.util.List;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * 
 */
public class TreeTableView_AddItem extends Application {
    TreeTableView<Entry> tree;
    BorderPane pane;
    static long seq = 1;
    
    @Override
    public void start(Stage stage) {
        stage.setTitle("TreeView Selection Bug Demo");

        // Populate TreeView. Other than described in the bug report, 2 items are sufficient to reproduce the error.
        TreeItem<Entry> root = new TreeItem<>();
        root.getChildren().addAll(
            new TreeItem<>(new Entry()),
            new TreeItem<>(new Entry())
        );

        // Simple treeview configuration
        tree = new TreeTableView<>(root);
        tree.setShowRoot(false);
        {
            TreeTableColumn<Entry, String> c = new TreeTableColumn<>("Title");
            c.setCellValueFactory((v) -> v.getValue().getValue().title);
            tree.getColumns().add(c);
            c.setMinWidth(150);
        }
        {
            TreeTableColumn<Entry, String> c = new TreeTableColumn<>("Text");
            c.setCellValueFactory((v) -> v.getValue().getValue().text);
            tree.getColumns().add(c);
            c.setMinWidth(250);
        }
        tree.setOnContextMenuRequested((ev) -> {
            MenuItem mi;
            ContextMenu m = new ContextMenu();
            mi = new MenuItem("Add Child");
            mi.setOnAction((e) -> addChild());
            m.getItems().add(mi);
            mi = new MenuItem("Add After");
            mi.setOnAction((e) -> addAfter());
            m.getItems().add(mi);
            m.show(tree, ev.getScreenX(), ev.getScreenY());
        });
        
        tree.getSelectionModel().selectedItemProperty().addListener((s,p,sel) -> {
            updateSelection(sel);
        });

        pane = new BorderPane();
        
        SplitPane split = new SplitPane(tree, pane);
        
        Scene scene = new Scene(split, 600, 500);
        stage.setScene(scene);
        stage.show();
    }
    
    void addChild() {
        List<TreeTablePosition<Entry,?>> sel = tree.getSelectionModel().getSelectedCells();
        if(sel.size() == 1) {
            TreeItem<Entry> t = sel.get(0).getTreeItem();
            t.setExpanded(true);
            
            var ch = new TreeItem<>(new Entry());
            t.getChildren().add(ch);
            select(ch);
        }
    }

    void addAfter() {
        List<TreeTablePosition<Entry,?>> sel = tree.getSelectionModel().getSelectedCells();
        if(sel.size() == 1) {
            TreeItem<Entry> t = sel.get(0).getTreeItem();
            var ch = new TreeItem<>(new Entry());
            TreeItem<Entry> p = t.getParent();
            int ix = p.getChildren().indexOf(t);
            if(ix >= 0) {
                p.getChildren().add(ix + 1, ch);
            }
            select(ch);
        }
    }

    void select(TreeItem<Entry> t) {
        //tree.getSelectionModel().select(t);
    }
    
    void updateSelection(TreeItem<Entry> sel) {
        if(sel != null) {
            TextArea t = new TextArea();
            t.textProperty().bindBidirectional(sel.getValue().text);
            pane.setCenter(t);
        }
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