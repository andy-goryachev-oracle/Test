package goryachev.bugs;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TableViewVariableCellSizeBug extends Application {
  private static final int NUMBER_OF_COLUMNS = 5;

  private TableView<Integer> tableView;
  private final ObservableList<Integer> itemList = FXCollections.observableArrayList();

  @Override
  public void start(Stage primaryStage) {
    addNItemsToList(100000);

    System.err.println(System.getProperty("java.version"));
    System.err.println("JavaFX Version: " + System.getProperty("javafx.version"));
    System.err.println("JavaFX Runtime Version: " + System.getProperty("javafx.runtime.version"));

    tableView = new TableView<>();
    tableView.setFixedCellSize(-1);
    tableView.getColumns().addAll(Stream.generate(this::createColumn).limit(NUMBER_OF_COLUMNS).collect(Collectors.toList()));
    tableView.setItems(itemList);
    tableView.setRowFactory(table -> createStyledTableRow());
    tableView.getSelectionModel().setCellSelectionEnabled(true);

    final Button addItemButton = new Button("Add 1 Item");

    addItemButton.setOnAction(__ -> itemList.add(itemList.size()));

    final Scene scene = new Scene(new VBox(new HBox(addItemButton), tableView), 1200
        , 800);
    VBox.setVgrow(tableView, Priority.ALWAYS);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private void addNItemsToList(int n) {
    final List<Integer> items = Stream.iterate(itemList.size(), integer -> integer + 1).limit(n).collect(Collectors.toList());
    itemList.addAll(items);
  }

  private TableRow<Integer> createStyledTableRow() {
    final TableRow<Integer> row = new TableRow<>() {

      @Override
      protected void updateItem(Integer newValue, boolean empty) {
        super.updateItem(newValue, empty);
        if (!empty) {
          setPrefHeight(newValue == 29 ? 100: 25);
        }
      }
    };
    return row;
  };

  private TableColumn<Integer, ?> createColumn() {
    final TableColumn<Integer, String> column = new TableColumn<>();
    column.setCellValueFactory(dataFeature -> new SimpleStringProperty(String.valueOf(dataFeature.getValue())));
    return column;
  }
}
