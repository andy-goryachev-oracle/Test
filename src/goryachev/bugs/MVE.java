package goryachev.bugs;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MVE extends Application {
    @Override
    public void start(Stage primaryStage) {
        var root = new StackPane();
        var vbox = new VBox();
        var hbox = new HBox();
        var textBox = new VBox();
        var textField = new TextField();
        textBox.getChildren().add(textField);
        var label = new Label("TestLabel");
        var weirdButton = new Button();
        weirdButton.setText("");
        weirdButton.setPadding(new Insets(2.0));
        hbox.getChildren().add(label);
        hbox.getChildren().add(weirdButton);
        vbox.getChildren().add(textBox);
        vbox.getChildren().add(hbox);
        var cancelButton = new Button("Abbrechen");
        cancelButton.setCancelButton(true);
        vbox.getChildren().add(cancelButton);
        root.getChildren().add(vbox);
        var scene = new Scene(root, 640, 480);
        primaryStage.setScene(scene);
        primaryStage.setTitle("MVE");
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}