package goryachev.bugs;

import java.util.List;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8331464
 */
public class TableView_FocusWithin_8331464 extends Application {

    private static class Student {

        private int id;

        public Student(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    private TableView<Student> leftTable = new TableView<>(FXCollections.observableList(List.of(
        new Student(1),
        new Student(2),
        new Student(3),
        new Student(4),
        new Student(5)
    )));

    private TableView<Student> rightTable = new TableView<>(FXCollections.observableList(List.of(
        new Student(6),
        new Student(7),
        new Student(8),
        new Student(9),
        new Student(10)
    )));

    @Override
    public void start(Stage primaryStage) {
        var leftColumn = new TableColumn<Student, Integer>();
        leftColumn.setCellValueFactory((data) -> new ReadOnlyObjectWrapper<>(data.getValue().getId()));
        leftTable.getColumns().add(leftColumn);
        leftTable.focusWithinProperty().addListener((ov, oldV, newV) -> {
            System.out.println("LEFT  focusWithin=" + newV);
        });
        leftTable.focusedProperty().addListener((ov, oldV, newV) -> {
            System.out.println("LEFT  focused=" + newV);
        });

        var rightColumn = new TableColumn<Student, Integer>();
        rightColumn.setCellValueFactory((data) -> new ReadOnlyObjectWrapper<>(data.getValue().getId()));
        rightTable.getColumns().add(rightColumn);
        rightTable.focusWithinProperty().addListener((ov, oldV, newV) -> {
            System.out.println("RIGHT focusWithin=" + newV);
        });
        rightTable.focusedProperty().addListener((ov, oldV, newV) -> {
            System.out.println("RIGHT focused=" + newV);
        });

        HBox root = new HBox(leftTable, rightTable);
        var scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
