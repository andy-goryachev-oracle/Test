package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class TextAreaTest extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        StringBuilder sb = new StringBuilder();
        String text = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int len = text.length();
        for (int i = 0; i < 100000; i++) {
            sb.append(text.charAt(i % len));
            if ((i % 100 == 0) && (i > 0)) {
                sb.append("\n");
            }
        }

        TextArea textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.setFont(Font.getDefault().font(5.0));
        textArea.setText(sb.toString());

        BorderPane pane = new BorderPane(textArea);
        pane.setBottom(new Button("Steal Focus"));

        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
    }
}