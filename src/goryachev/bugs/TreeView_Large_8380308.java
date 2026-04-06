package goryachev.bugs;

import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/// https://bugs.openjdk.org/browse/JDK-8380308
public class TreeView_Large_8380308 extends Application {
    
    private long last;

    @Override
    public void start(Stage stage) {
        int count = 200_000;
        long seq = 0;
        TreeItem<Object> root = new TreeItem<>(count + " items");
        root.setExpanded(true);
        for (int i = 0; i < count; i++) {
            root.getChildren().add(new TreeItem<>(String.valueOf("Item_" + (seq++))));
        }
        
        TreeView<Object> t = new TreeView<>(root);
        t.setShowRoot(true);
        t.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        t.addEventFilter(KeyEvent.ANY, (ev) -> reset());
        t.addEventFilter(MouseEvent.ANY, (ev) -> reset());
        t.getSelectionModel().getSelectedIndices().addListener((ListChangeListener.Change<? extends Integer> ch) -> measure());
        
        BorderPane p = new BorderPane(t);
        Scene scene = new Scene(p, 300, 500);
        stage.setTitle("TreeView");
        stage.setScene(scene);
        stage.show();
    }
    
    private void reset() {
        last = System.nanoTime();
    }
    
    private void measure() {
        long elapsed = System.nanoTime() - last;
        System.out.println("elapsed=" + (elapsed / 1_000_000_000.0));
    }
}