package goryachev.bugs;

import java.util.List;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ComboBox_WrongLocation_8338145 extends Application {
    @Override
    public void start(Stage stage) {
        ObservableList<String> items = FXCollections.observableArrayList(List.of("a", "b"));
        ComboBox<String> cb = new ComboBox<>();
        cb.setItems(items);
        cb.setOnHidden((ev) -> {
            toggle(items);
        });

        var vBox = new VBox(cb);
        vBox.setStyle("-fx-font-size: 15");
        vBox.setAlignment(Pos.BOTTOM_LEFT);

        Scene scene = new Scene(vBox, 600, 200);
        scene.getStylesheets().add("data:text/css," +
            """
            .combo-box-popup .list-cell {
                -fx-padding: 0 10 0 10;
            }
            """);
        stage.setScene(scene);
        stage.show();
        stage.setMaximized(true);
    }

    private void toggle(ObservableList<String> items) {
        if(items.size() == 2) {
            items.addAll("c", "d", "e", "f");
        } else {
            while(items.size() > 2) {
                items.remove(items.size() - 1);
            }
        }
    }
}
