package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8357070
 */
public class TextArea_Egyptian_8357070 extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Font f = Font.font(24.0);
        String text = "𓅂𓁹𓁋𓁨";
        
        TextField textField = new TextField(text);
        textField.setFont(f);
        
        TextArea textArea = new TextArea(text);
        textArea.setFont(f);

        BorderPane bp = new BorderPane();
        bp.setTop(textField);
        bp.setCenter(textArea);
        
        Scene scene = new Scene(bp);

        stage.setScene(scene);
        stage.setTitle("Aramaic issue JDK-8318099");
        stage.show();
    }
}