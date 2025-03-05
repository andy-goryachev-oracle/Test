package goryachev.bugs;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;
import javafx.application.Platform;

/**
 * https://bugs.openjdk.org/browse/JDK-8351038
 */
public class EventType_ConcurrentModificationException_8351038 {

    public static void main(String[] args) {
        Platform.startup(() -> {
            try (var executor = Executors.newCachedThreadPool()) {
                try {
                    List<Future<Class<?>>> futures = executor.invokeAll(Stream.of(
                            "com.sun.javafx.event.RedirectedEvent",
                            "javafx.concurrent.WorkerStateEvent",
                            "javafx.css.TransitionEvent",
                            "javafx.event.ActionEvent",
                            "javafx.event.EventType",
                            "javafx.scene.control.CheckBoxTreeItem",
                            "javafx.scene.control.ChoiceBox",
                            "javafx.scene.control.ComboBoxBase",
                            "javafx.scene.control.DialogEvent",
                            "javafx.scene.control.ListView",
                            "javafx.scene.control.Menu",
                            "javafx.scene.control.MenuButton",
                            "javafx.scene.control.MenuItem",
                            "javafx.scene.control.ScrollToEvent",
                            "javafx.scene.control.SortEvent",
                            "javafx.scene.control.Tab",
                            "javafx.scene.control.TableColumn",
                            "javafx.scene.control.TreeItem",
                            "javafx.scene.control.TreeTableColumn",
                            "javafx.scene.control.TreeTableView",
                            "javafx.scene.control.TreeView",
                            "javafx.scene.input.ContextMenuEvent",
                            "javafx.scene.input.DragEvent",
                            "javafx.scene.input.GestureEvent",
                            "javafx.scene.input.InputMethodEvent",
                            "javafx.scene.input.KeyEvent",
                            "javafx.scene.input.MouseDragEvent",
                            "javafx.scene.input.MouseEvent",
                            "javafx.scene.input.RotateEvent",
                            "javafx.scene.input.ScrollEvent",
                            "javafx.scene.input.SwipeEvent",
                            "javafx.scene.input.TouchEvent",
                            "javafx.scene.input.ZoomEvent",
                            "javafx.scene.media.MediaErrorEvent",
                            "javafx.scene.transform.TransformChangedEvent",
                            "javafx.scene.web.WebErrorEvent",
                            "javafx.scene.web.WebEvent",
                            "javafx.stage.WindowEvent"
                    ).map(className -> (Callable<Class<?>>) () -> Class.forName(className)).toList());
                    for (Future<Class<?>> future : futures) {
                        future.get();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
            System.exit(0);
        });
    }
}
