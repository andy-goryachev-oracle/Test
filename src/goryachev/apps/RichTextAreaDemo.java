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
package goryachev.apps;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import goryachev.rich.RichTextArea;
import goryachev.rich.StyledTextModel;
import goryachev.util.VerticalGridPane;

/**
 * RichTextArea demo.
 */
public class RichTextAreaDemo extends Application {
    public static void main(String[] args) {
        Application.launch(RichTextAreaDemo.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        StyledTextModel m = new RichTextAreaDemoModel();
        
        RichTextArea textField = new RichTextArea();
        textField.setModel(m);
        textField.setWrapText(true);
        
        RichTextArea textField2 = new RichTextArea();
        textField2.setModel(m);
        
        MenuBar mb = new MenuBar();
        FX.menu(mb, "File");
        FX.item(mb, "Quit", () -> Platform.exit());
        
        Label status = new Label();
        
        // grid pane does not work as expected
//        GridPane g = new GridPane();
//        g.add(new BorderPane(textField), 0, 0);
//        g.add(new BorderPane(textField2), 0, 1);
        
        // cannot do fill
//        VBox vb = new VBox();
//        VBox.setVgrow(textField, Priority.ALWAYS);
//        VBox.setVgrow(textField2, Priority.ALWAYS);
//        vb.getChildren().addAll(textField, textField2);
        
        SplitPane p = new SplitPane(textField, textField2);
        p.setOrientation(Orientation.VERTICAL);
        
        //VerticalGridPane p = new VerticalGridPane(textField, textField2);
        
        BorderPane bp = new BorderPane();
        bp.setTop(mb);
        bp.setCenter(p);
        bp.setBottom(status);
        
        Scene scene = new Scene(bp);
        scene.getStylesheets().add(HelloTooltip.class.getResource("RichTextAreaDemo.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("RichTextArea Demo " + System.getProperty("java.version"));
        stage.setWidth(800);
        stage.setHeight(500);
        stage.show();
    }
}
