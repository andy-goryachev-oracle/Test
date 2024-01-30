package goryachev.bugs;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8324939
 */
public class TableView_FocusCommit_8324939 extends Application {

    @Override
    public void start(Stage stage) {
        TableView<Person> tableView = new TableView<>();
        tableView.setPadding(new Insets(2));
        tableView.setEditable(true);
        tableView.focusedProperty().subscribe(nv -> tableView.setBackground(Background.fill(nv ? Color.GREEN : Color.RED)));

        TableColumn<Person, String> column1 = new TableColumn<>("First Name");
        column1.setCellValueFactory((p) -> p.getValue().firstName);

        TableColumn<Person, String> column2 = new TableColumn<>("Last Name");
        column2.setCellValueFactory((p) -> p.getValue().lastName);
        column2.setCellFactory(TextFieldTableCell.forTableColumn());

        tableView.getColumns().addAll(column1, column2);

        tableView.getItems().add(new Person("John", "Doe"));
        tableView.getItems().add(new Person("Jane", "Deer"));

        Scene scene = new Scene(new VBox(new TextField(), tableView), 400, 300);
        stage.setScene(scene);
        stage.show();
    }

    private static class Person {
        public final StringProperty firstName = new SimpleStringProperty("");
        public final StringProperty lastName = new SimpleStringProperty("");

        public Person(String firstName, String lastName) {
            this.firstName.set(firstName);
            this.lastName.set(lastName);
        }
    }
}