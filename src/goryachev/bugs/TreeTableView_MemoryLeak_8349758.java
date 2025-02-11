package goryachev.bugs;

import java.util.Random;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.skin.TreeTableViewSkin;
import javafx.stage.Stage;

/**
 * Memory leak in TreeTableView
 * https://bugs.openjdk.org/browse/JDK-8349758
 */
public class TreeTableView_MemoryLeak_8349758 extends Application {

    TreeTableView<String> c;

    @Override
    public void start(Stage stage) {
        c = new TreeTableView<>();
        c.setSkin(new TreeTableViewSkin(c));
        c.setRoot(createRoot());
        TreeTableColumn<String, String> col = new TreeTableColumn<>("TreeTable");
        col.setCellValueFactory((cdf) -> {
            Object v = cdf.getValue();
            return new SimpleObjectProperty(v.toString());
        });
        c.getColumns().add(col);

        int mx = 1000000000;
        for (int i = 0; i < mx; i++) {
            c.setRoot(createRoot());
        }

        System.out.println("cycle finished");
    }

    private static TreeItem<String> createRoot() {
        TreeItem<String> root = new TreeItem<>(null);
        int sz = new Random().nextInt(20);
        for (int i = 0; i < sz; i++) {
            root.getChildren().add(new TreeItem<>("yo"));
        }
        root.setExpanded(true);
        return root;
    }
}