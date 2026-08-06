package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

// https://bugs.openjdk.org/browse/JDK-8386859
public class WebView_TextureMapper_8386859 extends Application {

    @Override
    public void start(Stage primaryStage) {
        WebView webView = new WebView();
        WebEngine engine = webView.getEngine();
        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                engine.executeScript(
                    """
                    if (document.startViewTransition) {
                      document.startViewTransition(function() {
                      });
                    }
                    """);
            }
        });
        engine.loadContent("<html></html>", "text/html");

        Scene scene = new Scene(webView, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}