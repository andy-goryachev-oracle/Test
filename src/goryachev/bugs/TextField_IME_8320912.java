package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * IME should commit on focus change.
 *
 * https://bugs.openjdk.org/browse/JDK-8320912
 */
public class TextField_IME_8320912 extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        VBox vb = new VBox(
            new TextField(),
            new TextField(),
            new TextArea(),
            new TextArea(
                "- switch to Japanese input\n" +
                "- start typing in one field\n" +
                "- click and continue typing in a different field\n" +
                ">> observe incomplete text + partial input carried over."
            )
        );

        Scene scene = new Scene(vb);

        stage.setScene(scene);
        stage.setTitle("IME");
        stage.show();
    }
}