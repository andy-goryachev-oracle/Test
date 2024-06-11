package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.TextAreaSkin;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8284113
 */
public class TextArea_MousePressRelease_8284113 extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        var area = new TextArea();
        area.addEventHandler(MouseEvent.MOUSE_PRESSED, System.out::println);
        area.addEventHandler(MouseEvent.MOUSE_RELEASED, System.out::println);
        // unrelated
        // area.setSkin(new TextAreaSkin(area));
        
        var field = new TextField();
        field.addEventHandler(MouseEvent.MOUSE_PRESSED, System.out::println);
        field.addEventHandler(MouseEvent.MOUSE_RELEASED, System.out::println);
        
        var pane = new BorderPane();
        pane.setTop(field);
        pane.setCenter(area);
        stage.setScene(new Scene(pane));
        stage.show();
    }
}