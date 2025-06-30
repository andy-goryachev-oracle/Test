package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Shortcut_CmdPlus_8360886 extends Application {
    private final TextArea messageArea = new TextArea("");
    private final Menu menu = new Menu("Test");
    private Scene scene = null;

    private void addMenuItem(String title, KeyCombination accelerator) {
        MenuItem item = new MenuItem(title);
        item.setAccelerator(accelerator);
        item.setOnAction(e -> {
            messageArea.appendText(title + " invoked\n");
        });
        menu.getItems().add(item);
    }

    private void addItem(String symbol) {
        String title = symbol + " item";
        KeyCombination accelerator = new KeyCharacterCombination(symbol, KeyCombination.SHORTCUT_DOWN);
        addMenuItem(title, accelerator);
    }

    @Override
    public void start(Stage stage) {
        messageArea.setEditable(false);

        addItem("+");
        addItem("<");

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(menu);

        VBox box = new VBox(menuBar, messageArea);
        box.setVgrow(messageArea, Priority.ALWAYS);
        scene = new Scene(box, 640, 640);

        stage.setScene(scene);
        stage.setTitle("Cmd Plus Test");
        stage.show();
    }
}