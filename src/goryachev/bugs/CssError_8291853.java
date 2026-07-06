package goryachev.bugs;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// https://bugs.openjdk.org/browse/JDK-8291853
public class CssError_8291853 extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        StackPane testEditor = new StackPane();
        TabPane tabPane = new TabPane();

        TextField textField = new TextField();
        tabPane.getTabs().add(new Tab("Test", new VBox(textField, new Label("Test"))));
        testEditor.getChildren().setAll(tabPane);

        ChangeListener<Scene> sceneChangeListener = new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Scene> o, Scene oo, Scene newValue) {
                if (newValue != null) {
                    textField.sceneProperty().removeListener(this);
                    Parent oldRoot = newValue.getRoot();

                    StackPane newRoot = new StackPane();
                    newValue.setRoot(newRoot);

                    newRoot.getChildren().setAll(oldRoot);
                }
            }
        };
        textField.sceneProperty().addListener(sceneChangeListener);

        Button btn = new Button("Add Editor");
        VBox root = new VBox(btn);
        btn.setOnAction(_ -> root.getChildren().add(testEditor));

        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }
}
