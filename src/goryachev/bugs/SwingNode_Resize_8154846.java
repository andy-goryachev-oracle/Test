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
package goryachev.bugs;

import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 *
 */
public class SwingNode_Resize_8154846 extends Application {
    StackPane root;

    @Override
    public void start(Stage primaryStage) {
        SwingNode swingNode = new SwingNode();

        init(swingNode);

        root = new StackPane(new Rectangle(500, 500, Color.RED), swingNode);
        Scene scene = new Scene(root);

        primaryStage.setTitle("SwingNode Resize");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void init(final SwingNode node) {
        SwingUtilities.invokeLater(() -> {
            JButton button = new JButton("Click me!");
            button.addActionListener(event -> {
                Dimension buttonSize = button.getSize();
                buttonSize.setSize(buttonSize.getWidth() * 2, buttonSize.getHeight() * 2);

                button.setPreferredSize(buttonSize);
                button.setMinimumSize(buttonSize);
                button.setMaximumSize(buttonSize);
                button.setSize(buttonSize);

                System.out.println("Button size: " + button.getPreferredSize());
                Platform.runLater(
                    () -> System.out.println("SwingNode size: " + node.prefWidth(-1) + " " + node.prefHeight(-1)));
            });

            node.setContent(button);
        });
    }
}