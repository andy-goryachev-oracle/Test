package goryachev.bugs;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import jfx.incubator.scene.control.richtext.RichTextArea;

/**
 * 
 */
public class TextArea_PreferredSize extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Control t =
            //new TextArea("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            //new TextArea();
            new RichTextArea();
        
        Scene scene =
            //new Scene(t);
            new Scene(new StackPane(t));
        
        stage.setScene(scene);
        stage.setTitle(t.getClass().getSimpleName() + " Preferred Size");
        stage.show();
        
        Platform.runLater(() -> {
            double w = t.prefWidth(-1);
            double h = t.prefHeight(-1);
            System.out.println(String.format("%s prefWidth=%s prefHeight=%s",
                System.getProperty("os.name"),
                w,
                h
            ));
        });
    }
}