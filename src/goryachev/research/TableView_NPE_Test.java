package goryachev.research;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

// NPE scenario for PR2129
public class TableView_NPE_Test extends Application {

    private TableView<String> table;

    private final ObservableList<String> data = FXCollections.observableArrayList("A");

    @Override
    public void start(Stage stage) {
        table = new TableView<>(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);

        TableColumn<String, String> col = new TableColumn<>("Column 1");
        col.setCellValueFactory((f) -> {
            return new SimpleStringProperty(f.getValue());
        });
        col.setCellFactory((tp) -> {
            TableCell<String,String> cell = new TableCell<>();
            cell.addEventFilter(MouseEvent.MOUSE_PRESSED, (ev) -> {
                table.setItems(null);
            });
            return cell;
        });
        table.getColumns().add(col);

        stage.setScene(new Scene(table, 500, 500));
        stage.show();
    }
}