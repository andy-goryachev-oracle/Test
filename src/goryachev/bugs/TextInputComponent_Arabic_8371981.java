package goryachev.bugs;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/// https://bugs.openjdk.org/browse/JDK-8371981
///
public class TextInputComponent_Arabic_8371981 extends Application {
    @Override
    public void start(Stage primaryStage) {
        String text = "Arabic:العربية:Arabic:العربية";

        Label instructions = new Label("Try to place the caret at boundaries between Arabic and English words (after colons) to observe caret rendering.");

        TextField f = new TextField(text);
        f.setPrefColumnCount(40);
        f.setPadding(new Insets(0, 0, 100, 0));
        
        TextArea t = new TextArea(text);
        t.setPrefColumnCount(40);
        
        // can also be seen with RichTextArea

        BorderPane bp = new BorderPane(t);
        bp.setTop(instructions);
        bp.setBottom(f);
        Scene scene = new Scene(bp, 600, 400);
        primaryStage.setTitle("JavaFX Mixed RTL/LTR Caret Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}