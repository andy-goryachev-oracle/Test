package goryachev.tests;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8087700
 * 
 * 1) Run TestKeyCombination
 * 2) Press Control+-
 * 3) BUG: On Mac, only one key event is fired ("Hi" is printed twice on Windows_
 */
public class TestKeyCombination extends Application {
//    public static void main(String[] args) {
//        Application.launch(args);
//    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Test KeyCombination");
        Button button1 = new Button();
        button1.setText("Click Me");
        Scene scene = new Scene(new Group(button1), 600, 450);
        stage.setScene(scene);
        stage.show();

        KeyCombination cmdMinus = new KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN);
        KeyCombination cmdMinusFromCharacter = new KeyCharacterCombination("-", KeyCombination.CONTROL_DOWN);
        Runnable runnable = () -> System.out.println("HI");
        scene.getAccelerators().put(cmdMinus, runnable);
        scene.getAccelerators().put(cmdMinusFromCharacter, runnable);
        
        System.out.println(cmdMinus + " equals " + cmdMinusFromCharacter + " = " + cmdMinus.equals(cmdMinusFromCharacter));
    }
}
