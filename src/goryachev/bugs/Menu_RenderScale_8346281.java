package goryachev.bugs;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8346281
 */
public class Menu_RenderScale_8346281 extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        for (Screen screen: Screen.getScreens()) {
            open(screen);
        }
    }

    private void open(Screen screen) {
        Rectangle2D r = screen.getVisualBounds();
        System.out.println(r); // FIX

        MenuItem item = new MenuItem("Open");
        Menu menu = new Menu("File", null, item);
        MenuBar menuBar = new MenuBar(menu);
        Label label = new Label("Hello World");
        BorderPane root = new BorderPane(label);
        root.setTop(menuBar);

        Scene scene = new Scene(root, 400, 300);

        Stage stage = new Stage();
        stage.setTitle("Menu Scale");
        stage.setScene(scene);
        double x = r.getMinX() + (r.getMaxX() - r.getMinX() - scene.getWidth()) / 2;
        double y = r.getMinY() + (r.getMaxY() - r.getMinY() - scene.getHeight()) / 2;
        stage.setX(x);
        stage.setY(y);
        label.textProperty().bind(Bindings.format("Scale %.1f", stage.renderScaleXProperty().multiply(100)));

        stage.show();
    }
}