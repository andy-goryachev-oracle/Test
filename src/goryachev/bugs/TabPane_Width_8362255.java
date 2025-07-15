package goryachev.bugs;

import javafx.application.Application;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8362255
 */
public class TabPane_Width_8362255 extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        
        TabPane tabPane = new TabPane();
        tabPane.setMaxWidth(200);
        Tab tab = new Tab("aaaaaaaaa");
        tabPane.getTabs().add(tab);
        
        Scene scene = new Scene(tabPane, 400, 300);
        // .tab-container is not specified in CSS Ref.
        //
        scene.getStylesheets().add(
            """
            data:text/css,
            .tab-pane .tab-container { 
                -fx-max-width:50; -fx-pref-width:50;
                -fx-background-color:red; 
            }
            """);
        stage.setScene(scene);
        stage.show();
    }
}