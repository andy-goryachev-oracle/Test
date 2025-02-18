package goryachev.bugs;

import java.io.File;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8350281
 */
public class MediaPlayer_MP4_8350281 extends Application {
    @Override
    public void start(Stage stage) {
        String name = 
            "test.mov";
//          "test.mp4";
        File f = new File(name);
        String uri = f.toURI().toString();

        try {
            Media m = new Media(uri);
            System.out.println(m.getMetadata());
            
            MediaPlayer p = new MediaPlayer(m);
            
            MediaView v = new MediaView(p);
            
            BorderPane bp = new BorderPane(v);
            Scene scene = new Scene(bp, 500, 300);
            stage.setScene(scene);
            
            v.fitWidthProperty().bind(scene.widthProperty());
            stage.show();
            
            p.setAutoPlay(true);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
