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
package goryachev.rich.util;

import java.text.DecimalFormat;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import goryachev.rich.SideDecorator;

/**
 * Side decorator that shows model line numbers, 1-based.
 */
public class LineNumberDecorator implements SideDecorator {
    private final DecimalFormat format;
    private final Background background;

    public LineNumberDecorator() {
        this("#,##0");
    }

    public LineNumberDecorator(String spec) {
        format = new DecimalFormat(spec);
        background = new Background(new BackgroundFill(Color.gray(0.5, 0.5), null, null));
    }

    @Override
    public double getPrefWidth(double viewWidth) {
        return 0;
    }

    @Override
    public Node getNode(int ix, boolean forMeasurement) {
        if (forMeasurement) {
            // for measurer node only: allow for extra digit(s) in the bottom rows
            ix += 300;
        }

        String s = format.format(ix + 1);
        if(forMeasurement) {
            // account for some variability if propertional font is used
            s += " ";
        }

        Label t = new Label();
        t.setText(s);
        t.setAlignment(Pos.TOP_RIGHT);
        t.setPadding(Insets.EMPTY);

        // why do I need to wrap it into Pane?
        //HBox p = new HBox(t);
        //HBox.setHgrow(t, Priority.ALWAYS);
        BorderPane p = new BorderPane(t);
        p.setBackground(background);
        p.setPadding(Insets.EMPTY);
        return p;
    }
}
