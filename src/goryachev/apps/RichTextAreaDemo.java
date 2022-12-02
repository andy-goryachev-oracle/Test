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
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import goryachev.rich.RichTextArea;

/**
 * RichTextArea demo.
 */
public class RichTextAreaDemo extends Application {
    public static void main(String[] args) {
        Application.launch(RichTextAreaDemo.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        RichTextArea textField = new RichTextArea();
        
        MenuBar mb = new MenuBar();
        FX.menu(mb, "File");
        FX.item(mb, "Quit", () -> Platform.exit());
        
        Label status = new Label();
        
        BorderPane bp = new BorderPane();
        bp.setTop(mb);
        bp.setCenter(textField);
        bp.setBottom(status);

        stage.setScene(new Scene(bp));
        stage.setTitle("RichTextArea Demo " + System.getProperty("java.version"));
        stage.setWidth(800);
        stage.setHeight(500);
        stage.show();
    }
}
