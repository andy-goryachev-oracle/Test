package goryachev.bugs;

import java.nio.charset.Charset;
import java.util.Base64;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/*
 * https://bugs.openjdk.org/browse/JDK-8336389
 */
public class Css_InfiniteLoop_8336389 extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        String css = """
            .root {
                -fx-base-fill: ladder(-fx-base, white 49%, black 50%);
                -fx-base: ladder(-fx-base-fill, white 49%, black 50%);
            }

            .pane {
                -fx-background-color: -fx-base;
            }
            """;

        Application.setUserAgentStylesheet(encodeStylesheet(css));

        Pane p = new Pane();
        p.getStyleClass().add("pane");
        stage.setScene(new Scene(p));

        // This should not result in a StackOverflowError
        stage.show();
    }
    
    /** encode stylesheet to a data: url */
    private static String encodeStylesheet(String s) {
        if (s == null) {
            return null;
        }
        Charset utf8 = Charset.forName("utf-8");
        byte[] b = s.getBytes(utf8);
        return "data:text/css;base64," + Base64.getEncoder().encodeToString(b);
    }
}
