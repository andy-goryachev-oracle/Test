package goryachev.bugs;
import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import goryachev.bugs.TableViewEditNotWritable_8187314.Dummy;

// https://bugs.openjdk.org/browse/JDK-8313956
public class TableViewFocusWithinWriteable_8313957 extends Application {

    @Override
    public void start(Stage primaryStage) {
        TableView<Entry> tableView = new TableView<>();
        tableView.setFixedCellSize(24);
        tableView.setPadding(new Insets(5));

        tableView.setEditable(true);
        tableView.setItems(Entry.all());
        tableView.focusWithinProperty().addListener(inv -> {
            if (tableView.isFocusWithin()) {
                tableView.setBackground(Background.fill(Color.GREEN));
            } else {
                tableView.setBackground(Background.fill(Color.RED));
            }
        });

        TableColumn<Entry, String> col1 = new TableColumn<>("checkbox");
        col1.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().toString()));
        col1.setCellFactory(CheckBoxTableCell.forTableColumn(idx -> new SimpleBooleanProperty(false)));

        TableColumn<Entry, String> col2 = new TableColumn<>("text");
        col2.setCellValueFactory((d) -> new SimpleStringProperty(d.getValue().toString()));

        TableColumn<Entry, String> col3 = new TableColumn<>("text2");
        col3.setCellValueFactory((d) -> d.getValue().f1);

        TableColumn<Entry, String> col4 = new TableColumn<>("text3");
        col4.setCellValueFactory((d) -> d.getValue().f2);
        col4.setCellFactory(TextFieldTableCell.forTableColumn());

        tableView.getColumns().addAll(col1, col2, col3, col4);

        BorderPane pane = new BorderPane(tableView);

        Button focusLost = new Button("focus lost");
        BorderPane.setMargin(focusLost, new Insets(5, 0, 0, 0));

        pane.setBottom(focusLost);

        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public static class Entry {
        public final SimpleStringProperty f1;
        public final SimpleStringProperty f2;

        public Entry(String text) {
            this.f1 = new SimpleStringProperty(text + "1");
            this.f2 = new SimpleStringProperty(text + "2");
        }

        public static ObservableList<Entry> all() {
            return FXCollections.observableArrayList(
                new Entry("1"),
                new Entry("2"),
                new Entry("3")
            );
        }
    }
}
