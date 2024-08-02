package goryachev.tests;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8325445
 */
public class ColorSpaceTest_8325445 extends Application {
    // Construct an array of rects where each row
    // traverses one of the edges of the RGB color
    // cube.
    private double mix(double a, double b, double mixValue) {
        return a * (1.0 - mixValue) + b * mixValue;
    }

    private HBox createColorRow(Color start, Color end, int numColumns) {
        double startRed = start.getRed();
        double endRed = end.getRed();
        double startGreen = start.getGreen();
        double endGreen = end.getGreen();
        double startBlue = start.getBlue();
        double endBlue = end.getBlue();

        HBox row = new HBox();
        for (int i = 0; i < numColumns; ++i) {
            double mixValue = (double) i / (double) (numColumns - 1);
            Color color = new Color(mix(startRed, endRed, mixValue),
                mix(startGreen, endGreen, mixValue),
                mix(startBlue, endBlue, mixValue),
                1.0);
            Rectangle rect = new Rectangle();
            rect.setWidth(50);
            rect.setHeight(50);
            rect.setFill(color);
            row.getChildren().add(rect);
        }

        return row;
    }

    private VBox createColorArray(int numColumns) {
        VBox array = new VBox();
        Color black = new Color(0.0, 0.0, 0.0, 1.0);
        Color white = new Color(1.0, 1.0, 1.0, 1.0);
        Color red = new Color(1.0, 0.0, 0.0, 1.0);
        Color green = new Color(0.0, 1.0, 0.0, 1.0);
        Color blue = new Color(0.0, 0.0, 1.0, 1.0);
        array.getChildren().add(createColorRow(black, red, numColumns));
        array.getChildren().add(createColorRow(black, green, numColumns));
        array.getChildren().add(createColorRow(black, blue, numColumns));
        array.getChildren().add(createColorRow(white, red, numColumns));
        array.getChildren().add(createColorRow(white, green, numColumns));
        array.getChildren().add(createColorRow(white, blue, numColumns));
        array.getChildren().add(createColorRow(red, green, numColumns));
        array.getChildren().add(createColorRow(red, blue, numColumns));
        array.getChildren().add(createColorRow(blue, green, numColumns));
        return array;
    }

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(createColorArray(11));
        stage.setScene(scene);
        stage.show();
    }
}