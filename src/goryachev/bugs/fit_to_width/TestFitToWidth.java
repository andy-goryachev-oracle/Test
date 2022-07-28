package goryachev.bugs.fit_to_width;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.TableViewSkin;
import javafx.stage.Stage;

/**
Two issues:
- drag right window edge with mouse: horizontal bar appears and disappears
- initial column widths correct, drag right window edge: columns jump to equal width
- initial column widths correct, drag bottom window edge to make vertical scroll bar appear:
  columns jump to equal width
  
  
  
Related:
JDK-8157687 TableView.CONSTRAINED_RESIZE_POLICY does honor column preferred widths (java8)
JDK-8090112 Wrong TableView headers width when using TableView.CONSTRAINED_RESIZE_POLICY
JDK-8088010 TableView.CONSTRAINED_RESIZE_POLICY only works from the second table
JDK-8087882 TableView with columnResizePolicy = CONSTRAINED_RESIZE_POLICY does not obey the column minWidth.
JDK-8089009 TableView with CONSTRAINED_RESIZE_POLICY incorrectly displays a horizontal scroll bar.
JDK-8089280 horizontal scrollbar should never become visible in TableView with constrained resize policy
JDK-8091269 Cannot set initial column sizes with CONSTRAINED_RESIZE_POLICY
JDK-8130747 TableView Header is not resized appropriately when TableView is empty
JDK-8089456 CONSTRAINED_RESIZE_POLICY sometimes ignored when setMaxWidth is used
JDK-8090167 TableView not layouted correctly in scene


JDK-8087673 [TableView] TableView and TreeTableView menu button overlaps columns when using a constrained resize policy.
*/
public class TestFitToWidth extends Application {
    public static void main(String[] args) {
        Application.launch(TestFitToWidth.class, args);
    }

    @Override
    public void start(Stage s) throws Exception {
        TableView<Entry> v = new TableView();
        v.setSkin(new TableViewSkin(v));
        v.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        {
            TableColumn<Entry,String> c = new TableColumn<>("Name");
            c.setPrefWidth(100);
            c.setCellValueFactory((f) ->
            {
                return f.getValue().name;
            });
            v.getColumns().add(c);
        }
        {
            TableColumn<Entry,String> c = new TableColumn("Text");
            c.setPrefWidth(1000);
            c.setCellValueFactory((f) ->
            {
                return f.getValue().text;
            });
            v.getColumns().add(c);
        }
        
        v.getItems().addAll(
                new Entry("1", "One"),
                new Entry("2", "Two")
                );
        
        Scene sc = new Scene(v);
        s.setScene(sc);
        s.show();
    }
    
    protected static class Entry {
        public final SimpleStringProperty name = new SimpleStringProperty();
        public final SimpleStringProperty text = new SimpleStringProperty();
        
        public Entry(String name, String text) {
            this.name.set(name);
            this.text.set(text);
        }
    }
}
