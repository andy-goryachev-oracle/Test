package goryachev.apps;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// https://bugs.openjdk.org/browse/JDK-8296653
public class ComboBoxPromptTextIssue extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPromptText("Select Value");
        comboBox.getItems().addAll("One", "Two", "Three", "Four");

        Button clear = new Button("Clear value");
        clear.setOnAction(e -> comboBox.setValue(null));

        VBox root = new VBox(comboBox, clear);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(20);

        Scene scene = new Scene(root, 600, 200);
        stage.setTitle("ComboBox FX " + System.getProperty("javafx.runtime.version"));
        stage.setScene(scene);
        stage.show();
    }
}