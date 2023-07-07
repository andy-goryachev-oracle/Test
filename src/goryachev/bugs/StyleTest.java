package goryachev.bugs;
import javafx.application.Application;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class StyleTest extends Application
{

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        ContextMenu menu = new ContextMenu();
        menu.getItems().add(new MenuItem("Should"));
        menu.getItems().add(new MenuItem("not"));
        menu.getItems().add(new MenuItem("be"));
        menu.getItems().add(new MenuItem("large!"));
        Button button = new Button("Open menu");
        button.setStyle("-fx-font-size: 2em");
        button.setOnAction(e -> menu.show(button, Side.RIGHT, 0, 0));
        BorderPane root = new BorderPane(button);
        root.setStyle("-fx-padding: 3em");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args)
    {
        Application.launch(args);
    }

}