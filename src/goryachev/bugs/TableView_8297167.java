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
package goryachev.bugs;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * https://bugs.openjdk.org/browse/JDK-8297167
 */
public class TableView_8297167 extends Application {
    private TableView<String> table;
    private TableViewSelectionModel<String> sm;
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        table = new TableView<String>();
        sm = table.getSelectionModel();

        sm.setSelectionMode(SelectionMode.MULTIPLE);
        sm.setCellSelectionEnabled(false);

        table.getItems().setAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12");

        TableColumn<String, String> col0 = new TableColumn<String, String>("col0");
        TableColumn<String, String> col1 = new TableColumn<String, String>("col1");
        TableColumn<String, String> col2 = new TableColumn<String, String>("col2");
        TableColumn<String, String> col3 = new TableColumn<String, String>("col3");
        TableColumn<String, String> col4 = new TableColumn<String, String>("col4");
        table.getColumns().setAll(
            createColumn("col0"),
            createColumn("col1"),
            createColumn("col2"),
            createColumn("col3"),
            createColumn("col4")
        );
        
        // TableViewKeyInputTest:860
        sm.setSelectionMode(SelectionMode.MULTIPLE);
        sm.setCellSelectionEnabled(true);
        sm.clearAndSelect(1, col0);
        
        Scene scene = new Scene(table);
        stage.setScene(scene);
        stage.setTitle("JDK-8137244 " + System.getProperty("java.version"));
        stage.show();
    }
    
    private TableColumn<String, String> createColumn(String name) {
        TableColumn<String, String> c = new TableColumn<String, String>(name);
        c.setCellValueFactory((f) -> new SimpleStringProperty(" "));
        return c;
    }
}
