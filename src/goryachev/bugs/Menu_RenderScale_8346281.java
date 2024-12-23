package goryachev.bugs;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8346281
 */
public class Menu_RenderScale_8346281 extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        MenuItem item = new MenuItem("Open");
        Menu menu = new Menu("File", null, item);
        MenuBar menuBar = new MenuBar(menu);
        Label label = new Label("Hello World");
        label.textProperty().bind(Bindings.format("Scale %.1f", stage.renderScaleXProperty().multiply(100)));
        BorderPane root = new BorderPane(label);
        root.setTop(menuBar);
        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.show();
    }
}