package goryachev.bugs;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

/**
 * 
 */
public class Node_LocalToScreen extends Application {
    private TextFlow flow;
    private Label top;

    @Override
    public void start(Stage stage) throws Exception {

        String text = "TextFlow{lineSpacing=20}\nline two\nline three\nline four";
        flow = new TextFlow(new Text(text));
        flow.setBorder(createBorder(33));

        top = new Label("Top");

        BorderPane bp = new BorderPane();
        bp.setTop(top);
        bp.setCenter(flow);

        Scene scene = new Scene(bp);
        stage.setScene(scene);
        stage.setWidth(400);
        stage.setHeight(200);
        stage.setTitle("Local to Screen");
        test();

        stage.show();

        Platform.runLater(() -> {
            test();
        });
    }

    private static Border createBorder(double width) {
        Color color = Color.TRANSPARENT;
        BorderStrokeStyle style = BorderStrokeStyle.SOLID;
        CornerRadii radii = null;
        BorderWidths widths = new BorderWidths(width);
        return new Border(new BorderStroke(color, style, radii, widths));
    }

    private void test() {
        System.out.println("top localToScreen=" + top.localToScreen(0, 0));
        System.out.println("flow localToScreen=" + flow.localToScreen(0, 0));
    }
}