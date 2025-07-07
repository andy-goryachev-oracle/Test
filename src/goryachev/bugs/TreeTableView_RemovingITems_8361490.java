package goryachev.bugs;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8361490
 * 
 * STEPS TO FOLLOW TO REPRODUCE THE PROBLEM:
 * 1. Run the code
 * 2. Select 4 cells 2x2. Its important to select cells over more then 1 column. If your select cells only for one column, the bug is not reproducible
 * 3. Press delete key
 * 4. Click on one of the column headers to sort
 */
public class TreeTableView_RemovingITems_8361490 extends Application {

    @Override
    public void start(Stage primaryStage) {
        TreeTableView<String> treeTableView = new TreeTableView<>();
        treeTableView.getSelectionModel().setCellSelectionEnabled(true);
        treeTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        TreeTableColumn<String, String> column1 = new TreeTableColumn<>("Spalte 1");
        TreeTableColumn<String, String> column2 = new TreeTableColumn<>("Spalte 2");
        TreeTableColumn<String, String> column3 = new TreeTableColumn<>("Spalte 3");
        TreeTableColumn<String, String> column4 = new TreeTableColumn<>("Spalte 4");
        TreeTableColumn<String, String> column5 = new TreeTableColumn<>("Spalte 5");

        column1.setCellValueFactory(rowDataStringCellDataFeatures -> new SimpleStringProperty("value"));
        column2.setCellValueFactory(rowDataStringCellDataFeatures -> new SimpleStringProperty("value"));
        column3.setCellValueFactory(rowDataStringCellDataFeatures -> new SimpleStringProperty("value"));
        column4.setCellValueFactory(rowDataStringCellDataFeatures -> new SimpleStringProperty("value"));
        column5.setCellValueFactory(rowDataStringCellDataFeatures -> new SimpleStringProperty("value"));

        treeTableView.getColumns().addAll(column1, column2, column3, column4, column5);

        // Root-Element (nicht sichtbar)
        TreeItem<String> root = new TreeItem<>("String");
        root.setExpanded(true);

        // 30 Zeilen hinzufügen
        for (int i = 1; i <= 30; i++) {
            TreeItem<String> item = new TreeItem<>("String");
            root.getChildren().add(item);
        }

        treeTableView.setRoot(root);
        treeTableView.setShowRoot(false); // Root nicht anzeigen

        VBox vbox = new VBox(treeTableView);
        Scene scene = new Scene(vbox, 800, 600);

        vbox.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.DELETE) {
                root.getChildren().removeAll(treeTableView.getSelectionModel().getSelectedItems());
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}