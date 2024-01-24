package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8324327
 */
public class ColorPicker_WhiteRect_8324327 extends Application {
    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox();
        // change to default constructor and the issue disappears
        ColorPicker picker = new ColorPicker(Color.CORAL);
        root.getChildren().addAll(picker);

        var scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}