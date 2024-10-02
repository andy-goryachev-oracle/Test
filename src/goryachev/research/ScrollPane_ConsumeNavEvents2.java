package goryachev.research;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Custom component traversal.
 */
public class ScrollPane_ConsumeNavEvents2 extends Application {

    @Override
    public void start(Stage stage) {

        GridPane gp = new GridPane(10, 10);

        gp.add(new VBox(
            new Label("Standard Buttons in normal container"),
            new HBox(5, 
                new Button("A1"),
                new Button("B1"),
                new Button("C1"),
                new L("D1"),
                new L("E1")
            )
        ), 0, 0);
        
        ScrollPane sp = new ScrollPane(
            new HBox(5, 
                new Button("A2"),
                new Button("B2"),
                new Button("C2"),
                new L("D2"),
                new L("E2")
            )
        );
        //sp.setFocusTraversable(true);
        
        gp.add(new VBox(
            new Label("Standard Buttons in ScrollPane"),
            sp
        ), 1, 0);
        
        Scene scene = new Scene(gp);
        scene.focusOwnerProperty().addListener((s,p,c) -> {
            System.out.println(c);
        });

        stage.setScene(scene);
        stage.show();
    }
    
    static class L extends Label {
        public L(String s) {
            super(s);
            setFocusTraversable(true);
            setMouseTransparent(false);
        }
    }
}