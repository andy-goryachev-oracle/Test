package goryachev.bugs;

import javafx.application.Application;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8357247
 */
public class TableView_ExtractorShiftsSelection_8357247 extends Application {

    private final ObservableList<Person> data = FXCollections.observableArrayList((p) -> {
        return new Observable[] { p.firstName };
    });
    private final TableView<Person> tableView = new TableView<>(data);

    @Override
    public void start(Stage stage) {
        TableColumn<Person, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setMinWidth(100);
        firstNameCol.setCellValueFactory(p -> p.getValue().firstName);
        firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<Person, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setMinWidth(100);
        lastNameCol.setCellValueFactory(p -> p.getValue().lastName);

        TableColumn<Person, String> emailCol = new TableColumn<>("Email");
        emailCol.setMinWidth(200);
        emailCol.setCellValueFactory(p -> p.getValue().email);

        tableView.setEditable(true);
        tableView.getSelectionModel().setCellSelectionEnabled(true);
        tableView.getColumns().addAll(firstNameCol, lastNameCol, emailCol);
        tableView.getSelectionModel().getSelectedCells().addListener((ListChangeListener<TablePosition>)(ch) -> {
            System.out.println("change=" + ch);   
        });

        Scene scene = new Scene(tableView, 450, 350);
        stage.setScene(scene);
        stage.setTitle("Table View Sample");
        stage.show();

        data.setAll(
            new Person("Jacob", "Smith", "jacob.smith@example.com"),
            new Person("Isabella", "Johnson", "isabella.johnson@example.com"),
            new Person("Ethan", "Williams", "ethan.williams@example.com"));
    }

    static class Person {
        private final StringProperty firstName;
        private final StringProperty lastName;
        private final StringProperty email;

        private Person(String fName, String lName, String email) {
            this.firstName = new SimpleStringProperty(fName);
            this.lastName = new SimpleStringProperty(lName);
            this.email = new SimpleStringProperty(email);
        }
    }
}