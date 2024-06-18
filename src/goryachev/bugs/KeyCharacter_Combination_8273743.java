package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8273743
 */
public class KeyCharacter_Combination_8273743 extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle("KeyCharacterCombination test");
        Pane root = new Pane();
        Scene scene = new Scene(root, 300, 250);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.L, KeyCombination.SHORTCUT_DOWN),
            () -> System.out.println("KeyCodeCombination worked"));
        scene.getAccelerators().put(new KeyCharacterCombination("+", KeyCombination.SHORTCUT_DOWN),
            () -> System.out.println("KeyCharacterCombination worked"));
        stage.setScene(scene);
        stage.show();
    }
}