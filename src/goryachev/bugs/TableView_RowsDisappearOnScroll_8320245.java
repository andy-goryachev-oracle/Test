package goryachev.bugs;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.PickResult;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Reproduces JavaFX bug
 * <a href="https://bugs.openjdk.org/browse/JDK-8320245">JDK-8320245</a>.
 */
public class TableView_RowsDisappearOnScroll_8320245 extends Application {

    @Override
    public void start(final Stage stage) {
        // Create the TableView with data
        final TableView<String> tableView = new TableView<>(
            FXCollections.observableList(
                FXCollections.observableArrayList(
                    "Row 1",
                    "Row 2",
                    "Row 3",
                    "Row 4",
                    "Row 5")));

        // Add seven columns
        for (int i = 0; i < 7; i++) {
            final TableColumn<String, String> column = new TableColumn<>("Column " + (i + 1));
            column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));
            tableView.getColumns().add(column);
        }

        // Create explanatory label
        final Label label = new Label(
            """
            The TableView is in a corrupted state.
            Some rows are clipped at the top of the VirtualFlow,
            and the vertical ScrollBar is not showing.""");
        label.setMinWidth(Label.USE_PREF_SIZE);
        label.setMinHeight(Label.USE_PREF_SIZE);
        label.setPadding(new Insets(10));

        // Compose scene and stage
        final VBox root = new VBox(label, tableView);
        final Scene scene = new Scene(root, 300.0, 150.0);
        stage.setScene(scene);
        stage.setTitle(this.getClass().getSimpleName());
        stage.show();

        // Set dimensions to consistently cause the vertical ScrollBar (vbar)
        // to appear erroneously.
        stage.setWidth(308.6666564941406);
        stage.setHeight(271.3333282470703);

        // Locate the VirtualFlow from the TableView
        final VirtualFlow<?> virtualFlow = tableView.getChildrenUnmodifiable()
            .stream()
            .filter(node -> node instanceof VirtualFlow)
            .map(node -> (VirtualFlow<?>)node)
            .findFirst()
            .orElse(null);

        if (virtualFlow == null) {
            System.exit(0);
        }

        Platform.runLater(() -> {
            virtualFlow.fireEvent(
                new ScrollEvent(
                    ScrollEvent.SCROLL,
                    88.0,
                    74.0,
                    1218.0,
                    505.3333333333333,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    0.0,
                    -52.0,
                    0.0,
                    0.0,
                    40.0,
                    40.0,
                    ScrollEvent.HorizontalTextScrollUnits.CHARACTERS,
                    0.0,
                    ScrollEvent.VerticalTextScrollUnits.LINES,
                    -4.0,
                    0,
                    new PickResult(tableView, 45.0, 45.0)));
            Platform.runLater(() -> {
                virtualFlow.fireEvent(
                    new ScrollEvent(
                        ScrollEvent.SCROLL,
                        88.0,
                        74.0,
                        1218.0,
                        505.3333333333333,
                        false,
                        false,
                        false,
                        false,
                        false,
                        false,
                        0.0,
                        26.0,
                        0.0,
                        0.0,
                        40.0,
                        40.0,
                        ScrollEvent.HorizontalTextScrollUnits.CHARACTERS,
                        0.0,
                        ScrollEvent.VerticalTextScrollUnits.LINES,
                        2.0,
                        0,
                        new PickResult(tableView, 45.0, 45.0)));

                // Now the VirtualFlow is primed to trigger the
                // disappearing row(s).
                // When manually interacting with the TableView, we found that
                // from this state clicking a row consistently triggered
                // the disappearing row(s).
                // In code here, we found it simpler to do this:
                // Do something that makes a TableRow need layout.
                tableView.getSelectionModel().select(3); // Select a row
                // Do something that causes
                // VirtualFlowPrefersContentLength.layoutChildren().
                tableView.layout();

                System.out.println(
                    "Note that the vertical ScrollBar is not showing,\n" +
                    "the virtualFlow.firstVisibleCell.layoutY\n" +
                    "is negative: " +
                    virtualFlow.getFirstVisibleCell().getLayoutY() +
                    ",\nand some rows have disappeared.");
            });
        });
    }
}