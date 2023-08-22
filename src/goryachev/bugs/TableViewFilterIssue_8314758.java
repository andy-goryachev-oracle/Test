package goryachev.bugs;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.stream.Stream;

// https://bugs.openjdk.org/browse/JDK-8314758
public class TableViewFilterIssue_8314758 extends Application {

    @Override
    public void start(Stage primaryStage) {
        final ObservableList<Integer> itemList = FXCollections.observableArrayList();
        itemList.addAll(Stream.iterate(itemList.size(), i -> i + 1).limit(100000).toList());

        final FilteredList<Integer> filteredList = new FilteredList<>(itemList);

        final TableColumn<Integer, String> column = new TableColumn<>();
        column.setCellValueFactory(p -> new SimpleStringProperty(String.valueOf(p.getValue() + 1)));
        column.setPrefWidth(200);

        TableView<Integer> tableView = new TableView<>();
        tableView.getColumns().add(column);
        tableView.setItems(filteredList);

        final Button filterButton = new Button("Filter Odd Items");

        // 1. Scroll to the very bottom of the table, select last item 100000
        filterButton.setOnAction(e -> {
            if (filteredList.getPredicate() == null) {
                // 2. Click the button once to apply the filter:
                // visible cells remain around that item (expected)
                filteredList.setPredicate(i -> i % 2 == 0);
            } else {
                // 3. Click the button again to remove the filter:
                // the tableView shows items around 50000 (unexpected)
                filteredList.setPredicate(null);
            }
        });

        final Scene scene = new Scene(new VBox(filterButton, tableView), 600, 400);
        VBox.setVgrow(tableView, Priority.ALWAYS);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}