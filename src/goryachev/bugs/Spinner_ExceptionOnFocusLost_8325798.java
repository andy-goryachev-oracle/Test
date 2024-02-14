package goryachev.bugs;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8325798
 */
public class Spinner_ExceptionOnFocusLost_8325798 extends Application {
    @Override
    public void start(final Stage primaryStage) throws Exception {
        Spinner<Integer> spinner = new Spinner<>(new IntegerSpinnerValueFactory(0, 10, 0));
        spinner.setEditable(true);

        Button button = new Button("Focus me");
        final Scene sc = new Scene(new HBox(spinner, button));
        primaryStage.setScene(sc);
        primaryStage.show();
    }
}