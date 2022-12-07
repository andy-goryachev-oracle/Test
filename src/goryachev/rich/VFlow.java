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
package goryachev.rich;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

/**
 * Virtual text flow, manages text LineBoxes, scroll bars, and conversion
 * between the model and the screen coordinates.
 * 
 * TODO specific for the rich text control, or generic for any kind of virtual
 * flow?
 * 
 * TODO an interface, to allow for different (optimized) implementations?
 * TODO or a VFlowPolicy ?
 */
public class VFlow extends Region {
    private final RichTextArea control;
    private final Rectangle clip;

    public VFlow(RichTextArea control) {
        this.control = control;

        clip = new Rectangle();
        setClip(clip);
    }

    @Override
    protected void layoutChildren() {
        populate();
        updateCaretAndSelection();
    }

    public void populate() {
        double height = getHeight();
        clip.setWidth(getWidth());
        clip.setHeight(height);
        
        getChildren().clear();

        StyledTextModel model = control.getModel();
        List<? extends StyledTextLine> lines = model.getTextLines();
        
        // TODO properties
        double boxOffsetY = 0;
        double boxOffsetX = 0;
        int topBoxIndex = 0;
        double x = boxOffsetX;
        double y = boxOffsetY;
        double width = getWidth();
        
        boolean wrap = control.isWrapText();
        double maxWidth = wrap ? width : -1;
        double unwrappedWidth = -1;
        
        // TODO size from previous layout
        ArrayList<LineBox> boxes = new ArrayList<>(32);
        for(int i=topBoxIndex; i<lines.size(); i++)
        {
            // TODO can use cache
            StyledTextLine tline = lines.get(i);
            LineBox box = tline.createBox();
            boxes.add(box);
            Region r = box.getContent();
                        
            getChildren().add(r);
            r.applyCss();
            
            r.setMaxWidth(maxWidth);
            double h = r.prefHeight(maxWidth);
            box.setPreferredHeight(h);
            
            if(wrap) {
                box.setPreferredWidth(-1.0);
            } else {
                double w = r.prefWidth(-1);
                box.setPreferredWidth(w);
                if(unwrappedWidth < w) {
                    unwrappedWidth = w;
                }
            }
            
            // TODO actual box height might be different from h due to snapping?
            h = r.getHeight();
            
            y += h;
            if(y > getHeight()) {
                break;
            }
        }
        
        for (LineBox box : boxes) {
            Region r = box.getContent();
            double w = wrap ? maxWidth : unwrappedWidth;
            double h = box.getPreferredHeight();
            layoutInArea(r, x, y, w, h, 0, HPos.CENTER, VPos.CENTER);

            // TODO actual box height might be different from h due to snapping?
            y += box.getPreferredHeight();
        }
    }

    public void updateCaretAndSelection() {
        // TODO
    }
}
