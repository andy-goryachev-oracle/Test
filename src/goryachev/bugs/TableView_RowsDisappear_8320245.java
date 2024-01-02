package goryachev.bugs;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Rows disappear in TableView on rare circumstances when vertical scroll bar appear
 * https://bugs.openjdk.org/browse/JDK-8320245
 */
public class TableView_RowsDisappear_8320245 extends Application {
    private static int count = 15;

    @Override
    public void start(Stage primaryStage) throws Exception {
        VBox vbox = new VBox();
        Scene scene = new Scene(vbox);
        ToolBar toolBar = new ToolBar();
        Button button1 = new Button("Button1");
        toolBar.getItems().add(button1);
        Button button2 = new Button("Button2");
        toolBar.getItems().add(button2);
        vbox.getChildren().add(toolBar);
        ObservableList<Person> data = FXCollections.observableArrayList();
        for (int i = 0; i < count; i++)
            data.add(new Person("Jacob", "Smith", "jacob.smith@example.com"));
        TableView<Person> table = new TableView<Person>();
        TableColumn<Person, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setMinWidth(100);
        firstNameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("fname"));
        TableColumn<Person, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setMinWidth(100);
        lastNameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("lname"));
        TableColumn<Person, String> emailCol = new TableColumn<>("Email");
        emailCol.setMinWidth(200);
        emailCol.setCellValueFactory(new PropertyValueFactory<Person, String>("email"));
        table.setItems(data);
        table.getColumns().add(firstNameCol);
        table.getColumns().add(lastNameCol);
        table.getColumns().add(emailCol);
        vbox.getChildren().add(table);
        VBox.setVgrow(table, Priority.ALWAYS);
        ChangeListener<Person> changeListener = new ChangeListener<Person>() {
            @Override
            public void changed(ObservableValue<? extends Person> observable, Person oldValue, Person newValue) {
                System.out.println(scene.getWindow().getWidth() + " " + scene.getWindow().getHeight());
            }
        };
        table.getSelectionModel().selectedItemProperty().addListener(changeListener);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setWidth(402);
        primaryStage.setHeight(475); //488);
    }

    public static void run(String[] args) {
        if (args.length == 1)
            count = Integer.parseInt(args[0]);
        launch(args);
    }

    public static class Person {
        private String fname;
        private String lname;
        private String email;
        private static int seq;

        public Person(String fname, String lname, String email) {
            this.fname = String.valueOf(seq++);
            this.lname = lname;
            this.email = email;
        }

        public String getFname() {
            return fname;
        }

        public String getLname() {
            return lname;
        }

        public String getEmail() {
            return email;
        }
    }
}
