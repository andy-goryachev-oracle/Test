package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/// https://bugs.openjdk.org/browse/JDK-8374013
public class Font_ChineseFallback_8374013 extends Application {
    @Override
    public void start(Stage stage) {
        Label label = new Label("中文显示测试：漢字測試 包菜 骨真直");
        label.setStyle("-fx-font-size: 36px;");

        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 800, 200);

        stage.setTitle("JavaFX Chinese Font Fallback Test");
        stage.setScene(scene);
        stage.show();
    }
}