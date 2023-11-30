package goryachev.tests;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SortAndFilterTableViewExample extends Application {

    @Override
    public void start(Stage primaryStage) {
        ObservableList<Person> data = FXCollections.observableArrayList(
                new Person("John", "Doe", 25),
                new Person("Jane", "Smith", 30),
                new Person("Bob", "Johnson", 22)
        );

        TableView<Person> tableView = new TableView<>();

        TableColumn<Person, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        firstNameColumn.setSortable(true);

        TableColumn<Person, String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        lastNameColumn.setSortable(true);

        TableColumn<Person, Integer> ageColumn = new TableColumn<>("Age");
        ageColumn.setCellValueFactory(cellData -> cellData.getValue().ageProperty());
        ageColumn.setSortable(true);

        tableView.getColumns().addAll(firstNameColumn, lastNameColumn, ageColumn);

        FilteredList<Person> filteredData = new FilteredList<>(data);

        tableView.setItems(FXCollections.observableList(filteredData));

        TextField filterTextField = new TextField();
        filterTextField.setPromptText("Filter");

        filterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(person ->
                    person.getFirstName().toLowerCase().contains(newValue.toLowerCase()) ||
                            person.getLastName().toLowerCase().contains(newValue.toLowerCase()) ||
                            String.valueOf(person.getAge()).toLowerCase().contains(newValue.toLowerCase()));
        });

        // Set up the layout
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(filterTextField, tableView);

        // Set up the JavaFX scene
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Sortable and Filterable TableView Example");
        primaryStage.show();
    }

    public static class Person {
        private final StringProperty firstName = new SimpleStringProperty();
        private final StringProperty lastName =  new SimpleStringProperty();
        private final ObjectProperty<Integer> age = new SimpleObjectProperty<>();

        public Person(String firstName, String lastName, Integer age) {
            this.firstName.set(firstName);
            this.lastName.set(lastName);
            this.age.set(age);
        }

        public String getFirstName() {
            return firstName.get();
        }

        public StringProperty firstNameProperty() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName.set(firstName);
        }

        public String getLastName() {
            return lastName.get();
        }

        public StringProperty lastNameProperty() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName.set(lastName);
        }

        public Integer getAge() {
            return age.get();
        }

        public ObjectProperty<Integer> ageProperty() {
            return age;
        }

        public void setAge(Integer age) {
            this.age.set(age);
        }
    }
}