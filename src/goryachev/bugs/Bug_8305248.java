package goryachev.bugs;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Bug_8305248 extends Application {

    @Override
    public void start(Stage stage) {
        CheckBox checkBox = new CheckBox("Show column 2");

        TableView<Person> tableView = new TableView<>();
        tableView.setFixedCellSize(24);

        TableColumn<Person, String> column1 = new TableColumn<>("First Name");
        column1.setCellValueFactory((p) -> p.getValue().firstName);

        TableColumn<Person, String> column2 = new TableColumn<>("Last Name");
        column2.setCellValueFactory((p) -> p.getValue().lastName);
        column2.visibleProperty().bind(checkBox.selectedProperty());

        TableColumn<Person, String> column3 = new TableColumn<>("Gender");
        column3.setCellValueFactory((p) -> p.getValue().gender);

        tableView.getColumns().addAll(column1, column2, column3);

        tableView.getItems().add(new Person("John", "Doe", "male"));
        tableView.getItems().add(new Person("Jane", "Deer", "female"));
        tableView.getItems().add(new Person("Robert", "Planck", "male"));

        VBox root = new VBox(10, tableView, checkBox);
        root.setPadding(new Insets(10));
        stage.setScene(new Scene(root, 320, 280));
        stage.show();
    }

    private static class Person {
        public StringProperty firstName = new SimpleStringProperty("");
        public StringProperty lastName = new SimpleStringProperty("");
        public StringProperty gender = new SimpleStringProperty("");

        public Person(String firstName, String lastName, String gender) {
            this.firstName.set(firstName);
            this.lastName.set(lastName);
            this.gender.set(gender);
        }
    }

}