package goryachev.bugs;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.skin.MenuBarSkin;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8355615
 */
public class MenuBar_SystemMenuBackgroundThread_8355615 extends Application {

    @Override
    public void start(Stage stage) {
        //new Thread(this::create).start();
        create();
    }

    private void create() {
        Menu mn = new Menu("Menu");
        mn.getItems().setAll(
            new MenuItem("Menu1"),
            new MenuItem("Menu2")
        );

        MenuBar mb = new MenuBar();
        mb.setUseSystemMenuBar(true);
        // uncomment this and it fails to create the system menu
        // mb.setSkin(new MenuBarSkin(mb));
        mb.getMenus().add(mn);

        BorderPane bp = new BorderPane();
        bp.setTop(mb);
        Scene sc = new Scene(bp, 400, 400);

        Platform.runLater(() -> {
            show(sc);
        });
    }

    private void show(Scene sc) {
        Stage s = new Stage();
        s.setScene(sc);
        s.setTitle("System Menu");
        s.show();
    }
}