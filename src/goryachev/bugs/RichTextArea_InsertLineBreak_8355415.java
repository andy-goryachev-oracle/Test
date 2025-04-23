package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import jfx.incubator.scene.control.richtext.RichTextArea;
import jfx.incubator.scene.control.richtext.TextPos;
import jfx.incubator.scene.control.richtext.skin.RichTextAreaSkin;

/**
 * https://bugs.openjdk.org/browse/JDK-8355415
 */
public class RichTextArea_InsertLineBreak_8355415 extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        var r = new RichTextArea();

        // this makes the code break
        r.setSkin(new RichTextAreaSkin(r));

        r.appendText("123");
        r.select(TextPos.ofLeading(0, 1));
        // breaks here
        r.insertLineBreak();

        var pane = new BorderPane();
        pane.setCenter(r);
        stage.setScene(new Scene(pane));
        stage.show();
    }
}