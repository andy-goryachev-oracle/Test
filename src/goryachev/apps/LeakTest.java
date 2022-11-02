package goryachev.apps;

import java.time.LocalDate;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Pagination;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Skin;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.skin.AccordionSkin;
import javafx.scene.control.skin.ButtonBarSkin;
import javafx.scene.control.skin.ColorPickerSkin;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.control.skin.MenuBarSkin;
import javafx.scene.control.skin.MenuButtonSkin;
import javafx.scene.control.skin.PaginationSkin;
import javafx.scene.control.skin.ScrollBarSkin;
import javafx.scene.control.skin.ScrollPaneSkin;
import javafx.scene.control.skin.SplitMenuButtonSkin;
import javafx.scene.control.skin.SplitPaneSkin;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.control.skin.TextAreaSkin;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.control.skin.TreeTableViewSkin;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

/**
 * Tests memory leak when replacing the skin.
 * 
 * To test using VisualVM:
 * - launch the app
 * - select the control to test
 * - exercise control functionality
 * - click on [Change Skin] button as many times as you like
 * - exercise control some more
 * - in VisualVM, Monitor -> [Perform GC] -> [Heap Dump]
 * - in heap dump, select Objects pulldown (instead of Summary)
 * - type in Class Filter: "qq" (notice how all dummy skin classes are named starting with QQ)
 */
public class LeakTest extends Application {
    
    enum Type {
        ACCORDION,
        BUTTON_BAR,
        COLOR_PICKER,
        COMBO_BOX,
        DATE_PICKER,
        MENUBAR,
        MENU_BUTTON,
        PAGINATION,
        SCROLL_BAR,
        SCROLL_PANE,
        SPLIT_MENU_BUTTON,
        SPLIT_PANE,
        TABLE_VIEW,
        TEXT_AREA,
        TEXT_FIELD,
        TREE_TABLE_VIEW
    }
    
    private Stage currentStage;
    private BorderPane rootPane;
    private BorderPane content;
    private ComboBox<Type> choiceField;
    private Type type;
    private Test test;
    private Control control;
    
    interface Test<T extends Control> {
        public T createNode();
        
        public Skin<T> createSkin(T control);
    }
    
    public static void main(String[] args) {
        Application.launch(LeakTest.class, args);
    }
    
    @Override
    public void start(final Stage stage) throws Exception {
        Button replaceSkinButton = new Button("Replace Skin");
        replaceSkinButton.setOnAction(e -> {
            replaceSkin();
        });
        
        Button newWindowButton = new Button("New Window");
        
        Button clearButton = new Button("Remove Controls");
        
        content = new BorderPane();
        
        choiceField = new ComboBox<Type>();
        choiceField.getItems().addAll(Type.values());
        
        HBox tb = new HBox(
            5,
            choiceField,
            replaceSkinButton,
            newWindowButton,
            clearButton
        );
            
        rootPane = new BorderPane();
        rootPane.setTop(tb);
        rootPane.setCenter(content);

        Scene scene = new Scene(rootPane, 800, 600);
        
        currentStage = stage;
        stage.setScene(scene);
        stage.show();
        
        updateTitle();
        
        newWindowButton.setOnAction(e -> {
            newWindow();
        });
        
        clearButton.setOnAction(e -> {
            clearControl();
        });
        
        choiceField.getSelectionModel().selectedItemProperty().addListener((s,p,cur) -> {
            setTest(cur);
        });
    }
    
    public Test<?> createTest(Type t) {
        switch(t) {
        case ACCORDION:
            return  new Test<Accordion>() {
                @Override
                public Accordion createNode() {
                    Accordion a = new Accordion(
                        new TitledPane("Panel A", new Label("aaa")),
                        new TitledPane("Panel B", new Label("bbb")),
                        new TitledPane("Panel C", new Label("ccc"))
                    );
                    return a;
                }

                @Override
                public Skin<Accordion> createSkin(Accordion control) {
                    class QQAccordionSkin extends AccordionSkin {
                        public QQAccordionSkin(Accordion control) {
                            super(control);
                        }
                    };
                    return new QQAccordionSkin(control);
                }
            };
            
        case BUTTON_BAR:
            return  new Test<ButtonBar>() {
                @Override
                public ButtonBar createNode() {
                    ButtonBar b = new ButtonBar();
                    b.getButtons().addAll(
                        new Button("OK"),
                        new Button("Cancel"),
                        new Button("Help")
                    );
                    return b;
                }

                @Override
                public Skin<ButtonBar> createSkin(ButtonBar control) {
                    class QQButtonBarSkin extends ButtonBarSkin {
                        public QQButtonBarSkin(ButtonBar control) {
                            super(control);
                        }
                    }
                    return new QQButtonBarSkin(control);
                }
            };
            
        case COLOR_PICKER:
            return new Test<ComboBoxBase<Color>>() {
                @Override
                public ComboBoxBase<Color> createNode() {
                    return new ColorPicker();
                }

                @Override
                public Skin<ComboBoxBase<Color>> createSkin(ComboBoxBase<Color> control) {
                    class QQColorPickerSkin extends ColorPickerSkin {
                        public QQColorPickerSkin(ColorPicker control) {
                            super(control);
                        }
                    }
                    return new QQColorPickerSkin((ColorPicker)control);
                }
            };
            
        case COMBO_BOX:
            return new Test<ComboBoxBase<Object>>() {
                @Override
                public ComboBoxBase<Object> createNode() {
                    ComboBox<Object> cb = new ComboBox<Object>();
                    cb.getItems().addAll(
                        "1",
                        "2",
                        "3"
                    );
                    cb.setEditable(true);
                    return cb;
                }

                @Override
                public Skin<ComboBoxBase<Object>> createSkin(ComboBoxBase<Object> control) {
                    class QQComboBoxSkin extends ComboBoxListViewSkin {
                        public QQComboBoxSkin(ComboBox control) {
                            super(control);
                        }
                    }
                    return new QQComboBoxSkin((ComboBox)control);
                }
            };
            
        case DATE_PICKER:
            return new Test<ComboBoxBase<LocalDate>>() {
                @Override
                public ComboBoxBase<LocalDate> createNode() {
                    return new DatePicker();
                }

                @Override
                public Skin<ComboBoxBase<LocalDate>> createSkin(ComboBoxBase<LocalDate> control) {
                    class QQDatePickerSkin extends DatePickerSkin {
                        public QQDatePickerSkin(DatePicker control) {
                            super(control);
                        }
                    }
                    return new QQDatePickerSkin((DatePicker)control);
                }
            };

        case MENUBAR:
            return new Test<MenuBar>() {
                @Override
                public MenuBar createNode() {
                    MenuBar b = new MenuBar();
                    Menu m = new Menu("menu");
                    b.getMenus().add(m);
                    m.getItems().add(new MenuItem("item 1"));
                    m.getItems().add(new MenuItem("item 2"));
                    m.getItems().add(new MenuItem("item 3"));
                    
                    m = new Menu("menu");
                    b.getMenus().add(m);
                    Menu m2;
                    MenuItem mi;
                    m.getItems().add(m2 = new Menu("item 1"));
                    m.getItems().add(new MenuItem("item 2"));
                    m.getItems().add(new MenuItem("item 3"));
                    
                    m2.getItems().add(new MenuItem("item 21"));
                    m2.getItems().add(new MenuItem("item 22"));
                    m2.getItems().add(new MenuItem("item 23"));
                    m2.getItems().add(mi = new MenuItem("With Action"));
                    mi.setOnAction((ev) -> System.out.println("yo, action!"));
                    return b;
                }

                @Override
                public Skin<MenuBar> createSkin(MenuBar control) {
                    class QQMenuBarSkin extends MenuBarSkin {
                        public QQMenuBarSkin(MenuBar control) {
                            super(control);
                        }
                    }
                    return new QQMenuBarSkin(control);
                }
            };
            
        case MENU_BUTTON:
            return new Test<MenuButton>() {
                @Override
                public MenuButton createNode() {
                    MenuButton b = new MenuButton("Menu Button");
                    Menu m = new Menu("menu");
                    b.getItems().add(m);
                    m.getItems().add(new MenuItem("item 1"));
                    m.getItems().add(new MenuItem("item 2"));
                    m.getItems().add(new MenuItem("item 3"));
                    
                    m = new Menu("menu");
                    b.getItems().add(m);
                    Menu m2;
                    MenuItem mi;
                    m.getItems().add(m2 = new Menu("item 1"));
                    m.getItems().add(new MenuItem("item 2"));
                    m.getItems().add(new MenuItem("item 3"));
                    
                    m2.getItems().add(new MenuItem("item 21"));
                    m2.getItems().add(new MenuItem("item 22"));
                    m2.getItems().add(new MenuItem("item 23"));
                    m2.getItems().add(mi = new MenuItem("With Action"));
                    mi.setOnAction((ev) -> System.out.println("yo, action!"));
                    return b;
                }

                @Override
                public Skin<MenuButton> createSkin(MenuButton control) {
                    class QQMenuButtonSkin extends MenuButtonSkin {
                        public QQMenuButtonSkin(MenuButton control) {
                            super(control);
                        }
                    };
                    return new QQMenuButtonSkin(control);
                }
            };
            
        case PAGINATION:
            return new Test<Pagination>() {
                @Override
                public Pagination createNode() {
                    Pagination p = new Pagination(100, 0);
                    p.setPageFactory(new Callback<Integer, Node>() {
                        @Override
                        public Node call(Integer pageIndex) {
                            return new Label(pageIndex + 1 + ".\nLorem ipsum dolor sit amet,\n"
                                         + "consectetur adipiscing elit,\n"
                                         + "sed do eiusmod tempor incididunt ut\n"
                                         + "labore et dolore magna aliqua.");
                        }
                    });
                    new Timeline(
                        new KeyFrame(Duration.seconds(2), (ev) -> {
                            System.out.println("on");
                            p.getStyleClass().add(Pagination.STYLE_CLASS_BULLET);
                        }),
                        new KeyFrame(Duration.seconds(4), (ev) -> {
                            System.out.println("off");
                            p.getStyleClass().remove(Pagination.STYLE_CLASS_BULLET);
                        }),
                        new KeyFrame(Duration.seconds(6), (ev) -> {
                            System.out.println("on");
                            p.getStyleClass().add(Pagination.STYLE_CLASS_BULLET);
                        }),
                        new KeyFrame(Duration.seconds(8), (ev) -> {
                            System.out.println("off");
                            p.getStyleClass().remove(Pagination.STYLE_CLASS_BULLET);
                        })
                    ).play();
                    return p;
                }

                @Override
                public Skin<Pagination> createSkin(Pagination control) {
                    class QQPaginationSkin extends PaginationSkin {
                        public QQPaginationSkin(Pagination control) {
                            super(control);
                        }
                    }
                    return new QQPaginationSkin(control);
                }
            };
            
        case SCROLL_BAR:
            return new Test<ScrollBar>() {
                @Override
                public ScrollBar createNode() {
                    ScrollBar s = new ScrollBar();
                    s.setOrientation(Orientation.HORIZONTAL);
                    return s;
                }

                @Override
                public Skin<ScrollBar> createSkin(ScrollBar control) {
                    class QQScrollBarSkin extends ScrollBarSkin {
                        public QQScrollBarSkin(ScrollBar control) {
                            super(control);
                        }
                    }
                    return new QQScrollBarSkin(control);
                }
            };
            
        case SCROLL_PANE:
            return new Test<ScrollPane>() {
                @Override
                public ScrollPane createNode() {
                    Label p = new Label("scroll pane yo");
                    p.setPrefHeight(1000);
                    p.setPrefWidth(1000);
                    ScrollPane s = new ScrollPane(p);
                    return s;
                }

                @Override
                public Skin<ScrollPane> createSkin(ScrollPane control) {
                    class QQScrollPaneSkin extends ScrollPaneSkin {
                        public QQScrollPaneSkin(ScrollPane control) {
                            super(control);
                        }
                    }
                    return new QQScrollPaneSkin(control);
                }
            };
            
        case SPLIT_MENU_BUTTON:
            return new Test<SplitMenuButton>() {
                @Override
                public SplitMenuButton createNode() {
                    SplitMenuButton b = new SplitMenuButton();
                    Menu m = new Menu("menu");
                    b.getItems().add(m);
                    m.getItems().add(new MenuItem("item 1"));
                    m.getItems().add(new MenuItem("item 2"));
                    m.getItems().add(new MenuItem("item 3"));
                    
                    m = new Menu("menu");
                    b.getItems().add(m);
                    Menu m2;
                    MenuItem mi;
                    m.getItems().add(m2 = new Menu("item 1"));
                    m.getItems().add(new MenuItem("item 2"));
                    m.getItems().add(new MenuItem("item 3"));
                    
                    m2.getItems().add(new MenuItem("item 21"));
                    m2.getItems().add(new MenuItem("item 22"));
                    m2.getItems().add(new MenuItem("item 23"));
                    m2.getItems().add(mi = new MenuItem("With Action"));
                    mi.setOnAction((ev) -> System.out.println("yo, action!"));
                    return b;
                }

                @Override
                public Skin<SplitMenuButton> createSkin(SplitMenuButton control) {
                    class QQSplitMenuButtonSkin extends SplitMenuButtonSkin {
                        public QQSplitMenuButtonSkin(SplitMenuButton control) {
                            super(control);
                        }
                    };
                    return new QQSplitMenuButtonSkin(control);
                }
            };
            
        case SPLIT_PANE:
            return new Test<SplitPane>() {
                @Override
                public SplitPane createNode() {
                    SplitPane sp = new SplitPane(new BorderPane(), new BorderPane());
                    sp.setMinHeight(100);
                    sp.setMinWidth(100);
                    return sp;
                }

                @Override
                public Skin<SplitPane> createSkin(SplitPane control) {
                    class QQSplitPaneSkin extends SplitPaneSkin {
                        public QQSplitPaneSkin(SplitPane control) {
                            super(control);
                        }
                    }
                    return new QQSplitPaneSkin(control);
                }
            };
            
        case TABLE_VIEW:
            return new Test<TableView>() {
                @Override
                public TableView createNode() {
                    TableView t = new TableView();
                    t.getColumns().addAll(
                        tableColumn("Key"),
                        tableColumn("Value"),
                        tableColumn("Goats per Second")
                    );
                    t.getItems().addAll(
                        "",
                        " ",
                        "  "
                    );
                    t.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                    t.getSelectionModel().setCellSelectionEnabled(true);
                    return t;
                }

                @Override
                public Skin<TableView> createSkin(TableView control) {
                    class QQTableViewSkin extends TableViewSkin {
                        public QQTableViewSkin(TableView control) {
                            super(control);
                        }
                    }
                    return new QQTableViewSkin(control);
                }
            };
            
        case TEXT_AREA:
            return new Test<TextArea>() {
                @Override
                public TextArea createNode() {
                    return new TextArea();
                }

                @Override
                public Skin<TextArea> createSkin(TextArea control) {
                    class QQTextAreaSkin extends TextAreaSkin {
                        public QQTextAreaSkin(TextArea control) {
                            super(control);
                        }
                    }
                    return new QQTextAreaSkin(control);
                }
            };
            
        case TEXT_FIELD:
            return new Test<TextField>() {
                @Override
                public TextField createNode() {
                    return new TextField("yo");
                }

                @Override
                public Skin<TextField> createSkin(TextField control) {
                    class QQTextFieldSkin extends TextFieldSkin {
                        public QQTextFieldSkin(TextField control) {
                            super(control);
                        }
                    }
                    return new QQTextFieldSkin(control);
                }
            };
            
        case TREE_TABLE_VIEW:
            return new Test<TreeTableView>() {
                @Override
                public TreeTableView createNode() {
                    TreeItem root = new TreeItem(null);
                    root.setExpanded(true);
                    root.getChildren().addAll(
                        new TreeItem(" "),
                        new TreeItem("  "),
                        new TreeItem("   "),
                        new TreeItem("    "),
                        new TreeItem("     ")
                    );
                    
                    TreeTableView t = new TreeTableView(root);
                    t.getColumns().addAll(
                        treeTableColumn("Key"),
                        treeTableColumn("Value"),
                        treeTableColumn("Goats per Second"),
                        treeTableColumn("1"),
                        treeTableColumn("2"),
                        treeTableColumn("3"),
                        treeTableColumn("4"),
                        treeTableColumn("5"),
                        treeTableColumn("6")
                    );
                    return t;
                }

                @Override
                public Skin<TreeTableView> createSkin(TreeTableView control) {
                    class QQTreeTableViewSkin extends TreeTableViewSkin {
                        public QQTreeTableViewSkin(TreeTableView control) {
                            super(control);
                        }
                    }
                    return new QQTreeTableViewSkin(control);
                }
            };
            
        default:
            throw new Error("?" + t);
        }
    }
    
    protected void clearControl() {
        control = null;
        content.setCenter(null);
    }
    
    protected void replaceSkin() {
        if(control == null) {
            setTest(type);
        }
        
        content.setCenter(control);
        //System.out.println("before: " + c.getChildrenUnmodifiable());
        control.setSkin(test.createSkin(control));
        //System.out.println("after:  " + c.getChildrenUnmodifiable());
    }
    
    protected void setTest(Type t) {
        type = t;
        test = createTest(t);
        control = test.createNode();
        control.setFocusTraversable(true);
        
        content.setCenter(control);
        updateTitle();
    }
    
    protected void updateTitle() {
        StringBuilder sb = new StringBuilder();
        sb.append("Skin Change Memory Leak Test ");
        if(type != null) {
            sb.append("- ");
            sb.append(type);
            sb.append(" - ");
        }
        sb.append(System.getProperty("java.version"));
        currentStage.setTitle(sb.toString());
    }
    
    protected void newWindow() {
        Scene oldScene = currentStage.getScene();

        Parent root = oldScene.getRoot();
        oldScene.setRoot(new BorderPane());
        
        Platform.runLater(() -> {
            Scene s = new Scene(root, oldScene.getWidth(), oldScene.getHeight());
            Stage st = new Stage();
            st.setScene(s);
            st.setTitle(currentStage.getTitle());
            st.setWidth(currentStage.getWidth());
            st.setHeight(currentStage.getHeight());
            st.setX(currentStage.getX());
            st.setY(currentStage.getY());
            st.show();
            
            currentStage.hide();
            
            currentStage = st;
        });
    }
    
    protected static TableColumn tableColumn(String name) {
        TableColumn c = new TableColumn(name);
        c.setCellValueFactory((f) -> {
            return f == null ? null : new SimpleStringProperty("yo");
        });
        c.setCellFactory((cell) -> {
            return new TableCell() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null) {
                        super.setText(null);
                    } else {
                        super.setText("yo");
                    }
                }
            };
        });
        return c;
    }
    
    protected static TreeTableColumn treeTableColumn(String name) {
        TreeTableColumn c = new TreeTableColumn(name);
        c.setCellValueFactory((f) -> {
            return f == null ? null : new SimpleStringProperty("yo");
        });
        c.setCellFactory((cell) -> {
            return new TreeTableCell() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null) {
                        super.setText(null);
                    } else {
                        super.setText("yo");
                    }
                }
            };
        });
        c.setPrefWidth(200);
        return c;
    }
}
