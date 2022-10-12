package goryachev.apps;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Skin;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.MenuBarSkin;
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
        MENUBAR,
        SPLITPANE,
        TEXTFIELD
    }
    
    /** set the skin we are testing */
    protected final Type WE_ARE_TESTING = Type.SPLITPANE;
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
                    class AA_MenuBarSkin extends MenuBarSkin {
                        public AA_MenuBarSkin(MenuBar control) {
                            super(control);
                        }
                    }
                    return new AA_MenuBarSkin(control);
                }
            };
        case SPLITPANE:
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
                    class AASplitPaneSkin extends SplitPaneSkin {
                        public AASplitPaneSkin(SplitPane control) {
                            super(control);
                        }
                    }
                    return new AASplitPaneSkin(control);
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
                    class AATextFieldSkin extends TextFieldSkin {
                        public AATextFieldSkin(TextField control) {
                            super(control);
                        }
                    }
                    return new AATextFieldSkin(control);
                }
            };
        default:
            throw new Error("?" + t);
        }
    }
    
    protected <T extends Control> void createStage(Stage stage, Test<T> test) {
        T c = test.createNode();
         
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
