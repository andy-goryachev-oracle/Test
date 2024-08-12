package goryachev.bugs;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.List;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ComboBox_WrongPopupLocation_8338145 extends Application {
    @Override
    public void start(Stage stage) {
        var items = FXCollections.observableArrayList(List.of("a", "b"));
        var combobox = new ComboBox<String>();
        combobox.setItems(items);

        var vBox = new VBox(combobox);
        vBox.setStyle("-fx-font-size: 15");
        vBox.setAlignment(Pos.BOTTOM_LEFT);

        Scene scene = new Scene(vBox, 600, 200);
        var css = encodeStylesheet(
            """
            .combo-box-popup .list-cell {
                -fx-padding: 0 10 0 10;
            }
            """);
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
        stage.setMaximized(true);
    }

    /** encode stylesheet to a data: url */
    public static String encodeStylesheet(String s) {
        if (s == null) {
            return null;
        }
        Charset utf8 = Charset.forName("utf-8");
        byte[] b = s.getBytes(utf8);
        return "data:text/css;base64," + Base64.getEncoder().encodeToString(b);
    }
}
