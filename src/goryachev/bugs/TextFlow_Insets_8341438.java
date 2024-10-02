package goryachev.bugs;

import java.text.DecimalFormat;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.text.HitInfo;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8341438
 */
public class TextFlow_Insets_8341438 extends Application {

    private static final DecimalFormat DOUBLE_FORMAT = new DecimalFormat("0.###");

    @Override
    public void start(Stage stage) throws Exception {
        String text = "TextFlow";
        
        Text t = new Text(text);
        
        TextFlow f = new TextFlow(t);
        // causes the issue
        f.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, null, new BorderWidths(100))));
        f.setPadding(new Insets(33));

        Path path = new Path();
        path.setManaged(false);
        path.setFill(Color.rgb(255, 255, 128, 0.5));
        path.setStroke(Color.rgb(128, 128, 0));
        
        Scene scene = new Scene(f);
        stage.setScene(scene);
        stage.setWidth(600);
        stage.setHeight(500);
        stage.setTitle("TextFlow");
        stage.show();
        
        Platform.runLater(() -> {
            PathElement[] pe = f.caretShape(0, true);
            System.out.println("caretShape(0,true) = " + dump(pe));
            
            Insets m = f.getInsets();
            Point2D p = new Point2D(m.getLeft(), m.getTop());
            HitInfo h = f.hitTest(p);
            System.out.println("hitInfo(" + f(p.getX()) + "," + f(p.getY()) + ") = " + h);

            pe = f.rangeShape(0, 1);
            System.out.println("rangeShape(0,1) = " + dump(pe));
            path.getElements().setAll(pe);
        });
    }

    /** dumps the path element array to a compact human-readable string */
    private static String dump(PathElement[] elements) {
        StringBuilder sb = new StringBuilder();
        if (elements == null) {
            sb.append("null");
        } else {
            for (PathElement em : elements) {
                if (em instanceof MoveTo p) {
                    sb.append('M');
                    sb.append(f(p.getX()));
                    sb.append(',');
                    sb.append(f(p.getY()));
                    sb.append(' ');
                } else if (em instanceof LineTo p) {
                    sb.append('L');
                    sb.append(f(p.getX()));
                    sb.append(',');
                    sb.append(f(p.getY()));
                    sb.append(' ');
                } else {
                    sb.append(em);
                    sb.append(' ');
                }
            }
        }
        return sb.toString();
    }

    private static String f(double v) {
        return DOUBLE_FORMAT.format(v);
    }
}