package goryachev.bugs;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Border;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Tooltip_BackgroundThread_8348100 extends Application {
    @Override
    public void start(Stage stage) {
        VBox root = new VBox();
        Scene scene = new Scene(root);
        stage.setScene(scene);

        Label text = new Label("some text");
        text.setBorder(Border.stroke(Color.BLACK));

        Tooltip tooltip1 = new Tooltip("tooltip1");
        tooltip1.setShowDelay(Duration.ZERO);
        tooltip1.setHideDelay(Duration.ZERO);
        text.setTooltip(tooltip1);

        root.getChildren().add(text);
        stage.show();

        // Create a thread to continuously resize text and force re-layout
        new Thread(() -> {
            double h1 = 400.0;
            double h2 = 410.0;
            while (true) {
                javafx.application.Platform.runLater(() -> {
                    text.setMinHeight(h1);
                    text.setMaxHeight(h1);
                    stage.setHeight(h1);
                });
                javafx.application.Platform.runLater(() -> {
                    text.setMinHeight(h2);
                    text.setMaxHeight(h2);
                    stage.setHeight(h2);
                });
            }
        }).start();

        // Another thread for creating tooltips
        new Thread(() -> {
            while (true) {
                Tooltip tooltip2 = new Tooltip();
            }
        }).start();
    }
}