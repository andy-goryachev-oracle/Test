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

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;

import goryachev.rich.impl.Markers;
import goryachev.rich.util.FxPathBuilder;

/**
 * Virtual text flow deals with TextCells, scroll bars, and conversion
 * between the model and the screen coordinates.
 * 
 * TODO or a VFlowPolicy ?
 * TODO left paragraph component (line numbers)
 * TODO right paragraph component (annotation?)
 */
public class VFlow extends Pane {
    private final RichTextArea control;
    private final ScrollBar vscroll;
    private final ScrollBar hscroll;
    private final Rectangle clip;
    private TextCellLayout layout;
    private final Path caretPath;
    private final Path caretLineHighlight;
    private final Path selectionHighlight;
    private final SimpleIntegerProperty topLineIndex = new SimpleIntegerProperty(0);

    public VFlow(RichTextArea control, ScrollBar vscroll, ScrollBar hscroll) {
        this.control = control;
        this.vscroll = vscroll;
        this.hscroll = hscroll;

        clip = new Rectangle();
        setClip(clip);
        
        caretPath = new Path();
        caretPath.getStyleClass().add("caret");
        caretPath.setManaged(false);
        
        caretLineHighlight = new Path();
        caretLineHighlight.getStyleClass().add("caret-line");
        caretLineHighlight.setManaged(false);
        caretLineHighlight.setFill(Color.rgb(255, 0, 255, 0.02)); // FIX

        selectionHighlight = new Path();
        selectionHighlight.getStyleClass().add("selection-highlight");
        selectionHighlight.setManaged(false);
        
        getChildren().addAll(caretLineHighlight, selectionHighlight, caretPath);
    }
    
    public int getTopLineIndex() {
        return topLineIndex.get();
    }
    
    public void setTopLineIndex(int ix) {
        topLineIndex.set(ix);
    }
    
    public IntegerProperty topLineIndexProperty() {
        return topLineIndex;
    }
    
    @Override
    protected void layoutChildren() {
        // do we need to rebuild layout?
        if ((layout == null) || !layout.isValid(this)) {
            layout = layoutCells(layout);
            updateCaretAndSelection();
        }
    }
    
    public void invalidateLayout() {
        if(layout != null) {
            clear();
            layout = null;
        }
    }
    
    protected void clear() {
        getChildren().clear();
    }
    
    // TODO resizing should try keep the current line at the same level
    // TODO update topBoxOffset
    // TODO update scrollbars
    protected TextCellLayout layoutCells(TextCellLayout previous) {
        if(previous != null) {
            clear();
        }
        
        double height = getHeight();
        clip.setWidth(getWidth());
        clip.setHeight(height);

        StyledTextModel model = control.getModel();
        List<? extends StyledParagraph> lines = model.getParagraphs();
        TextCellLayout la = new TextCellLayout(this);
        
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
        ArrayList<TextCell> cells = new ArrayList<>(32);
        for(int i=topBoxIndex; i<lines.size(); i++)
        {
            // TODO can use cache
            StyledParagraph tline = lines.get(i);
            TextCell cell = tline.createTextCell();
            cells.add(cell);
            Region r = cell.getContent();
                        
            getChildren().add(r);
            r.applyCss();
            la.addBox(cell);
            
            r.setMaxWidth(maxWidth);
            double h = r.prefHeight(maxWidth);
            cell.setPreferredHeight(h);
            
            if(wrap) {
                cell.setPreferredWidth(-1.0);
            } else {
                double w = r.prefWidth(-1);
                cell.setPreferredWidth(w);
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
        
        la.setUnwrappedWidth(unwrappedWidth);
        
        for (TextCell box : cells) {
            Region r = box.getContent();
            double w = wrap ? maxWidth : unwrappedWidth;
            double h = box.getPreferredHeight();
            layoutInArea(r, x, y, w, h, 0, HPos.CENTER, VPos.CENTER);

            // TODO actual box height might be different from h due to snapping?
            y += box.getPreferredHeight();
        }
        
        return la;
    }

    public void updateCaretAndSelection() {
        SelectionSegment sel = control.getSelectionModel().getSelectionSegment();
        if(sel == null) {
            return;
        }
        
        Marker caret = sel.getCaret();

        // current line highlight
        if (control.isHighlightCurrentLine()) {
            FxPathBuilder caretLineBuilder = new FxPathBuilder();
            createCurrentLineHighlight(caretLineBuilder, caret);
            caretLineHighlight.getElements().setAll(caretLineBuilder.getPathElements());
        }

        // selection and caret
        FxPathBuilder selectionBuilder = new FxPathBuilder();
        FxPathBuilder caretBuilder = new FxPathBuilder();
        Marker start = sel.getMin();
        Marker end = sel.getMax();
        createSelectionHighlight(selectionBuilder, start, end);
        createCaretPath(caretBuilder, caret);
        selectionHighlight.getElements().setAll(selectionBuilder.getPathElements());
        caretPath.getElements().setAll(caretBuilder.getPathElements());
    }

    protected void createCaretPath(FxPathBuilder caretBuilder, Marker caret) {
        // TODO
    }

    protected void createSelectionHighlight(FxPathBuilder b, Marker start, Marker end) {
        // TODO
    }

    protected void createCurrentLineHighlight(FxPathBuilder b, Marker caret) {
        int ix = caret.getLineIndex();
        TextCell cell = layout.getCell(ix);
        if(cell != null) {
            if(control.isWrapText()) {
                cell.addBoxOutline(b, snappedLeftInset(), snapPositionX(getWidth() - snappedLeftInset() - snappedRightInset()));
            } else {
                cell.addBoxOutline(b, snappedLeftInset(), snapPositionX(layout.getTotalWidth()));
            }
        }
    }

    public Marker getTextPosition(double screenX, double screenY, Markers markers) {
        if (layout == null) {
            return null;
        }

        return layout.getTextPosition(screenX, screenY, markers);
    }
}
