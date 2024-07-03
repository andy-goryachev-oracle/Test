package goryachev.bugs;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.text.HitInfo;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * JDK-8319050
 * Text.caretShape() and .rangeShape() wrong in RTL orientation.
 * 2024/04/05
 */
public class Text_CaretRangeShape_8319050 extends Application {

    private Text control;
    private Path caret;
    private Path range;

    @Override
    public void start(Stage stage) throws Exception {
        control = new Text(
            "Arabic:: " + // also has problem rendering the ':' when a line break occurs here 
            "العربية"
//                +
//            "\n" +
//            "Hebrew: " +
//            "עברית"
            );
        control.setStyle("-fx-font-size:400%");
        control.setManaged(false);
        control.addEventFilter(MouseEvent.MOUSE_MOVED, this::handleMouseMoved);
        control.setLayoutY(100);

        caret = new Path();
        caret.setManaged(false);
        caret.setStroke(Color.MAGENTA);
        caret.setStrokeWidth(1);
        caret.setMouseTransparent(true);
        caret.setLayoutY(100);

        range = new Path();
        range.setManaged(false);
        range.setStroke(null);
        range.setFill(Color.rgb(255, 0, 0, 0.2));
        range.setMouseTransparent(true);
        range.setLayoutY(100);

        StackPane bp = new StackPane();
        bp.getChildren().addAll(control, caret, range);
        StackPane.setAlignment(control, Pos.TOP_RIGHT);
        StackPane.setAlignment(caret, Pos.TOP_RIGHT);
        StackPane.setAlignment(range, Pos.TOP_RIGHT);
        control.wrappingWidthProperty().bind(bp.widthProperty());
        bp.widthProperty().addListener((p) -> {
            updateRange();
        });

        Scene scene = new Scene(bp);
        scene.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        stage.setScene(scene);
        stage.setTitle("Text RTL");
        stage.setWidth(500);
        stage.setHeight(300);
        stage.show();

        Timeline a = new Timeline(
            new KeyFrame(Duration.millis(100), (ev) -> {
                updateRange();
            }));
        a.setCycleCount(Timeline.INDEFINITE);
        a.play();
    }

    void updateRange() {
        int len = control.getText().length();
        PathElement[] pe = control.rangeShape(0, len);
        //System.out.println("range=" + dump(pe));
        range.getElements().setAll(pe);
    }

    private void handleMouseMoved(MouseEvent ev) {
        // expecting minY to be 0, but it is not - why?
        //System.out.println("bounds=" + control.getBoundsInParent());

        Point2D p = new Point2D(ev.getX(), ev.getY());
        HitInfo h = control.hitTest(p);
        //System.out.println("hit=" + h);
        //System.out.println("text.bounds=" + control.getBoundsInLocal());

        PathElement[] pe = control.caretShape(h.getCharIndex(), h.isLeading());
        //System.out.println("caret=" + dump(pe));
        caret.getElements().setAll(pe);
        updateRange();
    }

    /** dumps the path element array to a compact human-readable string */
    public static String dump(Object[] elements) {
        StringBuilder sb = new StringBuilder();
        if (elements == null) {
            sb.append("null");
        } else {
            for (Object em : elements) {
                if (em instanceof MoveTo p) {
                    sb.append('M');
                    sb.append(r(p.getX()));
                    sb.append(',');
                    sb.append(r(p.getY()));
                    sb.append(' ');
                } else if (em instanceof LineTo p) {
                    sb.append('L');
                    sb.append(r(p.getX()));
                    sb.append(',');
                    sb.append(r(p.getY()));
                    sb.append(' ');
                } else {
                    sb.append(em);
                    sb.append(' ');
                }
            }
        }
        return sb.toString();
    }

    private static int r(double x) {
        return (int)Math.round(x);
    }
}