/*
 * Copyright (c) 2022, Oracle and/or its affiliates. All rights reserved.
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
package goryachev.ensemble;

import java.util.Arrays;
import java.util.Comparator;

import goryachev.ensemble.pages.DemoPage;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Test Ensemble Application
 */
public class TestEnsembleApp extends Application {
    
    protected Stage stage;
    protected ObservableList<DemoPage> pages = FXCollections.observableArrayList();
    protected ListView<DemoPage> listField;
    protected BorderPane contentPane;
    protected DemoPage currentPage;
    
    public static void main(String[] args) {
        Application.launch(TestEnsembleApp.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        
        pages.setAll(createPages());
        
        listField = new ListView(pages);
        listField.getSelectionModel().selectedItemProperty().addListener((s,p,c) -> {
            updatePage(c);
        });
        
        contentPane = new BorderPane();
        
        SplitPane split = new SplitPane(listField, contentPane);
        split.setDividerPositions(0.4); // the end result does not look like 0.4
        SplitPane.setResizableWithParent(listField, Boolean.FALSE);
        
        stage.setScene(new Scene(split));
        stage.setWidth(1000);
        stage.setHeight(600);
        stage.show();
        
        updateTitle();
    }

    protected void updatePage(DemoPage p) {
        currentPage = p;
        contentPane.setCenter(p == null ? null : p.createPane());
        updateTitle();
    }
    
    protected void updateTitle() {
        StringBuilder sb = new StringBuilder();
        sb.append("Test Ensemble ");
        if(currentPage != null) {
            sb.append(currentPage.toString());
        }
        sb.append(" - ");
        sb.append(System.getProperty("java.runtime.version"));
        stage.setTitle(sb.toString());
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
