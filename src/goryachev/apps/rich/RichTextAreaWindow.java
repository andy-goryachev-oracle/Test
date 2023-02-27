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

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import goryachev.apps.FX;
import goryachev.rich.Origin;
import goryachev.rich.RichTextArea;
import goryachev.rich.TextPos;

/**
 * Rich Text Area Demo window
 */
public class RichTextAreaWindow extends Stage {
    public final RichTextAreaDemoPane demoPane;
    public final Label status;
    
    public RichTextAreaWindow() {
        demoPane = new RichTextAreaDemoPane();
        
        MenuBar mb = new MenuBar();
        FX.menu(mb, "File");
        FX.item(mb, "New Window", this::newWindow);
        FX.separator(mb);
        FX.item(mb, "Close Window", this::hide);
        FX.separator(mb);
        FX.item(mb, "Quit", () -> Platform.exit());
        
        status = new Label();
        status.setPadding(new Insets(2, 10, 2, 10));
        
        BorderPane bp = new BorderPane();
        bp.setTop(mb);
        bp.setCenter(demoPane);
        bp.setBottom(status);
        
        Scene scene = new Scene(bp);
        scene.getStylesheets().add(RichTextAreaWindow.class.getResource("RichTextAreaDemo.css").toExternalForm());

        setScene(scene);
        setTitle("RichTextArea Demo " + System.getProperty("javafx.runtime.version") + " " + System.getProperty("java.version"));
        setWidth(1200);
        setHeight(600);
        
        demoPane.control.caretPositionProperty().addListener((x) -> updateStatus());
        demoPane.control.originProperty().addListener((x) -> updateStatus());
    }

    protected void updateStatus() {
        RichTextArea t = demoPane.control;
        TextPos p = t.getCaretPosition();
        Origin origin = t.getOrigin();

        StringBuilder sb = new StringBuilder();

        sb.append(origin);

        if (p != null) {
            sb.append(" line=").append(p.lineIndex());
            sb.append(" char=").append(p.charIndex());
        }

        status.setText(sb.toString());
    }

    protected void newWindow() {
        double offset = 20;
        
        RichTextAreaWindow w = new RichTextAreaWindow();
        w.setX(getX() + offset);
        w.setY(getY() + offset);
        w.setWidth(getWidth());
        w.setHeight(getHeight());
        w.show();
    }
}
