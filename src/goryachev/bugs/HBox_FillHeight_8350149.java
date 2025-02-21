package goryachev.bugs;

import javafx.application.Application;
import javafx.geometry.VerticalDirection;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import goryachev.util.VerticalLabel;

/**
 * https://bugs.openjdk.org/browse/JDK-8350149
 * but for HBox
 */
public class HBox_FillHeight_8350149 extends Application {
    @Override
    public void start(Stage stage) {
        VerticalLabel label = new VerticalLabel(VerticalDirection.DOWN, "Relatively long text that may need wrapping at some point") ;
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