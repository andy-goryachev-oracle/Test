package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MacOS_CoreTextCrash extends Application {
    Pane pane;
    Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;
        pane = new VBox(10);

        String FULL_UNICODE_SET;
        StringBuilder builder = new StringBuilder();
        for (int character = 0; character < 10000; character++) {
            char[] chars = Character.toChars(character);
            builder.append(chars);
        }
        FULL_UNICODE_SET = builder.toString();
        Text text = new Text(FULL_UNICODE_SET);
        pane.getChildren().add(text);

        Scene scene = new Scene(pane, 400, 200);
        stage.setScene(scene);
        stage.show();
    }
}
