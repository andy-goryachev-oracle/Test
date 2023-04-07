package goryachev.apps;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * JDK-8296387 [Tooltip, CSS] -fx-show-delay is only applied to the first tooltip that is shown before it is displayed
 */
public class X extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Label t = new Label("100");
        t.setOpacity(1.0);
        t.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
        
        Scene scene = new Scene(t, 300, 250);
        scene.getStylesheets().add(X.class.getResource("HelloTooltip.css").toExternalForm());
        primaryStage.setScene(scene);

        primaryStage.show();
    }
}
