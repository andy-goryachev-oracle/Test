package goryachev.bugs;

import java.util.List;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8331463
 */
public class TableView_ColumnCss_8331463 extends Application {

    private static class Student {
        private int id;
        private int mark;

        public Student(int id, int mark) {
            this.id = id;
            this.mark = mark;
        }

        public int getId() {
            return id;
        }

        public int getMark() {
            return mark;
        }
    }

    private TableView<Student> table = new TableView<>(FXCollections.observableArrayList(
        new Student(1, 3),
        new Student(2, 4),
        new Student(3, 5)
    ));

    @Override
    public void start(Stage primaryStage) {
        var idColumn = new TableColumn<Student, Integer>();
        idColumn.setCellValueFactory((data) -> new ReadOnlyObjectWrapper<>(data.getValue().getId()));
        var markColumn = new TableColumn<Student, Integer>();
        markColumn.setCellValueFactory((data) -> new ReadOnlyObjectWrapper<>(data.getValue().getMark()));

        boolean works = !true;
        if (works) {
            markColumn.setMaxWidth(200);
            markColumn.setMinWidth(200);
        } else {
            markColumn.setStyle("-fx-min-width: 200; -fx-max-width: 200;");
        }

        table.getColumns().addAll(idColumn, markColumn);

        VBox root = new VBox(table);
        var scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}