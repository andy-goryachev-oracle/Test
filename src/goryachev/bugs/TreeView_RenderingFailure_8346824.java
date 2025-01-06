package goryachev.bugs;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

// https://bugs.openjdk.org/browse/JDK-8346824
public class TreeView_RenderingFailure_8346824 extends Application {
    private static final String TWO_ITEM = "two";

    @Override
    public void start(final Stage primaryStage) throws Exception {
        System.err.println(System.getProperty("javafx.version"));

        final TreeView<Label> itemTreeView = new TreeView<>();
        itemTreeView.setShowRoot(false);
        itemTreeView.setRoot(createItems());

        final var replicateTheBug = new Button("Replicate the bug");
        replicateTheBug.setOnAction(e -> Platform.runLater(() -> {
            final var heightProperty = new SimpleDoubleProperty(primaryStage.getHeight());
            heightProperty.addListener((observable, oldValue, newValue) -> {
                primaryStage.setHeight(newValue.doubleValue());
            });
            final var timeline = new Timeline(
                new KeyFrame(Duration.millis(2000),
                    new KeyValue(heightProperty, 300, Interpolator.EASE_BOTH)));
            timeline.play();
            timeline.setOnFinished(finishEvent -> {
                itemTreeView.getRoot().getChildren().stream().filter(item -> {
                    final var value = item.getValue();
                    return value != null && TWO_ITEM.equals(value.getText());
                }).findAny().ifPresent(i -> i.setExpanded(false));
            });
        }));
        final BorderPane borderPane = new BorderPane(itemTreeView);
        borderPane.setBottom(replicateTheBug);
        borderPane.setPadding(new Insets(5));
        Scene scene = new Scene(borderPane, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private TreeItem<Label> createItems() {
        final TreeItem<Label> root = new TreeItem<>(null);
        addTreeItemChildren(root, "test_", 5);
        final var two = new TreeItem<Label>(new Label(TWO_ITEM));
        two.setExpanded(true);
        addTreeItemChildren(two, "two_", 5);
        root.getChildren().add(two);
        final var three = new TreeItem<Label>(new Label("three"));
        three.setExpanded(true);
        addTreeItemChildren(three, "three_", 5);
        root.getChildren().add(three);
        return root;
    }

    private static void addTreeItemChildren(final TreeItem<Label> parent, String prefix, final int count) {
        for (int i = 0; i < count; i++) {
            final var item = new TreeItem<Label>(new Label(prefix + "_" + i));
            parent.getChildren().add(item);
        }
    }
}