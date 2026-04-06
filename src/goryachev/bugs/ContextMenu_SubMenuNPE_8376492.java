package goryachev.bugs;

import java.util.ArrayList;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.skin.ContextMenuSkin;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ContextMenu_SubMenuNPE_8376492 extends Application {
    
    private ArrayList<MenuItem> items = new ArrayList<>();
    private int seq;

    @Override
    public void start(Stage stage) throws Exception {
        MenuBar menuBar = new MenuBar(new Menu("Main menu", null, new MenuItem("dummy"), createSubMenu()));

        ContextMenu cm = new ContextMenu();
        cm.getItems().addAll(new MenuItem("dummy"), createSubMenu());
        cm.setSkin(new ContextMenuSkin(cm));
        
        BorderPane root = new BorderPane();

        root.setTop(menuBar);
        Label label = new Label("open context menu here");
        label.setContextMenu(cm);
        root.setCenter(label);
        
        stage.setScene(new Scene(root));
        stage.addEventFilter(KeyEvent.KEY_PRESSED, (ev) -> {
            update();
        });
        stage.show();
    }

    private void update() {
        for(MenuItem mi: items) {
            update(mi);
        }
    }
    
    private Menu createSubMenu() {
        Menu menu = new Menu("menu...");

        MenuItem item = new MenuItem("item");
        menu.getItems().add(item);
        items.add(item);

        menu.setOnShown(event -> {
            Platform.runLater(() -> {
                update(item);
            });
        });
        return menu;
    }
    
    private void update(MenuItem item) {
        // Update the graphic while the menu is visible
        item.setGraphic(new Label("graphic " + seq));

        // BUG: NullPointer
        item.getStyleClass().removeAll("test" + seq);
        seq++;
        item.getStyleClass().add("test" + seq);
    }
}