package goryachev.bugs;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8353548
 */
public class DragAndDrop_WrongCoord_8353548 extends Application {

    private static final boolean useWorkaround = false;

    @Override
    public void start(Stage stage) {

        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        Scene scene = new Scene(root, 200, 300);

        Text text = new Text(20, 20, "Drag me to the Rectangle");
        text.setOnDragDetected(event -> {
            Dragboard db = text.startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();
            content.putString("Done");
            db.setContent(content);
        });

        Rectangle rectangle = new Rectangle(100, 100, Color.LIGHTGREEN);
        rectangle.setOnDragOver((ev) -> {
            ev.acceptTransferModes(TransferMode.COPY);
        });
        rectangle.setOnDragDropped((ev) -> {
            StackPane dialogRoot = new StackPane(new Text(ev.getDragboard().getString()));
            Scene dialogScene = new Scene(dialogRoot, 100, 30);
            Stage dialog = new Stage();
            dialog.setScene(dialogScene);
            if (useWorkaround) {
                dialog.setX(stage.getX() + scene.getX() + ev.getSceneX());
                dialog.setY(stage.getY() + scene.getY() + ev.getSceneY());
            } else {
                dialog.setX(ev.getScreenX());
                dialog.setY(ev.getScreenY());
            }
            dialog.show();
        });

        root.getChildren().addAll(text, rectangle);
        stage.setScene(scene);
        stage.show();
    }
}