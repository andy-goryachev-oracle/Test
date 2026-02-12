package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

/// https://bugs.openjdk.org/browse/JDK-8377426
public class Stage_Background_8377426 extends Application {

    private Paint createPaint() {
        int choice =
            7
            ;
        
        return switch(choice) {
        case 0 ->
            // dark
            Color.rgb(55, 0, 0, 0.2);
        case 1 ->
            // light
            Color.rgb(255, 100, 255, 0.5);
        case 2 ->
            // Stark linear gradient: transparent to black
            new LinearGradient(
                0, 0, 0, 1, true, 
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT),
                new Stop(1, Color.BLACK));
        case 3 ->
            // Soft linear gradient with colors
            new LinearGradient(
                0, 0, 0, 1, true,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.RED),
                new Stop(1, Color.ORANGE));
        case 4 ->
            // Image pattern 1
            new ImagePattern(new Image(ClassLoader.getSystemResource("bg1.png").toString(), false));
        case 5 ->
            // Image pattern 2 transparent background
            new ImagePattern(new Image(ClassLoader.getSystemResource("bg2.png").toString(), false));
        case 6 ->
            // Stark linear gradient: transparent to white
            new LinearGradient(
                0, 0, 0, 1, true, 
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT),
                new Stop(1, Color.WHITE));
        case 7 ->
            // linear gradient: multiple colors
            new LinearGradient(
                0, 0, 0, 1, true, 
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT),
                new Stop(0.333, Color.WHITE),
                new Stop(0.666, Color.BLACK),
                new Stop(1, Color.GREEN));
        default ->
            Color.rgb(0, 0, 0, 0.5);
        };
    }

    @Override
    public void start(Stage stage) {
        Label t = new Label("yo");
        t.setMaxWidth(100);
        t.setMaxHeight(50);
        t.setBackground(Background.fill(Color.SALMON));
        
        Scene sc = new Scene(new Group(t), 1240, 1080);
        sc.setFill(createPaint());
        stage.setScene(sc);
        stage.show();
    }
}