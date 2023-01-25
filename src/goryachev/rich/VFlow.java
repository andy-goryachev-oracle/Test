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

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import goryachev.rich.impl.CellCache;
import goryachev.rich.impl.Markers;
import goryachev.rich.impl.SelectionHelper;
import goryachev.rich.util.FxPathBuilder;
import goryachev.rich.util.NewAPI;
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
    protected final SimpleObjectProperty<Origin> origin = new SimpleObjectProperty(new Origin(0, 0));
    protected final SimpleBooleanProperty caretVisible = new SimpleBooleanProperty(true);
    protected final SimpleBooleanProperty suppressBlink = new SimpleBooleanProperty(false);
    protected final SimpleDoubleProperty offsetX = new SimpleDoubleProperty(0.0);
    protected final SimpleDoubleProperty rightEdge = new SimpleDoubleProperty(0.0);
    protected final Timeline caretAnimation;
    protected final CellCache cache;
    private int topCellIndex;
    private boolean handleScrollEvents = true;
    // TODO replace with ListenerHelper
    InvalidationListener modelIL;
    InvalidationListener wrapIL;

    public VFlow(RichTextArea control, ScrollBar vscroll, ScrollBar hscroll) {
        this.control = control;
        this.vscroll = vscroll;
        this.hscroll = hscroll;

        getStyleClass().add("content"); // maybe
        
        cache = new CellCache(Config.cellCacheSize);
        // TODO invalidate cache when cell indexes change

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
        
        control.modelProperty().addListener(modelIL = (p) -> updateModel());
        control.wrapTextProperty().addListener(wrapIL = (p) -> updateWrap());
        widthProperty().addListener((p) -> updateWidth());
        heightProperty().addListener((p) -> updateHeight());
        
        NewAPI.addChangeListener(
            this::updateHorizontalScrollBar,
            true,
            rightEdge,
            offsetX
        );
        
        NewAPI.addChangeListener(
            this::updateLayout,
            true,
            origin
            // TODO lineCount?
        );
    }
    
    public void dispose() {
        control.wrapTextProperty().removeListener(wrapIL);
        control.modelProperty().removeListener(modelIL);
    }
    
    public void updateModel() {
        setOrigin(0, 0);
        setOffsetX(0.0);

        // requestLayout() does not call layoutChildren() after changing the model - why?
        cache.clear();
        invalidateLayout();
        layoutChildren();
    }
    
    protected void updateLayout() {
        invalidateLayout();
        layoutChildren();
        updateVerticalScrollBar();
    }
    
    public void updateWrap() {
        if(control.isWrapText()) {
            setOffsetX(0.0);
            double w = getWidth(); // TODO padding
            setRightEdge(w);
        }
        cache.clear();
        invalidateLayout();
        layoutChildren();
    }

    public Origin getOrigin() {
        return origin.get();
    }

    public void setOrigin(Origin p) {
        if (p == null) {
            throw new NullPointerException();
        }
        System.err.println(p); // TODO
        origin.set(p);
    }
    
    public void setOrigin(int index, double offset) {
        setOrigin(new Origin(index, offset));
    }

    public int getTopLineIndex() {
        return getOrigin().index();
    }
    
    public double getOffsetX() {
        return offsetX.get();
    }
    
    public void setOffsetX(double x) {
        offsetX.set(x);
    }
    
    public DoubleProperty offsetXProperty() {
        return offsetX;
    }
    
    public double getOffsetY() {
        return getOrigin().offset();
    }
    
    public double rightEdge() {
        return rightEdge.get();
    }
    
    public void setRightEdge(double w) {
        //System.err.println("setRightEdge " + w);
        rightEdge.set(w);
    }

    public void setCaretVisible(boolean on) {
        caretVisible.set(on);
    }

    public boolean isCaretVisible() {
        return caretVisible.get();
    }

    /** reacts to width changes */
    protected void updateWidth() {
        if (control.isWrapText()) {
            setRightEdge(0.0);
        } else {
            double w = getOffsetX() + getWidth();
            if (layout != null) {
                if (layout.getUnwrappedWidth() > w) {
                    w = layout.getUnwrappedWidth();
                }
            }

            if (w > rightEdge()) {
                setRightEdge(w);
            }
            updateHorizontalScrollBar();
        }
    }
    
    /** reacts to height changes */
    protected void updateHeight() {
        // TODO what to do? do we still have enough nodes in the cell layout?
    }

    public void updateCaretAndSelection() {
        if(layout == null) {
            return;
        }

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
        double right = rightEdge();

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
                cell.addBoxOutline(b, snappedLeftInset(), snapPositionX(rightEdge()));
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
    
    public int lineCount() {
        // TODO use control.lineCount property?
        StyledTextModel m = control.getModel();
        return (m == null) ? 0 : m.getParagraphCount();
    }

    @Override
    protected void layoutChildren() {
        if ((layout == null) || !layout.isValid(this)) {
            layout = layoutCells(layout);
            updateCaretAndSelection();
            updateVerticalScrollBar();
        }
    }

    public void invalidateLayout() {
        if (layout != null) {
            layout.removeNodesFrom(this);
            layout = null;
            
            // FIX check
            if(getChildren().size() != 3) {
                System.err.println("ERROR: children left from previous layout");
            }
        }
    }

    /** updates VSB in response to change in height, layout, or offsetY */ 
    protected void updateVerticalScrollBar() {
        double visible;
        double val;

        if (layout == null || (lineCount() == 0)) {
            visible = 1.0;
            val = 0.0;
        } else {
            // TODO normalize
            double av = layout.averageHeight();
            double max = layout.estimatedMax();
            double h = getHeight();
            visible = h / max;
            val = normalizeScrollBarValue((getTopLineIndex() - layout.topCount()) * av + layout.topHeight(), h, max);
        }

        handleScrollEvents = false;

        vscroll.setMin(0.0);
        vscroll.setMax(1.0);
        vscroll.setVisibleAmount(visible);
        vscroll.setValue(val);

        handleScrollEvents = true;
    }

    /** handles user moving the vertical scroll bar */
    public void handleVerticalScroll() {
        if (handleScrollEvents) {
            if (lineCount() == 0) {
                return;
            }

            double max = layout.estimatedMax();
            double h = getHeight();
            double pos = vscroll.getValue() * (max - h);
            double av = layout.averageHeight();
            
            Origin p = layout.fromAbsolutePixels(pos);
            setOrigin(p);
        }
    }

    /** updates HSB in response to change in width, layout, or offsetX */ 
    protected void updateHorizontalScrollBar() {
        boolean wrap = control.isWrapText();
        if (wrap) {
            return;
        }

        double max = rightEdge();
        double w = getWidth();
        double off = getOffsetX();
        double vis = w / max;
        double val = normalizeScrollBarValue(off, w, max);

        handleScrollEvents = false;

        hscroll.setMin(0.0);
        hscroll.setMax(1.0);
        hscroll.setVisibleAmount(vis);
        hscroll.setValue(val);

        handleScrollEvents = true;
    }

    /** handles user moving the scroll bar */
    public void handleHorizontalScroll() {
//        System.err.println(
//            "handleHorizontalScroll" + 
//            " val=" + hscroll.getValue() + 
//            " vis=" + hscroll.getVisibleAmount() + 
//            " max=" + hscroll.getMax() + 
//            " offx=" + getOffsetX() +
//            " right=" + rightEdge()
//        );

        if (handleScrollEvents) {
            if (layout == null) {
                return;
            } else if (control.isWrapText()) {
                return; // is this needed?
            }
            
            double max = rightEdge();
            double w = getWidth();
            double off = hscroll.getValue() * (max - w);

            setOffsetX(off);

            // no need to recompute the flow
            layoutNodes(layout);

            updateCaretAndSelection();
        }
    }

    /**
     * javafx ScrollBar is weird in that the value has a range between [min,max] regardless of visible amount.
     * this method generates the value ScrollBar expects by renormalizing it to a [min,max-visible] range,
     * assuming min == 0.
     */
    private static double normalizeScrollBarValue(double val, double visible, double max) {
        if (Math.abs(max - visible) < 1e-10) {
            return 0.0;
        } else {
            return val / (max - visible);
        }
    }

    // TODO resizing should try keep the current line at the same level
    // TODO update topBoxOffset
    protected TextCellLayout layoutCells(TextCellLayout previous) {
        if (previous != null) {
            previous.removeNodesFrom(this);
        }
        
        // FIX check
        if(getChildren().size() != 3) {
            System.err.println("ERROR: children left from previous layout");
            System.err.println(getChildren());
        }

        double width = getWidth();
        double height = getHeight();
        clip.setWidth(width);
        clip.setHeight(height);

        StyledTextModel model = control.getModel();
        if(model == null) {
            // TODO suppress scroll bars?
            return null;
        }

        boolean wrap = control.isWrapText();
        int paragraphCount = lineCount();
        
        double y = -getOffsetY(); // TODO content padding
        double unwrappedWidth = 0;
        double margin = Config.slidingWindowMargin * height;
        int topMarginCount = 0;
        int bottomMarginCount = 0;
        int count = 0;
        boolean visible = true;

        TextCellLayout la = new TextCellLayout(this);
        
        // populating visible part of the sliding window + bottom margin
        for (int i = topCellIndex; i < paragraphCount; i++) {
            TextCell cell = cache.get(i);
            if (cell == null) {
                StyledParagraph p = model.getParagraph(i);
                cell = p.createTextCell();
                cache.add(cell);
            }
            Region r = cell.getContent();
            getChildren().add(r);
            r.applyCss();

            la.addCell(cell);

            r.setMaxWidth(wrap ? width : Double.MAX_VALUE); // TODO needed?
            
            // TODO actual box height might be different from h due to snapping?
            // TODO account for side components
            double h = r.prefHeight(wrap ? width : -1);
            cell.setPreferredHeight(h);
            cell.setOffset(y);

            if (!wrap) {
                if (visible) {
                    double w = r.prefWidth(-1);
                    if (unwrappedWidth < w) {
                        unwrappedWidth = w;
                    }
                }
            }

            y += h;
            count++;

            // stop populating the bottom part of the sliding window
            // when exceeded both pixel and line count margins
            if (visible) {
                if (y > height) {
                    topMarginCount = (int)Math.ceil(count * Config.slidingWindowMargin);
                    bottomMarginCount = count + topMarginCount;
                    la.setVisibleCount(count);
                    visible = false;
                }
            } else {
                getChildren().remove(r);

                if ((y > (height + margin)) && (count > bottomMarginCount)) {
                    break;
                }
            }
        }

        // less paragraphs than can fit in the view
        if (visible) {
            la.setVisibleCount(count);
        }

        la.setBottomCount(count);
        la.setBottomHeight(y);
        la.setUnwrappedWidth(unwrappedWidth);
        count = 0;
        y = 0.0;
        
        // populate top margin, going backwards from topCellIndex
        // TODO populate more, if bottom ended prematurely
        for (int i = topCellIndex - 1; i >= 0; i--) {
            TextCell cell = cache.get(i);
            if (cell == null) {
                StyledParagraph p = model.getParagraph(i);
                cell = p.createTextCell();
                cache.add(cell);
            }
            Region r = cell.getContent();
            getChildren().add(r);
            r.applyCss();

            la.addCell(cell);

            r.setMaxWidth(wrap ? width : Double.MAX_VALUE); // TODO needed?
            
            // TODO actual box height might be different from h due to snapping?
            // TODO account for side components
            double h = r.prefHeight(wrap ? width : -1);
            cell.setPreferredHeight(h);
            
            getChildren().remove(r);

            y += h;
            count++;

            // stop populating the top part of the sliding window
            // when exceeded both pixel and line count margins
            if ((y > margin) && (count > topMarginCount)) {
                break;
            }
        }
        
        la.setTopHeight(y);
        //System.err.println(la);
        
        // lay out content nodes
        layoutNodes(la);

        if (!wrap) {
            if(rightEdge() < unwrappedWidth) {
                setRightEdge(unwrappedWidth);
            }
        }
        
        return la;
    }
    
    private void layoutNodes(TextCellLayout la) {
        double x = 0.0 - getOffsetX(); // TODO content padding
        double y = -getOffsetY();
        boolean wrap = control.isWrapText();
        double w = wrap ? getWidth() : rightEdge(); // TODO padding
        
        //System.err.println("offsetX=" + getOffsetX());

        int sz = la.getVisibleCellCount();
        for (int i=0; i < sz; i++) {
            TextCell cell = la.getCellAt(i);
            Region r = cell.getContent();
            double h = cell.getPreferredHeight();
            // TODO clip cell?
            layoutInArea(r, x, y, w, h, 0, HPos.CENTER, VPos.CENTER);

            // TODO actual box height might be different from h due to snapping?
            // TODO also consider using maxx, maxy from boundsInLocal instead?
            y += cell.getPreferredHeight();
        }
    }
}
