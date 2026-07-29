package goryachev.research;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

// https://bugs.openjdk.org/browse/JDK-8389094
public class Css_FontFace extends Application {
    
    @Override
    public void start(Stage stage) {
        Label label = new Label("Hello @FontFace");
        label.setStyle("-fx-font-family: 'Playwrite NZ Guides'; -fx-font-size: 80;");
        Scene scene = new Scene(label);
        
        int choice = 0;
        
        String url;
        switch(choice) {
        case 0:
            url = "https://fonts.googleapis.com/css2?family=Playwrite+NZ+Guides&display=swap";
            break;
        case 1:
            url = dataUrl(
                """
                @font-face {
                  font-family: 'Playwrite NZ Guides';
                  font-style: normal;
                  font-weight: 400;
                  font-display: swap;
                  src: url('https://fonts.gstatic.com/s/playwritenzguides/v2/t5t8IQQPN4uFDRepJwiX4vzIikyGzv71WhoxrW5O.woff2') format('woff2');
                }
                """);
            break;
        default:
            throw new Error();
        }
        scene.getStylesheets().add(url);
        
        stage.setTitle("CSS @font-face");
        stage.setScene(scene);
        stage.show();
    }

    private static String dataUrl(String css) {
        return "data:text/css;base64," + Base64.getEncoder().encodeToString(css.getBytes(StandardCharsets.US_ASCII));
    }
}