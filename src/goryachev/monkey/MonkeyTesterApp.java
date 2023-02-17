/*
 * Copyright (c) 2022, 2023, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package goryachev.monkey;

import java.util.Arrays;
import java.util.Comparator;
import goryachev.monkey.pages.DemoPage;
import goryachev.monkey.util.FX;
import goryachev.monkey.util.TestPaneBase;
import goryachev.settings.FxSettings;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Monkey Tester Application
 */
public class MonkeyTesterApp extends Application {
    
    protected Stage stage;
    protected ObservableList<DemoPage> pages = FXCollections.observableArrayList();
    protected ListView<DemoPage> listField;
    protected BorderPane contentPane;
    protected DemoPage currentPage;
    protected Label status;
    
    public static void main(String[] args) {
        Application.launch(MonkeyTesterApp.class, args);
    }
    
    @Override
    public void init() {
        FxSettings.useDirectory(".MonkeyTesterApp");
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        
        status = new Label();
        status.setPadding(new Insets(2, 2, 2, 2));
        
        pages.setAll(createPages());
        
        listField = new ListView(pages);
        listField.getSelectionModel().selectedItemProperty().addListener((s,p,c) -> {
            updatePage(c);
        });
        
        contentPane = new BorderPane();
        contentPane.setId("ContentPane");
        
        SplitPane split = new SplitPane(listField, contentPane);
        split.setDividerPositions(0.15);
        SplitPane.setResizableWithParent(listField, Boolean.FALSE);
        SplitPane.setResizableWithParent(contentPane, Boolean.TRUE);
        
        BorderPane bp = new BorderPane();        
        bp.setTop(createMenu());
        bp.setCenter(split);
        bp.setBottom(status);

        stage.setScene(new Scene(bp));
        stage.setWidth(1200);
        stage.setHeight(800);
        
        stage.renderScaleXProperty().addListener((x) -> updateStatus());
        stage.renderScaleYProperty().addListener((x) -> updateStatus());
        updateTitle();
        updateStatus();

        stage.show();
    }
    
    protected MenuBar createMenu() {
        CheckMenuItem orientation = new CheckMenuItem("Orientation: RTL");
        orientation.setOnAction((ev) -> {
            NodeOrientation v = (orientation.isSelected()) ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT;
            stage.getScene().setNodeOrientation(v);
        });
        
        CheckMenuItem usePreferred = new CheckMenuItem("Use Preferred Size");
        usePreferred.setOnAction((ev) -> {
            boolean on = usePreferred.isSelected();
            TestPaneBase.usePreferredSize.set(on);
        });
        
        MenuBar b = new MenuBar();
        // File
        FX.menu(b, "_File");
        FX.item(b, "Quit", Platform::exit);
        // Page
        FX.menu(b, "_Page");
        FX.item(b, "Reload Current Page", this::reloadCurrentPage);
        FX.separator(b);
        FX.item(b, usePreferred);
        // Window
        FX.menu(b, "_Window");
        FX.item(b, orientation);
        // Menu
        FX.menu(b, "_Menu");
        ToggleGroup g = new ToggleGroup();
        FX.radio(b, "RadioMenuItem 1", KeyCombination.keyCombination("Shortcut+1"), g);
        FX.radio(b, "RadioMenuItem 2", KeyCombination.keyCombination("Shortcut+2"), g);
        FX.radio(b, "RadioMenuItem 3", KeyCombination.keyCombination("Shortcut+3"), g);
        return b;
    }

    protected void updatePage(DemoPage p) {
        FxSettings.store(contentPane);
        currentPage = p;
        contentPane.setCenter(p == null ? null : p.createPane());
        updateTitle();
        FxSettings.restore(contentPane);
    }
    
    protected void reloadCurrentPage() {
        updatePage(currentPage);
    }
    
    protected void updateTitle() {
        StringBuilder sb = new StringBuilder();
        sb.append("Monkey Tester");
        if(currentPage != null) {
            sb.append(" - ");
            sb.append(currentPage.toString());
        }
        stage.setTitle(sb.toString());
    }
    
    protected void updateStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append(System.getProperty("java.runtime.version"));
        
        if(stage.getRenderScaleX() == stage.getRenderScaleY()) {
            sb.append("  scale=");
            sb.append(stage.getRenderScaleX());
        } else {
            sb.append("  scaleX=");
            sb.append(stage.getRenderScaleX());
            sb.append("  scaleY=");
            sb.append(stage.getRenderScaleY());
        }
        status.setText(sb.toString());
    }
    
    protected DemoPage[] createPages() {
        DemoPage[] pages = Pages.create();
        Arrays.sort(pages, new Comparator<DemoPage>() {
            @Override
            public int compare(DemoPage a, DemoPage b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
        return pages;
    }
}
