package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.PathElement;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Text.caret
 * JDK-?
 */
public class Text_WrongCoord extends Application {

    private Text text;

    @Override
    public void start(Stage stage) throws Exception {
        text = new Text("1. Wrong\n2. coordinates");
        text.setStyle("-fx-font-size:400%");
        text.setManaged(false);
        text.setLayoutX(0);
        text.setLayoutY(0);

        StackPane bp = new StackPane();
        bp.getChildren().addAll(text);

        Scene scene = new Scene(bp);

        stage.setScene(scene);
        stage.setTitle("Text: Wrong Coordinates");
        stage.setWidth(500);
        stage.setHeight(300);
        stage.show();
        
        int len = text.getText().length();
        text.setSelectionStart(0);
        text.setSelectionEnd(len);
        
        PathElement[] pe = text.caretShape(0, true);
        System.out.println("caret=" + dump(pe));
        
        pe = text.rangeShape(0, len);
        System.out.println("range=" + dump(pe));
        
        pe = text.underlineShape(0, len);
        System.out.println("underline=" + dump(pe));
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