package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 *
 */
public class JDK_8293444 extends Application {
    private int id;
    
    public static void main(String[] args) {
        Application.launch(JDK_8293444.class, args);
    }

    @Override
    public void start(final Stage stage) throws Exception {
        Label f = new Label("Test label");
        f.setMinHeight(1000);
        f.setMinWidth(1000);

        BorderPane rootPane = new BorderPane();

        Button testButton = new Button("test");
        testButton.setOnAction(e -> {
            rootPane.setCenter(new AndyScrollPane(f));
            f.setText("test label " + id++);
            f.setMinWidth(1000 * id);
        });
        rootPane.setTop(testButton);
        rootPane.setCenter(new ScrollPane(f));
        stage.setScene(new Scene(rootPane, 800, 600));
        stage.setTitle("JDK-8293444 " + System.getProperty("java.version"));
        stage.show();
    }
    
    public static class AndyScrollPane extends ScrollPane {
        public AndyScrollPane(Node c) {
            super(c);
        }
    }
}
