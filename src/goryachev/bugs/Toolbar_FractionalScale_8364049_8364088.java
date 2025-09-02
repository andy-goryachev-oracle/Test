package goryachev.bugs;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8364049
 * https://bugs.openjdk.org/browse/JDK-8364088
 */
public class Toolbar_FractionalScale_8364049_8364088 extends Application {

    @Override
    public void start(Stage primaryStage) {
        ToolBar tb = new ToolBar(
            new Separator(Orientation.VERTICAL),
            new Button("Create Schema")
        );

        BorderPane bp = new BorderPane();
        bp.setTop(new HBox(tb));
        primaryStage.setScene(new Scene(bp, 600, 400));
        primaryStage.show();
    }
    
    public void start2(Stage stage) {
        ToolBar tb = new ToolBar(new Button("Create Schema"));

        BorderPane bp = new BorderPane();
        bp.setTop(new HBox(tb));
        
        stage.setScene(new Scene(bp, 600, 400));
        stage.setTitle(getClass().getSimpleName());
        stage.show();
    }
}