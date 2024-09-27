package goryachev.research;
import java.util.List;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

/**
 * https://stackoverflow.com/questions/596216/formula-to-determine-perceived-brightness-of-rgb-color
 * look at the post called "The "Accepted" Answer is Incorrect and Incomplete"
 */
public class ColorInterpolationGamma extends Application {
    private static final double WIDTH = 300;
    private static final double HEIGHT = 30;
    ColorPicker leftColor;
    ColorPicker rightColor;
    Label fxVersion;
    Label gammaVersion;
    TextField gamma;
    
    @Override
    public void start(Stage stage) {
        stage.setTitle("Color Interpolation with Gamma");
        stage.setWidth(450);
        stage.setHeight(550);

        leftColor = new ColorPicker(Color.RED);
        
        rightColor = new ColorPicker(Color.GREEN);
        
        gamma = new TextField("2.2");
        gamma.setOnAction((ev) -> {
            update();
        });
        
        fxVersion = new Label("fx linear gradient");
        fxVersion.setPrefHeight(HEIGHT);
        fxVersion.setPrefWidth(WIDTH);
        
        gammaVersion = new Label();
        gammaVersion.setPrefHeight(HEIGHT);
        gammaVersion.setPrefWidth(WIDTH);
        
        GridPane p = new GridPane(10, 5);
        p.setPadding(new Insets(20));
        int r = 0;
        p.add(leftColor, 0, r);
        p.add(rightColor, 1, r);
        p.add(gamma, 2, r);
        r++;
        p.add(fxVersion, 0, r, 3, 1);
        r++;
        p.add(gammaVersion, 0, r, 3, 1);

        Scene scene = new Scene(p);
        stage.setScene(scene);
        stage.show();
        
        leftColor.valueProperty().addListener((ob) -> update());
        rightColor.valueProperty().addListener((ob) -> update());
        update();
    }

    void update() {
        Color c0 = leftColor.getValue();
        Color c1 = rightColor.getValue();
        
        LinearGradient p = new LinearGradient(0, 0, WIDTH, HEIGHT, false, CycleMethod.NO_CYCLE, List.of(
            new Stop(0, c0),
            new Stop(WIDTH, c1)
        ));
        fxVersion.setBackground(Background.fill(p));
        
        double gamma = getGamma();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext g = canvas.getGraphicsContext2D();
        for(int x=0; x<WIDTH; x++) {
            double f = x / WIDTH;
            Color c = interp(c0, c1, f, gamma);
            g.setFill(c);
            g.fillRect(x, 0, 1, HEIGHT);
        }
        WritableImage im = canvas.snapshot(null, null);
        gammaVersion.setGraphic(new ImageView(im));
    }

    private Color interp(Color left, Color right, double f, double gamma) {
        double r = interp(left.getRed(), right.getRed(), f, gamma);
        double g = interp(left.getGreen(), right.getGreen(), f, gamma);
        double b = interp(left.getBlue(), right.getBlue(), f, gamma);
        return Color.color(r, g, b);
    }

    private double interp(double a, double b, double f, double gamma) {
        // Convert a gamma encoded RGB to a linear value. sRGB (computer standard) for instance requires a power curve of approximately V^2.2
        double v = (Math.pow(a, gamma) * (1.0 - f)) + (Math.pow(b, gamma) * (f));
        return Math.pow(v, 1.0 / gamma);
    }

    private double getGamma() {
        try {
            String s = gamma.getText().trim();
            return Double.parseDouble(s);
        } catch(Exception e) {
            return 2.2;
        }
    }
}