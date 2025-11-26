package goryachev.bugs;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8372453
 */
public class Stage_OwnerIconify_8372453 extends Application {
    private static final int WIDTH = 200;
    private static final int HEIGHT = 200;
    private static final Color COLOR = Color.LIGHTBLUE;

    private void setupStage(Stage stage, int count) {
        Scene scene = new Scene(getFocusedLabel(COLOR, stage), WIDTH, HEIGHT);
        scene.setFill(COLOR);
        stage.setScene(scene);
        if (count > 0) {
            stage.setOnShown(e -> {
                Stage ownedStage = new Stage();
                ownedStage.initOwner(stage);
                setupStage(ownedStage, count - 1);
                ownedStage.setX(stage.getX() + 25);
                ownedStage.setY(stage.getY() + 25);
                ownedStage.show();
            });
        }
    }

    @Override
    public void start(Stage primaryStage) {
        setupStage(primaryStage, 2);
        primaryStage.show();
    }

    private static StackPane getFocusedLabel(Color c, Stage stage) {
        Label label = new Label();
        label.textProperty().
            bind(Bindings.when(stage.focusedProperty()).
            then("Focused").
            otherwise("Unfocused"));

        StackPane pane = new StackPane(label);
        pane.setBackground(Background.EMPTY);

        double luminance = 0.2126 * c.getRed() + 0.7152 * c.getGreen() + 0.0722 * c.getBlue();
        Color textColor = luminance < 0.5 ? Color.WHITE : Color.BLACK;
        label.setTextFill(textColor);
        return pane;
    }
}