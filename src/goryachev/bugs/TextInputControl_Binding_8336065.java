package goryachev.bugs;

import java.io.IOException;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8336065
 */
public class TextInputControl_Binding_8336065 extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Dialog<String> alert = new Dialog<>();
        alert.setTitle("Information Dialog");
        alert.setHeaderText("Look, an Information Dialog");
        alert.setContentText("I have a great message for you! With some longer text to show the issue");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.initOwner(stage.getOwner());
        alert.setResizable(true);

        DialogPane dlgPane = new DialogPane();
        dlgPane.getButtonTypes().addAll(ButtonType.CANCEL);
        VBox vbox = new VBox();
        vbox.setPrefWidth(400);
        vbox.setPrefHeight(200);

        TextField textField = new TextField();
        textField.setPromptText("Type something and then press Ctrl+Z.");
        vbox.getChildren().add(textField);
        
        SimpleStringProperty textProperty = new SimpleStringProperty();
        
        // this makes no sense:
        textProperty.addListener((s, p, newValue) -> textField.textProperty().set(newValue));
        textField.textProperty().addListener((s, p, newValue) -> textProperty.set(newValue));
        // the following code should be used instead
        // textField.textProperty().bindBidirectional(textProperty);
        
        dlgPane.setContent(vbox);

        alert.setDialogPane(dlgPane);
        alert.showAndWait();
    }
}