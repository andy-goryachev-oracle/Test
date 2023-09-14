package goryachev.bugs;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TestListViewJavaFX_8315610 extends Application {

//    public static void main(String[] args) {
//        System.setProperty("prism.order", "sw");
//        System.setProperty("javafx.pulseLogger", "true");
//        System.setProperty("javafx.pulseLogger.threshold", "-1");
//        System.setProperty("prism.showdirty", "true");
//
//        launch(args);
//    }

    @Override
    public void start(Stage primaryStage) {
        var items = FXCollections.<String>observableArrayList();
        for (int i = 0; i < 100; i++) {
            items.add("Item " + i);
        }

        var listView = new ListView<>(items);

        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    var hbox = new HBox();
                    hbox.setPrefHeight(50.0);
                    var label = new Label(item);
                    hbox.getChildren().add(label);
                    setGraphic(hbox);
                }
            }
        });

        listView.setPrefWidth(805.0);
        listView.setPrefHeight(720.0);

        var scene = new Scene(new VBox(listView), 1350, 900);
        primaryStage.setTitle("ListView Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}