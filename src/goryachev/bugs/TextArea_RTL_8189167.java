package goryachev.bugs;

import javafx.application.Application;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/// https://bugs.openjdk.org/browse/JDK-8189167
/// 
public class TextArea_RTL_8189167 extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello World!");

        VBox vbox = new VBox(10);

        TextArea text1 = new TextArea();
        TextArea text2 = new TextArea();
        text2.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        vbox.getChildren().addAll(text1, text2);

        StackPane root = new StackPane();
        root.getChildren().add(vbox);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }
}