package goryachev.apps;
import java.util.List;
import java.util.Locale;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ATreeTableViewTester extends Application {
    protected TableView<Entry> table;
    protected TableColumn lastTableColumn;
    TreeTableView<Locale> tree;
    TreeTableColumn<Locale,String> lastTreeColumn;
    private TreeTableViewSelectionModel<Locale> oldTreeSelectionModel;
    private TableViewSelectionModel<Entry> oldTableSelectionModel;
    
    public static void main(String[] args) {
        Application.launch(ATreeTableViewTester.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // table
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
            lastTableColumn = c;
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
        
        CheckBox tableCellSelectionEnabled = new CheckBox("cell selection");
        table.getSelectionModel().cellSelectionEnabledProperty().bind(tableCellSelectionEnabled.selectedProperty());
        
        CheckBox showLastColumnCheckbox = new CheckBox("show last column");
        showLastColumnCheckbox.setSelected(true);
        lastTableColumn.visibleProperty().bind(showLastColumnCheckbox.selectedProperty());
        showLastColumnCheckbox.selectedProperty().addListener((src,p,c) -> {
            Platform.runLater(this::dumpTable);
        });
        
        CheckBox nullTableSelectionModel = new CheckBox("null cell selection model");
        nullTableSelectionModel.selectedProperty().addListener((src,prev,on) -> {
            if(on) {
                oldTableSelectionModel = table.getSelectionModel();
                table.setSelectionModel(null);
            }
            else
            {
                table.setSelectionModel(oldTableSelectionModel);
            }
        });
        
        VBox vb = new VBox();
        vb.setPadding(new Insets(5));
        vb.setSpacing(5);
        vb.getChildren().addAll(
                tableCellSelectionEnabled,
                showLastColumnCheckbox,
                nullTableSelectionModel
                );
        
        BorderPane p = new BorderPane();
        p.setCenter(table);
        p.setBottom(vb);
        
        // tree
        
        TreeItem<Locale> root = new TreeItem(null);
        
        for (Locale loc: Locale.getAvailableLocales()) {
            TreeItem<Locale> ch = new TreeItem(loc);
            root.getChildren().add(ch);
        }
        
        // instantiate the table with null items
        tree = new TreeTableView<Locale>(root);
        
        {
            TreeTableColumn<Locale, String> c = new TreeTableColumn<>("Language");
            c.setCellValueFactory(new TreeItemPropertyValueFactory<>("displayLanguage"));
            tree.getColumns().add(c);
        }
        {
            TreeTableColumn<Locale, String> c = new TreeTableColumn<>("ISO3Language");
            c.setCellValueFactory(new TreeItemPropertyValueFactory<>("ISO3Language"));
            tree.getColumns().add(c);
        }
        {
            TreeTableColumn<Locale, String> c = new TreeTableColumn<>("Children");
            lastTreeColumn = c;
            c.setCellValueFactory((cdf) -> {
                TreeItem v = cdf.getValue();
                if(v == null) {
                    return new ReadOnlyStringWrapper("");
                }
                else {
                    int n = v.getChildren().size();
                    return new ReadOnlyStringWrapper(String.valueOf(n));
                }
            });
            tree.getColumns().add(c);
        }

        root.setExpanded(true);

        tree.setShowRoot(true);

        boolean nullSelectionModel = false;
        if(nullSelectionModel) {
            tree.setSelectionModel(null);
        }
        else {
            System.err.println("cell selection model is not null!");
            tree.getSelectionModel().setCellSelectionEnabled(true);
            tree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        }

        tree.setEditable(true);
        
        CheckBox treeCellSelectionEnabled = new CheckBox("cell selection");
        tree.getSelectionModel().cellSelectionEnabledProperty().bind(treeCellSelectionEnabled.selectedProperty());
        
        CheckBox showLastTreeColumnCheckbox = new CheckBox("show last column");
        showLastTreeColumnCheckbox.setSelected(true);
        lastTreeColumn.visibleProperty().bind(showLastTreeColumnCheckbox.selectedProperty());
        showLastTreeColumnCheckbox.selectedProperty().addListener((src,prev,c) -> {
            Platform.runLater(this::dumpTree);
        });
        
        CheckBox nullTreeSelectionModel = new CheckBox("null cell selection model");
        nullTreeSelectionModel.selectedProperty().addListener((src,prev,on) -> {
            if(on) {
                oldTreeSelectionModel = tree.getSelectionModel();
                tree.setSelectionModel(null);
            }
            else
            {
                tree.setSelectionModel(oldTreeSelectionModel);
            }
        });
        
        VBox vb2 = new VBox();
        vb2.setPadding(new Insets(5));
        vb2.setSpacing(5);
        vb2.getChildren().addAll(
                treeCellSelectionEnabled,
                showLastTreeColumnCheckbox,
                nullTreeSelectionModel
                );
        
        BorderPane p2 = new BorderPane();
        p2.setCenter(tree);
        p2.setBottom(vb2);
        
        // layout
        
        SplitPane split = new SplitPane(p, p2);
        
        Scene sc = new Scene(split);
        stage.setScene(sc);
        stage.setTitle("Tree/TableView Tester " + System.getProperty("java.version"));
        stage.show();
    }
    
    protected void dumpTable() {
        StringBuilder sb = new StringBuilder();
        
        List<Integer> indexes = table.getSelectionModel().getSelectedIndices();
        sb.append("table selected indexes: [");
        
        for(int ix: indexes) {
            sb.append(ix).append(' ');
        }
        sb.append("]\n");
        
        sb.append("isSelected(int): [");
        for(int i=0; i<table.getItems().size(); i++) {
            boolean sel = table.getSelectionModel().isSelected(i);
            sb.append(sel ? "T" : "-");
        }
        sb.append("]\n");
        
        System.out.println(sb);
    }
    
    protected void dumpTree() {
        StringBuilder sb = new StringBuilder();
        
        List<Integer> indexes = tree.getSelectionModel().getSelectedIndices();
        sb.append("tree selected indexes: [");
        
        for(int ix: indexes) {
            sb.append(ix).append(' ');
        }
        sb.append("]\n");
        
        sb.append("isSelected(int): [");
        for(int i=0; i<10000000; i++) {
            if(tree.getTreeItem(i) == null) {
                break;
            }
            boolean sel = tree.getSelectionModel().isSelected(i);
            sb.append(sel ? "T" : "-");
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
