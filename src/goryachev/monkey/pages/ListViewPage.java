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
package goryachev.monkey.pages;

import java.util.Random;
import goryachev.monkey.util.OptionPane;
import goryachev.monkey.util.ToolPane;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

/**
 * ListView page
 */
public class ListViewPage extends ToolPane {
    enum Demo {
        EMPTY("Empty"),
        LARGE("Large"),
        SMALL("Small"),
        VARIABLE("Variable Height"),
        ;

        private final String text;
        Demo(String text) { this.text = text; }
        public String toString() { return text; }
    }

    public enum Selection {
        SINGLE("single selection"),
        MULTIPLE("multiple selection"),
        NULL("null selection model");
        
        private final String text;
        Selection(String text) { this.text = text; }
        public String toString() { return text; }
    }

    public enum Cmd {
        ROWS,
        VARIABLE_ROWS,
    }

    protected final ComboBox<Demo> demoSelector;
    protected final ComboBox<Selection> selectionSelector;
    protected final CheckBox nullFocusModel;
    protected ListView<Object> list;
    
    public ListViewPage() {
        // selector
        demoSelector = new ComboBox<>();
        demoSelector.getItems().addAll(Demo.values());
        demoSelector.setEditable(false);
        demoSelector.getSelectionModel().selectedItemProperty().addListener((s,p,c) -> {
            updatePane();
        });

        selectionSelector = new ComboBox<>();
        selectionSelector.getItems().addAll(Selection.values());
        selectionSelector.setEditable(false);
        selectionSelector.getSelectionModel().selectedItemProperty().addListener((s,p,c) -> {
            updatePane();
        });
        
        nullFocusModel = new CheckBox("null focus model");
        nullFocusModel.selectedProperty().addListener((s,p,c) -> {
            updatePane();
        });
        
        Button addButton = new Button("Add Item");
        addButton.setOnAction((ev) -> {
            list.getItems().add(newItem());
        });
        
        Button clearButton = new Button("Clear Items");
        clearButton.setOnAction((ev) -> {
            list.getItems().clear();
        });

        // layout

        OptionPane p = new OptionPane();
        p.label("Data:");
        p.option(demoSelector);
        p.option(addButton);
        p.option(clearButton);
        p.label("Selection Model:");
        p.option(selectionSelector);
        p.option(nullFocusModel);
        setOptions(p);

        demoSelector.getSelectionModel().selectFirst();
        selectionSelector.getSelectionModel().select(Selection.MULTIPLE);
    }

    protected Object[] createSpec(Demo d) {
        switch(d) {
        case EMPTY:
            return new Object[] {
            };
        case LARGE:
            return new Object[] {
                Cmd.ROWS, 10_000,
            };
        case SMALL:
            return new Object[] {
                Cmd.ROWS, 3,
            };
        case VARIABLE:
            return new Object[] {
                Cmd.VARIABLE_ROWS, 500,
            };
        default:
            throw new Error("?" + d);
        }
    }

    protected void updatePane() {
        Demo d = demoSelector.getSelectionModel().getSelectedItem();
        Object[] spec = createSpec(d);

        Pane n = createPane(d, spec);
        setContent(n);
    }

    protected Pane createPane(Demo demo, Object[] spec) {
        if ((demo == null) || (spec == null)) {
            return new BorderPane();
        }
        
        boolean nullSelectionModel = false;
        SelectionMode selectionMode = SelectionMode.SINGLE;
        Selection sel = selectionSelector.getSelectionModel().getSelectedItem();
        if(sel != null) {
            switch(sel) {
            case MULTIPLE:
                selectionMode = SelectionMode.MULTIPLE;
                break;
            case NULL:
                nullSelectionModel = true;
                break;
            case SINGLE:
                break;
            default:
                throw new Error("?" + sel);
            }
        }

        list = new ListView<>();
        list.getSelectionModel().setSelectionMode(selectionMode);
        if(nullSelectionModel) {
            list.setSelectionModel(null);
        }
        if(nullFocusModel.isSelected()) {
            list.setFocusModel(null);
        }
        
        for (int i = 0; i < spec.length;) {
            Object x = spec[i++];
            if (x instanceof Cmd cmd) {
                switch (cmd) {
                case ROWS:
                    {
                        int n = (int)(spec[i++]);
                        for (int j = 0; j < n; j++) {
                            list.getItems().add(newItem());
                        }
                    }
                    break;
                case VARIABLE_ROWS:
                    {
                        int n = (int)(spec[i++]);
                        for (int j = 0; j < n; j++) {
                            list.getItems().add(newVariableItem());
                        }
                    }
                    break;
                default:
                    throw new Error("?" + cmd);
                }
            } else {
                throw new Error("?" + x);
            }
        }

        BorderPane bp = new BorderPane();
        bp.setCenter(list);
        return bp;
    }

    protected String newItem() {
        return System.currentTimeMillis() + "." + System.nanoTime();
    }
    
    protected String newVariableItem() {
        int rows = 1 << new Random().nextInt(5);
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<rows; i++) {
            if(i > 0) {
                sb.append('\n');
            }
            sb.append(i);
        }
        return System.currentTimeMillis() + "." + System.nanoTime() + "." + sb;
    }
}
