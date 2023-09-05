package goryachev.bugs;
import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
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

// https://bugs.openjdk.org/browse/JDK-8313956
public class TableViewFocusWithin_8313956 extends Application {

    @Override
    public void start(Stage primaryStage) {
        TableView<String> tableView = new TableView<>();
        tableView.setFixedCellSize(24);
        tableView.setPadding(new Insets(5));

        tableView.setEditable(true);
        tableView.setItems(FXCollections.observableArrayList("a", "b", "c", "d", "e", "f", "g", "h", "i"));
        tableView.focusWithinProperty().addListener(inv -> {
            if (tableView.isFocusWithin()) {
                tableView.setBackground(Background.fill(Color.GREEN));
            } else {
                tableView.setBackground(Background.fill(Color.RED));
            }
        });

        TableColumn<String, String> col1 = new TableColumn<>("checkbox");
        col1.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));
        col1.setCellFactory(CheckBoxTableCell.forTableColumn(idx -> new SimpleBooleanProperty(false)));

        TableColumn<String, String> col2 = new TableColumn<>("text");
        col2.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));

        TableColumn<String, String> col3 = new TableColumn<>("text2");
        col3.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));

        TableColumn<String, String> col4 = new TableColumn<>("text3");
        col4.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));
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
}
