package goryachev.apps;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.skin.SpinnerSkin;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 *
 */
public class LeakTest extends Application {
    private int id;
    
    public static void main(String[] args) {
        Application.launch(LeakTest.class, args);
    }

    @Override
    public void start(final Stage stage) throws Exception {
        BorderPane rootPane = new BorderPane();
        
        Spinner t = new Spinner();

        Button testButton = new Button("Replace Skin");
        testButton.setOnAction(e -> {
            t.setSkin(new AA(t));
        });
        rootPane.setTop(testButton);
        rootPane.setCenter(new ScrollPane(t));
        stage.setScene(new Scene(rootPane, 800, 600));
        stage.setTitle("JDK-8293444 " + System.getProperty("java.version"));
        stage.show();
    }
    
    public static class AndyScrollPane extends ScrollPane {
        public AndyScrollPane(Node c) {
            super(c);
        }
    }
    
    protected static class AA extends SpinnerSkin {
        public AA(Spinner control) {
            super(control);
        }
    }
}
