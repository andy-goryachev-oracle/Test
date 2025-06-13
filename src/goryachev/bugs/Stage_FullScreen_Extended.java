package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * 
 */
public class Stage_FullScreen_Extended extends Application {
    @Override
    public void start(Stage stage) {
        var root = new BorderPane();
        root.setTop(new Label("yo"));
        //root.setTop(new javafx.scene.layout.HeaderBar());
        
        Button btnFullScreen = new Button("FullScreen");
        btnFullScreen.setOnAction((_) -> stage.setFullScreen(!stage.isFullScreen()));
        root.setCenter(btnFullScreen);

        stage.setScene(new Scene(root, 640, 480));
        //stage.initStyle(StageStyle.EXTENDED);
        //stage.setResizable(false);
        stage.show();
    }
}