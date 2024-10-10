package goryachev.bugs;
import java.util.List;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8341687
 */
public class TableView_ButtonMemoryLeak_8341687 extends Application {
    // Steps to reproduce:
    // 1. Run the application.
    // 2. Click the table menu button (located in the upper right corner of the TableView).
    // 3. No need to change the visibility or order of any column—just open the menu.
    // 4. Click "Set columns" button
    // 5. Observe the increased memory usage after each click.
    // Even performing garbage collection (GC) typically reduces heap usage by only about 10%.
    // 6. (Optional) Repeat step 4 multiple times. Notice that each click further increases heap memory usage.

    // Important Notes:
    // If you do not interact with the table menu button and only click the "Set columns" button, memory usage remains stable.
    //
    // The issue is exacerbated with tables that contain more columns,
    // where users change column visibility and order, then save and frequently restore these configurations.
    //
    // Attempts to mitigate the issue using tableView.getColumns().clear() before applying setAll(tableColumns)
    // do not resolve the memory leak.
    //
    // Impact: This problem becomes particularly noticeable in applications where
    // users dynamically customize column visibility and order. Over time, repeated interactions with the table menu button,
    // followed by saving/restoring column configurations, result in significant memory consumption.

    @Override
    public void start(Stage stage) {
        VBox vBox = new VBox();

        Scene scene = new Scene(vBox, 320, 240);
        stage.setScene(scene);
        stage.show();

        // Create a TableView with one column and set table menu button visible.
        TableView<String> tableView = new TableView<>();
        tableView.getColumns().add(new TableColumn<>("Column"));
        // Enable the table menu button, which triggers the memory issue.
        tableView.setTableMenuButtonVisible(true);

        // Save the original list of columns for later restoration.
        ObservableList<TableColumn<String, ?>> tableColumns = FXCollections.observableArrayList(tableView.getColumns());

        // Add a button to simulate setting columns repeatedly.
        Button button = new Button("Set columns");
        button.setOnMouseClicked(event -> {
            // This loop is to demonstrate the memory leak quickly.
            // On larger tables with more columns, even a lower iteration count (e.g., 200)
            // can cause significant memory usage, potentially over 16GB of heap use.
            for (int i = 0; i < 10000; i++) {
                
                // FIX
                // workaround: do not set columns that are already in the table
                //List<TableColumn<String,?>> cs = List.of(new TableColumn<>("Column 2"));
                //tableView.getColumns().setAll(cs);
                
                tableView.getColumns().setAll(tableColumns);
            }

            // (Optional) Print out memory usage after iterations for tracking purposes.
            System.gc();
            Runtime runtime = Runtime.getRuntime();
            long usedMemory = runtime.totalMemory() - runtime.freeMemory();
            System.out.println("Memory used (in MB): " + usedMemory / (1024 * 1024));
        });

        // Add the table view and button to the UI layout.
        vBox.getChildren().addAll(tableView, button);
    }
}