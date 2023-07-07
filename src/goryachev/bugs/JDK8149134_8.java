package goryachev.bugs;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class JDK8149134_8 extends Application {
    @Override public void start(Stage stage) {
        Rectangle highlight = new Rectangle(600, 400);
        highlight.setStroke(null);
        highlight.setFill(Color.LIGHTBLUE);

        Text text1 = mkText(0, 3);
        Text text2 = mkText(3, 6);
        Text text3 = mkText(0, 3);
        Text text4 = mkText(3, 6);

        int len1 = text1.getText().length();
        int i1 = len1 - 18;
        int i2 = len1 + 22;

        // Select from near end of text1 to a bit into text2
        text1.setSelectionStart(i1);
        text1.setSelectionEnd(len1);
        text2.setSelectionStart(0);
        text2.setSelectionEnd(i2 - len1 - 1);

        // Select from near end of text3 to a bit into text4
        int len3 = text3.getText().length();
        text3.setSelectionStart(0);
        text3.setSelectionEnd(len3);
        int d = len3;
        text4.setSelectionStart(0 + d);
        text4.setSelectionEnd(text4.getText().length() + d);
        
        TextFlow f = new TextFlow(text3, text4);
//        f.setSelectionStart(i1);
//        f.setSelectionEnd(len1);
//        f.setSelectionStart(0);
//        f.setSelectionEnd(i2 - len1 - 1);

        VBox vbox = new VBox(0);
        vbox.getChildren().addAll(text1, text2, 
                                  new Separator(), 
                                  f);
        stage.setScene(new Scene(new Group(highlight, vbox), 
                                 600, 400));
        stage.show();
    }

    private Text mkText(int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++) {
            sb.append(i + ". Lorem ipsum dolor sit amet, consectetur adipisicing elit.\n");
        }
        return new Text(sb.toString());
    }
}