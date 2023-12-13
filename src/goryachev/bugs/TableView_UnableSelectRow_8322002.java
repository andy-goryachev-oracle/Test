package goryachev.bugs;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8322002
 * 
Steps to reproduce:
1. Launch application
2. Use the "+" or "-" button to change the stage height 
3. Try to select a different item in the TableView. If selection is still possible, go to step 2.

Finding a stage height where this problem occurs seems to depend on the screen scale.
With a scale of 100 % I can reproduce the issue with at least the following heights:
1018, 1022, 1038, 1042, 1044, 1045
 */
public class TableView_UnableSelectRow_8322002 extends Application {
    VBox root = new VBox();

    private final TableView<Entry> table = new TableView<>();

    private final ObservableList<Entry> data = FXCollections.observableArrayList(new Entry("A"), new Entry("B"),
        new Entry("C"), new Entry("D"));

//    public static void main(final String[] args) {
//        launch(args);
//    }

    @Override
    public void start(final Stage stage) {
        Scene scene = new Scene(new Group());
        final double weight = scene.getWidth();
        final double height = scene.getHeight();
        stage.setTitle("Table View Sample");
        stage.setWidth(1000);
        stage.setHeight(1038);
        table.setEditable(true);

        final TableColumn<Entry, String> valueCol = new TableColumn<>("Value");
        valueCol.setMinWidth(1000);
        valueCol.setCellValueFactory(new PropertyValueFactory<Entry, String>("value"));

        table.setItems(data);
        table.getColumns().add(valueCol);
        root.getChildren().addAll(table);

        final Button plus = new Button("+");
        plus.setOnMouseClicked(evt -> {
            stage.setHeight(stage.getHeight() + 1);
        });
        final Button minus = new Button("-");
        minus.setOnMouseClicked(evt -> {
            stage.setHeight(stage.getHeight() - 1);
        });
        root.getChildren().addAll(plus, minus);

        stage.heightProperty().addListener((obs, oldV, newV) -> {
            System.out.println("New height is: " + newV);
        });

        // set to false and the problem goes away
        boolean depthBuffer = true;
        
        scene = new Scene(root, weight, height, depthBuffer);
        stage.setScene(scene);
        stage.show();
    }

    public static class Entry {
        private final SimpleStringProperty value;

        private Entry(final String value) {
            this.value = new SimpleStringProperty(value);
        }

        public SimpleStringProperty valueProperty() {
            return value;
        }
    }
}