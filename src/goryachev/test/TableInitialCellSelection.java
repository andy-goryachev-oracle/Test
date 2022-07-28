package goryachev.test;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

// https://bugs.openjdk.org/browse/JDK-8219720
public class TableInitialCellSelection extends Application {

    private Parent createContent() {
        ObservableList<Locale> data = FXCollections.observableArrayList(
                Arrays.stream(Locale.getAvailableLocales(), 10, 20).collect(Collectors.toList()));
        TableView<Locale> table = new TableView<>(data);
        table.getColumns().addAll(createTableColumn("displayLanguage"), createTableColumn("displayCountry"));
        table.getSelectionModel().setCellSelectionEnabled(true);
        return new BorderPane(table);
    }

    private <T> TableColumn<T, String> createTableColumn(String property) {
        TableColumn<T, String> column = new TableColumn<>(property);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        return column;
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(createContent()));
        //stage.setTitle(FXUtils.version());
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}