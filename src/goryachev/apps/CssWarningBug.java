package goryachev.apps;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

// https://bugs.openjdk.org/browse/JDK-8291853
// JDK-8291853 ClassCastException in CssStyleHelper calculateValue
public class CssWarningBug extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new StackPane(buildTableView()));
        stage.setScene(scene);
        
        stage.setWidth(1000);
        stage.setHeight(500);

        stage.show();
    }

    private TableView<String> buildTableView() {
        TableView<String> tableView = new TableView<>();

        TableColumn<String, String> column = new TableColumn<>("column");
        column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue()));
        column.setCellFactory(param -> new ScenePropertyTableCell<>());

        tableView.getColumns().add(column);
        tableView.getItems().addAll("item1", "item2", "item3");

        return tableView;
    }

    private static class ScenePropertyTableCell<S> extends TableCell<S, String> {
        public ScenePropertyTableCell() {
            sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene == null || newScene.getRoot().getProperties().containsKey("new.root")) {
                    return;
                }

                StackPane newRoot = new StackPane();
                newRoot.getProperties().put("new.root", true);
                Parent oldRoot = newScene.getRoot();

                newScene.setRoot(newRoot);

                // CLASS CAST EXCEPTION
                // If this call is before newScene.setRoot(newRoot);, nothing will happen.
                // Maybe because -fx-table-cell-border-color is not yet parsed because we changed the root?
                newRoot.getChildren().setAll(oldRoot);
            });
        }
    }
}