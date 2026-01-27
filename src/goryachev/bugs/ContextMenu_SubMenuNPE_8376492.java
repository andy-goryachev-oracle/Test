package goryachev.bugs;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ContextMenu_SubMenuNPE_8376492 extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        MenuBar menuBar = new MenuBar(new Menu("Main menu", null, new MenuItem("dummy"), createSubMenu()));

        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(new MenuItem("dummy"), createSubMenu());

        BorderPane root = new BorderPane();

        root.setTop(menuBar);
        Label label = new Label("open context menu here");
        label.setContextMenu(contextMenu);
        root.setCenter(label);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private Menu createSubMenu() {
        Menu menu = new Menu("menu...");

        MenuItem item = new MenuItem("item");
        menu.getItems().add(item);

        menu.setOnShown(event -> {
            Platform.runLater(() -> {
                // Update the graphic while the menu is visible
                item.setGraphic(new Label("graphic"));

                // BUG: NullPointer
                item.getStyleClass().removeAll("test");
                item.getStyleClass().add("test");
            });
        });
        return menu;
    }
}