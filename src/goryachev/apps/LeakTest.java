package goryachev.apps;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.MenuBarSkin;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.layout.BorderPane;
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
        TEXTFIELD
    }
    
    interface Test<T extends Control> {
        public T createNode();
        
        public Skin<T> createSkin(T control);
    }
    
    public static void main(String[] args) {
        Application.launch(LeakTest.class, args);
    }
    
    @Override
    public void start(final Stage stage) throws Exception {
        // change type
        Type t =
//          Type.MENUBAR;
            Type.TEXTFIELD;
        createStage(stage, createTest(t));
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
                    m.getItems().add(m2 = new Menu("item 1"));
                    m.getItems().add(new MenuItem("item 2"));
                    m.getItems().add(new MenuItem("item 3"));
                    
                    m2.getItems().add(new MenuItem("item 21"));
                    m2.getItems().add(new MenuItem("item 22"));
                    m2.getItems().add(new MenuItem("item 23"));
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
         
        Button button = new Button("Replace Skin");
        button.setOnAction(e -> {
            c.setSkin(test.createSkin(c));
        });
        
        BorderPane rootPane = new BorderPane();
        rootPane.setTop(c);
        rootPane.setBottom(button);
        stage.setScene(new Scene(rootPane, 800, 600));
        stage.setTitle("Memory Leak Test " + System.getProperty("java.version"));
        stage.show();
    }
}
