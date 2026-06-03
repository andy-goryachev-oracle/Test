package goryachev.bugs;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

// https://bugs.openjdk.org/browse/JDK-8385816
public class FlowPane_InsideTableCell_8385816 extends Application {
    @Override
    public void start(Stage stage) {
        TableColumn<String, String> column1 = new TableColumn<>("Column 1");
        column1.setPrefWidth(500f);
        column1.setCellValueFactory(param -> new SimpleStringProperty(param.getValue()));
        column1.setCellFactory(param -> new TableCell<String, String>() {
            private final FlowPane flowPane = new FlowPane();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                flowPane.getChildren().clear();

                Label label = new Label(item);
                Button button = new Button("OK");
                flowPane.getChildren().addAll(label, button);

                setText(null);
                setGraphic(flowPane);
                Platform.runLater(() -> {
                    System.out.println("Label Height:" + label.getHeight());
                    System.out.println("Button Height:" + button.getHeight());
                    System.out.println("FlowPlane Height:" + flowPane.getHeight());
                });
            }
        });
        TableView<String> tableView = new TableView<>();
        tableView.getColumns().add(column1);

        Scene scene = new Scene(tableView, 620, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        Platform.runLater(() -> {
            tableView.getItems().add("Test");
        });
    }
}