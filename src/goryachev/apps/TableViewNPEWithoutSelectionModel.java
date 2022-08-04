package goryachev.apps;
import java.util.Locale;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * TableView with null selectionModel: NPE on sorting
 * 
 * To reproduce:

- run
- click header
- expected: data sorted
- actual: NPE thrown
 * 
 * https://bugs.openjdk.org/browse/JDK-8187145
 */
public class TableViewNPEWithoutSelectionModel extends Application {

    private Parent getContent() {
        ObservableList<Locale> data = FXCollections.observableArrayList(
                Locale.getAvailableLocales());
        SortedList<Locale> sorted = new SortedList<>(data);
        TableView<Locale> table = new TableView<>(sorted);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setSelectionModel(null);
        TableColumn<Locale, String> countryCode = new TableColumn<>("CountryCode");
        countryCode.setCellValueFactory(new PropertyValueFactory<>("country"));
        table.getColumns().addAll(countryCode);
        BorderPane pane = new BorderPane(table);
        return pane;
    }

    @Override
    public void start(Stage s) throws Exception {
        s.setTitle(getClass().getSimpleName() + " " + System.getProperty("java.version"));
        s.setWidth(600);
        s.setScene(new Scene(getContent(), 800, 400));
        s.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}