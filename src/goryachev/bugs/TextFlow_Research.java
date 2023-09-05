package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

// can TextFlows be a part of another TextFlow?
public class TextFlow_Research extends Application {

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

        TextFlow f1 = new TextFlow(t("red ", Color.RED), t(" green\ngrass", Color.GREEN));
        TextFlow f2 = new TextFlow(t("yo\nyo\nyo ", Color.BLUE), t(" mama ", Color.BLACK));
        TextFlow p = new TextFlow(f1, f2);

        Scene scene = new Scene(p);
        stage.setScene(scene);
        stage.show();
    }
    
    private static Text t(String text, Color c) {
        Text t = new Text(text);
        t.setFill(c);
        return t;
    }
}