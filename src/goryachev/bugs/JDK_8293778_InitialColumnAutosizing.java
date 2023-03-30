package goryachev.bugs;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8293778
 */
public class JDK_8293778_InitialColumnAutosizing extends Application {
    public static void main(String[] args) {
        Application.launch(JDK_8293778_InitialColumnAutosizing.class, args);
    }

    @Override
    public void start(final Stage stage) throws Exception {
        final var rootPane = new HBox(5d);
        final TableView<Map<String,Object>> tableView = createTableView();
        rootPane.getChildren().add(tableView);

        final TreeTableView<Map<String,Object>> treeTableView = createTreeTableView();
        rootPane.getChildren().add(treeTableView);

        stage.setScene(new Scene(rootPane, 800, 600));
        stage.setTitle("JDK-8293778 " + System.getProperty("java.version"));
        stage.show();
    }

    private TreeTableView<Map<String,Object>> createTreeTableView() {
        final TreeTableView<Map<String,Object>> treeTableView = new TreeTableView<>();
        treeTableView.setShowRoot(false);
        final TreeItem<Map<String,Object>> root = new TreeItem<>();
        for (int i = 0; i < 10; i++) {
            final Map<String,Object> data = Map.of("id", i, "data", "row" + i);
            final TreeItem<Map<String,Object>> treeItem = new TreeItem<>(data);
            root.getChildren().add(treeItem);
        }
        final var idCol = new TreeTableColumn<Map<String,Object>,Object>("id");
        idCol.setCellValueFactory(param -> {
            final var value = param.getValue();
            if (value == null || value.getValue() == null) {
                return null;
            }
            return new SimpleObjectProperty<>(value.getValue().get("id"));
        });
        idCol.setText(null);
        idCol.setGraphic(new Label("Long text 'id'"));

        final var dataCol = new TreeTableColumn<Map<String,Object>,Object>("data");
        dataCol.setCellValueFactory(param -> {
            final var value = param.getValue();
            if (value == null || value.getValue() == null) {
                return null;
            }
            return new SimpleObjectProperty<>(value.getValue().get("data"));
        });
        dataCol.setText(null);
        dataCol.setGraphic(new Label("Long text 'data'"));
        treeTableView.getColumns().addAll(idCol, dataCol);
        treeTableView.setRoot(root);
        return treeTableView;
    }

    private TableView<Map<String,Object>> createTableView() {
        final TableView<Map<String,Object>> tableView = new TableView<>(FXCollections
            .observableArrayList(IntStream.range(0, 10)
                .mapToObj(i -> Map.<String,Object>of("id", i, "data", "row_" + i))
                .collect(Collectors.toList())));
        final var idCol = new TableColumn<Map,Object>("id");
        idCol.setCellValueFactory(new MapValueFactory<Object>("id"));
        idCol.setText(null);
        idCol.setGraphic(new Label("Long text 'id'"));
        
        // TODO
        Label g = new Label("Long text 'data'");
//        g.setPrefWidth(Region.USE_COMPUTED_SIZE);
        
        final var dataCol = new TableColumn<Map,Object>("data");
        dataCol.setCellValueFactory(new MapValueFactory<Object>("data"));
        dataCol.setText(null);
        dataCol.setGraphic(g);
        dataCol.setPrefWidth(300);
        tableView.getColumns().add((TableColumn)idCol);
        tableView.getColumns().add((TableColumn)dataCol);
        return tableView;
    }
}
