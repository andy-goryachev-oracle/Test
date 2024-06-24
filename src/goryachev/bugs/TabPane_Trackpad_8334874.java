package goryachev.bugs;

import javafx.application.Application;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8334874
 */
public class TabPane_Trackpad_8334874 extends Application {

    private static final int NUM_TABS = 40;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createTabPane(), 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private TabPane createTabPane() {
        TabPane t = new TabPane();
        //t.setSide(Side.LEFT);
        for (int i = 0; i < NUM_TABS; ++i) {
            final Tab tab = new Tab(Integer.toString(1000 + i));
            t.getTabs().add(tab);
        }
        return t;
    }

}