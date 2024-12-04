package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8345536
 */
public class MenuBar_Accessibility_8345536 extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Sample");

        MenuBar mb = new MenuBar();

        Menu m = new Menu("File");
        m.getItems().add(new MenuItem("yo"));
        mb.getMenus().add(m);
        
        BorderPane bp = new BorderPane();
        bp.setTop(mb);
        
        stage.setScene(new Scene(bp, 800, 500));
        stage.show();
    }
}