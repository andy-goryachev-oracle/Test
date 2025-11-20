package goryachev.bugs;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import jfx.incubator.scene.control.richtext.RichTextArea;
import jfx.incubator.scene.control.richtext.TextPos;
import jfx.incubator.scene.control.richtext.model.ParagraphDirection;
import jfx.incubator.scene.control.richtext.model.StyleAttributeMap;

/// https://bugs.openjdk.org/browse/JDK-8372293
public class RichTextArea_RTL_8372293 extends Application {

    @Override
    public void start(Stage stage) {
        String hebrewText = "זוהי דוגמה לטקסט בעברית. הטקסט נכתב מימין לשמאל";
        RichTextArea r = new RichTextArea();
        r.appendText(hebrewText);
        r.applyStyle(TextPos.ZERO, TextPos.ZERO, StyleAttributeMap.of(StyleAttributeMap.PARAGRAPH_DIRECTION, ParagraphDirection.RIGHT_TO_LEFT));
        r.setWrapText(true);

        BorderPane bp = new BorderPane();
        bp.setPadding(new Insets(15));
        bp.setCenter(r);

        Scene scene = new Scene(bp, 600, 700);
        stage.setTitle(getClass().getSimpleName());
        stage.setScene(scene);
        stage.show();

        r.requestFocus();
    }
}