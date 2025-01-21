package goryachev.bugs;

import javafx.animation.Transition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * 
 */
public class Animation_TitleBarFlicker extends Application {
    @Override
    public void start(Stage primaryStage) {
        Button button = new Button("Button");

        Transition transition = new Transition() {
            {
                setCycleDuration(Duration.millis(500));
            }

            @Override
            protected void interpolate(double frac) {
                button.setOpacity(Math.min(1, frac + 0.5));
            }
        };

        button.setOnMouseEntered(_ -> {
            transition.setRate(1.0);
            transition.play();
        });

        button.setOnMouseExited(_ -> {
            transition.setRate(-1.0);
            transition.play();
        });

        StackPane root = new StackPane(button);
        root.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        root.setEffect(new InnerShadow());

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}