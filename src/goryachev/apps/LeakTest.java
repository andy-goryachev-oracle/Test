package goryachev.apps;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Skin;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.skin.AccordionSkin;
import javafx.scene.control.skin.MenuBarSkin;
import javafx.scene.control.skin.MenuButtonSkin;
import javafx.scene.control.skin.ScrollBarSkin;
import javafx.scene.control.skin.SplitMenuButtonSkin;
import javafx.scene.control.skin.SplitPaneSkin;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

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
 * - type in Class Filter: "aa"
 */
public class LeakTest extends Application {
    
    enum Type {
        ACCORDION,
        MENUBAR,
        MENU_BUTTON,
        SCROLLBAR,
        SPLIT_MENU_BUTTON,
        SPLIT_PANE,
        TEXTFIELD
    }
    
    /** set the skin we are testing */
    protected final Type WE_ARE_TESTING = Type.ACCORDION;
    private Stage currentStage;
    
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

        case MENUBAR:
            return  new Test<MenuBar>() {
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
            c.setSkin(test.createSkin(c));
        });
        
        Button newWindowButton = new Button("New Window");
        
        HBox bp = new HBox(
            replaceSkinButton,
            newWindowButton
            );
        
        BorderPane rootPane = new BorderPane();
        // FIX rootPane.setTop(cm());
        rootPane.setTop(c);
        rootPane.setBottom(bp);

        Scene scene = new Scene(rootPane, 800, 600);
        
        currentStage = stage;
        stage.setScene(scene);
        stage.setTitle("Skin Change Memory Leak Test " + System.getProperty("java.version"));
        stage.show();
        
        newWindowButton.setOnAction(e -> {
            newWindow();
      });
    }
    
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
