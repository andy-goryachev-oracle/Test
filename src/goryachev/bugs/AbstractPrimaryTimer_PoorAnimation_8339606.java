package goryachev.bugs;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * The program will show the following:
 * - It animates a vertical line from left to right that should be drawn on even positions only (0, 2, 4, 6, ...)
 * - There is a "comb" at the top that is spaced exactly 2 pixels apart
 * - While animating you should see no interaction with the animated line and the pre-drawn comb
 * - There is a plot that shows the calculated X value of the rectangle, and its fractional value (and also plots "frame skips")
 *
 * On a correct implementation, you should see nothing out of ordinary. 
 * The plot is a single flat line, and there is no interaction with the comb.
 * 
 * On a faulty implementation, the plot will show large fractions, frame skips, 
 * and the animated line will interact with the comb constantly 
 * (a pattern is drawn on the comb that looks a bit like a moire pattern).
 * 
 * https://bugs.openjdk.org/browse/JDK-8339606
 */
public class AbstractPrimaryTimer_PoorAnimation_8339606 extends Application {
    // TODO
    // add this to the main() method
    //    System.setProperty("com.sun.scenario.animation.fixed.pulse.length", "true");
    // or specify on the command line:
    //    -Dcom.sun.scenario.animation.fixed.pulse.length=true

    @Override
    public void start(Stage primaryStage) {
        StackPane pane = new StackPane();

        pane.setBackground(Background.fill(Color.BROWN));

        Scene scene = new Scene(pane);

        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();

        double H = primaryStage.getHeight();

        /*
         * Set up animation
         */

        Canvas canvas = new Canvas(2000, H);

        for (int i = 0; i < 2000; i += 2) {
            canvas.getGraphicsContext2D().strokeLine(i, H / 10, i, H / 8); // create comb
        }

        Rectangle r = new Rectangle(1, 10000);

        r.setTranslateY(1000);

        pane.getChildren().add(r);
        pane.getChildren().add(canvas);
        pane.getChildren().add(new Label(
            "Value of 'com.sun.scenario.animation.fixed.pulse.length' = "
                + System.getProperty("com.sun.scenario.animation.fixed.pulse.length") +
                "\nPlatform = " + System.getProperty("os.name")));

        // Set up a timeline that should move the rectangle exactly 2 pixels each frame (60 Hz screen):
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(r.translateXProperty(), 0)),
            new KeyFrame(Duration.seconds(60), new KeyValue(r.translateXProperty(), 3600 * 2)) // 60 * 60 Hz
        );

        // Listen to the translate property, and illustrate how smooth (or not smooth)
        // the animation is being calculated:
        r.translateXProperty().subscribe((old, v) -> {
            double likelyValue = Math.round(v.doubleValue() * 1000) / 1000.0 / 2; // Smooth out slight double calculation errors
            double oldLikelyValue = Math.round(old.doubleValue() * 1000) / 1000.0 / 2;

            double fraction = likelyValue % 1;

            canvas.getGraphicsContext2D().strokeLine(likelyValue, (H / 4), likelyValue,
                (H / 4) + (H / 2) * fraction);
            canvas.getGraphicsContext2D().strokeLine(likelyValue * 2, (H / 10), likelyValue * 2, (H / 9)); // draw the position on the comb

            if (likelyValue - oldLikelyValue > 1.1) { // frame skip
                canvas.getGraphicsContext2D().strokeLine(likelyValue, (H / 4) - 10, likelyValue, (H / 4) - 5);
            }
        });

        timeline.playFromStart();
    }
}