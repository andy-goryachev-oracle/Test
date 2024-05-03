package goryachev.tests;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TruncatedTest extends Application {

    private static final String clickMe = "----- Click me";
    private static final String clickMeAgain = "------ Click me again, please";

    @Override public void start(Stage stage) {
        stage.setTitle("Truncated Button Text (fails)");

        var button = new Button(clickMe);
        button.setPrefWidth(150);
        button.setOnAction(e -> {
            button.setText(clickMe.equals(button.getText()) ? clickMeAgain : clickMe);
        });

        var root = new VBox(10);
        Scene scene = new Scene(root, 600, 450);
        root.getChildren().add(button);

        var label = new Label();
        //label.textProperty().bind(button.textTruncatedProperty().asString());
        root.getChildren().add(label);

        stage.setScene(scene);
        stage.show();
    }
}