package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ContextMenu_NullOwner_8316796 extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        MenuItem menuItem = new MenuItem("Click me");
        menuItem.setOnAction(event -> {
            String text = menuItem.getParentPopup().getOwnerNode() != null ? "Owner node is not null" : "Owner node is null";
            new Alert(Alert.AlertType.INFORMATION, text).showAndWait();
        });

        ContextMenu contextMenu = new ContextMenu(menuItem);

        Button button = new Button("Right click me");
        button.setContextMenu(contextMenu);

        primaryStage.setScene(new Scene(new BorderPane(button)));
        primaryStage.show();
    }
}