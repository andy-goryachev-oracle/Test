package goryachev.bugs;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8351047
 */
public class TitledPane_ResizeableLabels_8351047 extends Application {

    @Override
    public void start(Stage primaryStage) {
        TitledPane tp1 = new TitledPane("Left Aligned Title", c("Very long text so alignment of TitledPane's title is visible"));
        TitledPane tp2 = new TitledPane("Center Aligned Title", c("Very long text so alignment of TitledPane's title is visible"));
        TitledPane tp3 = new TitledPane("Right Aligned Title", c("Very long text so alignment of TitledPane's title is visible"));
        TitledPane tp4 = new TitledPane("Title + Left Graphic", c("Very long text so alignment of TitledPane's title is visible"));
        TitledPane tp5 = new TitledPane("Title + Right Graphic", c("Very long text so alignment of TitledPane's title is visible"));
        TitledPane tp6 = new TitledPane("Title + Gap + Graphic", c("Very long text so alignment of TitledPane's title is visible"));

        tp1.setPadding(new Insets(20));
        tp1.setAlignment(Pos.CENTER_LEFT);
        tp2.setPadding(new Insets(20));
        tp2.setAlignment(Pos.CENTER);
        tp3.setPadding(new Insets(20));
        tp3.setAlignment(Pos.CENTER_RIGHT);
        tp4.setPadding(new Insets(20));
        tp4.setGraphic(new Button("Delete"));
        tp5.setPadding(new Insets(20));
        tp5.setGraphic(new Button("Delete"));
        tp5.setContentDisplay(ContentDisplay.RIGHT);

        VBox vbox = new VBox(new Button("Delete"));

        vbox.setAlignment(Pos.CENTER_RIGHT);
        vbox.setMaxWidth(10000);
        vbox.setBorder(Border.stroke(Color.RED));

        HBox header = new HBox(new Label("Title + Gap + Graphic"), vbox);

        header.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(vbox, Priority.ALWAYS);

        tp6.setPadding(new Insets(20));
        tp6.setGraphic(header);
        tp6.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        VBox root = new VBox(tp1, tp2, tp3, tp4, tp5, tp6);

        Scene value = new Scene(root, 300, 500);

        primaryStage.setScene(value);
        primaryStage.show();
    }

    private Node c(String text) {
        Label n = new Label(text);
        n.setWrapText(true);
        return n;
    }
}