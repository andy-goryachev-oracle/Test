package goryachev.bugs;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

/**
 * After TableView.refresh(), cells that come into view can still show the old value.
 *
 * The test runs automatically:
 * 1. show the table, then make the window smaller, so that the rows at the bottom move to the VirtualFlow pile
 * 2. change the (non-observable) value of every item, then call table.refresh()
 * 3. make the window large again, so that the rows come back from the pile
 * 4. compare the text of every visible cell with the current item value
 *
 * Expected: every cell shows "new N".
 * Actual (jfx26 and later): the rows that come back from the pile still show "old N".
 */
public class TableView_StaleCells_8394617 extends Application {

    /** Item with a plain, non-observable field: the use case that TableView.refresh() is documented for. */
    public static class Item {
        final int id;
        String value;

        Item(int id) {
            this.id = id;
            this.value = "old " + id;
        }
    }

    private TableView<Item> table;

    @Override
    public void start(Stage stage) {
        table = new TableView<>();
        TableColumn<Item, String> col = new TableColumn<>("Value");
        col.setPrefWidth(150);
        col.setCellValueFactory(f -> new SimpleStringProperty(f.getValue().value));
        table.getColumns().add(col);
        for (int i = 0; i < 300; i++) {
            table.getItems().add(new Item(i));
        }
        stage.setScene(new Scene(table, 200, 600));
        stage.show();
        later(500, () -> stage.setHeight(250));
        later(1000, () -> {
            table.getItems().forEach(item -> item.value = "new " + item.id);
            table.refresh();
        });
        later(1500, () -> stage.setHeight(600));
        later(2000, this::check);
    }

    private void check() {
        int ok = 0;
        int stale = 0;
        for (Node n : table.lookupAll(".table-cell")) {
            if (!(n instanceof TableCell<?, ?> cell) || cell.isEmpty() || cell.getTableRow() == null
                || !cell.getTableRow().isVisible() || cell.getIndex() < 0) {
                continue;
            }
            String expected = table.getItems().get(cell.getIndex()).value;
            if (expected.equals(cell.getText())) {
                ok++;
            } else {
                stale++;
                System.out.println("STALE: row " + cell.getIndex() + " shows '" + cell.getText() + "', expected '" + expected + "'");
            }
        }
        System.out.println("JavaFX " + System.getProperty("javafx.runtime.version") + ": " + ok + " cells ok, " + stale + " cells stale");
    }

    private static void later(long millis, Runnable r) {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                return;
            }
            Platform.runLater(r);
        });
        t.setDaemon(true);
        t.start();
    }
}
