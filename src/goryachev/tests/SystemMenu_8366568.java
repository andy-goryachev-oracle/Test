package goryachev.tests;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// https://bugs.openjdk.org/browse/JDK-8366568
public class SystemMenu_8366568 extends Application {

    // One stage uses the system menu bar, one stage uses a standard menu bar.
    // For each stage we track a node we can use to focus the stage and a
    // menu item that is unique to that stage.
    static class StageData {
        Stage stage;
        Node focusItem;
        MenuBar menuBar;
        MenuItem menuItem;
        KeyCode acceleratorCode;
    };

    static StageData systemMenuBarData;
    static StageData standardMenuBarData;

    // The list of common system menus
    static ObservableList<Menu> commonMenus;
    // A menu item that is always available in the common system menus
    static MenuItem commonMenuItem;

    // Accelerator codes used to test the presence of menu items and their
    static final KeyCode SYSTEM_MENU_ACCELERATOR = KeyCode.G;
    static final KeyCode STANDARD_MENU_ACCELERATOR = KeyCode.B;
    static final KeyCode COMMON_MENU_ACCELERATOR = KeyCode.L;

    @Override
    public void start(Stage s) {
        systemMenuBarData = initStage(true, SYSTEM_MENU_ACCELERATOR);
        standardMenuBarData = initStage(false, STANDARD_MENU_ACCELERATOR);

        commonMenuItem = new MenuItem("Common Item");
        commonMenuItem.setAccelerator(new KeyCodeCombination(COMMON_MENU_ACCELERATOR, KeyCombination.SHORTCUT_DOWN));
        commonMenuItem.setOnAction((_) -> {
            IO.println("common menu: " + COMMON_MENU_ACCELERATOR);
        });

        var menu = new Menu("Common Menu");
        menu.getItems().add(commonMenuItem);

        commonMenus = FXCollections.<Menu>observableArrayList();
        commonMenus.add(menu);
        MenuBar.setCommonSystemMenus(commonMenus);
    }

    private static StageData initStage(boolean useSystemMenuBar, KeyCode stageAccelCode) {
        var menuItem = new MenuItem("Stage Item");
        menuItem.setAccelerator(new KeyCodeCombination(stageAccelCode, KeyCombination.SHORTCUT_DOWN));
        menuItem.setOnAction((_) -> {
            IO.println("stage menu: " + stageAccelCode);
        });

        var menu = new Menu("Stage Menu");
        menu.getItems().add(menuItem);

        var menuBar = new MenuBar();
        menuBar.setUseSystemMenuBar(useSystemMenuBar);
        menuBar.getMenus().add(menu);

        var button = new Button("Focus Button");
        var vbox = new VBox(menuBar, button);
        var scene = new Scene(vbox);
        var stage = new Stage();
        stage.setScene(scene);
        stage.show();

        StageData data = new StageData();
        data.stage = stage;
        data.focusItem = button;
        data.menuBar = menuBar;
        data.menuItem = menuItem;
        data.acceleratorCode = stageAccelCode;
        return data;
    }
}