package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8365801
 */
public class TextArea_Khmer_8365801 extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // \u1794\u17b6\u1793\u179f\u17d2\u1793\u17be\u179f\u17bb\u17c6\u1793\u17c5\u178f\u17c2\u178f\u17d2\u179a\u17bc\u179c\u1794\u17b6\u1793\u1794\u178a\u17b7\u179f\u17c1\u1792
        TextArea ta = new TextArea("បានស្នើសុំនៅតែត្រូវបានបដិសេធ");
        ta.setFont(Font.getDefault().font(48.0));
        ta.setWrapText(true);
        
        BorderPane p = new BorderPane();
        p.setCenter(ta);
        stage.setScene(new Scene(p));
        stage.setWidth(310);
        stage.show();
    }
}