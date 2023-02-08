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
package goryachev.monkey.pages;

import goryachev.monkey.util.FX;
import goryachev.monkey.util.ToolPane;
import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

/**
 *
 */
public class LabelPage extends ToolPane {
    public LabelPage() {
        Image im = createImage();
        
        Label text = new Label("text only");
        
        Label grTextLeft = new Label("text + graphic left");
        grTextLeft.setGraphic(new ImageView(im));
        grTextLeft.setContentDisplay(ContentDisplay.LEFT);
        
        Label grTextRight = new Label("text + graphic right");
        grTextRight.setGraphic(new ImageView(im));
        grTextRight.setContentDisplay(ContentDisplay.RIGHT);

        Label grTextTop = new Label("text + graphic top");
        grTextTop.setGraphic(new ImageView(im));
        grTextTop.setContentDisplay(ContentDisplay.TOP);

        Label grTextBottom = new Label("text + graphic bottom");
        grTextBottom.setGraphic(new ImageView(im));
        grTextBottom.setContentDisplay(ContentDisplay.BOTTOM);
        
        Label grTextOnly = new Label("text + graphic text only");
        grTextOnly.setGraphic(new ImageView(im));
        grTextOnly.setContentDisplay(ContentDisplay.TEXT_ONLY);
        
        Label grGraphicOnly = new Label("text + graphic graphic only");
        grGraphicOnly.setGraphic(new ImageView(im));
        grGraphicOnly.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        
        Label graphic = new Label();
        graphic.setGraphic(new ImageView(im));
        
        GridPane p = new GridPane();
        p.setPadding(new Insets(5, 5, 5, 5));
        p.setHgap(5);
        p.setVgap(5);
        int r = 0;
        FX.add(p, text, 0, r++);
        FX.add(p, grTextLeft, 0, r++);
        FX.add(p, grTextRight, 0, r++);
        FX.add(p, grTextTop, 0, r++);
        FX.add(p, grTextBottom, 0, r++);
        FX.add(p, grTextOnly, 0, r++);
        FX.add(p, grGraphicOnly, 0, r++);
        FX.add(p, graphic, 0, r++);
        setContent(p);
    }

    private static Image createImage() {
        int w = 24;
        int h = 16;
        Color c = Color.GREEN;

        WritableImage im = new WritableImage(w, h);
        PixelWriter wr = im.getPixelWriter();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                wr.setColor(x, y, c);
            }
        }

        return im;
    }
}
