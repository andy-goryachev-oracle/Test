package goryachev.bugs;

import java.nio.charset.Charset;
import java.util.Base64;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8352611
 */
public class ContextMenu_PrefWidth_8352611 extends Application {

    private static final String stylesheet =
        """
        data:text/css,

        .context-menu {
            //-fx-pref-width: 150;
            -fx-min-width: 150;
        }
        """;
    private static final String stylesheet2 =
        """
        data:text/css,
        .context-menu {
            -fx-max-width: 50;
        }
        """;

    @Override
    public void start(Stage stage) {
        Label label = new Label("Show ContextMenu");
        Menu menu = new Menu("Menu", null, new MenuItem("menu item 1"));
        ContextMenu contextMenu = new ContextMenu(menu, new MenuItem("menu item 2"));
        label.setOnContextMenuRequested(e -> contextMenu.show(stage, e.getScreenX(), e.getScreenY()));

        Scene scene = new Scene(new StackPane(label), 300, 200);
        scene.getStylesheets().add(stylesheet2);
        stage.setScene(scene);
        stage.show();
    }
    
    private static String encode(String s) {
        if (s == null) {
            return null;
        }
        Charset utf8 = Charset.forName("utf-8");
        byte[] b = s.getBytes(utf8);
        return "data:text/css;base64," + Base64.getEncoder().encodeToString(b);
    }
}