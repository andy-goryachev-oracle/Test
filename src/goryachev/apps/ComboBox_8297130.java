package goryachev.apps;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ComboBox_8297130 extends Application {

    ComboBox<String> comboBox;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {

        comboBox = new ComboBox<>();
        comboBox.showingProperty().addListener((observable1, oldValue1, newValue1) -> System.out.println("showing: " + newValue1));
        comboBox.focusedProperty().addListener((observable1, oldValue1, newValue1) -> System.out.println("focused: " + newValue1));

        //try different cases
// case1(); //no bug
// case2(); //no bug
        case3(); //bug
// case4(); //no bug
// case5(); //bug

        Scene scene = new Scene(new HBox(comboBox), 320, 240);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * no bug
     */
    private void case1() {
        comboBox.getItems().addAll("case1");
    }

    /**
     * no bug
     */
    private void case2() {
        Platform.runLater(() -> comboBox.getItems().addAll("case2", "case2_"));
    }

    /**
     * bug
     */
    private void case3() {
        Platform.runLater(() -> comboBox.getItems().add("case3"));
    }

    /**
     * no bug
     */
    private void case4() {
        comboBox.getItems().addAll("case4", "case4_");
        Platform.runLater(() -> comboBox.getItems().remove("case4"));
    }

    /**
     * bug
     */
    private void case5() {
        comboBox.getItems().addAll("case5", "case5_");
        Platform.runLater(() -> {
            comboBox.getItems().clear();
            comboBox.getItems().add("case5");
        });
    }
}