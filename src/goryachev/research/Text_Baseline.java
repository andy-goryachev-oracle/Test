package goryachev.research;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * 
 */
public class Text_Baseline extends Application {
    @Override
    public void start(Stage stage) {
        Text t = new Text();

        stage.setScene(new Scene(new Group(t)));
        stage.setWidth(400);
        stage.setHeight(200);
        stage.show();
        
        // unconnected
        t = new Text();
        t.getBaselineOffset();
        System.out.println(t.getBaselineOffset());
        System.out.println(Font.getDefault().equals(t.getFont()));
        Font f = new Font(44);
        t.setFont(f);
        System.out.println(t.getBaselineOffset());
        var p = new Point2D(0, 0);
        t.hitTest(p);
    }
}