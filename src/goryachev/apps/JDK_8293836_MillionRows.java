package goryachev.apps;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8293836
 */
public class JDK_8293836_MillionRows extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        List<String> rows = IntStream.rangeClosed(1, 1000_000).
            boxed().
            map(i -> "row-" + i).
            collect(Collectors.toList());

        TableView<String> tableView = new TableView<>();
        TableColumn<String,String> tc = new TableColumn<>("column-1");
        tc.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue()));
        tc.setPrefWidth(200);
        tableView.getColumns().add(tc);
        tableView.setItems(FXCollections.observableArrayList(rows));

        Scene scene = new Scene(new StackPane(tableView), 250, 200);
        stage.setTitle("JDK-8293444 " + System.getProperty("java.version"));
        stage.setScene(scene);
        stage.show();

        tableView.scrollTo(rows.size());
    }

    public static void main(String[] args) {
        launch();
    }
}
