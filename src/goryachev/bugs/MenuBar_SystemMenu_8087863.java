/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
 */
package goryachev.bugs;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8087863
 * @author slions
 */
public class MenuBar_SystemMenu_8087863 extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Sample");
        final Scene scene = new Scene(new Group(), 800, 500);

        ///////////////////////////////////////

        final TextField tf = new TextField("TextField");
        tf.setLayoutX(100);
        tf.setLayoutY(100);

        final ObservableList<String> list = FXCollections.observableArrayList();
        list.addAll("toto", "titi", "tutu");
        final ComboBox cb = new ComboBox(list);
        cb.setEditable(true);
        cb.setPrefWidth(100);
        cb.setPromptText("empty");
        cb.setLayoutX(100);
        cb.setLayoutY(200);

        final Button b = new Button("Button");
        b.setLayoutX(10);
        b.setLayoutY(30);

        final ListView listView = new ListView();
        listView.setLayoutX(80);
        listView.setLayoutY(30);

        final TreeView treeView = new TreeView();
        treeView.setLayoutX(350);
        treeView.setLayoutY(30);

        ///////////////////////////////////////
        final MenuItem selectAllMenuItem = new MenuItem("SelectAll");
        selectAllMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.META_DOWN));
        selectAllMenuItem.setOnAction((ev) -> {
            System.out.println("==>> SELECT ALL Menu onAction called = " + scene.getFocusOwner());
        });
        final MenuItem customMenuItem = new MenuItem("customMenuItem");
        customMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.B, KeyCombination.META_DOWN));
        customMenuItem.setOnAction((ev) -> {
            System.out.println("==>> CUSTOM Menu onAction called = " + scene.getFocusOwner());
        });

        final Menu mainMenu = new Menu("Main");
        mainMenu.getItems().addAll(selectAllMenuItem, customMenuItem);

        final MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(mainMenu);
        menuBar.setUseSystemMenuBar(true);

        final Group root = (Group)scene.getRoot();
        root.getChildren().clear();
        root.getChildren().addAll(menuBar, b, listView, treeView);

        stage.setScene(scene);
        stage.show();
    }
}