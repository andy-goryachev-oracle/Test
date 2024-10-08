package goryachev.bugs;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.text.HitInfo;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import goryachev.util.D;

/**
 * https://bugs.openjdk.org/browse/JDK-8330559
 * https://bugs.openjdk.org/browse/JDK-8341670
 */
public class TextFlow_ExtraSpace_8330559 extends Application {

    private TextFlow flow;
    private Path caret;
    private Path range;

    @Override
    public void start(Stage stage) throws Exception {
        flow = new TextFlow(
                t("Arabic:", Color.RED),
                t("   ", Color.YELLOW),
                t("العربية", Color.GREEN),
                new Text("\n"),
                t("Hebrew: ", Color.BLUE),
                t("עברית", Color.BLACK));
        flow.setStyle("-fx-font-size:400%");
        flow.addEventFilter(MouseEvent.MOUSE_PRESSED, this::handleMousePress);

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

        BorderPane bp = new BorderPane(flow);
        bp.getChildren().addAll(caret, range);

        Scene scene = new Scene(bp);
        scene.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        stage.setScene(scene);
        stage.setTitle("TextFlow RTL");
//        stage.setWidth(220);
        stage.setWidth(500);
        stage.setHeight(300);
        stage.show();
        scene.widthProperty().addListener((p) -> {
            updateRange();
        });

        Platform.runLater(() -> {
            updateRange();
        });
    }

    private void updateRange() {
        int len = getTextLength(flow);
        PathElement[] pe = flow.rangeShape(0, len);
        //System.out.println("range=" + dump(pe));
        range.getElements().setAll(pe);
    }

    private int getTextLength(TextFlow f) {
        int len = 0;
        for (Node n : f.getChildren()) {
            if (n instanceof Text t) {
                len += t.getText().length();
            }
        }
        return len;
    }

    private static Text t(String text, Color c) {
        Text t = new Text(text);
        t.setFill(c);
        t.setUnderline(true);
        return t;
    }

    private void handleMousePress(MouseEvent ev) {
        System.out.println("x: " + ev.getX() + " y:" + ev.getY());

        /** requires JDK-8341670
        javafx.scene.text.LayoutInfo la = flow.getLayoutInfo();
        System.out.println("layout=" + la);
        */

        Point2D p = new Point2D(ev.getX(), ev.getY());
        HitInfo h = flow.hitTest(p);
        System.out.println("hit=" + h);

        PathElement[] pe = flow.caretShape(h.getCharIndex(), h.isLeading());
        System.out.println("caret=" + D.dump(pe));
        caret.getElements().setAll(pe);
        updateRange();
    }
}