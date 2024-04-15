package goryachev.bugs;

import java.nio.charset.Charset;
import java.util.Base64;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Tooltip disappears under certain conditions.
 * https://bugs.openjdk.org/browse/JDK-8330187
 */
public class Tooltip_Disappearing_8330187 extends Application {
    private static String oldStylesheet;

    @Override
    public void start(Stage stage) throws Exception {
        Label t = new Label("Hover to show the tooltip");
        t.setMaxHeight(Double.MAX_VALUE);
        t.setMaxWidth(Double.MAX_VALUE);
        t.setTooltip(new Tooltip("And here comes the tooltip..."));
        t.setAlignment(Pos.CENTER);
        
        BorderPane bp = new BorderPane();
        bp.setCenter(t);

        stage.setScene(new Scene(bp));
        stage.setTitle("Tooltip");
        stage.setWidth(500);
        stage.setHeight(300);
        stage.show();
        
        // also fails with default values
        //setCss();
    }

    private void setCss() {
        String css = ".tooltip { -fx-show-delay: 500ms; }";
        applyStyleSheet(css);
    }

    private static String encode(String s) {
        if (s == null) {
            return null;
        }
        Charset utf8 = Charset.forName("utf-8");
        byte[] b = s.getBytes(utf8);
        return "data:text/css;base64," + Base64.getEncoder().encodeToString(b);
    }

    private static void applyStyleSheet(String styleSheet) {
        String ss = encode(styleSheet);
        if (ss != null) {
            for (Window w : Window.getWindows()) {
                Scene scene = w.getScene();
                if (scene != null) {
                    ObservableList<String> sheets = scene.getStylesheets();
                    if (oldStylesheet != null) {
                        sheets.remove(oldStylesheet);
                    }
                    sheets.add(ss);
                }
            }
        }
        oldStylesheet = ss;
    }
}