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
// this code borrows heavily from the following project, with permission from the author:
// https://github.com/andy-goryachev/FxEditor
package goryachev.rich;

import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

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
}
