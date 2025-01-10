package goryachev.bugs;

import java.nio.charset.Charset;
import java.util.Base64;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * https://bugs.openjdk.org/browse/JDK-8347464
 */
public class TreeView_FlickerTogglingExpandedState extends Application {

    private static final String TWO_ITEM = "two";

    @Override
    public void start(Stage primaryStage) {
        // TreeView setup
        TreeView<Label> treeView = new TreeView<>();
        treeView.setShowRoot(false);
        treeView.setRoot(createItems());

        // Button to trigger actions
        Button replicateBugButton = new Button("Replicate the bug");
        replicateBugButton.setOnAction(e -> {
            // Trigger changes in the UI when clicking the button
            var heightProperty = new SimpleDoubleProperty(primaryStage.getHeight());
            heightProperty.addListener((observable, oldValue, newValue) -> primaryStage.setHeight(newValue.doubleValue()));
            var timeline = new Timeline(
                new KeyFrame(Duration.millis(2000), new KeyValue(heightProperty, 300)));
            timeline.play();
            timeline.setOnFinished(finishEvent -> {
                // Example of triggering a collapse on specific nodes
                treeView.getRoot().getChildren().stream()
                    .filter(item -> item.getValue().getText().equals(TWO_ITEM))
                    .findAny()
                    .ifPresent(item -> item.setExpanded(false));
            });
        });

        // Layout setup
        BorderPane borderPane = new BorderPane(treeView);
        borderPane.setBottom(replicateBugButton);
        Scene scene = new Scene(borderPane, 800, 500);

        // Apply CSS for hover effect
        String css =
            """
            /* CSS for TreeView cell hover effect */
            .tree-cell {
              -fx-font-size: 14px;
              -fx-padding: 5px;
            }
            
            .tree-cell:hover {
              -fx-background-color: #ADD8E6; /* Light blue on hover /
              -fx-text-fill: #000000; / Black text on hover */
            }
            
            .tree-cell .label {
              -fx-font-family: Arial, sans-serif;
              -fx-font-size: 14px;
            }
            
            .tree-cell:hover .label {
              -fx-font-weight: bold;
              -fx-text-fill: #00008B; /* Dark blue text when hovered */
            }
            """;
        byte[] b = css.getBytes(Charset.forName("utf-8"));
        String stylesheet = "data:text/css;base64," + Base64.getEncoder().encodeToString(b);
        scene.getStylesheets().add(stylesheet);

        primaryStage.setScene(scene);
        primaryStage.setTitle("TreeView Hover Example");
        primaryStage.show();
    }

    private TreeItem<Label> createItems() {
        TreeItem<Label> root = new TreeItem<>(null);
        addTreeItemChildren(root, "test_", 5);

        TreeItem<Label> two = new TreeItem<>(new Label(TWO_ITEM));
        two.setExpanded(true);
        addTreeItemChildren(two, "two_", 5);
        root.getChildren().add(two);

        TreeItem<Label> three = new TreeItem<>(new Label("three"));
        three.setExpanded(true);
        addTreeItemChildren(three, "three_", 5);
        root.getChildren().add(three);

        return root;
    }

    private static void addTreeItemChildren(final TreeItem<Label> parent, String prefix, final int count) {
        for (int i = 0; i < count; i++) {
            TreeItem<Label> item = new TreeItem<>(new Label(prefix + "_" + i));
            parent.getChildren().add(item);
        }
    }
}