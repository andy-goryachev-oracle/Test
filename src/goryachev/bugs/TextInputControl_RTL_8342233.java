package goryachev.bugs;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8342233
 * Regression: TextInputControl selection is backwards in RTL mode
 * 
 * also
 * 
 * https://bugs.openjdk.org/browse/JDK-8296266
 * TextArea: Navigation breaks with RTL text
 */
public class TextInputControl_RTL_8342233 extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        TextArea t = new TextArea(
            """
            - try selecting text with the mouse
            - try navigating text using LEFT/RIGHT ARROW keys (JDK-8296266)
            """
        );
        t.setEditable(false);
        t.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);

        VBox vb = new VBox(
            new Label("TextField:"),
            new TextField("navigate and select"),
            new Label("PasswordField:"),
            new PasswordField(),
            new Label("TextArea:"),
            new TextArea(
                """
                select with mouse
                navigate using arrow keys
                """
            ),
            t
        );
        vb.setSpacing(5);
        vb.setPadding(new Insets(10));

        Scene scene = new Scene(vb);
        scene.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        stage.setScene(scene);
        stage.setTitle("JDK-8342233");
        stage.show();
    }
}