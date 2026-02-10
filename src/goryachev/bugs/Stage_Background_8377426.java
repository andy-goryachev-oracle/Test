package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/// https://bugs.openjdk.org/browse/JDK-8377426
public class Stage_Background_8377426 extends Application {
    @Override
    public void start(Stage stage) {
        Label t = new Label("yo");
        t.setMaxWidth(100);
        t.setMaxHeight(50);
        t.setBackground(Background.fill(Color.YELLOW));
        
        Scene sc = new Scene(new Group(t), 640, 480);
        sc.setFill(Color.rgb(55, 0, 0, 0.2));
        stage.setScene(sc);
        stage.show();
    }
}