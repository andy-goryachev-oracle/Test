package goryachev.research;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import goryachev.util.FX;

/// Positron
public class Positron extends Application {
    private WebView webView;
    private WebEngine engine;

    @Override
    public void start(Stage stage) throws Exception {
        webView = new WebView();
        engine = webView.getEngine();
        engine.setOnError((ev) -> {
            System.out.println("onError:" + ev);
        });
        engine.setOnStatusChanged((ev) -> {
            System.out.println("onStatusChanged:" + ev);
        });
        engine.getLoadWorker().stateProperty().addListener((s, p, c) -> {
            System.out.println("state:" + c);
        });
        
        MenuBar mb = new MenuBar();
        Menu m = FX.menu(mb, "File");
        FX.item(m, "Reload", this::load);
        FX.separator(m);
        FX.item(m, "Quit", this::quit);

        BorderPane bp = new BorderPane();
        bp.setTop(mb);
        bp.setCenter(webView);

        stage.setScene(new Scene(bp));
        stage.setTitle("Positron");
        stage.show();
        
        Platform.runLater(this::load);
    }

    private void load() {
        engine.load("http://127.0.0.1:8085/notes");
    }
    
    private void quit() {
        System.exit(0);
    }
    
    public static void main(String[] args) throws Throwable {
        Application.launch(Positron.class, args);
    }
}