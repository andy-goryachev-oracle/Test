package goryachev.bugs;

import java.io.IOException;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8322486
 */
public class ColorPicker_Blurry_8322486 extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        for (Screen s: Screen.getScreens()) {
            open(s);
        }
    }

    private void open(Screen screen) {
        Rectangle2D r = screen.getVisualBounds();
        System.out.println(r); // FIX

        ColorPicker p = new ColorPicker();

        Group root = new Group();
        VBox vbox = new VBox(8);
        vbox.getChildren().addAll(p);
        root.getChildren().add(vbox);
        Scene scene = new Scene(root, 200, 150);
        Stage stage = new Stage();
        stage.setTitle("ColorPicker");
        stage.setScene(scene);
        double x = r.getMinX() + (r.getMaxX() - r.getMinX() - scene.getWidth()) / 2;
        double y = r.getMinY() + (r.getMaxY() - r.getMinY() - scene.getHeight()) / 2;
        stage.setX(x);
        stage.setY(y);
        stage.show();
    }
}