package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * WebView: setting page fill has no effect.
 * https://bugs.openjdk.org/browse/JDK-8333304
 */
public class WebView_PageFill_8333304 extends Application {
    private WebView webView;
    private WebEngine engine;

    @Override
    public void start(Stage stage) throws Exception {
        webView = new WebView();
        webView.setOpacity(1.0); // setting opacity != 1.0 has no effect
        webView.setStyle("-fx-background-color:green;"); // nothing
        // there is no background property, WebView is a Parent

        engine = webView.getEngine();
        engine.setOnError((ev) -> {
            System.err.println("onError:" + ev);
        });
        engine.setOnStatusChanged((ev) -> {
            System.err.println("onStatusChanged:" + ev);
        });
        engine.getLoadWorker().stateProperty().addListener((s, p, c) -> {
            System.err.println("state:" + c);
        });

        ToolBar tb = new ToolBar();
        tb.getItems().add(button("TogglePageFill", this::setBackground));
        tb.getItems().add(button("LoadHTML", this::loadHtml));

        BorderPane bp = new BorderPane();
        bp.setTop(tb);
        bp.setCenter(webView);

        //webView.setPageFill(Color.BLUE);
        //engine.loadContent("<html></html>");

        Scene scene = new Scene(bp);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("JDK-8333304");
    }

    private Button button(String name, Runnable action) {
        Button b = new Button(name);
        b.setOnAction((ev) -> action.run());
        return b;
    }

    private void setBackground() {
        boolean red = !Color.RED.equals(webView.getPageFill());
        webView.setPageFill(red ? Color.RED : Color.BLACK);
    }

    private void loadHtml() {
        engine.loadContent("<html></html>");
    }
}