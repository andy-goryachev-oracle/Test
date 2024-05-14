package goryachev.research;

import java.util.List;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8279140
 */
public class ComboBox_SetAll_Difference13vs17_8279140 extends Application {
    /**
     * What does this do? <br />
     * - Creates a ComboBox backed by an ObservableList (ComboBox#setItems). The backing list is initialized with
     * three strings. <br />
     * - There is a property for a selected value, which is bidirectionally bound to the selected item of the
     * ComboBox. <br />
     * - When the property for the selected value is changed, the contents of the backing list/ComboBox are updated.
     * The selected value is contained in those contents. <br />
     * - We programmatically change the selected value twice. <br />
     * <p>
     * What to try? <br />
     * - Start the app <br />
     * - Observe the initial state after programmatic changes to the selected value:<br />
     * --- expected: "D" is selected in the ComboBox <br />
     * --- actual 13.0.1: "D" is selected in the ComboBox <br />
     * --- actual 17.0.1: null is selected in the ComboBox <br />
     * - Depending on certain small changes to the code (marked with NOTE 1 and NOTE 2), the behavior can be broken
     * also in 13.0.1, or made to start working in 17.0.1 . Those changes are very small (registration (order) of
     * listeners/bindings), but strongly affect the outcome. For which changes lead to which result, see "RESULTS"
     * below. <br />
     */
    @Override
    public void start(final Stage stage) throws Exception {

        final ObservableList<String> comboBoxItemsList = FXCollections.observableArrayList();

        final ObjectProperty<String> selectedValue = new SimpleObjectProperty<>();

        final List<String> stringsABC = List.of("A", "B", "C");
        final List<String> stringsDEF = List.of("D", "E", "F");

        final ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setItems(comboBoxItemsList);

        // listeners only for debugging output
        initSelectionListeners(comboBox, "comboBoxWithSetItems");

        // NOTE 1a: If bidi binding is registered HERE (before adding listeners), it fails in fx 13.0.1 as well (selected value: null)
        // comboBox.valueProperty().bindBidirectional(selectedValue);

        final BooleanProperty valueAdjusting = new SimpleBooleanProperty();
        selectedValue.addListener(obs -> {
            System.out.println("selectedValue property inval listener " + selectedValue.get());
        });
        selectedValue.addListener((obs, oldV, newV) -> {
            System.out.println(
                "selectedValue property change listener newV=" + newV + " valueAdjusting="
                    + valueAdjusting.get());

            if (valueAdjusting.get() == true) {
                // avoid infinite loop / stack overflow
                System.out.println("ignoring value update during item update");
            } else {
                if ("D".equals(newV) || "A".equals(newV)) {
                    valueAdjusting.set(true);
                    System.out.println("valueListener: triggering item update");
                    final List<String> newContent = "A".equals(newV) ? stringsABC : stringsDEF;
                    comboBoxItemsList.setAll(newContent);
                    System.out.println("valueListener: value after item update: " + comboBox.getValue());
                    System.out.println(
                        "valueListener: selection after item update: "
                            + comboBox.getSelectionModel().getSelectedItem());
                    valueAdjusting.set(false);
                }
            }
        });

        // NOTE 1b: If bidi binding is registered HERE, it works in fx 13.0.1 but fails in fx 17.0.1 as described felow
        comboBox.valueProperty().bindBidirectional(selectedValue);

        selectedValue.set("A");
        selectedValue.set("D");

        System.out.println("Selected value: " + selectedValue.get());

        // RESULTS :
        // fx 13.0.1 :
        // - with early bidi binding registration (NOTE 1a):
        // ---- with change listener on selectedIndex (NOTE 2): Selected value: D
        // ---- without change listener on selectedIndex: Selected value: null
        // - with late bidi binding registration (NOTE 1b):
        // ---- with change listener on selectedIndex (NOTE 2): Selected value: D
        // ---- without change listener on selectedIndex: Selected value: D
        //
        // fx 17.0.1 :
        // - with early bidi binding registration (NOTE 1a):
        // ---- with change listener on selectedIndex (NOTE 2): Selected value: D
        // ---- without change listener on selectedIndex: Selected value: null
        // - with late bidi binding registration (NOTE 1b):
        // ---- with change listener on selectedIndex (NOTE 2): Selected value: D
        // ---- without change listener on selectedIndex: Selected value: null
        //

        final StackPane mainRoot = new StackPane(new VBox(comboBox));
        final Scene scene = new Scene(mainRoot);

        stage.setTitle("ComboBoxSetAllDifference13vs17App");
        stage.setScene(scene);
        stage.setWidth(300);
        stage.setHeight(100);
        stage.show();
    }

    private static void initSelectionListeners(final ComboBox<?> comboBox, String string) {
        comboBox.valueProperty().addListener((obs, old, newV) -> {
            System.out.println(string + " -- " + "new selected value: " + newV);
        });

        comboBox.getSelectionModel().selectedItemProperty().addListener((obs, old, newItem) -> {
            System.out.println(string + " -- " + "new selected item: " + newItem);
        });

        // NOTE 2: when this listener is used, it also works in 17.0.1 !!!! -> property becomes valid because of change evaluation!
        // comboBox.getSelectionModel().selectedIndexProperty().addListener((obs, old, newIndex) -> {
        // System.out.println(string + " -- " + "new selected index: " + newIndex);
        // });

        // invalidation listener does not make a difference!
        comboBox.getSelectionModel().selectedIndexProperty().addListener(obs -> {
            System.out.println(string + " -- " + "selected index invalidated");
        });
    }
}
