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
package goryachev.research;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 *
 */
public class IosevkaFont extends Application {
    @Override
    public void start(Stage stage) {
        SwingNode swingNode = new SwingNode();
        init(swingNode);

        System.out.println("\nFamilies:");
        for (var s : javafx.scene.text.Font.getFamilies()) {
            if (s.startsWith("Iosevka")) {
                System.out.println(s);
            }
        }

        System.out.println("\nNames:");
        for (var s : javafx.scene.text.Font.getFontNames()) {
            if (s.startsWith("Iosevka")) {
                System.out.println(s);
            }
        }

        System.out.println("\nIosevka:");
        for (var nam : javafx.scene.text.Font.getFontNames("Iosevka Fixed SS16")) {
            System.out.println(nam);
        }

        var f = new javafx.scene.text.Font("Iosevka Fixed SS16 Extended Oblique", 12.0);
        System.out.println("fx font=" + f + ", style=" + f.getStyle());
        
        TextArea t = new TextArea();
        t.setText(f.toString());
        t.setFont(f);

        BorderPane bp = new BorderPane();
        bp.setTop(t);
        bp.setCenter(swingNode);
        Scene scene = new Scene(bp);

        stage.setTitle("Font");
        stage.setWidth(500);
        stage.setHeight(400);
        stage.setScene(scene);
        stage.show();
    }

    private void init(final SwingNode node) {
        SwingUtilities.invokeLater(() -> {
            Font f = getFont();
            System.out.println("fx font=" + f + ", style=" + f.getStyle());
            // font2DHandle:
            //  fullName = "Iosevka-Fixed-SS16-Bold-Oblique"
            //  nativeFontName  "Iosevka-Fixed-SS16-Bold-Oblique"
            JTextArea t = new JTextArea();
            t.setText(f.toString());
            t.setFont(f);
            node.setContent(t);
        });
    }

    private Font getFont() {
        Font f = null;
        String names[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String s : names) {
            System.out.println(s);
            if (s.startsWith("Iosevka")) {
                f = new Font(s, Font.BOLD | Font.ITALIC, 12);
            }
        }
        return f;
    }
}