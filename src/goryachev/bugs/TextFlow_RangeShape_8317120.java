package goryachev.bugs;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8317120
 */
public class TextFlow_RangeShape_8317120 extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        String text = "TextFlow{lineSpacing=20}\nline two\nline three\nline four";
        TextFlow t = new TextFlow(new Text(text));
        t.setLineSpacing(20);
        
        Path path = new Path();
        path.setManaged(false);
        path.setFill(Color.rgb(255, 255, 128, 0.5));
        path.setStroke(Color.rgb(128, 128, 0));
        
        Label label = new Label("Next Component Starts Here");
        label.setOpacity(1.0);
        label.setBackground(Background.fill(Color.DARKGRAY));
        
        VBox vb = new VBox();
        vb.getChildren().addAll(path, t, label);
        VBox.setVgrow(label, Priority.ALWAYS);

        Scene scene = new Scene(vb);
        stage.setScene(scene);
        stage.setWidth(400);
        stage.setHeight(200);
        stage.setTitle("TextFlow.rangeShape() ignores lineSpacing");
        stage.show();
        
        Platform.runLater(() -> {
            PathElement[] pe = t.rangeShape(0, text.length());
            path.getElements().setAll(pe);
        });
    }
}