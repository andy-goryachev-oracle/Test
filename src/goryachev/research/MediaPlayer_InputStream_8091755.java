package goryachev.research;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

// https://bugs.openjdk.org/browse/JDK-8091755
//
public class MediaPlayer_InputStream_8091755 extends Application {
    @Override
    public void start(Stage stage) {
        
        // TODO
        boolean fromInputStream = true;
        String name = "/Users/angorya/Movies/test.mp4";
        File f = new File(name);
        String uri = f.toURI().toString();

        try {
            InputStream in = new BufferedInputStream(new FileInputStream(f));
            Media m =
                fromInputStream ?
                    new Media(f.getName(), in) :
                    new Media(uri);
            System.out.println(m.getMetadata());
            
            MediaPlayer p = new MediaPlayer(m);
            p.statusProperty().addListener((_,_,v) -> System.out.println("STATUS: " + v));
            
            MediaView v = new MediaView(p);
            
            BorderPane bp = new BorderPane(v);
            Scene scene = new Scene(bp, 500, 300);
            stage.setScene(scene);
            
            v.fitWidthProperty().bind(scene.widthProperty());
            stage.show();
            
            p.play();
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
