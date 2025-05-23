package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Spinner;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8328701
 */
public class Spinner_SetValue_8328701 extends Application {
    int count = 0;

    @Override
    public void start(Stage stage) {
        Spinner<Integer> spinner = new Spinner<>(0, 100, 5, 1);
        spinner.valueProperty().addListener(o -> {
            System.out.println("valueProperty=" + spinner.getValue());
            count++;
        });
        spinner.setEditable(true);
        spinner.getEditor().setText("3");
        spinner.commitValue();

        System.out.println(count);
        System.out.println(spinner.getValue());

        VBox vBox = new VBox(spinner);

        Scene scene = new Scene(vBox, 400, 200);
        stage.setScene(scene);
        stage.show();
    }
}