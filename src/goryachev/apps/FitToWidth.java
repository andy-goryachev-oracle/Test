package goryachev.apps;
import java.util.List;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.layout.BorderPane;
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
public class FitToWidth extends Application {
    protected TableView<Entry> table;
    protected TableColumn lastColumn;
    
    public static void main(String[] args) {
        Application.launch(FitToWidth.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        table = new TableView();
        table.getSelectionModel().setCellSelectionEnabled(true);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.setSkin(new TableViewSkin(table));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        {
            TableColumn<Entry,String> c = new TableColumn<>("Name");
            c.setPrefWidth(50);
            c.setCellValueFactory((f) ->
            {
                return f.getValue().name;
            });
            table.getColumns().add(c);
        }
        {
            TableColumn<Entry,String> c = new TableColumn("Text");
            c.setPrefWidth(200);
            c.setCellValueFactory((f) ->
            {
                return f.getValue().text;
            });
            table.getColumns().add(c);
        }
        {
            TableColumn<Entry,String> c = new TableColumn("Hash");
            lastColumn = c;
            c.setPrefWidth(300);
            c.setCellValueFactory((f) ->
            {
                return new ReadOnlyStringWrapper(String.valueOf(f.getValue().hashCode()));
            });
            table.getColumns().add(c);
        }
        
        table.getItems().addAll(
                new Entry("1", "One"),
                new Entry("2", "Two"),
                new Entry("3", "Three"),
                new Entry("4", "Four"),
                new Entry("99", "Ninety Nine")
                );
        
        CheckBox showLastColumnCheckbox = new CheckBox("show last column");
        showLastColumnCheckbox.setSelected(true);
        lastColumn.visibleProperty().bind(showLastColumnCheckbox.selectedProperty());
        showLastColumnCheckbox.selectedProperty().addListener((src,p,c) -> {
            dump();
        });
        
        BorderPane p = new BorderPane();
        p.setCenter(table);
        p.setBottom(showLastColumnCheckbox);
        
        Scene sc = new Scene(p);
        stage.setScene(sc);
        stage.show();
    }
    
    protected void dump() {
        StringBuilder sb = new StringBuilder();
        
        List<Integer> indexes = table.getSelectionModel().getSelectedIndices();
        sb.append("selected indexes: [");
        
        for(int ix: indexes) {
            sb.append(ix).append(' ');
        }
        sb.append("]\n");
        
        sb.append("isSelected(int): [");
        for(int i=0; i<table.getItems().size(); i++) {
            boolean sel = table.getSelectionModel().isSelected(i);
            sb.append(sel ? "T " : "F ");
        }
        sb.append("]\n");
        
        System.out.println(sb);
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
