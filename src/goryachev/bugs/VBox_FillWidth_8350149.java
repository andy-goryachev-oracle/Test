package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8350149
 */
public class VBox_FillWidth_8350149 extends Application {
    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("Relatively long text that may need wrapping at some point");

        label.setWrapText(true); // make it get a horizontal bias

        VBox root = new VBox(label);

        root.setStyle("-fx-font-size: 50px");
        root.setFillWidth(false); // breaks label reflow (works as expected)

        Scene value = new Scene(root, 300, 500);

        primaryStage.setScene(value);
        primaryStage.show();
        
        System.out.println("prefw=" + label.prefWidth(-1));
    }
}