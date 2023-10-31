package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MaximizedStage_8255835 extends Application {
    public void start(Stage stage) {
        StackPane sp = new StackPane(new Label("Hello"));
        stage.setScene(new Scene(sp, 500, 500));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
        stage.setMaximized(true);
    }
}