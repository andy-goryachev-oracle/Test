package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8334995
 */
public class Tooltip_EdgeBackground_8334995 extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Tooltip tooltip = new Tooltip("This is a tooltip.");
        tooltip.getStyleClass().clear();
        tooltip.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        tooltip.setFont(new Font("Arial", 12));

        Button button = new Button("Hover me");
        button.setTooltip(tooltip);
        button.setOnMouseClicked((ev) -> {
            tooltip.show(stage);
            
            Label t = (Label)tooltip.getSkin().getNode();
            System.out.println("label.width: " + t.getWidth());
            System.out.println("tooltip.width: " + tooltip.getWidth());
            System.out.println("label.snapToPixel: " + t.isSnapToPixel());
        });

        Scene scene = new Scene(button);
        scene.setFill(Color.WHITE);

        stage.setScene(scene);
        stage.setWidth(600);
        stage.setHeight(600);
        stage.show();
    }
}