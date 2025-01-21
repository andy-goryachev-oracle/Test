package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8348098
 */
public class Application_Focus_8348098 extends Application {
    private Group root;
    private Node currentNode;

    @Override
    public void start(Stage primaryStage) {
        // Create the scene graph
        root = new Group();
        Rectangle rect = new Rectangle(100, 50, Color.RED);
        Circle circle = new Circle(150, 150, 50, Color.BLUE);
        root.getChildren().addAll(rect, circle);

        // Set initial current node
        currentNode = rect;

        // Create the scene
        Scene scene = new Scene(root, 400, 300);

        // Handle keyboard input
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.RIGHT) {
                if (currentNode == rect) {
                    currentNode = circle;
                }
            } else if (event.getCode() == KeyCode.LEFT) {
                if (currentNode == circle) {
                    currentNode = rect;
                }
            }

            // Visual feedback (optional)
            highlightCurrentNode();
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("Traverse Scenegraph");
        primaryStage.show();
    }

    private void highlightCurrentNode() {
        // Remove previous highlight (if any)
        for (Node node: root.getChildren()) {
            node.setStyle("-fx-effect: null;");
        }

        // Highlight current node
        currentNode.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 10, 0, 0, 0);");
    }
}