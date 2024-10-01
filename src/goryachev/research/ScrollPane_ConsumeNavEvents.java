package goryachev.research;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Skin;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * 
 */
public class ScrollPane_ConsumeNavEvents extends Application {

    @Override
    public void start(Stage stage) {

        GridPane gp = new GridPane(10, 10);

        gp.add(new VBox(
            new Label("Standard Buttons in normal container"),
            new HBox(5, 
                new Button("A"),
                new Button("B"),
                new Button("C")
            )
        ), 0, 0);
        
        gp.add(new VBox(
            new Label("Standard Buttons in ScrollPane"),
            new ScrollPane(
                new HBox(5, 
                    new Button("A"),
                    new Button("B"),
                    new Button("C")
                )
            )
        ), 1, 0);
        
        gp.add(new VBox(
            new Label("Custom Buttons in normal container"),
            new HBox(5,
                new CustomButton("A"),
                new CustomButton("B"),
                new CustomButton("C")
            )
        ), 0, 1);
        
        gp.add(new VBox(
            new Label("Custom Buttons in normal container"),
            new ScrollPane(
                new HBox(5,
                    new CustomButton("A"),
                    new CustomButton("B"),
                    new CustomButton("C")
                )
            )
        ), 1, 1);

        Scene scene = new Scene(gp);

        stage.setScene(scene);
        stage.show();
    }

    static class CustomButton extends Button {
        CustomButton(String title) {
            super(title);
            setSkin(new CustomButtonSkin(this));
        }
    }

    static class CustomButtonSkin implements Skin<Button> {
        private final Button control;
        private final StackPane container;

        public CustomButtonSkin(Button button) {
            this.control = button;
            this.container = new StackPane();
            this.container.getChildren().add(new Label(button.getText()));
        }

        @Override
        public Button getSkinnable() {
            return control;
        }

        @Override
        public Node getNode() {
            return container;
        }

        @Override
        public void dispose() {
            container.getChildren().clear();
        }
    }
}