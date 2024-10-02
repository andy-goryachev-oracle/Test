package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8341440
 */
public class ScrollPane_FitTo_8341440 extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        TextArea t = new TextArea();
        t.setBorder(Border.stroke(Color.RED));
        t.setPrefSize(400, 400);
        
        ScrollPane scroll = new ScrollPane(t);

        CheckBox fitHeight = new CheckBox("fit height");
        fitHeight.selectedProperty().bindBidirectional(scroll.fitToHeightProperty());
        
        CheckBox fitWidth = new CheckBox("fit width");
        fitWidth.selectedProperty().bindBidirectional(scroll.fitToWidthProperty());

        BorderPane bp = new BorderPane();
        bp.setTop(new VBox(
            fitHeight,
            fitWidth
            ));
        bp.setCenter(scroll);

        Scene scene = new Scene(bp);
        
        stage.setScene(scene);
        stage.setWidth(700);
        stage.setHeight(600);
        stage.setTitle("ScrollPane");
        stage.show();
    }
}