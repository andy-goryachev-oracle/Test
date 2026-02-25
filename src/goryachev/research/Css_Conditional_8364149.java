package goryachev.research;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Css_Conditional_8364149 extends Application {

    @Override
    public void start(Stage stage) {
        Label t = new Label();
        
        Scene scene = new Scene(t, 300, 250);
        scene.getStylesheets().add(Css_Conditional_8364149.class.getResource("Css_Conditional_8364149_Main.css").toExternalForm());

        t.textProperty().bind(Bindings.createStringBinding(
            () -> {
                return scene.getWidth() + " x " + scene.getHeight();
            },
            scene.widthProperty(),
            scene.heightProperty()
            ));

        stage.setScene(scene);
        stage.show();
    }
}
