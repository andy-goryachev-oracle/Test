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
import javafx.stage.Stage;

/**
 * Text in RTL mode
 * https://bugs.openjdk.org/browse/JDK-8319844
 */
public class Text_RTL_8319844 extends Application {
    
    private Label status;
    private Text control;

    @Override
    public void start(Stage stage) throws Exception {
        control = new Text();
        control.setFont(Font.getDefault().font(24.0));
        control.setText("Arabic: العربية\nHebrew: עברית");
        control.addEventHandler(MouseEvent.ANY, this::handleMouseEvent);
        
        status = new Label();

        BorderPane bp = new BorderPane();
        bp.setTop(new Label("Move the mouse pointer across the text from edge to edge -"));
        bp.setCenter(control);
        bp.setBottom(status);

        Scene scene = new Scene(bp);
        scene.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

        stage.setScene(scene);
        stage.setTitle("Text in RTL Mode");
        stage.setWidth(600);
        stage.setHeight(300);
        stage.show();
    }

    private void handleMouseEvent(MouseEvent ev) {
        Point2D p = new Point2D(ev.getX(), ev.getY());
        HitInfo h = control.hitTest(p);
        status.setText("Text.hitTest: " + h);
    }
}