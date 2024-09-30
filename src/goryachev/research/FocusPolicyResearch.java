package goryachev.research;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Researching focus-related issues.
 */
public class FocusPolicyResearch extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Focus Policy Research");
        stage.setWidth(450);
        stage.setHeight(550);
        
        VBox p = new VBox(
            createPane(),
            new ScrollPane(createPane())
        );

        Scene scene = new Scene(p);
        stage.setScene(scene);
        stage.show();
    }

    Node createPane() {
        ToggleButton t1 = new ToggleButton("1");
        ToggleButton t2 = new ToggleButton("2");
        ToggleButton t3 = new ToggleButton("3");
        
        ToggleGroup g = new ToggleGroup();
        g.getToggles().addAll(
            t1,
            t2,
            t3
        );

        return new VBox(
            t1,
            t2,
            t3,
            new Button("Button")
        );
    }
}