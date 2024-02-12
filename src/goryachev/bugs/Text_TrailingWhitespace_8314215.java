package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8314215
 */
public class Text_TrailingWhitespace_8314215 extends Application {
    @Override
    public void start(Stage stage) {
        String text =
            //"AAA\u2003\u2003\u2003\u2003BBB\u2003\u2003\u2003\u2003CCC\u2003\u2003\u2003\u2003";
            "AAA     BBB      CCC      ";

        TextFlow t = new TextFlow(new Text(text));
        t.setStyle("-fx-font-size:200%;");
        t.setTextAlignment(TextAlignment.CENTER);
        
        BorderPane root = new BorderPane(t);
        Scene scene = new Scene(root, 595, 150, Color.BEIGE);
        
        stage.setTitle("Text Alignment");
        stage.setScene(scene);
        stage.show();
    }
}