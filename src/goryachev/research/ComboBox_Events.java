package goryachev.research;

import javafx.application.Application;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 */
public class ComboBox_Events extends Application {

    @Override
    public void start(Stage stage) {
        ComboBox<String> c = new ComboBox<String>();
        c.setEditable(true);
        c.valueProperty().addListener((obs, ov, nv) -> {
            System.out.printf("ComboBox.onValueChanged: %s%n", nv);
        });
        c.getEditor().addEventHandler(MouseEvent.ANY, (ev) -> p("editor", ev));
        c.addEventHandler(MouseEvent.ANY, (ev) -> p("combobox", ev));
        c.getEditor().addEventHandler(KeyEvent.ANY, (ev) -> p("editor", ev));
        c.addEventHandler(KeyEvent.ANY, (ev) -> p("combobox", ev));


        stage.setScene(new Scene(new VBox(c)));
        stage.setWidth(400);
        stage.setHeight(200);
        stage.addEventHandler(MouseEvent.ANY, (ev) -> p("stage", ev));
        stage.addEventHandler(KeyEvent.ANY, (ev) -> p("stage", ev));
        
        stage.show();
    }
    
    private void p(String from, Event ev) {
        System.out.println(from + ": " + ev.getEventType() + " " + (ev.isConsumed() ? "consumed??" : ""));
    }
}