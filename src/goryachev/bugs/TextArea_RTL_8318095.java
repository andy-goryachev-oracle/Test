package goryachev.bugs;

import javafx.application.Application;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8318095
 */
public class TextArea_RTL_8318095 extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        TextArea textArea = new TextArea();
        // also fails when wrap text is off
        textArea.setWrapText(true);
        textArea.setFont(Font.getDefault().font(24.0));
        textArea.setText("Arabic: العربية\nHebrew: עברית");

        Scene scene = new Scene(textArea);
        scene.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        stage.setScene(scene);
        stage.setTitle("TextArea in RTL Mode");
        stage.show();
    }
}