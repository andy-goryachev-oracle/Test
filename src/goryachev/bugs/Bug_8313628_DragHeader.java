package goryachev.bugs;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Bug_8313628_DragHeader extends Application {
    @Override
    public void start(Stage primaryStage) {
        TableView<String> tableView = new TableView<>();
        tableView.setPadding(new Insets(5, 70, 15, 50));
        for (int i = 0; i < 10; i++) {
            tableView.getColumns().add(new TableColumn<>("col" + i));
        }

        Parent root = new BorderPane(tableView);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}