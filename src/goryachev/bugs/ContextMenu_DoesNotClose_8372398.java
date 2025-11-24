package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8372398
 */
public class ContextMenu_DoesNotClose_8372398 extends Application {

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        // ----- View 1 and View 2
        Label label = new Label("View one: right click for context menu");
        label.setId("labelWithContextMenu");
        Label label2 = new Label("View two");
        root.setCenter(label);

        // ----- ContextMenu
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setId("contextMenu");
        MenuItem item = new MenuItem("Action of view one");
        item.setOnAction(event -> {
            if (root.getCenter() != label) {
                throw new IllegalStateException("""
                    The view one that the action belongs to is no longer visible!
                    
                    JavaFX Bug: it was expected that, when a system menu item was pressed, the context menu would be hidden.
                    """);
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Action of view one executed successfully").show();
            }
        });
        contextMenu.getItems().add(item);
        label.setContextMenu(contextMenu);

        // ----- Main Menu
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("View");
        menuBar.getMenus().add(menu);
        MenuItem item1 = new MenuItem("View one");
        MenuItem item2 = new MenuItem("View two");
        item1.setOnAction(event -> {
            root.setCenter(label);
        });
        item2.setOnAction(event -> {
            root.setCenter(label2);
        });
        menu.getItems().add(item1);
        menu.getItems().add(item2);
        menuBar.setUseSystemMenuBar(true);
        root.setTop(menuBar);

        // ----- Scene
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setWidth(300);
        stage.setHeight(200);
        stage.show();
    }
}