package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

// https://bugs.openjdk.org/browse/JDK-8389582
// ⌨ = \u2328
public class MenuItem_Accelerator_8389582 extends Application {

    @Override
    public void start(Stage stage) {
        MenuItem item = new MenuItem("Test");
        item.setAccelerator(new KeyCodeCombination(KeyCode.ADD));
        item.setOnAction(e -> System.out.println("NumPad + pressed"));

        Menu menu = new Menu("Menu");
        menu.getItems().add(item);

        MenuBar menuBar = new MenuBar(menu);

        BorderPane root = new BorderPane();
        root.setTop(menuBar);

        stage.setScene(new Scene(root, 400, 200));
        stage.show();
    }
}