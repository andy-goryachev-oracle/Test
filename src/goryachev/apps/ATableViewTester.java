package goryachev.apps;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.ResizeFeatures;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Callback;

import goryachev.util.FxDebug;

public class ATableViewTester extends Application {
    
    enum Demo {
        LARGE("large model"),
        ALL("all set: min, pref, max"),
        PREF("pref only"),
        EMPTY("empty"),
        MIN_WIDTH("min width"),
        MAX_WIDTH("max width"),
        INCONSISTENT("inconsistent: pref < min"),
        ;

        private final String text;
        Demo(String text) { this.text = text; }
        public String toString() { return text; }
    }
    
    enum Cmd {
        ROWS,
        COL,
        COL_WITH_GRAPHICS,
        MIN,
        PREF,
        MAX
    }

    protected BorderPane contentPane;
    protected ComboBox<Demo> demoSelector;
    protected CheckBox unconstrainedPolicy;
    
    public static void main(String[] args) {
        Application.launch(ATableViewTester.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        contentPane = new BorderPane();
        
        // selector
        demoSelector = new ComboBox<>();
        demoSelector.getItems().addAll(Demo.values());
        demoSelector.setEditable(false);
        demoSelector.getSelectionModel().selectedItemProperty().addListener((s,p,c) -> {
            updatePane();
        });
        
        unconstrainedPolicy = new CheckBox("unconstrained policy");
        unconstrainedPolicy.selectedProperty().addListener((s,p,c) -> {
            updatePane();
        });
        
        // https://bugs.openjdk.org/browse/JDK-8087673
//        {
//            table.setTableMenuButtonVisible(true);
//            table.getColumns().get(2).setGraphic(new Slider());
//        }

        // layout

        SplitPane split = new SplitPane(contentPane, new BorderPane());
        
        HBox hb = new HBox(demoSelector, unconstrainedPolicy);
        hb.setSpacing(5);
        
        BorderPane bp = new BorderPane();
        bp.setTop(hb);
        bp.setCenter(split);
        
        Scene sc = new Scene(bp);

        FxDebug.attachNodeDumper(stage);
        stage.setScene(sc);
        stage.setWidth(800);
        stage.setHeight(300);
        stage.setTitle("TableView Tester " + System.getProperty("java.version"));
        stage.show();
        
        demoSelector.getSelectionModel().selectFirst();
    }

    protected Callback<ResizeFeatures,Boolean> wrap(Callback<ResizeFeatures,Boolean> policy) {
        return new Callback<ResizeFeatures,Boolean>() {
            @Override
            public Boolean call(ResizeFeatures f) {
                Boolean rv = policy.call(f);
                int ix = f.getTable().getColumns().indexOf(f.getColumn());
                System.out.println(
                    "col=" + (ix < 0 ? f.getColumn() : ix) + 
                    " delta=" + f.getDelta() + 
                    " w=" + f.getTable().getWidth() + 
                    " rv=" + rv
                    );
                return rv;
            }
        };
    }
    
    protected String describe(TableColumn c) {
        StringBuilder sb = new StringBuilder();
        if(c.getMinWidth() != 10.0) {
            sb.append("min=");
            sb.append((int)c.getMinWidth());
        }
        if(c.getPrefWidth() != 80.0) {
            sb.append(" pref=");
            sb.append((int)c.getPrefWidth());
        }
        if(c.getMaxWidth() != 5000.0) {
            sb.append(" max=");
            sb.append((int)c.getMaxWidth());
        }
        return sb.toString();
    }
    
    // min, pref, max, rows
    protected Pane createTable(Object ... spec) {
        TableView<String> table = new TableView();
        table.getSelectionModel().setCellSelectionEnabled(true);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        if (unconstrainedPolicy.isSelected()) {
           table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY); 
        } else {
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        }
        
        TableColumn<String,String> lastColumn = null;
        
        for(int i=0; i<spec.length; ) {
            Object x = spec[i++];
            if(x instanceof Cmd cmd) {
                switch(cmd) {
                case COL:
                    {
                        TableColumn<String,String> c = new TableColumn<>();
                        table.getColumns().add(c);
                        c.setText("C" + table.getColumns().size());
                        c.setCellValueFactory((f) -> new SimpleStringProperty(describe(c)));
                        lastColumn = c;
                    }
                    break;
                case COL_WITH_GRAPHICS:
                    {
                        TableColumn<String,String> c = new TableColumn<>();
                        table.getColumns().add(c);
                        c.setText("C" + table.getColumns().size());
                        c.setCellValueFactory((f) -> new SimpleStringProperty(describe(c)));
                        c.setCellFactory((r) -> {
                            return new TableCell<>() {
                                @Override
                                protected void updateItem(String item, boolean empty) {
                                    super.updateItem(item, empty);
                                    Text t = new Text("11111111111111111111111111111111111111111111111111111111111111111111111111111111111111\n2\n3\n");
                                    t.wrappingWidthProperty().bind(widthProperty());
                                    setPrefHeight(USE_COMPUTED_SIZE);
                                    setGraphic(t);
                                }
                            };
                        });
                        lastColumn = c;
                    }
                    break;
                case MAX:
                    {
                        int w = (int)(spec[i++]);
                        lastColumn.setMaxWidth(w);
                    }
                    break;
                case MIN:
                    {
                        int w = (int)(spec[i++]);
                        lastColumn.setMinWidth(w);
                    }
                    break;
                case PREF:
                    {
                        int w = (int)(spec[i++]);
                        lastColumn.setPrefWidth(w);
                    }
                    break;
                case ROWS:
                    int n = (int)(spec[i++]);
                    for(int j=0; j<n; j++) {
                        table.getItems().add(String.valueOf(i));
                    }
                    break;
                default:
                    throw new Error("?" + cmd);
                }
            } else {
                throw new Error("?" + x);
            }
        }
        
        BorderPane bp = new BorderPane();
        bp.setCenter(table);
        return bp;
    }
    
    protected void updatePane() {
        Demo d = demoSelector.getSelectionModel().getSelectedItem();
        Pane n = createPane(d);
        contentPane.setCenter(n);
    }

    protected Pane createPane(Demo d) {
        if(d == null) {
            return new BorderPane();
        }
        
        switch(d) {
        case ALL:
            return createTable(
                Cmd.ROWS, 3,
                Cmd.COL, Cmd.MIN, 20, Cmd.PREF, 20, Cmd.MAX, 20,
                Cmd.COL, Cmd.PREF, 200,
                Cmd.COL, Cmd.PREF, 300, Cmd.MAX, 400
            );
        case PREF:
            return createTable(
                Cmd.ROWS, 3,
                Cmd.COL, Cmd.PREF, 100,
                Cmd.COL, Cmd.PREF, 200,
                Cmd.COL, Cmd.PREF, 300
            );
        case EMPTY:
            return createTable(
                Cmd.COL,
                Cmd.COL,
                Cmd.COL
            );
        case MIN_WIDTH:
            return createTable(
                Cmd.ROWS, 3,
                Cmd.COL,
                Cmd.COL,
                Cmd.COL, Cmd.MIN, 300
            );
        case MAX_WIDTH:
            return createTable(
                Cmd.ROWS, 3,
                Cmd.COL,
                Cmd.COL,
                Cmd.COL, Cmd.MAX, 300
            );
        case INCONSISTENT:
            return createTable(
                Cmd.ROWS, 3,
                Cmd.COL, Cmd.PREF, 2000, Cmd.MAX, 200,
                Cmd.COL, Cmd.MIN, 300, Cmd.PREF, 20
            );
        case LARGE:
            return createTable(
                Cmd.ROWS, 3000,
                Cmd.COL_WITH_GRAPHICS,
                Cmd.COL_WITH_GRAPHICS,
                Cmd.COL_WITH_GRAPHICS
            );
        default:
            return new BorderPane();
        }
    }
}
