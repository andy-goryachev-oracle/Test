package goryachev.bugs;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.text.HitInfo;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * JDK-8319050
 * Text.caretShape() and .rangeShape() wrong in RTL orientation
 */
public class Text_CaretRangeShape_8319050 extends Application {

    private Text control;
    private Path caret;
    private Path range;

    @Override
    public void start(Stage stage) throws Exception {
        control = new Text(
            "\nArabic: " +
            "العربية" +
            "\n" +
            "Hebrew: " +
            "עברית");
        control.setStyle("-fx-font-size:400%");
        control.addEventFilter(MouseEvent.MOUSE_MOVED, this::handleMousePress);

        caret = new Path();
        caret.setManaged(false);
        caret.setStroke(Color.MAGENTA);
        caret.setStrokeWidth(1);
        caret.setMouseTransparent(true);

        range = new Path();
        range.setManaged(false);
        range.setStroke(null);
        range.setFill(Color.rgb(255, 0, 0, 0.2));
        range.setMouseTransparent(true);

        BorderPane bp = new BorderPane(control);
        bp.getChildren().addAll(caret, range);
        control.wrappingWidthProperty().bind(bp.widthProperty());

        Scene scene = new Scene(bp);
        scene.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        stage.setScene(scene);
        stage.setTitle("Text RTL");
        stage.setWidth(500);
        stage.setHeight(300);
        stage.show();

        Platform.runLater(() -> {
            updateRange();
        });
    }

    private void updateRange() {
        int len = control.getText().length();
        PathElement[] pe = control.rangeShape(0, len);
        //System.out.println("range=" + dump(pe));
        range.getElements().setAll(pe);
    }

    private void handleMousePress(MouseEvent ev) {
        // expecting minY to be 0, but it is not - why?
        System.out.println("bounds=" + control.getBoundsInParent());

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
    public static String dump(PathElement[] elements) {
        StringBuilder sb = new StringBuilder();
        if (elements == null) {
            sb.append("null");
        } else {
            for (PathElement em : elements) {
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