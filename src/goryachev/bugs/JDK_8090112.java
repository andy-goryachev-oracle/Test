package goryachev.bugs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 *
 */
public class JDK_8090112 extends Application {
    @Override
    public void start(Stage stage) {
        final AnchorPane root = new AnchorPane();
        root.setPadding(new Insets(10));
        final HBox controls = new HBox(5);
        final Button getDataButton = new Button("load data");
        final TextField textField = new TextField();
        final TableView<Map> table = new TableView<>();

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        getDataButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DataSource dataSource = new DataSource();
                table.getColumns().clear();
                table.getColumns().addAll(dataSource.getTableColumns());
                table.setItems(dataSource.getQueryResult());
            }
        });
        controls.getChildren().addAll(getDataButton, textField);
        AnchorPane.setTopAnchor(controls, 10.0);
        AnchorPane.setLeftAnchor(controls, 10.0);
        AnchorPane.setRightAnchor(controls, 10.0);
        AnchorPane.setTopAnchor(table, 45.0);
        AnchorPane.setRightAnchor(table, 10.0);
        AnchorPane.setLeftAnchor(table, 10.0);
        AnchorPane.setBottomAnchor(table, 10.0);
        root.getChildren().addAll(controls, table);
        stage.setScene(new Scene(root, 1000, 1000));
        stage.setTitle("JDK-8090112 " + System.getProperty("java.version"));
        stage.show();
    }

    public static class DataSource {
        public List<TableColumn<Map,Object>> getTableColumns() {
            TableColumn<Map,Object> idColumn = new TableColumn<>("ID");
            idColumn.setCellValueFactory(new MapValueFactory<Object>("ID"));
            TableColumn<Map,Object> nameColumn = new TableColumn<>("NAME");
            nameColumn.setCellValueFactory(new MapValueFactory<Object>("NAME"));
            return Arrays.asList(idColumn, nameColumn);
        }

        public ObservableList<Map> getQueryResult() {
            int num = 200;
            List<Map> result = new ArrayList<>();
            for (int i = 0; i < num; i++) {
                Map<String,Object> row = new HashMap<>();
                row.put("ID", i);
                row.put("NAME", "Name " + i);
                result.add(row);
            }
            return FXCollections.observableArrayList(result);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
