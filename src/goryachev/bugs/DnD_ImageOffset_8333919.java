package goryachev.bugs;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8333919
 */
public class DnD_ImageOffset_8333919 extends Application {
    @Override
    public void start(Stage stage) {
        Label source = new Label("DRAG ME");
        source.setStyle("-fx-font-size: 24; -fx-border-width:1; -fx-border-color:red;");

        Label target = new Label("DROP HERE");
        target.setStyle("-fx-font-size: 24; -fx-border-width:1; -fx-border-color:red;");

        HBox hb = new HBox(5, source, target);
        hb.setAlignment(Pos.CENTER);

        source.setOnDragDetected(event -> {
            Dragboard db = source.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString(source.getText());
            db.setContent(content);
            db.setDragView(source.snapshot(null, null));

            // set any value here, but on macOS it will be ignored
            db.setDragViewOffsetX(-30);
            db.setDragViewOffsetY(20);
        });

        target.setOnDragOver(event -> {
            if (event.getGestureSource() != target && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
        });

        Scene scene = new Scene(hb, 400, 200);
        stage.setScene(scene);
        stage.show();
    }
}