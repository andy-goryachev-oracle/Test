package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8361210
 */
public class TableView_ColMaxSize_8361210 extends Application {
    
    @Override
    public void start(Stage stage) {
        TableColumn<String, String> col1 = new TableColumn<>();
        col1.setGraphic(new Text("Column One (... ...)"));
        
        // Setting the max width causes this column to shrink unexpectedly
        col1.setMaxWidth(500);
        //col1.setPrefWidth(500);

        TableView<String> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.getColumns().addAll(
            col1,
            new TableColumn<>("Col 2"),
            new TableColumn<>("Col 3"),
            new TableColumn<>("Col 4"),
            new TableColumn<>("Col 5")
        );
        
        stage.setScene(new Scene(new VBox(table), 600, 200));
        stage.setTitle("JDK-8361210");
        stage.show();
    }
}