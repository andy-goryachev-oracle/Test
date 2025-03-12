package goryachev.bugs;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8351867
 */
public class Label_WhenMinimized_8351867 extends Application {
    private Stage stage;
    private Label label;

    @Override
    public void start(Stage s) throws Exception {
        stage = s;

        label = new Label("Initial State");

        Scene scene = new Scene(label, 400, 200);
        stage.setScene(scene);
        stage.show();
        
        // a possible workaround:
        /*
        stage.iconifiedProperty().addListener((src, p, on) -> {
            if (!on) {
                double w = stage.getWidth();
                stage.setWidth(w - 1.0);
                stage.setWidth(w + 1.0);
            }
        });
        */

        new Thread(this::reproduce).start();
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
        }
    }

    private void reproduce() {
        Platform.runLater(() -> {
            stage.setIconified(true);
        });

        sleep(300);

        Platform.runLater(() -> {
            label.setText("Updated while iconified");
        });

        sleep(300);

        Platform.runLater(() -> {
            stage.setIconified(false);
        });
    }
}