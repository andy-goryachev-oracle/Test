package goryachev.bugs;

import java.util.List;
import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8351094
 */
public class MenuBar_SystemMenuInvisible_8351094 extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Do you see the system menu?");

        SimpleBooleanProperty custom = new SimpleBooleanProperty();

        CheckMenuItem useSystem = new CheckMenuItem("Use System Menu");

        CheckMenuItem useCustom = new CheckMenuItem("Use Custom Menu Item");
        useCustom.selectedProperty().bindBidirectional(custom);

        Menu m = new Menu("Menu");
        m.getItems().setAll(
            useSystem,
            useCustom);

        MenuBar mb = new MenuBar();
        mb.getMenus().add(m);

        BorderPane bp = new BorderPane();
        bp.setTop(mb);
        stage.setScene(new Scene(bp, 800, 500));

        useSystem.selectedProperty().bindBidirectional(mb.useSystemMenuBarProperty());

        custom.addListener((s, p, on) -> {
            if (on) {
                m.getItems().add(new CustomMenuItem(new Button("Custom Menu Item")));
            } else {
                List<MenuItem> ms = m.getItems();
                for (int i = ms.size() - 1; i >= 0; i--) {
                    MenuItem mi = ms.get(i);
                    if (mi instanceof CustomMenuItem) {
                        ms.remove(i);
                    }
                }
            }
        });

        stage.show();
    }
}