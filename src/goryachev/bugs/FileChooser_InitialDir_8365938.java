package goryachev.bugs;

import java.io.File;
import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8365938
 * https://raw.githubusercontent.com/OlexYarm/JavaFx-test-FileChooser/refs/heads/master/src/main/java/com/olexyarm/javafx/test/filechooser/App.java
 */
public class FileChooser_InitialDir_8365938 extends Application {

    @Override
    public void start(Stage stage) {

        var button = new Button("FileChooser");
        var scene = new Scene(new StackPane(button), 640, 480);
        stage.setScene(scene);

        EventHandler ehSelectNn = new EventHandler<Event>() {
            @Override
            public void handle(Event event) {

                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*.*", "*.*"));
                // Test cases:
                fileChooser.setInitialDirectory(new File("C:\\Bad-dir"));
                //fileChooser.setInitialDirectory(new File("C:\\"));
                //fileChooser.setInitialDirectory(new File("XX:"));
                //fileChooser.setInitialDirectory(new File("C:\\Documents and Settings"));
                //fileChooser.setInitialDirectory(new File("C:\\Users\\alex2020\\Downloads"));
                //fileChooser.setInitialDirectory(new File("C:\\Users\\alex2020\\My Documents"));
                try {
                    File fileToSave = fileChooser.showSaveDialog(scene.getWindow());
                    System.out.println("showSaveDialog OK.\nSelected File=" + fileToSave);
                } catch (Throwable t) {
                    System.out.println("showSaveDialog failed.\nThrowable=" + t.toString());
                }
            }
        };
        button.setOnAction(ehSelectNn);
        stage.show();
    }
}