package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8273657
 */
public class TextField_InitialSelection_8273657 extends Application {

    private Parent createContent() {
        TextField textField = new TextField("just some text");
        TextField textField2 = new TextField("just some text2");
        Button add = new Button("insert");
        Button remove = new Button("remove");
        VBox content = new VBox(10, add, remove);

        add.setOnAction(e -> {
            content.getChildren().add(textField);
            content.getChildren().add(textField2);
            textField.requestFocus();
        });
        remove.setOnAction(e -> {
            content.getChildren().remove(textField);
            content.getChildren().remove(textField2);
        });
        return content;
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(createContent(), 300, 300));
        stage.show();
    }
}