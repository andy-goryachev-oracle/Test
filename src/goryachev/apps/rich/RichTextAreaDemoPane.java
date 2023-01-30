/*
 * Copyright (c) 2023, Oracle and/or its affiliates. All rights reserved.
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
package goryachev.apps.rich;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Window;
import goryachev.rich.RichTextArea;
import goryachev.rich.StyledTextModel;

/**
 * Main Panel contains RichTextArea, split panes for quick size adjustment, and an option pane.
 */
public class RichTextAreaDemoPane extends BorderPane {
    enum Model {
        DEMO,
        UNEVEN_SMALL,
        UNEVEN_LARGE,
        NULL,
        ZERO_LINES,
        ONE_LINE,
        TEN_LINES,
        THOUSAND_LINES,
        BILLION_LINES,
        MONOSPACED
    }
    
    private static StyledTextModel model;
    public final ROptionPane op;
    public final RichTextArea richTextArea;
    public final ComboBox<Model> modelField;

    public RichTextAreaDemoPane() {
        richTextArea = new RichTextArea();
        richTextArea.setModel(model());
        //richTextArea.setWrapText(true);

        SplitPane hsplit = new SplitPane(richTextArea, pane());
        hsplit.setBorder(null);
        hsplit.setDividerPositions(0.9);
        hsplit.setOrientation(Orientation.HORIZONTAL);
        
        SplitPane vsplit = new SplitPane(hsplit, pane());
        vsplit.setBorder(null);
        vsplit.setDividerPositions(0.9);
        vsplit.setOrientation(Orientation.VERTICAL);
        
        modelField = new ComboBox<>();
        modelField.getItems().setAll(Model.values());
        modelField.getSelectionModel().selectedItemProperty().addListener((s,p,c) -> updateModel());
        
        CheckBox wrapText = new CheckBox("wrap text");
        wrapText.selectedProperty().bindBidirectional(richTextArea.wrapTextProperty());
        
        CheckBox displayCaret = new CheckBox("display caret");
        displayCaret.selectedProperty().bindBidirectional(richTextArea.displayCaretProperty());
        
        Button reloadModel = new Button("Reload Model");
        reloadModel.setOnAction((ev) -> reloadModel());
        
        // TODO blink rate
        
        op = new ROptionPane();
        op.label("Model:");
        op.option(modelField);
        op.option(reloadModel);
        op.option(wrapText);
        op.option(displayCaret);
        
        setCenter(vsplit);
        setRight(op);
        
        modelField.getSelectionModel().selectFirst();
    }
    
    private static StyledTextModel model() {
        return model;
    }
    
    protected void updateModel() {
        model = createModel();
        richTextArea.setModel(model());
    }
    
    protected void reloadModel() {
        richTextArea.setModel(null);
        updateModel();
    }
    
    private StyledTextModel createModel() {
        Model m = modelField.getSelectionModel().getSelectedItem();
        if(m == null) {
            return null;
        }
        
        switch(m) {
        case BILLION_LINES:
            return new DemoStyledTextModel(1_000_000_000, false);
        case DEMO:
            return new RichTextAreaDemoModel();
        case MONOSPACED:
            return new DemoStyledTextModel(100_000, true);
        case NULL:
            return null;
        case ONE_LINE:
            return new DemoStyledTextModel(1, false);
        case TEN_LINES:
            return new DemoStyledTextModel(10, false);
        case THOUSAND_LINES:
            return new DemoStyledTextModel(1_000, false);
        case UNEVEN_SMALL:
            return new UnevenStyledTextModel(20);
        case UNEVEN_LARGE:
            return new UnevenStyledTextModel(2000);
        case ZERO_LINES:
            return new DemoStyledTextModel(0, false);
        default:
            throw new Error("?" + m);
        }
    }

    protected static Pane pane() {
        Pane p = new Pane();
        SplitPane.setResizableWithParent(p, false);
        p.setStyle("-fx-background-color:#dddddd;");
        return p;
    }
    
    public Button addButton(String name, Runnable action) {
        Button b = new Button(name);
        b.setOnAction((ev) -> {
            action.run();
        });
        
        toolbar().add(b);
        return b;
    }
    
    public TBar toolbar() {
        if(getTop() instanceof TBar) {
            return (TBar)getTop();
        }
        
        TBar t = new TBar();
        setTop(t);
        return t;
    }
    
    public Window getWindow() {
        Scene s = getScene();
        if(s != null) {
            return s.getWindow();
        }
        return null;
    }
    
    public void setOptions(Node n) {
        setRight(n);
    }
    
    //
    
    public static class TBar extends HBox {
        public TBar() {
            setFillHeight(true);
            setAlignment(Pos.CENTER_LEFT);
            setSpacing(2);
        }

        public <T extends Node> T add(T n) {
            getChildren().add(n);
            return n;
        }

        public void addAll(Node... nodes) {
            for (Node n : nodes) {
                add(n);
            }
        }
    }
}
