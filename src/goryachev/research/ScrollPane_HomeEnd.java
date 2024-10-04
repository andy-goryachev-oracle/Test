package goryachev.research;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ScrollPane_HomeEnd extends Application {

    @Override
    public void start(Stage stage) {
        VBox vbox = new VBox(
            new Label("Standard Buttons in ScrollPane"),
            new ScrollPane(
                new VBox(5, createButtons())
            )
        );

        Scene scene = new Scene(vbox);

        stage.setScene(scene);
        stage.show();
    }

    Button[] createButtons() {
        Button[] buttons = new Button[20];
        for (int i = 0; i < 20; i++) {
            buttons[i] = new Button("Button " + (i + 1));
        }
        return buttons;
    }
}