/*
 * Copyright (c) 2025, Oracle and/or its affiliates. All rights reserved.
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
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Removing Anchors Has No Effect.
 * https://bugs.openjdk.org/browse/JDK-8350809
 */
public class AnchorPane_SetNull_8350809 extends Application {
    private Label node;
    private int seq;
    
    @Override
    public void start(Stage stage) {
        node = new Label("Label");
        node.setBackground(Background.fill(Color.SALMON));
        node.textProperty().bind(Bindings.createStringBinding(() -> {
            return "Properties: " + node.getProperties();
        }, node.getProperties()));
        
        AnchorPane p = new AnchorPane();
        p.getChildren().add(node);
        
        Button clearButton = new Button("Clear Anchors");
        clearButton.setOnAction((ev) -> {
            clearAnchors();
        });
        
        Button setButton = new Button("Set Anchors");
        setButton.setOnAction((ev) -> {
            setAnchors();
        });
        
        BorderPane bp = new BorderPane(p);
        bp.setBottom(new HBox(5, setButton, clearButton));
        Scene scene = new Scene(bp, 500, 400);
        stage.setScene(scene);
        stage.setTitle("AnchorPane Null Anchor");
        stage.show();
    }

    private void setAnchors() {
        AnchorPane.setLeftAnchor(node, 100.0 + seq);
        AnchorPane.setTopAnchor(node, 50.0 + seq);
        seq++;
    }

    private void clearAnchors() {
        AnchorPane.setLeftAnchor(node, null);
        AnchorPane.setTopAnchor(node, null);
    }
}
