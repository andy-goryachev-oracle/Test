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

import java.nio.charset.Charset;
import java.util.Base64;
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
    private static StyledTextModel model;
    public final ROptionPane op;
    public final RichTextArea richTextArea;
    public final ComboBox<Model> modelField;

    public RichTextAreaDemoPane() {
        richTextArea = new RichTextArea();
        richTextArea.setModel(model());

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
        
        CheckBox fatCaret = new CheckBox("fat caret");
        fatCaret.selectedProperty().addListener((s,p,on) -> {
            Node n = richTextArea.lookup(".caret");
            if(n != null) {
                if(on) {
                    n.setStyle("-fx-stroke-width:2; -fx-stroke:red; -fx-effect:dropshadow(gaussian,rgba(0,0,0,.5),5,0,1,1);");
                } else {
                    n.setStyle(null);
                }
            }
        });
        
        Button reloadModel = new Button("Reload Model");
        reloadModel.setOnAction((ev) -> reloadModel());
        
        op = new ROptionPane();
        op.label("Model:");
        op.option(modelField);
        op.option(reloadModel);
        op.option(wrapText);
        op.option(displayCaret);
        op.option(fatCaret);
        op.label("Blink Rate: TODO"); // TODO
        
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
        return Model.create(m);
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
    
    protected String generateStylesheet(boolean fat) {
        String s = ".rich-text-area .caret { -fx-stroke-width:" + (fat ? 2 : 1) + "; }";
        return "data:text/css;base64," + Base64.getEncoder().encodeToString(s.getBytes(Charset.forName("utf-8")));
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
