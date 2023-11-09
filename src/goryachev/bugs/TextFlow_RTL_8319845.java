package goryachev.bugs;

import javafx.application.Application;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.HitInfo;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

/**
 * TextFlow in RTL mode
 * https://bugs.openjdk.org/browse/JDK-8319844
 */
public class TextFlow_RTL_8319845 extends Application {
    
    private Label status;
    private TextFlow control;

    @Override
    public void start(Stage stage) throws Exception {
        control = new TextFlow();
        control.getChildren().addAll(
            t("Arabic: العربية\n"),
            t("Hebrew: עברית")
        );
        control.addEventHandler(MouseEvent.ANY, this::handleMouseEvent);
        
        status = new Label();
        
        BorderPane bp = new BorderPane();
        bp.setTop(new Label("Move the mouse pointer across the text line from edge to edge -"));
        bp.setCenter(control);
        bp.setBottom(status);

        Scene scene = new Scene(bp);
        scene.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        stage.setScene(scene);
        stage.setTitle("TextFlow in RTL Mode");
        stage.setWidth(600);
        stage.setHeight(300);
        stage.show();
    }
    
    private Text t(String text) {
        Text t = new Text(text);
        t.setFont(Font.getDefault().font(24.0));
        return t;
    }

    private void handleMouseEvent(MouseEvent ev) {
        Point2D p = new Point2D(ev.getX(), ev.getY());
        HitInfo h = control.hitTest(p);
        status.setText("TextFlow.hitInfo " + h);
    }
}