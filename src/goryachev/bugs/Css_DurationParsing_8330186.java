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
 * CSS duration Parsing
 * https://bugs.openjdk.org/browse/JDK-8330186
 */
public class Css_DurationParsing_8330186 extends Application {
    private static String oldStylesheet;

    @Override
    public void start(Stage stage) throws Exception {
        Label t = new Label("Hover to show the tooltip");
        t.setMaxHeight(Double.MAX_VALUE);
        t.setMaxWidth(Double.MAX_VALUE);
        t.setTooltip(new Tooltip("And here comes the tooltip..."));
        t.setAlignment(Pos.CENTER);
        
        ToolBar tb = new ToolBar();
        {
            Button b = new Button("Set 1 (incorrect)");
            b.setOnAction((ev) -> setCss(false));
            tb.getItems().add(b);
        }
        {
            Button b = new Button("Set 1s (correct)");
            b.setOnAction((ev) -> setCss(true));
            tb.getItems().add(b);
        }
        
        BorderPane bp = new BorderPane();
        bp.setTop(tb);
        bp.setCenter(t);

        stage.setScene(new Scene(bp));
        stage.setTitle("CSS Parsing");
        stage.setWidth(500);
        stage.setHeight(300);
        stage.show();
    }

    private void setCss(boolean correct) {
        String css =
            ".tooltip { -fx-show-delay:" +
            (correct ? "1s" : "1") +
            "; }";
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