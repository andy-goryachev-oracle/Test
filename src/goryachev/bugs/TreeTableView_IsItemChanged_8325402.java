package goryachev.bugs;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class TreeTableView_IsItemChanged_8325402 extends Application {

    @Override
    public void start(Stage primaryStage) {
        TreeTableView<P> tableView = new TreeTableView<>();
        tableView.setRowFactory(e -> new TreeTableRow<>() {
            @Override
            protected boolean isItemChanged(P oldItem, P newItem) {
                System.out.println("isItemChanged");
                return super.isItemChanged(oldItem, newItem);
            }

            @Override
            protected void updateItem(P item, boolean empty) {
                System.out.println("updateItem");
                super.updateItem(item, empty);
            }
        });
        tableView.setRoot(new TreeItem<>());
        tableView.getRoot().getChildren().addAll(new TreeItem<>(new P("aa")));

        TreeTableColumn<P, String> col = new TreeTableColumn<>("ss");
        col.setCellValueFactory((v) -> {
            return new SimpleStringProperty(v.getValue().getValue() != null ? v.getValue().getValue().name : "");
        });
        tableView.getColumns().add(col);

        Parent root = new StackPane(tableView);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public record P(String name) { }
}
