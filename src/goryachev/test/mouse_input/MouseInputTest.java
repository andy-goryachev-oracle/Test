package goryachev.test.mouse_input;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.stage.Stage;

// TableViewMouseInputTest
// test_rt_38306
public class MouseInputTest extends Application {
    TableView<Person> table;
    TableView.TableViewSelectionModel sm;
    TableColumn firstNameCol_0;
    TableColumn lastNameCol_1;
    TableColumn emailCol_2;
    
    
    public static void main(String[] args) {
        Application.launch(MouseInputTest.class, args);
    }
    
    @Override
    public void start(Stage s) throws Exception {
        final ObservableList<Person> data =
                FXCollections.observableArrayList(
                        new Person("Jacob", "Smith", "jacob.smith@example.com"),
                        new Person("Isabella", "Johnson", "isabella.johnson@example.com"),
                        new Person("Ethan", "Williams", "ethan.williams@example.com"),
                        new Person("Emma", "Jones", "emma.jones@example.com"),
                        new Person("Michael", "Brown", "michael.brown@example.com"));
    
        table = new TableView<>();
        table.setItems(data);
    
        sm = table.getSelectionModel();
        sm.setCellSelectionEnabled(true);
        sm.setSelectionMode(SelectionMode.MULTIPLE);
        sm.clearSelection();
    
        firstNameCol_0 = new TableColumn("First Name");
        firstNameCol_0.setCellValueFactory(new PropertyValueFactory<Person, String>("firstName"));
    
        lastNameCol_1 = new TableColumn("Last Name");
        lastNameCol_1.setCellValueFactory(new PropertyValueFactory<Person, String>("lastName"));
    
        emailCol_2 = new TableColumn("Email");
        emailCol_2.setCellValueFactory(new PropertyValueFactory<Person, String>("email"));
    
        table.getColumns().addAll(firstNameCol_0, lastNameCol_1, emailCol_2);
        
        s.setScene(new Scene(table));
        s.show();
        
//        if(true)return;
        
        Platform.runLater(() -> {
            try {
                test_rt_38306(!true);
            } catch(Throwable e) {
                e.printStackTrace();
            }
        });
    }
    
    private void test_rt_38306(boolean selectTwoRows) {
        // select 0,0
        sm.select(0, firstNameCol_0);
    
        assertTrue(sm.isSelected(0, firstNameCol_0));
        assertEquals(1, sm.getSelectedCells().size());
    
        TableCell cell_0_0 = (TableCell) getCell(table, 0, 0);
        TableCell cell_0_1 = (TableCell) getCell(table, 0, 1);
        TableCell cell_0_2 = (TableCell) getCell(table, 0, 2);
    
        TableCell cell_1_0 = (TableCell) getCell(table, 1, 0);
        TableCell cell_1_1 = (TableCell) getCell(table, 1, 1);
        TableCell cell_1_2 = (TableCell) getCell(table, 1, 2);
    
        MouseEventFirer mouse = selectTwoRows ?
                new MouseEventFirer(cell_1_2) : new MouseEventFirer(cell_0_2);
    
        // click on cell 0:2
        mouse.fireMousePressAndRelease(KeyModifier.SHIFT);
    
        // all cells must be selected
        assertTrue(sm.isSelected(0, firstNameCol_0));
        assertTrue(sm.isSelected(0, lastNameCol_1)); // FIX fails in eclipse
        assertTrue(sm.isSelected(0, emailCol_2));
    
        if (selectTwoRows) {
            assertTrue(sm.isSelected(1, firstNameCol_0));
            assertTrue(sm.isSelected(1, lastNameCol_1));
            assertTrue(sm.isSelected(1, emailCol_2));
        }
    
        assertEquals(selectTwoRows ? 6 : 3, sm.getSelectedCells().size());
    
        assertTrue(cell_0_0.isSelected());
        assertTrue(cell_0_1.isSelected());
        assertTrue(cell_0_2.isSelected());
    
        if (selectTwoRows) {
            assertTrue(cell_1_0.isSelected());
            assertTrue(cell_1_1.isSelected());
            assertTrue(cell_1_2.isSelected());
        }
    }
    
    
    private void assertEquals(int a, int b) {
        if (a != b) {
            System.err.println(a + " != " + b);
            throw new Error(a + " != " + b);
        }
    }


    private void assertTrue(boolean on) {
        if (!on) {
            System.err.println("not true");
            throw new Error("not true");
        }
    }


    public static IndexedCell getCell(final Control control, final int row, final int column) {
        IndexedCell rowCell = getVirtualFlow(control).getCell(row);
        if (column == -1) {
            return rowCell;
        }

        int count = -1;
        for (Node n : rowCell.getChildrenUnmodifiable()) {
            if (! (n instanceof IndexedCell)) {
                continue;
            }
            count++;
            if (count == column) {
                return (IndexedCell) n;
            }
        }
        return null;
    }
    
    
    public static VirtualFlow<?> getVirtualFlow(Control control) {
        VirtualFlow<?> flow;
        if (control instanceof ComboBox) {
            final ComboBox cb = (ComboBox) control;
            final ComboBoxListViewSkin skin = (ComboBoxListViewSkin) cb.getSkin();
            control = (ListView) skin.getPopupContent();
        }

        flow = (VirtualFlow<?>)control.lookup("#virtual-flow");

        return flow;
    }
}

