package goryachev.bugs;

import java.util.Arrays;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

/**
 * https://bugs.openjdk.org/browse/JDK-8320232
 */
public class VirtualFlow_Duplicated_8320232 extends Application {
    @Override
    public void start(Stage stage) {
        StackPane root = new StackPane();
        root.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(root, 250, 250);

        createNodes(root);

        stage.setTitle("JavaFX Sample");
        stage.setScene(scene);
        stage.show();
    }

    private void createNodes(Pane root) {
        TableColumn<SimpleTableItem, String> nameTableColumn = new TableColumn<>("Name");
        nameTableColumn.setCellValueFactory(p -> p.getValue().nameProperty());

        TableColumn<SimpleTableItem, Integer> numberTableColumn = new TableColumn<>("Number");
        numberTableColumn.setCellValueFactory(p -> p.getValue().numberProperty().asObject());
        numberTableColumn.setCellFactory(column -> new TestTableCell<>());

        TableView<SimpleTableItem> tableView = new TableView<>(FXCollections.observableArrayList(
            new SimpleTableItem("Alpha", 1),
            new SimpleTableItem("Beta", 2),
            new SimpleTableItem("Gamma", 3),
            new SimpleTableItem("Delta", 4)));
        tableView.getColumns().setAll(Arrays.asList(nameTableColumn, numberTableColumn));
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setEditable(true);

        TitledPane titledPane = new TitledPane("TitledPane", tableView);
        titledPane.expandedProperty().addListener((observable, oldValue, newValue) -> System.out
            .println("titledPane " + (newValue ? "expanded" : "collapsed")));

        root.getChildren().add(titledPane);
    }

    static class TestTableCell<S> extends TextFieldTableCell<S, Integer> {
        TestTableCell() {
            super(new IntegerStringConverter());
        }

        @Override
        public void startEdit() {
            System.out.println("startEdit number: " + getItem());
            super.startEdit();
        }
    }

    private static class SimpleTableItem {
        SimpleTableItem(String name, int number) {
            this.name.set(name);
            this.number.set(number);

            numberProperty()
                .addListener((observable, oldValue, newValue) -> System.out.println("changed number: " + newValue));
        }

        private final ReadOnlyStringWrapper name = new ReadOnlyStringWrapper(this, "name");

        ReadOnlyStringProperty nameProperty() {
            return name.getReadOnlyProperty();
        }

        private final IntegerProperty number = new SimpleIntegerProperty(this, "discrete");

        IntegerProperty numberProperty() {
            return number;
        }
    }
}