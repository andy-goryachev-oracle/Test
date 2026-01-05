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

/// https://bugs.openjdk.org/browse/JDK-8366739
///
public class Toolbar_FractionalScale_8366739 extends Application {
    @Override
    public void start(Stage primaryStage) {
        ToolBar tb = new ToolBar(
            new Separator(Orientation.VERTICAL),
            new Button("Create Schema"));
        BorderPane bp = new BorderPane();
        bp.setTop(new HBox(tb));
        primaryStage.setScene(new Scene(bp, 600, 400));
        primaryStage.show();
    }
}