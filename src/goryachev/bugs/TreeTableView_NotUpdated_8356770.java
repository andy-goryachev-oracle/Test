package goryachev.bugs;

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
 * https://bugs.openjdk.org/browse/JDK-8356770
 */
public class TreeTableView_NotUpdated_8356770 extends Application {

    @Override
    public void start(Stage stage) {

        TreeItem<String> employeeA = new TreeItem<>("1");
        TreeItem<String> engineerA = new TreeItem<>("2");
        TreeItem<String> companyA = new TreeItem<>("Company A");
        companyA.getChildren().addAll(employeeA, engineerA);
        companyA.setExpanded(true);

        TreeItem<String> employeeB = new TreeItem<>("3");
        TreeItem<String> engineerB = new TreeItem<>("4");
        TreeItem<String> companyB = new TreeItem<>("Company B");
        companyB.getChildren().addAll(employeeB, engineerB);
        companyB.setExpanded(true);

        TreeItem<String> root = new TreeItem<>("Root");
        root.getChildren().addAll(companyA, companyB);
        root.setExpanded(true);

        TreeTableColumn<String, String> column = new TreeTableColumn<>("Name");
        column.setMinWidth(200);
        column.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getValue()));

        TreeTableView<String> table = new TreeTableView<>();
        table.getColumns().add(column);
        table.setRoot(root);

        Button move = new Button("Move");
        move.setOnAction(e -> {
            if (root.getChildren().remove(companyB)) {
                companyA.getChildren().add(companyB);
            }
        });

        stage.setScene(new Scene(new VBox(move, table)));
        stage.show();
    }
}