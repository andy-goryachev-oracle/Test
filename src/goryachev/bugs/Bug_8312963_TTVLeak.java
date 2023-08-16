package goryachev.bugs;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Bug_8312963_TTVLeak extends Application {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public static void main(String[] args) {
        launch(args);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void start(Stage primaryStage) throws Exception {
        long startTime = System.nanoTime();
        TableView<Map<String, Object>> tableView = new TableView<>();
        ObservableList<Map<String, Object>> data = FXCollections.observableArrayList();
        tableView.setItems(data);

        TableColumn<Map<String, Object>, String> firstCol = new TableColumn<>("first");
        firstCol.setPrefWidth(150);
        firstCol.setCellValueFactory(new MapValueFactory("first"));
        
        TableColumn<Map<String, Object>, String> secondCol = new TableColumn<>("second");
        secondCol.setCellValueFactory(new MapValueFactory("second"));

        secondCol.setCellFactory((p) -> {
            return new TableCell<Map<String, Object>, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        VBox box = new VBox();
                        box.getChildren().addAll(new Label("We have"),
                            new Label(String.valueOf(getTableView().getItems().size())), new Label("items"),
                            new Label("in the table"),
                            new Label(
                                "running time: " + TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - startTime) + " seconds"),
                            new Label("free memory: " + Runtime.getRuntime().freeMemory() + " bytes"),
                            new Label("and a lot"),
                            new Label("labels"),
                            new Text("and"),
                            new Text("Text"),
                            new Text("and"),
                            new Text("such"));
                        setText(null);
                        setGraphic(box);
                    }
                }
            };
        });
        
        tableView.getColumns().addAll(firstCol, secondCol);

        BorderPane root = new BorderPane();
        root.setCenter(tableView);

        Scene scene = new Scene(root, 300, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        Runnable beeper = new Runnable() {
            @Override
            public void run() {
                Map<String, Object> map = new HashMap<>();
                map.put("first", "test" + System.currentTimeMillis());
                map.put("second", "");
                data.add(map);
            }
        };
        ScheduledFuture<?> beeperHandle = scheduler.scheduleAtFixedRate(beeper, 1, 2, TimeUnit.MILLISECONDS);
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                beeperHandle.cancel(true);
            }
        }, 60 * 60L, TimeUnit.SECONDS);

        Runnable beeperRefresh = new Runnable() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    tableView.scrollTo(tableView.getItems().size());
                });
            }
        };
        ScheduledFuture<?> beeperHandler2 = scheduler.scheduleAtFixedRate(beeperRefresh, 1, 5, TimeUnit.SECONDS);
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                beeperHandler2.cancel(true);
            }
        }, 60 * 60L, TimeUnit.SECONDS);
    }
}