package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8337246
 */
public class Spinner_NonConsuming_8337246 extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        TextField textField = new TextField("Normal text field");
        textField.setOnAction((ev) -> {
            System.out.println("Normal action event: " + ev);
            ev.consume();
        });
        Spinner<Integer> integerSpinner = new Spinner<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 50));
        integerSpinner.getEditor().setOnAction((ev) -> {
            System.out.println("Spinner action event: " + ev);
            ev.consume();
        });
        integerSpinner.setEditable(true);
        VBox root = new VBox(textField, integerSpinner);
        root.setOnKeyPressed((ev) -> {
            System.out.println("Key pressed: " + ev);   
        });
        primaryStage.setScene(new Scene(root, 300, 300));
        primaryStage.show();
    }
}