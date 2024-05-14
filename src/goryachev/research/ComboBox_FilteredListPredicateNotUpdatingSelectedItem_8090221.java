package goryachev.research;

import java.util.Comparator;
import java.util.function.Predicate;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * https://bugs.openjdk.org/browse/JDK-8090221
 */
public class ComboBox_FilteredListPredicateNotUpdatingSelectedItem_8090221 extends Application {

    private FilteredList<ComboItem> _filteredList;

    @Override
    public void start(Stage stage) throws Exception {
        final ComboBox<ComboItem> combo = new ComboBox<>();

        ObservableList<ComboItem> items = FXCollections.observableArrayList();
        for (int i = 0; i < 10; i++) {
            items.add(new ComboItem("Hello " + i));
        }

        SortedList<ComboItem> sorted = items.sorted(new Comparator<ComboItem>() {
            @Override
            public int compare(ComboItem o1, ComboItem o2) {
                return o1.getText().compareTo(o2.getText());
            }
        });

        _filteredList = sorted.filtered(getPredicate());
        combo.setItems(_filteredList);
        combo.getSelectionModel().select(0);

        combo.setButtonCell(new ListCell<ComboItem>() {
            @Override
            protected void updateItem(ComboItem item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getText());
            }
        });
        combo.setCellFactory(new Callback<ListView<ComboItem>, ListCell<ComboItem>>() {
            @Override
            public ListCell<ComboItem> call(ListView<ComboItem> param) {
                return new ListCell<ComboItem>() {
                    @Override
                    protected void updateItem(ComboItem item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty ? null : item.getText());
                    }
                };
            }
        });

        TextArea out = new TextArea();

        combo.getSelectionModel().selectedItemProperty().addListener((prop, ov, nv) -> {
            out.appendText("Selected item property changed to " + nv + "\n");
        });

        Label howTo = new Label(
            "Press 'Change Filter' a few times, watch how the combo item you picked stays selected, but the printout says it's null");
        howTo.setWrapText(true);

        Button b = new Button("Change Filter");
        b.setOnAction(e -> {
            out.appendText("Selected Item Before: " + combo.getSelectionModel().getSelectedItem() + "\n");
            _filteredList.setPredicate(getPredicate());
            out.appendText("Selected Item After: " + combo.getSelectionModel().getSelectedItem() + "\n");
        });

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.getChildren().addAll(howTo, combo, b, out);
        Scene scene = new Scene(content, 600, 500);
        stage.setScene(scene);
        stage.setTitle("8090221");
        stage.show();
    }

    private Predicate<ComboItem> getPredicate() {
        return new Predicate<ComboItem>() {
            @Override
            public boolean test(ComboItem t) {
                return true;
            }
        };
    }

    public class ComboItem {
        private StringProperty _textProperty;

        public StringProperty textProperty() {
            if (_textProperty == null) {
                _textProperty = new SimpleStringProperty();
            }

            return _textProperty;
        }

        public void setText(String text) {
            textProperty().set(text);
        }

        public String getText() {
            return textProperty().get();
        }

        public ComboItem(String text) {
            setText(text);
        }

        @Override
        public String toString() {
            return "ComboItem [_textProperty=" + getText() + "]";
        }
    }
}
