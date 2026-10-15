package goryachev.bugs;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/// https://bugs.openjdk.org/browse/JDK-8384258
public class ListenerHelper_Leak_8384258 extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        BorderPane pane = new BorderPane();
        Scene scene = new Scene(pane, 300, 200);

        TreeTableView<String> treeTableView = new TreeTableView<>();

        Button refresh = new Button("setRoot() many times");
        refresh.setOnAction(event -> {
            for (int i = 0; i < 1000; i++) {
                TreeItem<String> newRoot = new TreeItem<>(null);
                newRoot.getChildren().add(new TreeItem<>("A"));
                newRoot.getChildren().add(new TreeItem<>("B"));
                newRoot.getChildren().add(new TreeItem<>("C"));
                newRoot.getChildren().add(new TreeItem<>("D"));
                treeTableView.setRoot(newRoot);
            }
        });
        pane.setBottom(refresh);

        TreeTableColumn<String, String> column1 = new TreeTableColumn<>("Data");
        column1.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue()));

        treeTableView.getColumns().setAll(column1);

        TreeItem<String> root = new TreeItem<>(null);
        root.getChildren().add(new TreeItem<>("A"));
        root.getChildren().add(new TreeItem<>("B"));
        root.getChildren().add(new TreeItem<>("C"));
        root.getChildren().add(new TreeItem<>("D"));

        treeTableView.setShowRoot(false);
        treeTableView.setRoot(root);

        pane.setCenter(treeTableView);

        stage.setTitle(getClass().getSimpleName());
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }
}