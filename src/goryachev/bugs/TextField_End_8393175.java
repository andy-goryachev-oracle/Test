package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/// https://bugs.openjdk.org/browse/JDK-8393175
public class TextField_End_8393175 extends Application {

    @Override
    public void start(Stage stage) {
        var t = new TextField("aaaaaaaaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbbbcccccc123456789");
        t.end();

        stage.setScene(new Scene(new VBox(t), 300, 100));
        stage.show();
    }
}
