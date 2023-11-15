package goryachev.bugs;

import java.io.IOException;
import java.time.LocalDate;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8303478
 */
public class DatePicker_8303478 extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Group root = new Group();
        VBox vbox = new VBox(8);
        vbox.getChildren().addAll(new DatePicker(LocalDate.now()), new TextField());
        root.getChildren().add(vbox);
        Scene scene = new Scene(root, 200, 150);
        stage.setTitle("DatePicker JDK-8303478");
        stage.setScene(scene);
        stage.show();
    }
}