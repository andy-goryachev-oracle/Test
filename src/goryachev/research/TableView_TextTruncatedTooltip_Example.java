package goryachev.research;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.DefaultStringConverter;

/**
 * Example illustrates using the textTruncated property to show the full text of the cell with a tooltip when truncated.
 * https://bugs.openjdk.org/browse/JDK-8092102
 * https://bugs.openjdk.org/browse/JDK-8205211
 */
public class TableView_TextTruncatedTooltip_Example extends Application {

    private final TableView<Person> table = new TableView<>();

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

        table.setEditable(true);

        TableColumn<Person, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(param -> param.getValue().email);
        emailCol.setCellFactory((tableColumn) -> new TextFieldTableCell<>(new DefaultStringConverter()) {
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
        emailCol.setOnEditCommit(t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).email.set(t.getNewValue()));

        table.setItems(data);
        table.getColumns().addAll(emailCol);

        Scene scene = new Scene(table);
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