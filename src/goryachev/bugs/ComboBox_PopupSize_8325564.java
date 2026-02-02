package goryachev.bugs;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * https://bugs.openjdk.org/browse/JDK-8325564
 */
public class ComboBox_PopupSize_8325564 extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        ComboBox<String> comboBox = new ComboBox<>();
        Timeline animation = new Timeline();
        animation.getKeyFrames().add(new KeyFrame(Duration.seconds(1), event -> {
            
            // turn this off to see another issue
            comboBox.show();
            
            comboBox.getItems().setAll("Dave");
        }));
        animation.getKeyFrames().add(new KeyFrame(Duration.seconds(5), event -> {
            comboBox.getItems().setAll("Rigby 1", "Rigby 2", "Rigby 3", "Rigby 4", "Rigby 5");
        }));

        Button update = new Button("Update");
        update.setOnAction(event -> animation.playFromStart());
        stage.setScene(new Scene(new VBox(comboBox, update)));
        stage.setWidth(200);
        stage.setHeight(200);
        stage.show();
    }
}