package goryachev.research;
import java.util.List;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
    private static final double WIDTH = 500;
    private static final double HEIGHT = 50;
    private static final String
        POWER_1 = "Power 1.0",
        POWER_2_4 = "Power 2.4",
        LINEARIZED = "Linearized sRGB";
    ColorPicker leftColor;
    ColorPicker rightColor;
    Label fxVersion;
    Label computedVersion;
    Label luminocity;
    ComboBox<String> algorithm;
    
    interface Interp {
        public Color interp(Color left, Color right, double fraction);
    }
    
    @Override
    public void start(Stage stage) {
        stage.setTitle("Color Interpolation");
        stage.setWidth(550);
        stage.setHeight(550);

        leftColor = new ColorPicker(Color.RED);
        
        rightColor = new ColorPicker(Color.GREEN);
        
        algorithm = new ComboBox<>();
        algorithm.getItems().setAll(
            POWER_1,
            POWER_2_4,
            LINEARIZED
        );
        algorithm.setOnAction((ev) -> {
            update();
        });
        
        fxVersion = new Label();
        fxVersion.setPrefHeight(HEIGHT);
        fxVersion.setPrefWidth(WIDTH);
        
        computedVersion = new Label();
        computedVersion.setPrefHeight(HEIGHT);
        computedVersion.setPrefWidth(WIDTH);
        
        luminocity = new Label();
        luminocity.setPrefHeight(HEIGHT);
        luminocity.setPrefWidth(WIDTH);
        
        GridPane p = new GridPane(10, 5);
        p.setPadding(new Insets(20));
        int r = 0;
        p.add(leftColor, 0, r);
        p.add(rightColor, 1, r);
        p.add(algorithm, 2, r);
        r++;
        p.add(new Label("FX Linear Gradient:"), 0, r, 3, 1);
        r++;
        p.add(fxVersion, 0, r, 3, 1);
        r++;
        p.add(new Label("Computed:"), 0, r, 3, 1);
        r++;
        p.add(computedVersion, 0, r, 3, 1);
        r++;
        p.add(new Label("Perceived Luminocity (L*):"), 0, r, 3, 1);
        r++;
        p.add(luminocity, 0, r, 3, 1);

        Scene scene = new Scene(p);
        stage.setScene(scene);
        stage.show();
        
        leftColor.valueProperty().addListener((ob) -> update());
        rightColor.valueProperty().addListener((ob) -> update());
        
        algorithm.getSelectionModel().selectFirst();
    }

    void update() {
        Color c0 = leftColor.getValue();
        Color c1 = rightColor.getValue();

        // fx linear gradient
        LinearGradient p = new LinearGradient(0, 0, WIDTH, HEIGHT, false, CycleMethod.NO_CYCLE, List.of(
            new Stop(0, c0),
            new Stop(WIDTH, c1)
        ));
        fxVersion.setBackground(Background.fill(p));
        
        String choice = algorithm.getSelectionModel().getSelectedItem();
        if(choice == null) {
            return;
        }

        Interp alg;
        switch(choice) {
        case POWER_1:
            alg = this::power_1;
            break;
        case POWER_2_4:
            alg = this::power_2_4;
            break;
        case LINEARIZED:
            alg = this::linearized;
            break;
        default:
            return;
        }

        // gamma corrected gradient
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        Canvas lcanvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext g = canvas.getGraphicsContext2D();
        GraphicsContext lg = lcanvas.getGraphicsContext2D();
        for (int x = 0; x < WIDTH; x++) {
            double f = x / WIDTH;
            Color c = alg.interp(c0, c1, f);
            g.setFill(c);
            g.fillRect(x, 0, 1, HEIGHT);
            
            double lum = luminocity(c);
            c = Color.gray(lum);
            lg.setFill(c);
            lg.fillRect(x, 0, 1, HEIGHT);
            // dot
            lg.setFill(Color.RED);
            double y = HEIGHT * (1.0 - lum);
            lg.fillRect(x, y, 1, 1);
        }
        
        WritableImage im = canvas.snapshot(null, null);
        computedVersion.setGraphic(new ImageView(im));
        
        im = lcanvas.snapshot(null, null);
        luminocity.setGraphic(new ImageView(im));
    }

    private Color power_1(Color left, Color right, double f) {
        return powerInterp(left, right, f, 1.0);
    }

    private Color power_2_4(Color left, Color right, double f) {
        return powerInterp(left, right, f, 2.4);
    }

    private Color linearized(Color left, Color right, double f) {
        double r = linearizedInterp(left.getRed(), right.getRed(), f);
        double g = linearizedInterp(left.getGreen(), right.getGreen(), f);
        double b = linearizedInterp(left.getBlue(), right.getBlue(), f);
        return Color.color(r, g, b);
    }
    
    private double linearizedInterp(double a, double b, double f) {
        double v = (expand(a) * (1.0 - f)) + (expand(b) * f);
        return compress(v);
    }
    
    // gamma-expanded values (linear-light)
    private double expand(double c) {
        if (c <= 0.04045) {
            return c / 12.92;
        } else {
            return Math.pow(((c + 0.055) / 1.055), 2.4);
        }
    }

    // gamma-compressed (sRGB)
    private double compress(double c) {
        if(c <= 0.0031308) {
            return 12.92 * c;
        } else {
            return 1.055 * Math.pow(c, (1.0 / 2.4)) - 0.055;
        }
    }

    private Color powerInterp(Color left, Color right, double f, double gamma) {
        double r = interp(left.getRed(), right.getRed(), f, gamma);
        double g = interp(left.getGreen(), right.getGreen(), f, gamma);
        double b = interp(left.getBlue(), right.getBlue(), f, gamma);
        return Color.color(r, g, b);
    }

    private double interp(double a, double b, double f, double gamma) {
        // Convert a gamma encoded RGB to a linear value
        // sRGB (computer standard) for instance requires a power curve of approximately V^2.4
        double v = (Math.pow(a, gamma) * (1.0 - f)) + (Math.pow(b, gamma) * (f));
        return Math.pow(v, 1.0 / gamma);
    }

    private double luminocity(Color c) {
        double r = expand(c.getRed());
        double g = expand(c.getGreen());
        double b = expand(c.getBlue());
        // luminance (Y)
        double y = 0.2126 * r + 0.7152 * g + 0.0722 * b;
        // L* perceived lightness
        double lstar;
        if (y <= 0.008856) {
            lstar = 903.3 * y;
        } else {
            lstar = Math.pow(y, (1.0 / 3.0)) * 116 - 16;
        }
        // L* from 0 ... 100
        return lstar / 100.0;
    }
}