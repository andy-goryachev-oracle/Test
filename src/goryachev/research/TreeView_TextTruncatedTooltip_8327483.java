package goryachev.research;
import java.util.Set;
import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Example illustrates using the textTruncated property to show the full text of the cell with a tooltip when truncated.
 * https://bugs.openjdk.org/browse/JDK-8327483
 */
public class TreeView_TextTruncatedTooltip_8327483 extends Application {

    private final TreeView<Person> tree = new TreeView<>();
    private final SimpleBooleanProperty hsbVisible = new SimpleBooleanProperty();

    private final ObservableList<Person> data = FXCollections.observableArrayList(
        new Person("jacob.smith@example.com"),
        new Person("isabella.johnson@example.com"),
        new Person("ethan.williams@example.com"),
        new Person("emma.jones@example.com"),
        new Person("michael.brown@example.com")
    );

//    public static void main(String[] args) {
//        launch(args);
//    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("TreeView Tooltip JDK-8327483");
        stage.setWidth(450);
        stage.setHeight(550);

        tree.setEditable(true);
        tree.setCellFactory((tc) -> new TextFieldTreeCell<>() {
            {
                hsbVisible.addListener((s, p, on) -> {
                    String text = getText();
                    if (isBlank(text)) {
                        on = false;
                    }
                    setTooltip(on ? new Tooltip(text) : null);
                });
            }
        });
        tree.skinProperty().addListener((src, prev, cur) -> {
            if (prev != null) {
                hsbVisible.unbind();
            }
            if (cur != null) {
                hsbVisible.bind(getHorizontalScrollBar().visibleProperty());
            }
        });

        TreeItem<Person> root = new TreeItem<>(null);
        for(Person p: data) {
            root.getChildren().add(new TreeItem(p));
        }
        tree.setRoot(root);
        tree.setShowRoot(true);

        BorderPane bp = new BorderPane(tree);

        Scene scene = new Scene(bp);
        stage.setScene(scene);
        stage.show();
    }

    private ScrollBar getHorizontalScrollBar() {
        Set<Node> nodes = tree.lookupAll(".scroll-bar");
        for (Node n: nodes) {
            if (n instanceof ScrollBar sb) {
                if (sb.getOrientation() == Orientation.HORIZONTAL) {
                    return sb;
                }
            }
        }
        return null;
    }

    private static boolean isBlank(String text) {
        if (text != null) {
            return text.trim().length() == 0;
        }
        return true;
    }

    public static class Person {
        public final StringProperty email;

        private Person(String email) {
            this.email = new SimpleStringProperty(email);
        }
        
        @Override
        public String toString() {
            return email.get();
        }
    }
}