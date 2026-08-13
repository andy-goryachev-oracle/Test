package goryachev.research;

import java.util.Arrays;
import java.util.Comparator;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class KeyCodeCombination_DisplayName extends Application {
    
    @Override
    public void start(Stage stage) {
        listKeyCodeCombinations();
        
        Scene scene = new Scene(new BorderPane());
        stage.setTitle(getClass().getSimpleName());
        stage.setScene(scene);
        stage.show();
        
        System.exit(0);
    }
    
    private void listKeyCodeCombinations() {
        KeyCode[] all = KeyCode.values();
        Arrays.sort(all, new Comparator<KeyCode>() {
            @Override
            public int compare(KeyCode a, KeyCode b) {
                return a.getName().compareTo(b.getName());
            }
        });
        for (KeyCode code : all) {
            String name = code.getName();
            try {
                KeyCodeCombination k = new KeyCodeCombination(code);
                IO.println("KeyCode." + code + ": " + k.getDisplayText());
            } catch(Exception ignore) {
            }
        }
    }
}