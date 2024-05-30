package goryachev.bugs;

import java.util.List;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8333275
 */
public class ComboBox_AddingFromEditor_8333275 extends Application {
    @Override
    public void start(Stage stage) {
        var list = List.of("A", "B", "C");
        var comboBox = new ComboBox<String>();
        comboBox.setEditable(true);
        comboBox.setItems(FXCollections.observableArrayList(list));

        var button = new Button("Push Me");
        button.setOnAction(e -> {
            String v = comboBox.getValue();
            comboBox.getItems().add(0, v);
        });

        Scene scene = new Scene(new VBox(comboBox, button), 400, 200);
        stage.setScene(scene);
        stage.show();
    }
}