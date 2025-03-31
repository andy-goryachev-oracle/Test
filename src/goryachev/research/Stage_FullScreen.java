package goryachev.research;

import java.awt.Paint;
import java.util.Random;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 */
public class Stage_FullScreen extends Application {

    private Label content;
    private StackPane container;

    @Override
    public void start(Stage stage) throws Exception {
//        stage.initStyle(StageStyle.UNDECORATED);
        stage.initStyle(StageStyle.DECORATED);
        stage.setFullScreenExitHint("");

        content = new Label("test");
        content.setMaxWidth(Double.POSITIVE_INFINITY);
        content.setMaxHeight(Double.POSITIVE_INFINITY);
        content.setAlignment(Pos.CENTER);
        content.setBackground(Background.fill(Color.BLACK));
        content.setTextFill(Color.GREEN);

        container = new StackPane(content);

        Scene scene = new Scene(container);

        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.addEventHandler(KeyEvent.KEY_PRESSED, (ev) -> {
            switch (ev.getCode()) {
            case ESCAPE:
                stage.hide();
                break;
            default:
                update();
                break;
            }
            ev.consume();
        });

        stage.widthProperty().addListener((s, p, c) -> {
            System.out.println("stage.width=" + c);
        });
        stage.heightProperty().addListener((s, p, c) -> {
            System.out.println("stage.height=" + c);
        });
        scene.widthProperty().addListener((s, p, c) -> {
            System.out.println("scene.width=" + c);
        });
        scene.heightProperty().addListener((s, p, c) -> {
            System.out.println("scene.height=" + c);
        });

        stage.show();
        update();
    }

    private void update() {
        Random r = new Random();
        int w = r.nextInt(1000) + 500;
        int h = r.nextInt(1000) + 500;
        Canvas c = new Canvas(w, h);
        GraphicsContext g = c.getGraphicsContext2D();
        g.setFill(Color.rgb(r.nextInt(256), r.nextInt(256), r.nextInt(256)));
        g.fillRect(0, 0, w, h);
        container.getChildren().add(c);
        try {
            SnapshotParameters p = new SnapshotParameters();
            p.setFill(Color.TRANSPARENT);
            WritableImage im = c.snapshot(p, null);
            //System.out.println(im.getWidth() + "x" + im.getHeight()); 
            ImageView v = new ImageView(im);

            v.setPreserveRatio(true);
            v.fitWidthProperty().bind(Bindings.createDoubleBinding(
                () -> {
                    return clip(im.getWidth(), content.getWidth());
                }, im.widthProperty(), content.widthProperty()));
            v.fitHeightProperty().bind(Bindings.createDoubleBinding(
                () -> {
                    return clip(im.getHeight(), content.getHeight());
                }, im.heightProperty(), content.heightProperty()));
            content.setGraphic(v); //new TextArea("yo"));
            content.setText(null);
        } finally {
            container.getChildren().remove(c);
        }
    }

    private static double clip(double im, double sc) {
        if (im < sc) {
            return im;
        }
        return sc;
    }
}
