package goryachev.bugs;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8380926
 */
public class Alert_NPE_8380926 extends Application {

    @Override
    public void start(Stage stage) {
        Stage s2 = new Stage();
        TextArea t2 = new TextArea("2nd");

        Button b1 = new Button("NPE");
        b1.setOnAction((_) -> {
            s2.requestFocus();
            t2.requestFocus();
            
            Platform.runLater(() -> {
                var parent = new Stage();
                Alert alert = new Alert(Alert.AlertType.NONE);
                alert.initOwner(parent);
                alert.show();
            });
        });
        Button b2 = new Button("Position");
        b2.setOnAction((_) -> {
            s2.requestFocus();
            var parent = new Stage();
            parent.setScene(new Scene(new Region()));
            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.initOwner(parent);
            alert.setContentText("Wrong position");
            alert.show();
        });

        Scene scene = new Scene(new HBox(b1, b2), 400, 300);
        stage.setScene(scene);
        stage.show();
        
        s2.setScene(new Scene(t2, 300, 200));
        s2.show();
    }
}