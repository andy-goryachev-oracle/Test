package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MacAccessibility_OutOfBounds_8235989 extends Application {
    @Override
    public void start(Stage primaryStage) {
        TextField textField1 = new TextField();
        textField1.setText("some text");

        MenuBar menuBar = new MenuBar();
        menuBar.setUseSystemMenuBar(true);
        Menu menuFile = new Menu("File");
        menuBar.getMenus().addAll(menuFile);

        MenuItem add = new MenuItem("Crash");
        add.setOnAction(e -> textField1.setText("text"));
        menuFile.getItems().add(add);

        VBox root = new VBox();
        root.getChildren().add(textField1);

        Scene scene = new Scene(root, 300, 250);

        ((VBox)scene.getRoot()).getChildren().addAll(menuBar);
        primaryStage.setTitle("8235989");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}