package goryachev.research;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Labeled;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.stage.Stage;
import javafx.util.converter.DefaultStringConverter;

/**
 * Example illustrates using the textTruncated property to show the full text of the cell with a tooltip when truncated.
 * https://bugs.openjdk.org/browse/JDK-8092102
 * https://bugs.openjdk.org/browse/JDK-8205211
 */
public class TreeTableView_TextTruncatedTooltip_Example extends Application {

    private final TreeTableView<Person> tree = new TreeTableView<>();

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
        stage.setTitle("Table View Sample");
        stage.setWidth(450);
        stage.setHeight(550);

        tree.setEditable(true);

        TreeTableColumn<Person, String> emailCol = new TreeTableColumn<>("Email");
        emailCol.setCellValueFactory(cf -> {
            Person it = cf.getValue().getValue();
            return it == null ? null : it.email;
        });
        emailCol.setCellFactory((tc) -> new TextFieldTreeTableCell<>(new DefaultStringConverter()) {
            {
//                textTruncatedProperty().addListener((s, p, on) -> {
//                    if (on) {
//                        setTooltip(new Tooltip(getText()));
//                    } else {
//                        setTooltip(null);
//                    }
//                });
            }
        });

        TreeItem<Person> root = new TreeItem<>(null);
        for(Person p: data) {
            root.getChildren().add(new TreeItem(p));
        }
        tree.setRoot(root);
        tree.setShowRoot(true);
        tree.getColumns().addAll(emailCol);

        Scene scene = new Scene(tree);
        stage.setScene(scene);
        stage.show();
    }

    public static class Person {
        public final StringProperty email;

        private Person(String email) {
            this.email = new SimpleStringProperty(email);
        }
    }
}