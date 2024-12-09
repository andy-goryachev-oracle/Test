package goryachev.research;

import javafx.application.Application;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 */
public class ComboBox_Events extends Application {

    private Stage stage;
    private ComboBox<String> cb;
    private TextField ed;
    private int seq = 1;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        
        cb = new ComboBox<String>();
        cb.setEditable(true);
        cb.valueProperty().addListener((obs, ov, nv) -> {
            System.out.printf("ComboBox.onValueChanged: %s%n", nv);
        });
        ed = cb.getEditor();

        boolean mouse = false;
        boolean filter = true;

        if (mouse) {
            ed.addEventHandler(MouseEvent.ANY, (ev) -> p("editor handler", ev));
            if (filter) {
                ed.addEventFilter(MouseEvent.ANY, (ev) -> p("editor filter", ev));
            }
        }
        ed.addEventHandler(KeyEvent.ANY, (ev) -> p("editor handler", ev));
        if (filter) {
            ed.addEventFilter(KeyEvent.ANY, (ev) -> p("editor filter", ev));
        }

        if (mouse) {
            cb.addEventHandler(MouseEvent.ANY, (ev) -> p("combobox handler", ev));
            if (filter) {
                cb.addEventFilter(MouseEvent.ANY, (ev) -> p("combobox filter", ev));
            }
        }
        cb.addEventHandler(KeyEvent.ANY, (ev) -> p("combobox handler", ev));
        if (filter) {
            cb.addEventFilter(KeyEvent.ANY, (ev) -> p("combobox filter", ev));
        }

        stage.setScene(new Scene(new VBox(cb)));
        stage.setWidth(400);
        stage.setHeight(200);
        if (mouse) {
            stage.addEventHandler(MouseEvent.ANY, (ev) -> p("stage handler", ev));
            if (filter) {
                stage.addEventFilter(MouseEvent.ANY, (ev) -> p("stage filter", ev));
            }
        }
        stage.addEventHandler(KeyEvent.ANY, (ev) -> p("stage handler", ev));
        if (filter) {
            stage.addEventFilter(KeyEvent.ANY, (ev) -> p("stage filter", ev));
        }

        stage.show();
    }

    private void p(String from, Event ev) {
        Object t = ev.getEventType();
        if (t == MouseEvent.MOUSE_MOVED) {
            return;
        }
        System.out.println(
            seq + ". " +
            from + ": " + ev.getEventType() +
            " h=" + h(ev) +
            " " + (ev.isConsumed() ? "consumed!" : "") +
            " target=" + h(ev.getTarget())
        );
        seq++;
        
        System.out.println(
            "   cb: " + foc(cb) +
            "\n" +
            "   ed: " + foc(ed)
        );
    }
    
    private String foc(Node n) {
        boolean fo = stage.getScene().getFocusOwner() == n;
        return
            (n.isFocused() ? "focused" : "") +
            " " + (n.isFocusVisible() ? "focusVisible" : "") +
            " " + (n.isFocusWithin() ? "focusWithin" : "") +
            " " + (fo ? "focusOwner" : "");
    }

    private String h(Object x) {
        String s = String.valueOf(x.hashCode());
        return s.substring(s.length() - 3);
    }
}