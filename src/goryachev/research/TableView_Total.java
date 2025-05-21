package goryachev.research;
import java.util.Comparator;
import java.util.function.Function;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * Sorting with the Totals Row.
 */
public class TableView_Total extends Application {

    private final TableView<Item> table = new TableView<>();

    private final ObservableList<Item> data = FXCollections.observableArrayList(
        new Item("jacob.smith@example.com", 1),
        new Item("isabella.johnson@example.com", 2),
        new Item("ethan.williams@example.com", 3),
        new Item("emma.jones@example.com", 4),
        new Item("michael.brown@example.com", 5),
        new Item("Total", -1)
    );

    @Override
    public void start(Stage stage) {
        table.setItems(data);
        table.setEditable(true);

        Comparator<Item> comparator = new Comparator<>() {
            @Override
            public int compare(Item a, Item b) {
                return 0;
            }
        };

        {
            TableColumn<Item, Item> tc = new TableColumn<>("Email");
            tc.setCellValueFactory((cdf) -> new SimpleObjectProperty<Item>(cdf.getValue()));
            tc.setCellFactory(cellFactory((item) -> item == null ? null : item.email));
            tc.setComparator(comparator);
            table.getColumns().addAll(tc);
        }
        {
            TableColumn<Item, Item> tc = new TableColumn<>("Value");
            tc.setCellFactory(cellFactory((item) -> item == null ? null : item.value));
            tc.setComparator(comparator);
            table.getColumns().addAll(tc);
        }
        
        Scene scene = new Scene(table);

        stage.setTitle("Sorting with Total Row");
        stage.setWidth(450);
        stage.setHeight(550);
        stage.setScene(scene);
        stage.show();
    }

    private static Callback<TableColumn<Item,Item>, TableCell<Item,Item>> cellFactory(Function<Item,Object> getter) {
        return new Callback<>() {
            @Override
            public TableCell<Item,Item> call(TableColumn<Item,Item> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Item item, boolean empty) {
                        Object value = getter.apply(item);
                        super.updateItem(item, empty);
                        if (empty || (value == null)) {
                            super.setText(null);
                            super.setGraphic(null);
                        } else {
                            super.setText(value.toString());
                            super.setGraphic(null);
                        }
                    }
                };
            }
        };
    }

    public static class Item {
        public final StringProperty email = new SimpleStringProperty();
        public final IntegerProperty value = new SimpleIntegerProperty();

        private Item(String email, int value) {
            this.email.set(email);
            this.value.set(value);
        }

        public boolean isTotalRow() {
            // quick hack
            return value.get() < 0;
        }
    }
}