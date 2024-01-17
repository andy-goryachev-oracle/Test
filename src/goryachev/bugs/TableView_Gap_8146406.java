package goryachev.bugs;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import goryachev.util.FX;

/**
 * https://bugs.openjdk.org/browse/JDK-8146406
 */
public class TableView_Gap_8146406 extends Application {

    List<TableColumn<Person, ?>> list = new ArrayList<>();

    private TableView<Person> table = new TableView<Person>();
    private final ObservableList<Person> data = FXCollections.observableArrayList(
        new Person("Jacob", "Smith", "jacob.smith@example.com"),
        new Person("Isabella", "Johnson", "isabella.johnson@example.com"),
        new Person("Ethan", "Williams", "ethan.williams@example.com"),
        new Person("Emma", "Jones", "emma.jones@example.com"),
        new Person("Michael", "Brown", "michael.brown@example.com"));
    final HBox hb = new HBox();

    @Override
    public void start(Stage stage) {
        final Label label = new Label("Address Book");
        label.setFont(new Font("Arial", 20));

        table.setEditable(true);
        table.getSelectionModel().setCellSelectionEnabled(true);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        for (int i = 0; i < 1000; ++i) {
            data.add(new Person("Jacob", "Smith", "jacob.smith@example.com"));
        }
        addColumns();
        table.setItems(data);

        Scene scene = new Scene(table);
        String css = ".table-cell { -fx-border-width:0px; -fx-background-color: black; }";
        scene.getStylesheets().add(FX.encodeStylesheet(css));

        stage.setTitle("Table View Sample");
        stage.setWidth(900);
        stage.setHeight(900);
        stage.setScene(scene);
        stage.show();
    }

    public void addColumns() {
        for (int i = 0; i < 20; ++i) {
            TableColumn firstNameCol = new TableColumn("First Name");
            firstNameCol.setMinWidth(100);
            firstNameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("firstName"));
            firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
            list.add(firstNameCol);
        }

        table.getColumns().addAll(list);
    }

    public static class Person {

        private final SimpleStringProperty firstName;
        private final SimpleStringProperty lastName;
        private final SimpleStringProperty email;

        private Person(String fName, String lName, String email) {
            this.firstName = new SimpleStringProperty(fName);
            this.lastName = new SimpleStringProperty(lName);
            this.email = new SimpleStringProperty(email);
        }

        public String getFirstName() {
            return firstName.get();
        }

        public void setFirstName(String fName) {
            firstName.set(fName);
        }

        public String getLastName() {
            return lastName.get();
        }

        public void setLastName(String fName) {
            lastName.set(fName);
        }

        public String getEmail() {
            return email.get();
        }

        public void setEmail(String fName) {
            email.set(fName);
        }
    }
}