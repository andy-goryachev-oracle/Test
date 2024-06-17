package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Stage_DebianMaximized_8332222 extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        StackPane root = new StackPane(new Label("JDK_8332222"));
        root.setOnMouseClicked((ev) -> {
            System.out.println("showing alert");
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.resultProperty().addListener((s,p,v) -> System.out.println("alert result=" + v));
            a.show();
        });
        stage.setScene(new Scene(root, 640, 480));

        stage.widthProperty().addListener((s,p,v) -> System.out.println("width=" + v));
        stage.heightProperty().addListener((s,p,v) -> System.out.println("height=" + v));
        stage.xProperty().addListener((s,p,v) -> System.out.println("x=" + v));
        stage.yProperty().addListener((s,p,v) -> System.out.println("y=" + v));
        stage.maximizedProperty().addListener((s,p,v) -> System.out.println("maximized=" + v));
        stage.iconifiedProperty().addListener((s,p,v) -> System.out.println("iconified=" + v));

        stage.show();
    }
}