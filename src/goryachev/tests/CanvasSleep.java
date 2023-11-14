package goryachev.tests;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Canvas coming out of sleep mode.
 */
public class CanvasSleep extends Application {
    private BorderPane bp;

    @Override
    public void start(Stage stage) {
        bp = new BorderPane();
        bp.widthProperty().addListener((x) -> {
            update();
        });
        bp.heightProperty().addListener((x) -> {
            update();
        });
        update();

        Scene scene = new Scene(bp, 800, 400);

        stage.setScene(scene);
        stage.show();
    }

    private void update() {
        int w = (int)bp.getWidth();
        if (w == 0) {
            return;
        }
        int h = (int)bp.getHeight();
        if (h == 0) {
            return;
        }
        System.out.println(w + "x" + h);

        Canvas c = new Canvas(w, h);
        GraphicsContext g = c.getGraphicsContext2D();
        g.setFont(Font.font("Courier New", 12));
        g.setFill(Color.BLACK);
        g.fillText("Look at this text on macOS retina display when coming out of sleep mode.\n" + w + " x " + h, 20, 20);
        bp.setCenter(c);
    }
}