package goryachev.bugs;

import java.util.List;
import java.util.stream.Stream;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8334038
 */
public class TableView_MissingContentScrolling_8334038 extends Application {
    private static final int NUMBER_OF_COLUMNS = 20;

    private TableView<Integer> tableView;
    private final ObservableList<Integer> itemList = FXCollections.observableArrayList();
    private final FilteredList<Integer> filteredList = new FilteredList<>(itemList);

    @Override
    public void start(Stage primaryStage) {
        addNItemsToList(10);

        tableView = new TableView<>();
        tableView.setFixedCellSize(50);
        tableView.getColumns().addAll(Stream.generate(this::createColumn).limit(NUMBER_OF_COLUMNS).toList());
        tableView.setItems(filteredList);

        final Scene scene = new Scene(tableView, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        Platform.runLater(() -> {
            tableView.scrollToColumn(tableView.getColumns().get(NUMBER_OF_COLUMNS - 1));
            itemList.clear();
            addNItemsToList(10);
        });
    }

    private void addNItemsToList(int n) {
        final List<Integer> items = Stream.iterate(itemList.size(), integer -> integer + 1).limit(n).toList();
        itemList.addAll(items);
    }

    private TableColumn<Integer, ?> createColumn() {
        final TableColumn<Integer, String> column = new TableColumn<>();
        column.setCellValueFactory(dataFeature -> new SimpleStringProperty(String.valueOf(dataFeature.getValue())));
        return column;
    }
}