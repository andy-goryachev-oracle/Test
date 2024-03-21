package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

// https://bugs.openjdk.org/browse/JDK-8248914
public class TextFieldTest_8248914 extends Application {

    @Override
    public void start(Stage primaryStage) {
        System.out.println("java.version--->" + System.getProperty("java.version"));
        System.out.println("javafx.runtime.version--->" + System.getProperties().get("javafx.runtime.version"));
        TextField text1 = new TextField("abc");
        TextField text2 = new TextField("abc");
        HBox root = new HBox(text1, text2);
        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("TextFieldTest");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}