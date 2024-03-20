package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/**
 * Toolbar Overflow
 * https://bugs.openjdk.org/browse/JDK-8328577
 */
public class ToolBar_OverflowButton_8328577 extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        ToolBar toolBar = new ToolBar();

        int i = 0;
        for (; i < 5; i++) {
            Button b = new Button("-----");
            b.setMinWidth(Region.USE_PREF_SIZE);
            toolBar.getItems().add(b);
        }

        // This is a problematic component
        Button button = new Button();
        button.setMinWidth(Region.USE_PREF_SIZE);
        button.sceneProperty().addListener((ov, o, n) -> button.setText(n != null ? "- XXXX -" : null));
        toolBar.getItems().add(button);

        for (; i < 10; i++) {
            Button b = new Button("-----");
            b.setMinWidth(Region.USE_PREF_SIZE);
            toolBar.getItems().add(b);
        }

        stage.setScene(new Scene(toolBar));
        stage.show();
    }
}