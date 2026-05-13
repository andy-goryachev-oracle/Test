package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

// https://stackoverflow.com/questions/79932827/cannot-set-initial-app-window-size-in-javafx/79932828#79932828
// https://bugs.openjdk.org/browse/JDK-8384503
//
public class Window_UnableSize_8384503 extends Application {
    @Override
    public void start(Stage stage) {
        // missing from the reproducer:
        BorderPane root = new BorderPane();
        Label statusBar = new Label("Status");

        stage.setTitle("test case");

        // unnecessary
        //            root.setPrefWidth(1044);
        //            root.setPrefHeight(728);

        root.setBottom(statusBar);

        Scene scene = new Scene(root, 1044, 728);
        stage.sizeToScene();
        stage.setScene(scene);

        stage.show();
    }
}