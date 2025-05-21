package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * JavaFX test case.
 * https://bugs.openjdk.org/browse/JDK-8356996
 */
public class TextArea_Egyptian_8356996 extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // TODO
        String text = "\ud80c\udd42\ud80c\udc79\ud80c\udc4b\ud80c\udc68";
        Font f = Font.font(24.0);
        
        TextField textField = new TextField(text);
        textField.setFont(f);
        
        TextArea textArea = new TextArea(text);
        textArea.setFont(f);

        BorderPane bp = new BorderPane();
        bp.setTop(textField);
        bp.setCenter(textArea);
        
        Scene scene = new Scene(bp);

        stage.setScene(scene);
        stage.setTitle("Ancient Egyptian (JavaFX) JDK-8356996 " + text);
        stage.show();
    }
}