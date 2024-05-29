package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8332938
 */
public class ComboBox_CommitLate_8332938 extends Application {

    @Override
    public void start(Stage primaryStage) {
        var comboBox = new ComboBox<String>();
        comboBox.setEditable(true);
        comboBox.valueProperty().addListener((obs, ov, nv) -> {
            System.out.printf("ComboBox.onValueChanged: %s%n", nv);
        });

        var defaultButton = new Button("Default button");
        defaultButton.setDefaultButton(true);
        defaultButton.setOnAction(e -> {
            var value = comboBox.getValue();
            System.out.printf("Button.onAction: %s%n", value);
        });

        primaryStage.setScene(new Scene(new VBox(comboBox, defaultButton)));
        primaryStage.show();
    }
}