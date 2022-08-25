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
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Fails to drag a stage on Linux, works fine on Mac OS X and Windows.
 * https://bugs.openjdk.org/browse/JDK-8292922
 * 
 * To reproduce:
 * - grab the red label and drag around
 * - notice that on Linux, the movement stops after the drag window gets shown,
 * works fine on other platforms.
 */
public class DragFailure extends Application {
    private DragWindow dragWindow;

    public static void main(String[] args) {
        Application.launch(DragFailure.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Label t = new Label("Drag me");
        t.setPrefWidth(Double.MAX_VALUE);
        t.setBackground(Background.fill(Color.RED));

        t.addEventHandler(MouseEvent.DRAG_DETECTED, this::onDragDetected);
        t.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::onMouseDragged);
        t.addEventHandler(MouseEvent.MOUSE_RELEASED, this::onMouseReleased);

        BorderPane bp = new BorderPane();
        bp.setTop(t);

        stage.setScene(new Scene(bp));
        stage.setTitle("JDK-8292922 " + System.getProperty("java.version"));
        stage.setWidth(500);
        stage.setHeight(300);
        stage.show();
    }

    protected void onDragDetected(MouseEvent ev) {
        System.out.println("drag detected " + ev.getScreenX() + ", " + ev.getSceneY());

        if (dragWindow == null) {
            dragWindow = new DragWindow();
            dragWindow.show();
        }
    }

    protected void onMouseDragged(MouseEvent ev) {
        System.out.println("dragging " + ev.getScreenX() + ", " + ev.getSceneY());

        if (dragWindow != null) {
            dragWindow.setX(ev.getScreenX());
            dragWindow.setY(ev.getScreenY());
        }
    }

    protected void onMouseReleased(MouseEvent ev) {
        System.out.println("released " + ev.getScreenX() + ", " + ev.getSceneY());

        if (dragWindow != null) {
            dragWindow.hide();
            dragWindow = null;
        }
    }

    protected static class DragWindow extends Stage {
        public DragWindow() {
            initModality(Modality.NONE);
            initStyle(StageStyle.UNDECORATED);
            setAlwaysOnTop(true);
            setOpacity(0.5);
            setWidth(500);
            setHeight(300);
        }
    }
}
