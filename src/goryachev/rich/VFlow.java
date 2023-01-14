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
// This code borrows heavily from the following project, with permission from the author:
// https://github.com/andy-goryachev/FxEditor
package goryachev.rich;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import goryachev.rich.impl.Markers;
import goryachev.rich.impl.SelectionHelper;
import goryachev.rich.util.FxPathBuilder;
import goryachev.rich.util.Util;

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
    protected final SimpleIntegerProperty topLineIndex = new SimpleIntegerProperty(0);
    protected final SimpleBooleanProperty caretVisible = new SimpleBooleanProperty(true);
    protected final SimpleBooleanProperty suppressBlink = new SimpleBooleanProperty(false);
    protected final Timeline caretAnimation;

    public VFlow(RichTextArea control, ScrollBar vscroll, ScrollBar hscroll) {
        this.control = control;
        this.vscroll = vscroll;
        this.hscroll = hscroll;

        getStyleClass().add("content"); // maybe

        clip = new Rectangle();
        
        caretPath = new Path();
        caretPath.getStyleClass().add("caret");
        caretPath.setManaged(false);
        caretPath.setStroke(Color.BLACK); // FIX
        caretPath.setStrokeWidth(1.0); // TODO ?
        
        caretLineHighlight = new Path();
        caretLineHighlight.getStyleClass().add("caret-line");
        caretLineHighlight.setFill(Color.rgb(255, 0, 255, 0.02)); // FIX
        caretLineHighlight.setManaged(false);

        selectionHighlight = new Path();
        selectionHighlight.getStyleClass().add("selection-highlight");
        selectionHighlight.setManaged(false);
        
        getChildren().addAll(caretLineHighlight, selectionHighlight, caretPath);
        setClip(clip);
        
        caretAnimation = new Timeline();
        caretAnimation.setCycleCount(Animation.INDEFINITE);
        
        caretPath.visibleProperty().bind(new BooleanBinding() {
            {
                bind(
                    caretVisible,
                    control.displayCaretProperty(),
                    control.focusedProperty(),
                    control.disabledProperty(),
                    suppressBlink
                );
            }

            @Override
            protected boolean computeValue() {
                return
                    (isCaretVisible() || suppressBlink.get()) &&
                    control.isDisplayCaret() &&
                    control.isFocused() &&
                    (!control.isDisabled());
            }
        });
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

    public void setCaretVisible(boolean on) {
        caretVisible.set(on);
    }

    public boolean isCaretVisible() {
        return caretVisible.get();
    }

    @Override
    protected void layoutChildren() {
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
            previous.removeFromLayout(this);
        }
        
        double height = getHeight();
        clip.setWidth(getWidth());
        clip.setHeight(height);

        StyledTextModel model = control.getModel();
        List<? extends StyledParagraph> paragraphs = model.getParagraphs();
        TextCellLayout la = new TextCellLayout(this);
        
        // TODO properties
        double boxOffsetY = 0;
        double boxOffsetX = 0;
        int topBoxIndex = 0;
        double x = boxOffsetX;
        double y = boxOffsetY;
        double width = getWidth(); // TODO padding
        
        boolean wrap = control.isWrapText();
        double maxWidth = wrap ? width : -1;
        double unwrappedWidth = -1;
        
        // TODO size from previous layout
        ArrayList<TextCell> cells = new ArrayList<>(32);
        for(int i=topBoxIndex; i<paragraphs.size(); i++)
        {
            // TODO use cache
            StyledParagraph p = paragraphs.get(i);
            TextCell cell = p.createTextCell();
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
        
        if(unwrappedWidth < width) {
            unwrappedWidth = width;
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

    protected void createCaretPath(FxPathBuilder b, Marker m) {
        CaretSize c = getCaretSize(m);
        if(c != null) {
            b.moveto(c.x(), c.y0());
            b.lineto(c.x(), c.y1());
        }
    }

    protected void createSelectionHighlight(FxPathBuilder b, Marker startMarker, Marker endMarker) {
        if ((startMarker == null) || (endMarker == null)) {
            return;
        }

        // enforce startMarker < endMarker
        if (startMarker.compareTo(endMarker) > 0) {
            throw new Error(startMarker + "<" + endMarker);
        }

        int topLine = getTopLineIndex();
        if (endMarker.getLineIndex() < topLine) {
            // selection is above visible area
            return;
        } else if (startMarker.getLineIndex() >= (topLine + layout.getVisibleCellCount())) {
            // selection is below visible area
            return;
        }

        // get selection shapes for top and bottom segments,
        // translated to this VFlow coordinates.
        PathElement[] top;
        PathElement[] bottom;
        if (startMarker.getLineIndex() == endMarker.getLineIndex()) {
            top = getRangeShape(startMarker.getLineIndex(), startMarker.getLineOffset(), endMarker.getLineOffset());
            bottom = null;
        } else {
            top = getRangeShape(startMarker.getLineIndex(), startMarker.getLineOffset(), -1);
            if (top == null) {
                top = getRangeTop();
            }

            bottom = getRangeShape(endMarker.getLineIndex(), 0, endMarker.getLineOffset());
            if (bottom == null) {
                bottom = getRangeBottom();
            }
        }

        // generate shapes
        Insets m = getPadding();
        double left = m.getLeft(); // + layout.getLineNumbersColumnWidth(); // FIX padding? border?
        double right = control.isWrapText() ? (getWidth() - m.getLeft() - m.getRight()) : layout.getTotalWidth();

        // TODO
        boolean topLTR = true;
        boolean bottomLTR = true;

        new SelectionHelper(b, left, right).generate(top, bottom, topLTR, bottomLTR);
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

    protected CaretSize getCaretSize(Marker m) {
        return layout.getCaretSize(this, m);
    }
    
    protected PathElement[] getRangeTop() {
        double w = getWidth();

        return new PathElement[] {
            new MoveTo(0, -1),
            new LineTo(w, -1),
            new LineTo(w, 0),
            new LineTo(0, 0),
            new LineTo(0, -1)
        };
    }

    protected PathElement[] getRangeBottom() {
        double w = getWidth();
        double h = getHeight();
        double h1 = h + 1.0;

        return new PathElement[] {
            new MoveTo(0, h),
            new LineTo(w, h),
            new LineTo(w, h1),
            new LineTo(0, h1),
            new LineTo(0, h)
        };
    }

    protected PathElement[] getRangeShape(int line, int startOffset, int endOffset) {
        TextCell cell = layout.getCell(line);
        if (cell == null) {
            return null;
        }

        if (endOffset < 0) {
            endOffset = cell.getTextLength();
        }

        PathElement[] pe;
        if (startOffset == endOffset) {
            pe = cell.getCaretShape(startOffset, true);
        } else {
            pe = cell.getRangeShape(startOffset, endOffset);
        }

        if (pe == null) {
            return null;
        } else {
            return Util.translatePath(this, cell.getContent(), pe);
        }
    }

    public void setSuppressBlink(boolean on) {
        suppressBlink.set(on);
        
        if(!on) {
            updateRateRestartBlink();
        }
    }

    public void updateRateRestartBlink() {
        Duration t1 = control.getCaretBlinkPeriod();
        Duration t2 = t1.multiply(2.0);

        caretAnimation.stop();
        caretAnimation.getKeyFrames().setAll(
            new KeyFrame(Duration.ZERO, (ev) -> setCaretVisible(true)),
            new KeyFrame(t1, (ev) -> setCaretVisible(false)),
            new KeyFrame(t2)
        );
        caretAnimation.play();
    }
}
