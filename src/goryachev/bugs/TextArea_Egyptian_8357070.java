package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8357070
 */
public class TextArea_Egyptian_8357070 extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        TextArea textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.setFont(Font.getDefault().font(24.0));
        textArea.setText("𓅂𓁹𓁋𓁨");

        BorderPane bp = new BorderPane();
        bp.setCenter(textArea);
        
        Scene scene = new Scene(bp);

        stage.setScene(scene);
        stage.setTitle("Aramaic issue JDK-8318099");
        stage.show();
    }
}