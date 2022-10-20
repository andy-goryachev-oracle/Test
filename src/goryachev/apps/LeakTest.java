package goryachev.apps;

import java.time.LocalDate;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
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
import javafx.scene.control.Skin;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.skin.AccordionSkin;
import javafx.scene.control.skin.ButtonBarSkin;
import javafx.scene.control.skin.ColorPickerSkin;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.control.skin.MenuBarSkin;
import javafx.scene.control.skin.MenuButtonSkin;
import javafx.scene.control.skin.PaginationSkin;
import javafx.scene.control.skin.ScrollBarSkin;
import javafx.scene.control.skin.SplitMenuButtonSkin;
import javafx.scene.control.skin.SplitPaneSkin;
import javafx.scene.control.skin.TextFieldSkin;
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
 * - exercise control functionality
 * - click on [Change Skin] button as many times as you like
 * - exercise control some more
 * - in VisualVM, Monitor -> [Perform GC] -> [Heap Dump]
 * - in heap dump, select Objects pulldown (instead of Summary)
 * - type in Class Filter: "qq" (all dummy skin classes are named starting with QQ)
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
        SCROLLBAR,
        SPLIT_MENU_BUTTON,
        SPLIT_PANE,
        TEXTFIELD
    }
    
    /** set the skin we are testing */
    protected final Type WE_ARE_TESTING = Type.PAGINATION;
    private Stage currentStage;
    private BorderPane rootPane;
    
    interface Test<T extends Control> {
        public T createNode();
        
        public Skin<T> createSkin(T control);
    }
    
    public static void main(String[] args) {
        Application.launch(LeakTest.class, args);
    }
    
    @Override
    public void start(final Stage stage) throws Exception {
        createStage(stage, createTest(WE_ARE_TESTING));
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
            
        case SCROLLBAR:
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
        case TEXTFIELD:
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
            
        default:
            throw new Error("?" + t);
        }
    }
    
    protected <T extends Control> void createStage(Stage stage, Test<T> test) {
        T c = test.createNode();
        c.setFocusTraversable(true);
         
        Button replaceSkinButton = new Button("Replace Skin");
        replaceSkinButton.setOnAction(e -> {
            //System.out.println("before: " + c.getChildrenUnmodifiable());
            c.setSkin(test.createSkin(c));
            //System.out.println("after:  " + c.getChildrenUnmodifiable());
        });
        
        Button newWindowButton = new Button("New Window");
        
        Button clearButton = new Button("Remove Controls");
        
        HBox bp = new HBox(
            replaceSkinButton,
            newWindowButton,
            clearButton
            );
        
        rootPane = new BorderPane();
        // FIX rootPane.setTop(cm());
        rootPane.setTop(c);
        rootPane.setBottom(bp);

        Scene scene = new Scene(rootPane, 800, 600);
        
        currentStage = stage;
        stage.setScene(scene);
        stage.setTitle("Skin Change Memory Leak Test - " + WE_ARE_TESTING + " - " + System.getProperty("java.version"));
        stage.show();
        
        newWindowButton.setOnAction(e -> {
            newWindow();
        });
        
        clearButton.setOnAction(e -> {
            rootPane.setTop(null);
            replaceSkinButton.setOnAction(null);
        });
    }
    
    @Deprecated // not used anymore
    protected MenuBar cm() {
        Menu menu1 = new Menu("Menu1");
        Menu menu2 = new Menu("Menu2");
        Menu menu3 = new Menu("Menu3");

        MenuItem menuItem1 = new MenuItem("MenuItem1");
        MenuItem menuItem2 = new MenuItem("MenuItem2");
        MenuItem menuItem3 = new MenuItem("MenuItem3");

        menu1.getItems().add(menuItem1);
        menu2.getItems().add(menuItem2);
        menu3.getItems().add(menuItem3);

        MenuBar mb = new MenuBar();
        mb.getMenus().addAll(menu1, menu2, menu3);
        menu2.setDisable(true);
        return mb;
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
}
