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

        boolean mouse = false;

        if (mouse) {
            c.getEditor().addEventHandler(MouseEvent.ANY, (ev) -> p("editor handler", ev));
            c.getEditor().addEventFilter(MouseEvent.ANY, (ev) -> p("editor filter", ev));
        }
        c.getEditor().addEventHandler(KeyEvent.ANY, (ev) -> p("editor handler", ev));
        c.getEditor().addEventFilter(KeyEvent.ANY, (ev) -> p("editor filter", ev));

        if (mouse) {
            c.addEventHandler(MouseEvent.ANY, (ev) -> p("combobox handler", ev));
            c.addEventFilter(MouseEvent.ANY, (ev) -> p("combobox filter", ev));
        }
        c.addEventHandler(KeyEvent.ANY, (ev) -> p("combobox handler", ev));
        c.addEventFilter(KeyEvent.ANY, (ev) -> p("combobox filter", ev));

        stage.setScene(new Scene(new VBox(c)));
        stage.setWidth(400);
        stage.setHeight(200);
        if (mouse) {
            stage.addEventHandler(MouseEvent.ANY, (ev) -> p("stage handler", ev));
            stage.addEventFilter(MouseEvent.ANY, (ev) -> p("stage filter", ev));
        }
        stage.addEventHandler(KeyEvent.ANY, (ev) -> p("stage handler", ev));
        stage.addEventFilter(KeyEvent.ANY, (ev) -> p("stage filter", ev));

        stage.show();
    }

    private void p(String from, Event ev) {
        Object t = ev.getTarget();
        System.out.println(from + ": " + ev.getEventType() + " " + (ev.isConsumed() ? "consumed??" : "") + " target=" + ev.getTarget().hashCode());
    }
}