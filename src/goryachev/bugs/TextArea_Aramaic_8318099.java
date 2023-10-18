package goryachev.bugs;

import javafx.application.Application;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8318099
 */
public class TextArea_Aramaic_8318099 extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        TextArea textArea = new TextArea();
        // also fails when wrap text is off
        textArea.setWrapText(true);
        textArea.setFont(Font.getDefault().font(24.0));
        textArea.setText("Old Aramaic: \ud802\udd00\ud802\udd13\ud802\udd0c\ud802\udd09\ud802\udd00; Imperial Aramaic: \ud802\udc40\ud802\udc53\ud802\udc4c\ud802\udc49\ud802\udc40;");

        CheckBox rtl = new CheckBox("Right-to-Left");
        
        ToolBar tb = new ToolBar(rtl);

        BorderPane bp = new BorderPane();
        bp.setTop(tb);
        bp.setCenter(textArea);
        
        Scene scene = new Scene(bp);
        
        rtl.selectedProperty().addListener((s, p, c) -> {
            NodeOrientation ori = c ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT;
            scene.setNodeOrientation(ori);
        });

        stage.setScene(scene);
        stage.setTitle("Aramaic issue JDK-8318099");
        stage.show();
    }
}