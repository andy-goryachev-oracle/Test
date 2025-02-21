package goryachev.bugs;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8350149
 * but for HBox
 */
public class HBox_FillHeight_8350150 extends Application {
    @Override
    public void start(Stage stage) {
        Label label = new Label("Relatively long text that may need wrapping at some point") {
            @Override
            public Orientation getContentBias() {
                super.getContentBias();
                return Orientation.VERTICAL;
            }
        };
        label.setRotate(90);
        label.setWrapText(true); // make it get a horizontal bias

        HBox root = new HBox(label);

        root.setStyle("-fx-font-size: 50px");
        root.setFillHeight(false); // breaks label reflow

        Scene scene = new Scene(root, 500, 300);

        stage.setScene(scene);
        stage.setTitle("HBox");
        stage.show();
    }
}