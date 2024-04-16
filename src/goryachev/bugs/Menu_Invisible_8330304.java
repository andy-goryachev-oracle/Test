package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * When some menu is invisible then menu bar doesn't work correctly when user uses keyboard left/right arrows to navigate.
 * https://bugs.openjdk.org/browse/JDK-8330304
 */
public class Menu_Invisible_8330304 extends Application {
    @Override
    public void start(Stage primaryStage) {
        var menu0 = new Menu("M0");
        menu0.getItems().addAll(new MenuItem("1"), new MenuItem("2"), new MenuItem("3"));

        var menu1 = new Menu("M1");
        menu1.getItems().addAll(new MenuItem("1"), new MenuItem("2"), new MenuItem("3"));

        var menu2 = new Menu("M2");
        menu2.getItems().addAll(new MenuItem("1"), new MenuItem("2"), new MenuItem("3"));

        var menu3 = new Menu("M3");
        menu3.getItems().addAll(new MenuItem("1"), new MenuItem("2"), new MenuItem("3"));

        var menu4 = new Menu("M4");
        menu4.getItems().addAll(new MenuItem("1"), new MenuItem("2"), new MenuItem("3"));

        VBox root = new VBox();
        root.getChildren().addAll(new MenuBar(menu0, menu1, menu2, menu3, menu4));
        menu2.setVisible(false);

        var scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}