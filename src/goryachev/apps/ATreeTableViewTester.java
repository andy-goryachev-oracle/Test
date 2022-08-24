package goryachev.apps;
import java.util.List;
import java.util.Locale;
import goryachev.util.D;
import goryachev.util.FxDebug;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.SetChangeListener;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Skin;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeTableView.TreeTableViewSelectionModel;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ATreeTableViewTester extends Application {
    
    protected static final boolean CELLS_WITH_BINDINGS = !true;
    protected static final boolean LIST_VIEW = !true;   
    protected static final boolean SNAP_TO_PIXEL = false;
    protected static final boolean ADD_ROWS = false;
    
    protected TableView<Entry> table;
    protected TableColumn lastTableColumn;
    protected TreeTableView<Locale> tree;
    protected TreeTableColumn<Locale,String> lastTreeColumn;
    protected TreeTableViewSelectionModel<Locale> oldTreeSelectionModel;
    protected TableViewSelectionModel<Entry> oldTableSelectionModel;
    
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
        table.setSnapToPixel(SNAP_TO_PIXEL);
        table.skinProperty().addListener((src,pre,cur) -> {
            Skin<?> skin = table.getSkin();
            if(skin != null) {
                Node nd = skin.getNode();
                if(nd instanceof Region r) {
                    r.setSnapToPixel(SNAP_TO_PIXEL);
                }
            }
        });
        
        {
            TableColumn<Entry,String> c = new TableColumn<>("Name");
            c.setPrefWidth(50);
            c.setCellValueFactory((f) -> {
                return f.getValue().name;
            });
            table.getColumns().add(c);
        }
        {
            TableColumn<Entry,String> c = new TableColumn("Text");
            c.setPrefWidth(100);
            c.setCellValueFactory((f) -> {
                return f.getValue().text;
            });
            table.getColumns().add(c);
        }
        {
            TableColumn<Entry,String> c = new TableColumn("Hash");
            lastTableColumn = c;
            c.setPrefWidth(200);
            c.setCellValueFactory((f) -> {
                return new ReadOnlyStringWrapper(String.valueOf(f.getValue().hashCode()));
            });
            table.getColumns().add(c);
        }
        
        for(TableColumnBase<?,?> c: table.getVisibleLeafColumns()) {
            Node nd = c.getStyleableNode();
            if(nd instanceof Region r) {
                r.setSnapToPixel(SNAP_TO_PIXEL);
            }
        }
        
        if (ADD_ROWS) {
            table.getItems().addAll(
                new Entry("1", "One"),
                new Entry("2", "Two"),
                new Entry("3", "Three"),
                new Entry("4", "Four"),
                new Entry("99", "Ninety Nine")
                );
        }
        
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
            else {
                table.setSelectionModel(oldTableSelectionModel);
            }
        });
        
        CheckBox constrainedTableModel = new CheckBox("constrained model");
        constrainedTableModel.setSelected(true);
        constrainedTableModel.selectedProperty().addListener((src,prev,on) -> {
            if(on) {
                table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            }
            else {
                table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            }
        });
        
        VBox vb = new VBox();
        vb.setPadding(new Insets(5));
        vb.setSpacing(5);
        vb.getChildren().addAll(
            tableCellSelectionEnabled,
            showLastColumnCheckbox,
            nullTableSelectionModel,
            constrainedTableModel
            );
        
        BorderPane p = new BorderPane();
        p.setCenter(table);
        p.setBottom(vb);
        
        // tree
        
        // FIX
        Pane p2 = CELLS_WITH_BINDINGS ? createTreeWithBindings() : createTreeWithTextField();
        
        // layout
        
        SplitPane split = new SplitPane(p, p2);
        split.setSnapToPixel(SNAP_TO_PIXEL);
        if(LIST_VIEW) {
            split.getItems().add(listView());
        }
        
        BorderPane mp = new BorderPane();
//        mp.setTop(createMenu());
        mp.setCenter(split);

        Scene sc = new Scene(mp);
//        sc.getStylesheets().add(getClass().getResource("/goryachev/apps/ATreeTableViewTester.css").toExternalForm());
        
        FxDebug.attachNodeDumper(stage);
        stage.setScene(sc);
        stage.setMinWidth(1500);
        stage.setTitle("Tree/TableView Tester " + System.getProperty("java.version"));
        stage.show();
    }
    
//    protected MenuBar createMenu() {
//        Menu m;
//        MenuBar b = new MenuBar();
//        b.getMenus().add(m = new Menu("File"));
//        m.getItems().add(new MenuItem("Open"));
//        return b;
//    }
    
    protected Node listView() {
        ListView list = new ListView();
        list.getItems().addAll(
                "One",
                "Two",
                "Three",
                "Four"
                );
        list.getSelectionModel().selectAll();
        list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
//        list.setSelectionModel(null);
        
        BorderPane p3 = new BorderPane();
        p3.setCenter(list);
        return p3;
    }
    
    protected MenuButton createMenu() {
        MenuButton b = new MenuButton();
        b.getItems().add(new MenuItem("Open"));
        b.getItems().add(new MenuItem("Close"));
        return b;
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
    
    protected TreeTableCell createTreeTableCell() {
        TreeTableCell cell = new TreeTableCell() {
            @Override public void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
//                setText(generateText(this));
            }
        };
        
//        cell.selectedProperty().addListener((s,p,on) -> {
//            System.out.println("selected=" + on + " " + cell.getTableColumn().getText());
//            new Error().printStackTrace();
//        });
//        cell.focusedProperty().addListener((s,p,on) -> {
//            System.out.println("focused=" + on + " " + cell.getTableColumn().getText());
//        });
//        cell.getPseudoClassStates().addListener((SetChangeListener.Change<? extends PseudoClass> ch) -> {
//            System.out.println(cell.getPseudoClassStates() + " " + cell.getTableColumn().getText());
////            new Error().printStackTrace();
//        });
//        cell.styleProperty().addListener((s,p,on) -> {
//            System.out.println(cell.getStyle() + " " + cell.getTableColumn().getText());
//        });
//        
        cell.textProperty().bind(Bindings.createStringBinding(() -> generateText(cell), cell.focusedProperty(), cell.selectedProperty(), cell.getPseudoClassStates()));
        
        return cell;
    }
    
    protected String generateText(TreeTableCell c) {
        StringBuilder sb = new StringBuilder();
        sb.append(c.isFocused() ? "F" : "-");
        sb.append(c.isSelected() ? "S" : "-");
        sb.append(' ');
        sb.append(c.getPseudoClassStates());
        return sb.toString();
    }
    
    protected Pane createTreeWithBindings() {
        TreeItem<Locale> root = new TreeItem(null);
        
        if (ADD_ROWS) {
            for (Locale loc: Locale.getAvailableLocales()) {
                TreeItem<Locale> ch = new TreeItem(loc);
                root.getChildren().add(ch);
            }
        }
        
        // instantiate the table with null items
        tree = new TreeTableView<Locale>(root);
        
        {
            TreeTableColumn<Locale, String> c = new TreeTableColumn<>("Column_0");
//            c.setCellValueFactory(new TreeItemPropertyValueFactory<>("displayLanguage"));
            c.setCellFactory((col) -> createTreeTableCell());
            c.setPrefWidth(50);
            tree.getColumns().add(c);
        }
        {
            TreeTableColumn<Locale, String> c = new TreeTableColumn<>("Column_2");
//            c.setCellValueFactory(new TreeItemPropertyValueFactory<>("ISO3Language"));
            c.setCellFactory((col) -> createTreeTableCell());
            c.setPrefWidth(100);
            tree.getColumns().add(c);
        }
        {
            TreeTableColumn<Locale, String> c = new TreeTableColumn<>("Column_2");
            lastTreeColumn = c;
//            c.setCellValueFactory((cdf) -> {
//                TreeItem v = cdf.getValue();
//                if(v == null) {
//                    return new ReadOnlyStringWrapper("");
//                }
//                else {
//                    int n = v.getChildren().size();
//                    return new ReadOnlyStringWrapper(String.valueOf(n));
//                }
//            });
            c.setCellFactory((col) -> createTreeTableCell());
            c.setPrefWidth(200);
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
        
        CheckBox constrainedTreeModel = new CheckBox("constrained model");
        constrainedTreeModel.selectedProperty().addListener((src,prev,on) -> {
            if(on) {
                tree.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
            }
            else
            {
                tree.setColumnResizePolicy(TreeTableView.UNCONSTRAINED_RESIZE_POLICY);
            }
        });
        
        VBox vb2 = new VBox();
        vb2.setPadding(new Insets(5));
        vb2.setSpacing(5);
        vb2.getChildren().addAll(
            treeCellSelectionEnabled,
            showLastTreeColumnCheckbox,
            nullTreeSelectionModel,
            constrainedTreeModel
            );
        
        BorderPane p2 = new BorderPane();
        p2.setCenter(tree);
        p2.setBottom(vb2);
        return p2;
    }
    
    protected TextFieldTreeTableCell createTreeTableCell2() {
        TextFieldTreeTableCell cell = new TextFieldTreeTableCell() ;
        
        cell.selectedProperty().addListener((s,p,on) -> {
            System.out.println("selected=" + on + info(cell));
        });
        cell.focusedProperty().addListener((s,p,on) -> {
            System.out.println("focused=" + on + info(cell));
        });
        cell.getPseudoClassStates().addListener((SetChangeListener.Change<? extends PseudoClass> ch) -> {
            PseudoClass pc = ch.getElementAdded();
            if(pc != null) {
                D.p("+" + pc + " " + cell.getPseudoClassStates() + " " + info(cell));

//                if (cell.isSelected() && cell.getPseudoClassStates().contains(PseudoClass.getPseudoClass("selected"))) {
//                    D.trace();
//                }
            }
            
            pc = ch.getElementRemoved();
            if(pc != null) {
                D.p("-" + pc + " " + cell.getPseudoClassStates() + " " + info(cell));
            }
        });
        
        return cell;
    }
    
    protected String info(TreeTableCell cell) {
        return " R" + cell.getIndex() + ":" + cell.getTableColumn().getText();
    }
    
    // FIX exhibits the issue
    protected Pane createTreeWithTextField() {
        TreeItem<Locale> root = new TreeItem(null);

        if (ADD_ROWS) {
            for (Locale loc: Locale.getAvailableLocales()) {
                TreeItem<Locale> ch = new TreeItem(loc);
                root.getChildren().add(ch);
            }
        }

        // instantiate the table with null items
        tree = new TreeTableView<Locale>(root);

        {
            TreeTableColumn<Locale, String> c = new TreeTableColumn<>("C1");
            c.setCellValueFactory(new TreeItemPropertyValueFactory<>("displayLanguage"));
            c.setCellFactory((col) -> createTreeTableCell2());
            tree.getColumns().add(c);
        }
        {
            TreeTableColumn<Locale, String> c = new TreeTableColumn<>("C2");
            c.setCellValueFactory(new TreeItemPropertyValueFactory<>("ISO3Language"));
            c.setCellFactory((col) -> createTreeTableCell2());
            tree.getColumns().add(c);
        }
        {
            TreeTableColumn<Locale, String> c = new TreeTableColumn<>("C3");
            lastTreeColumn = c;
            c.setCellValueFactory((cdf) -> {
                TreeItem v = cdf.getValue();
                if(v == null) {
                    return new ReadOnlyStringWrapper("");
                } else {
                    int n = v.getChildren().size();
                    return new ReadOnlyStringWrapper(String.valueOf(n));
                }
            });
            c.setCellFactory((col) -> createTreeTableCell2());
            tree.getColumns().add(c);
        }

        root.setExpanded(true);

        tree.setShowRoot(true);
        
//        tree.addEventFilter(MouseEvent.ANY, (ev) -> D.p(ev));

        boolean nullSelectionModel = false;
        if (nullSelectionModel) {
            tree.setSelectionModel(null);
        } else {
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
        showLastTreeColumnCheckbox.selectedProperty().addListener((src, prev, c) -> {
            Platform.runLater(this::dumpTree);
        });

        CheckBox nullTreeSelectionModel = new CheckBox("null cell selection model");
        nullTreeSelectionModel.selectedProperty().addListener((src, prev, on) -> {
            if (on) {
                oldTreeSelectionModel = tree.getSelectionModel();
                tree.setSelectionModel(null);
            } else {
                tree.setSelectionModel(oldTreeSelectionModel);
            }
        });

        CheckBox constrainedTreeModel = new CheckBox("constrained model");
        constrainedTreeModel.selectedProperty().addListener((src, prev, on) -> {
            if (on) {
                tree.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
            } else {
                tree.setColumnResizePolicy(TreeTableView.UNCONSTRAINED_RESIZE_POLICY);
            }
        });
        
        VBox vb2 = new VBox();
        vb2.setPadding(new Insets(5));
        vb2.setSpacing(5);
        vb2.getChildren().addAll(
                treeCellSelectionEnabled,
                showLastTreeColumnCheckbox,
                nullTreeSelectionModel,
                constrainedTreeModel
                );
        
        BorderPane p2 = new BorderPane();
        p2.setCenter(tree);
        p2.setBottom(vb2);
        return p2;
    }
    
    //
    
    protected static class Entry {
        public final SimpleStringProperty name = new SimpleStringProperty();
        public final SimpleStringProperty text = new SimpleStringProperty();
        
        public Entry(String name, String text) {
            this.name.set(name);
            this.text.set(text);
        }
    }
}
