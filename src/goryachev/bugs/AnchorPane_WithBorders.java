package goryachev.bugs;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class AnchorPane_WithBorders extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Rectangle r = new Rectangle(40, 40);
        r.setFill(Color.AQUA);
        
        AnchorPane.setLeftAnchor(r, 0.0);

        AnchorPane ap = new AnchorPane();
        ap.setBorder(new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, null, new BorderWidths(6))));
        ap.setPadding(new Insets(10));
        ap.setBackground(Background.fill(Color.grayRgb(256 / 4, 0.8)));
        ap.setMaxHeight(100);
        ap.setMaxWidth(100);
        ap.setMinHeight(100);
        ap.setMinWidth(100);
        ap.getChildren().add(r);
        
        stage.setScene(new Scene(new BorderPane(ap), 300, 200));
        stage.show();
    }
}