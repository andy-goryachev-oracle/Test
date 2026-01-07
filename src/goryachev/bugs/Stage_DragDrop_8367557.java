package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HeaderBar;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/// https://bugs.openjdk.org/browse/JDK-8367557
///
public class Stage_DragDrop_8367557 extends Application {

    @Override
    public void start(Stage primaryStage) {

        primaryStage.initStyle(StageStyle.EXTENDED);

        // Create drag source
        Label dragSource = new Label("Drag me!");
        dragSource.setStyle("-fx-background-color: lightblue; -fx-padding: 10px");

        // Create drop target
        Label dropTarget = new Label("Drop here!");
        dropTarget.setStyle(
            "-fx-background-color: lightcoral; -fx-padding: 20px; -fx-min-width: 150px; -fx-min-height: 100px;");

        // Set up drag detection
        dragSource.setOnDragDetected(event -> {
            Dragboard dragboard = dragSource.startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();
            content.putString("Hello from drag source!");
            dragboard.setContent(content);
            event.consume();
        });

        // Set up drag over
        dropTarget.setOnDragOver(event -> {
            if (event.getGestureSource() != dropTarget && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        // Set up drag entered (visual feedback)
        dropTarget.setOnDragEntered(event -> {
            if (event.getGestureSource() != dropTarget && event.getDragboard().hasString()) {
                dropTarget.setStyle(
                    "-fx-background-color: lightgreen; -fx-padding: 20px; -fx-min-width: 150px; -fx-min-height: 100px;");
            }
            event.consume();
        });

        // Set up drag exited (reset visual feedback)
        dropTarget.setOnDragExited(event -> {
            dropTarget.setStyle(
                "-fx-background-color: lightcoral; -fx-padding: 20px; -fx-min-width: 150px; -fx-min-height: 100px;");
            event.consume();
        });

        // Set up drop
        dropTarget.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;

            if (dragboard.hasString()) {
                dropTarget.setText("Dropped: " + dragboard.getString());
                success = true;
            }

            event.setDropCompleted(success);
            event.consume();
        });

        // Layout
        HBox layout = new HBox(20);
        layout.getChildren().addAll(dragSource, dropTarget);
        layout.setStyle("-fx-padding: 20px;");

        var root = new BorderPane();
        root.setTop(new HeaderBar());
        root.setCenter(layout);

        Scene scene = new Scene(root, 400, 200);

        // Test with different stage styles
        // Change this line to test different styles:
        // primaryStage.initStyle(StageStyle.DECORATED); // Try: UNDECORATED, UTILITY, TRANSPARENT, etc.

        primaryStage.setTitle("Drag and Drop Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}