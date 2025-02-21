package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import goryachev.util.HorizontalLabel;

/**
 * https://bugs.openjdk.org/browse/JDK-8350149
 * The original reproducer for VBox.
 */
public class VBox_FillWidth_8350149 extends Application {
    @Override
    public void start(Stage stage) {
        //Label label = new Label("Relatively long text that may need wrapping at some point");
        HorizontalLabel label = new HorizontalLabel("Relatively long text that may need wrapping at some point");

        label.setWrapText(true); // make it get a horizontal bias
        //label.setPrefWidth(50);

        VBox root = new VBox(label);

        root.setStyle("-fx-font-size: 50px");
        root.setFillWidth(false); // breaks label reflow

        Scene scene = new Scene(root, 300, 500);
        //label.prefWidthProperty().bind(value.widthProperty());

        stage.setScene(scene);
        stage.setTitle("VBox");
        stage.show();
        
        System.out.println("prefw=" + label.prefWidth(-1));
    }
}