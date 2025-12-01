package goryachev.bugs;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SubScene_ZeroSize_8372761 extends Application {

    @Override
    public void start(Stage stage) {
        var rect = new Rectangle(50, 50, Color.RED);
        var subScene = new SubScene(new Group(rect), 100, 100);
        subScene.setFill(Color.GRAY);

        // Also try a perspective camera:
        //
        // var camera = new PerspectiveCamera();
        // camera.setRotate(45);
        // camera.setTranslateX(-20);
        // camera.setRotationAxis(new Point3D(0, 1, 0));
        // subScene.setCamera(camera);

        class MyButton extends Button {
            public MyButton(String text, double width, double height) {
                super(text);
                setOnAction(_ -> {
                    var timeline = new Timeline(
                        new KeyFrame(Duration.seconds(1), new KeyValue(subScene.widthProperty(), width)),
                        new KeyFrame(Duration.seconds(1), new KeyValue(subScene.heightProperty(), height)));

                    timeline.setOnFinished(_ -> {
                        Point2D p0 = rect.localToScreen(0, 0);
                        System.out.println("rect.localToScreen(0, 0) = " + p0);
                    });

                    timeline.play();
                });
            }
        }

        VBox.setMargin(subScene, new Insets(0, 0, 20, 0));

        var root = new VBox(5,
            subScene,
            new CheckBox("Rotate SubScene") {
                {
                    setOnAction(_ -> subScene.setRotate(isSelected() ? 45 : 0));
                }
            },
            new MyButton("Size: 100 x 100", 100, 100),
            new MyButton("Size: 10 x 10", 10, 10),
            new MyButton("Size: 100 x 0", 100, 0),
            new MyButton("Size: 0 x 100", 0, 100),
            new MyButton("Size: -1 x -1", -1, -1),
            new MyButton("Size: MIN_VALUE x MIN_VALUE", Double.MIN_VALUE, Double.MIN_VALUE),
            new MyButton("Size: 0 x 0", 0, 0));

        Scene scene = new Scene(root, 500, 500);
        stage.setScene(scene);
        stage.show();
    }
}
