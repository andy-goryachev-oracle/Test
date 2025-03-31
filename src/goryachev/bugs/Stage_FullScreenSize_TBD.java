package goryachev.bugs;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 */
public class Stage_FullScreenSize_TBD extends Application {

    private Label content;
    private StackPane container;

    @Override
    public void start(Stage stage) throws Exception {
        content = new Label("A Label is a Label is a Label.");
        content.setMaxWidth(Double.POSITIVE_INFINITY);
        content.setMaxHeight(Double.POSITIVE_INFINITY);
        content.setAlignment(Pos.CENTER);
        content.setBackground(Background.fill(Color.WHITE));
        content.setTextFill(Color.BLACK);

        container = new StackPane(content);
        
        Scene scene = new Scene(container);

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.addEventHandler(KeyEvent.KEY_PRESSED, (ev) -> {
            switch (ev.getCode()) {
            case ESCAPE:
                stage.hide();
                ev.consume();
                break;
            }
        });
        stage.widthProperty().addListener((s, p, c) -> {
            System.out.println("stage.width=" + c);
        });
        stage.heightProperty().addListener((s, p, c) -> {
            System.out.println("stage.height=" + c);
        });
        scene.widthProperty().addListener((s, p, c) -> {
            System.out.println("scene.width=" + c);
        });
        scene.heightProperty().addListener((s, p, c) -> {
            System.out.println("scene.height=" + c);
        });
        stage.show();
    }
}
