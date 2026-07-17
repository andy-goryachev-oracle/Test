package goryachev.bugs;

import java.util.Comparator;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

// https://bugs.openjdk.org/browse/JI-9080656
// https://bugs.openjdk.org/browse/JDK-8388446
public class Canvas_Crash_JI_9080656 extends Application {
    
    int CYCLES = 200;

    class CS {
        public final Canvas layer = new Canvas();
        public final Canvas mask = new Canvas();
        public final Canvas under1 = new Canvas();
        public final Canvas under2 = new Canvas();
    }

    @Override
    public void start(Stage stage) {
        
        for(Screen screen: Screen.getScreens()) {
            start(screen);
        }
    }
    
    private void start(Screen screen) {
        
        Rectangle2D b = screen.getVisualBounds();

        CS cs = new CS();
        Pane root = new Pane(cs.under1, cs.under2, cs.layer);
        
        Stage stage = new Stage();
        stage.setScene(new Scene(root, b.getWidth(), b.getHeight()));
        stage.setTitle("Canvas clip + ImagePool exhaustion repro");
        stage.setX(b.getMinX());
        stage.setY(b.getMinY());
        stage.setWidth(b.getWidth());
        stage.setHeight(b.getHeight());
        stage.show();

        System.out.printf("[repro] window %.0fx%.0f renderScale=%.2f physical %.0fx%.0f%n",
            b.getWidth(), b.getHeight(), stage.getRenderScaleX(),
            b.getWidth() * stage.getRenderScaleX(), b.getHeight() * stage.getRenderScaleY());

        double maxW = b.getWidth();
        double minW = maxW * 0.55;
        double height = b.getHeight();

        AnimationTimer timer = new AnimationTimer() {
            private double w = maxW;
            private double dir = -1;
            private int halfCycles;

            @Override
            public void handle(long now) {
                w += 60 * dir;
                if (w < minW || w > maxW) {
                    dir = -dir;
                    w = Math.clamp(w, minW, maxW);
                    if (++halfCycles >= CYCLES) {
                        System.out.println("[repro] done (see stderr for Prism exceptions)");
                        stop();
                        Platform.exit();
                        return;
                    }
                }
                resizeAndDraw(cs, w, height);
            }
        };
        timer.start();
    }

    private void resizeAndDraw(CS cs, double w, double h) {
        for (Canvas c: new Canvas[] { cs.layer, cs.mask, cs.under1, cs.under2 }) {
            c.setWidth(w);
            c.setHeight(h);
        }

        GraphicsContext u1 = cs.under1.getGraphicsContext2D();
        u1.setStroke(Color.GRAY);
        for (int x = 0; x < (int)w; x += 50) {
            u1.strokeLine(x, 0, x, h);
        }
        GraphicsContext u2 = cs.under2.getGraphicsContext2D();
        u2.setFill(Color.rgb(0, 0, 40, 0.2));
        u2.fillRect(0, 0, w, h);

        GraphicsContext mgc = cs.mask.getGraphicsContext2D();
        mgc.clearRect(0, 0, w, h);
        mgc.setStroke(Color.WHITE);
        mgc.setLineWidth(2);
        for (int x = 0; x < (int)w; x += 4) {
            double y1 = h * 0.5 + Math.sin(x * 0.050) * h * 0.4;
            double y2 = h * 0.5 - Math.sin(x * 0.031) * h * 0.4;
            mgc.strokeLine(x, y1, x, y2);
        }

        GraphicsContext gc = cs.layer.getGraphicsContext2D();
        gc.clearRect(0, 0, w, h);
        gc.setFill(Color.CRIMSON);
        gc.fillRect(0, 0, w, h);
        cs.layer.setClip(cs.mask);
    }
}