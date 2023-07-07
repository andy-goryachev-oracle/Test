package goryachev.bugs;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class SpinnerTest extends Application {

    private final Spinner<Double> spinner = new Spinner<>(0.0, 10.0, 0.0);

    private final HBox root = new HBox(spinner);

    @Override
    public void init() throws Exception {
        root.setAlignment(Pos.CENTER);
        spinner.setEditable(true);
        spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            // There should be no negative values
            System.out.println("newValue = " + newValue);
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle(getClass().getSimpleName());
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
} 