package goryachev.bugs;

import java.util.Locale;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * TableColumn: auto-size broken when column has graphic.
 * https://bugs.openjdk.org/browse/JDK-8186188
 */
public class TableColumn_GraphicSize_8186188 extends Application {
    private Parent getContent() {
        TableView<Locale> table = new TableView<>(
            FXCollections.observableArrayList(Locale.getAvailableLocales())
        );
        TableColumn<Locale, String> countryCode = new TableColumn<>("Code");
        countryCode.setCellValueFactory(new PropertyValueFactory<>("country"));
        // arbitrary graphic
        countryCode.setGraphic(new Button("X"));
        table.getColumns().addAll(countryCode);
        BorderPane pane = new BorderPane(table);
        return pane;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new Scene(getContent(), 800, 400));
        primaryStage.show();
    }
}