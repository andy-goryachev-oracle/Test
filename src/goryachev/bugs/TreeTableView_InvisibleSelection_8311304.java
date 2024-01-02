package goryachev.bugs;

import java.util.Arrays;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Collapse all groups to make groupItem2 invisible. Select item1. Then call groupItem2.setExpanded(true).
 * https://bugs.openjdk.org/browse/JDK-8311304
 */
public class TreeTableView_InvisibleSelection_8311304 extends Application {
//    public static void main(String[] args) {
//        launch(args);
//    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("TreeView Selection Bug Demo");

        // Populate TreeView. Other than described in the bug report, 2 items are sufficient to reproduce the error.
        TreeItem<String> root = new TreeItem<>();
        TreeItem<String> group1 = new TreeItem<>("Group 1");
        TreeItem<String> group2 = new TreeItem<>("Group 2");
        group1.getChildren().add(group2);
        TreeItem<String> child = new TreeItem<>("Child");
        group2.getChildren().add(child);
        root.getChildren().addAll(Arrays.asList(new TreeItem<>("Item 1"), new TreeItem<>("Item 2"), group1));

        // Set expansion state as described in bug report
        group1.setExpanded(false);
        group2.setExpanded(false);

        // Simple treeview configuration
        TreeTableView<String> ttv = new TreeTableView<>(root);
        ttv.setShowRoot(false);
        TreeTableColumn<String, String> col = new TreeTableColumn<>("Column 1");
        col.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue()));
        ttv.getColumns().add(col);
        col.setMinWidth(150);

        // Initialize the selection to show the bug
        ttv.getSelectionModel().select(0);

        Button button = new Button("Trigger expand of group 2");
        button.setOnAction(event -> {
            ////////////////////////////////////////////////////
            // This triggers the bug. If an item gets expanded which is not visible (because owner group is collapsed),
            // the treeview selection moves 1 item down.
            ///////////////////////////////////////////////////
            group2.setExpanded(true);
        });

        final Scene scene = new Scene(new VBox(ttv, button), 600, 500);
        stage.setScene(scene);
        stage.show();
    }
}