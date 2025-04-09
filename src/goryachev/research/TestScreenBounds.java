package goryachev.research;

import java.util.Comparator;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class TestScreenBounds extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Move Outside Bounds");
        Rectangle2D bounds = Screen.getScreens().stream()
            .map(Screen::getBounds)
            .sorted(Comparator.comparingDouble(Rectangle2D::getMaxX).reversed())
            .findFirst()
            .orElseThrow();

        Button btn = new Button("Move To " + bounds.getMaxX());
        btn.setOnAction(event -> stage.setX(bounds.getMaxX()));

        double middleLastScreen = bounds.getMinX() + bounds.getWidth() / 2;

        Button btn2 = new Button("Move To " + middleLastScreen);
        btn2.setOnAction(event -> stage.setX(middleLastScreen));

        VBox root = new VBox(btn, btn2);
        root.setFillWidth(true);
        root.setAlignment(Pos.CENTER);
        Scene scene = new Scene(root, 300, 300);
        stage.setScene(scene);
        stage.show();
    }
}