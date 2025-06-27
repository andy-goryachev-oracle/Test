package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Scene_RepaintWhenMinimized_8146479 extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox root = new VBox();

        primaryStage.iconifiedProperty().addListener((_, _, _) -> {
            if (primaryStage.isIconified()) {
                root.getChildren().add(new Label("Label"));
            }
        });

        final Scene scene = new Scene(root, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Scene_RepaintWhenMinimized_8146479");
        primaryStage.show();
    }
}