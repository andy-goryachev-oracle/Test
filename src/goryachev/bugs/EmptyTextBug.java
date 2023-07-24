package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

/**
 * Empty Text corrupts TextFlow styling.
 */
public class EmptyTextBug extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        String GRAY = "-fx-fill:#808080; -fx-font-size:200%;";
        String RED = "-fx-fill:#ff0000; -fx-font-size:200%;";
        String BLACK = "-fx-font-size:200%;";

        TextFlow f = new TextFlow(
            t("gray", GRAY),
            // should be no red color in the output because this Text is empty
            t("", RED),
            t("GRAY", GRAY),
            t("black", BLACK)
        );
        
        ScrollPane sp = new ScrollPane(f);
        
        stage.setTitle(getClass().getSimpleName());
        Scene scene = new Scene(sp, 800, 600);
        stage.setScene(scene);
        stage.show();
    }
    
    private Text t(String text, String style) {
        Text t = new Text(text);
        t.setStyle(style);
        return t;
    }
}