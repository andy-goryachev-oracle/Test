package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 */
public class Label_Layout extends Application {
    @Override
    public void start(Stage stage) {
        Label t = new Label("");
        Scene scene = new Scene(t, 300, 250);
        stage.setScene(scene);
        stage.show();
    }
}
