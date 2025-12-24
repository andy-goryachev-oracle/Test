package goryachev.bugs;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/// https://bugs.openjdk.org/browse/JDK-8374329
public class PasswordField_AccessibleText_8374329 extends Application {

    private PasswordField passwordField;
    
    @Override
    public void start(Stage stage) {
        passwordField = new PasswordField();
        
        // queryAccessibilityAttribute returns either accessibleText or promptText
        // passwordField.setPromptText("Prompt Text: Password");
        //passwordField.setAccessibleText("Custom accessible password text");
        
        passwordField.addEventHandler(KeyEvent.ANY, (ev) -> {
            print();
        });

        stage.setScene(new Scene(new VBox(passwordField), 300, 100));
        stage.setTitle("PasswordField Accessible Text Test");
        stage.show();

        Platform.runLater(() -> {
            print();
        });
    }
    
    private void print() {
        // Query accessible attribute
        Object accessibleText = passwordField.queryAccessibleAttribute(AccessibleAttribute.TEXT);
        System.out.println("accessibleText=" + String.valueOf(accessibleText));
        System.out.println("text=" + String.valueOf(passwordField.getText()));
    }
}