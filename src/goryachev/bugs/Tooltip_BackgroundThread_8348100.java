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
    private Stage stage;

    @Override
    public void start(Stage stage) {

        var root = new VBox();
        var scene = new Scene(root);
        this.stage = stage;
        stage.setScene(scene);
        var text = new Label("some text");
        text.setBorder(Border.stroke(Color.BLACK));
        var tooltip1 = new Tooltip();
        tooltip1.setShowDelay(new Duration(0.0));
        tooltip1.setHideDelay(new Duration(0.0));
        tooltip1.setText("tooltip1");
        text.setTooltip(tooltip1);
        root.getChildren().add(text);
        stage.show();

        new Thread() {
            @Override
            public void run() {
                for (;;) {
                    new Thread() {
                        @Override
                        public void run() {
                            var tooltip2 = new Tooltip();
                            Platform.runLater(() -> resizeMe());
                        }
                    }.start();

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    void resizeMe() {
        double h = stage.getHeight();
        if (h != 400) {
            stage.setHeight(400);
        } else {
            stage.setHeight(410);
        }
    }
}