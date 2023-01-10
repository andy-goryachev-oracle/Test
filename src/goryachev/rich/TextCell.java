/*
 * Copyright (c) 2022, 2023, Oracle and/or its affiliates. All rights reserved.
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
// this code borrows heavily from the following project, with permission from the author:
// https://github.com/andy-goryachev/FxEditor
package goryachev.rich;

import javafx.scene.layout.Region;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.PathElement;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import goryachev.rich.util.FxPathBuilder;
import goryachev.rich.util.NewAPI;

/**
 * Represents a text flow cell - contains either a TextFlow or a Region. 
 */
public class TextCell {
    private final int index;
    private final Region content;
    private double width;
    private double height;

    public TextCell(int index, Region content) {
        this.index = index;
        this.content = content;
    }

    public TextCell(int index) {
        this(index, new TextFlow());
    }

    public Region getContent() {
        return content;
    }

    public void addSegment(String text, String style, String[] css) {
        Text t = new Text(text);
        if (style != null) {
            t.setStyle(style);
        }
        if (css != null) {
            t.getStyleClass().addAll(css);
        }
        flow().getChildren().add(t);
    }
    
    protected TextFlow flow() {
        if(content instanceof TextFlow f) {
            return f;
        } else {
            throw new IllegalArgumentException("Not a TextFlow: " + content.getClass());
        }
    }

    public void setPreferredHeight(double height) {
        this.height = height;
    }

    public double getPreferredHeight() {
        return height;
    }

    public void setPreferredWidth(double width) {
        this.width = width;
    }

    public double getPreferredWidth() {
        return width;
    }

    public int getLineIndex() {
        return index;
    }

    public void addBoxOutline(FxPathBuilder b, double x, double w) {
        double y0 = content.getLayoutY();
        double y1 = y0 + content.getHeight();
        
        b.moveto(x, y0);
        b.lineto(w, y0);
        b.lineto(w, y1);
        b.lineto(x, y1);
        b.lineto(x, y0);
    }

    // TODO rename getCaretPath?
    public PathElement[] getCaretShape(int charIndex, boolean leading) {
        if (content instanceof TextFlow f) {
            PathElement[] p = f.caretShape(charIndex, leading);
            if (p.length == 2) {
                PathElement p0 = p[0];
                PathElement p1 = p[1];
                if ((p0 instanceof MoveTo m0) && (p1 instanceof LineTo m1)) {
                    if (Math.abs(m0.getY() - m1.getY()) < 0.01) {
                        double x = m0.getX();
                        double y = m0.getY();
                        // empty line generates a single dot shape, not what we need
                        // using text flow height to get us a line caret shape
                        p[1] = new LineTo(x, y + f.getHeight());
                    }
                }
            }
            return p;
        }
        return null;
    }
    
    // TODO rename getRangePath?
    public PathElement[] getRange(int start, int end) {
        if (content instanceof TextFlow f) {
            return NewAPI.getRange(f, start, end);
        } else {
            double w = content.getWidth();
            double h = content.getHeight();
            
            return new PathElement[] {
                new MoveTo(0.0, 0.0),
                new LineTo(w, 0.0),
                new LineTo(w, h),
                new LineTo(0.0, h),
                new LineTo(0.0, 0.0)
            };
        }
    }

    public int getTextLength() {
        if (content instanceof TextFlow f) {
            
        }
        return 0;
    }
}
