package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * https://bugs.openjdk.org/browse/JDK-8255835
 */
public class MaximizedStage_8255835 extends Application {
    public void start(Stage stage) {
        StackPane sp = new StackPane(new Label("Hello"));
        Color color = new Color(1.0, 0.0, 0.0, 1.0);
        BorderWidths borderWidths = new BorderWidths(2.0);
        BorderStroke stroke = new BorderStroke(color, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, borderWidths);
        sp.setBorder(new Border(stroke));
        stage.setScene(new Scene(sp, 500, 500));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
        stage.setMaximized(true);
    }
}