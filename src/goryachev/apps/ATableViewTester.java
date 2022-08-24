package goryachev.apps;
import java.util.List;
import goryachev.util.FxDebug;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Skin;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ATableViewTester extends Application {
    
    protected static final boolean CELLS_WITH_BINDINGS = !true;
    protected static final boolean SNAP_TO_PIXEL = false;
    protected static final boolean ADD_ROWS = false;
    
    protected TableView<String> table;
    protected TableViewSelectionModel<String> oldTableSelectionModel;
    
    public static void main(String[] args) {
        Application.launch(ATableViewTester.class, args);
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
        
        table.getColumns().addAll(
            createColumn("C0", 50),
            createColumn("C1", 100),
            createColumn("C2", 200)
            );
        
        for(TableColumnBase<?,?> c: table.getVisibleLeafColumns()) {
            Node nd = c.getStyleableNode();
            if(nd instanceof Region r) {
                r.setSnapToPixel(SNAP_TO_PIXEL);
            }
        }
        
        if (ADD_ROWS) {
            table.getItems().addAll(
                "",
                "",
                "",
                "",
                ""
                );
        }
        
        CheckBox tableCellSelectionEnabled = new CheckBox("cell selection");
        table.getSelectionModel().cellSelectionEnabledProperty().bind(tableCellSelectionEnabled.selectedProperty());
        
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
            nullTableSelectionModel,
            constrainedTableModel
            );
        
        BorderPane p = new BorderPane();
        p.setCenter(table);
        p.setBottom(vb);
        
        // layout
        
        SplitPane split = new SplitPane(p, new BorderPane());
        split.setSnapToPixel(SNAP_TO_PIXEL);
        
        BorderPane mp = new BorderPane();
//        mp.setTop(createMenu());
        mp.setCenter(split);

        Scene sc = new Scene(mp);
//        sc.getStylesheets().add(getClass().getResource("/goryachev/apps/ATreeTableViewTester.css").toExternalForm());
        
        FxDebug.attachNodeDumper(stage);
        stage.setScene(sc);
        stage.setMinWidth(1500);
        stage.setTitle("TableView Tester " + System.getProperty("java.version"));
        stage.show();
    }
    
//    protected MenuBar createMenu() {
//        Menu m;
//        MenuBar b = new MenuBar();
//        b.getMenus().add(m = new Menu("File"));
//        m.getItems().add(new MenuItem("Open"));
//        return b;
//    }
    
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
    
    protected static TableColumn<String,String> createColumn(String name, int prefWidth) {
        TableColumn<String,String> c = new TableColumn<>(name);
        c.setPrefWidth(prefWidth);
        c.setCellValueFactory((f) -> new SimpleStringProperty("..."));
        return c;
    }
}
