package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8352992
 */
public class FileChooser_Stderr_8352992 extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Button openButton = new Button("Open File");
        openButton.setOnAction((ev) -> {
            FileChooser ch = new FileChooser();
            ch.setTitle("Open");
            ch.showOpenDialog(stage);
        });
        
        Button saveButton = new Button("Save File");
        saveButton.setOnAction((ev) -> {
            FileChooser ch = new FileChooser();
            ch.setTitle("Save");
            ch.showSaveDialog(stage);
        });

        HBox p = new HBox(openButton, saveButton);

        stage.setTitle(getClass().getSimpleName());
        Scene scene = new Scene(p, 800, 600);
        stage.setScene(scene);
        stage.show();
    }
}
