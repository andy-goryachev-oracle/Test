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
package goryachev.rich.simple;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import goryachev.rich.StyledParagraph;
import goryachev.rich.TextCell;

public class SimpleStyledImageParagraph implements StyledParagraph {
    private final int index; // TODO move to base class?
    private final Image image;
    
    public SimpleStyledImageParagraph(int index, Image image) {
        this.index = index;
        this.image = image;
    }
    
    @Override
    public TextCell createTextCell() {
        ImageView v = new ImageView(image);
        Pane p = new Pane(v) {            
            @Override
            protected void layoutChildren() {
                double width = getWidth();
                double sc;
                if(width < image.getWidth()) {
                    sc = width / image.getWidth();
                } else {
                    sc = 1.0;
                }
                v.setScaleX(sc);
                v.setScaleY(sc);
                layoutInArea(v, 0, 0, image.getWidth() * sc, image.getHeight() * sc, 0, null, true, false, HPos.CENTER, VPos.CENTER);
            }
            
            @Override
            protected double computePrefHeight(double w) {
                if(w < image.getWidth()) {
                    return image.getHeight() * w / image.getWidth();
                } else {
                    return image.getHeight();
                }
            }
        };
        return new TextCell(index, p);
    }

    @Override
    public int getIndex() {
        return index;
    }
}
