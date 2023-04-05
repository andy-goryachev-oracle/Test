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
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventType;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ScrollBar;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import goryachev.rich.impl.CellCache;
import goryachev.rich.impl.SelectionHelper;
import goryachev.rich.model.StyledParagraph;
import goryachev.rich.model.StyledTextModel;
import goryachev.rich.util.FxPathBuilder;
import goryachev.rich.util.NewAPI;
import goryachev.rich.util.Util;

/**
 * Virtual text flow deals with TextCells, scroll bars, and conversion
 * between the model and the screen coordinates.
 * 
 * TODO or a VFlowPolicy ?
 * TODO left paragraph component (example: line numbers), needs a method to get the max size
 * TODO right paragraph component (example: annotation?)
 * TODO threshold for a full-model rendering
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
    protected final ReadOnlyObjectWrapper<Origin> origin = new ReadOnlyObjectWrapper(Origin.ZERO);
    protected final SimpleBooleanProperty caretVisible = new SimpleBooleanProperty(true);
    protected final SimpleBooleanProperty suppressBlink = new SimpleBooleanProperty(false);
    protected final SimpleDoubleProperty offsetX = new SimpleDoubleProperty(0.0);
    protected final SimpleDoubleProperty rightEdge = new SimpleDoubleProperty(0.0);
    protected final Timeline caretAnimation;
    protected final CellCache cache;
    private boolean handleScrollEvents = true;
    private boolean vsbPressed;
    // TODO replace with ListenerHelper
    InvalidationListener modelLi;
    InvalidationListener wrapLi;

    public VFlow(RichTextAreaSkin skin, ScrollBar vscroll, ScrollBar hscroll) {
        this.control = skin.getSkinnable();
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

        // TODO should this be explicitly uninstalled?
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

        control.modelProperty().addListener(modelLi = (p) -> updateModel());
        control.wrapTextProperty().addListener(wrapLi = (p) -> updateWrap());
        widthProperty().addListener((p) -> updateWidth());
        heightProperty().addListener((p) -> updateHeight());
        
        NewAPI.addChangeListener(
            this::updateHorizontalScrollBar,
            true,
            rightEdge,
            offsetX
        );
        
        NewAPI.addChangeListener(
            this::recomputeLayout,
            true,
            origin
        );
        
        vscroll.addEventFilter(MouseEvent.ANY, this::handleVScrollMouseEvent);
    }
    
    public void dispose() {
        control.wrapTextProperty().removeListener(wrapLi);
        control.modelProperty().removeListener(modelLi);
    }
    
    public void updateModel() {
        control.clearSelection();
        setRightEdge(0.0);
        setOrigin(Origin.ZERO);
        setOffsetX(0.0);
        cache.clear();
        recomputeLayout();
    }
    
    protected void recomputeLayout() {
        invalidateLayout();
        layoutChildren();
    }
    
    public void updateWrap() {
        if(control.isWrapText()) {
            double w = getWidth(); // TODO padding
            setRightEdge(w);
        } else {
            setRightEdge(0.0);
        }
        setOffsetX(0.0);
        cache.clear();
        recomputeLayout();
    }

    public Origin getOrigin() {
        return origin.get();
    }

    // TODO rename moveOrigin
    // TODO add logic not to go outside of the document
    public void setOrigin(Origin p) {
        if (p == null) {
            throw new NullPointerException();
        }
        //System.err.println("setOrigin " + p); // FIX
        origin.set(p);
    }
    
    public ReadOnlyProperty<Origin> originProperty() {
        return origin.getReadOnlyProperty();
    }

    public int topCellIndex() {
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
        //System.err.println("setRightEdge " + w); // FIX
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
            setRightEdge(getWidth());
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
//        checkForExcessiveWhitespaceAtTheEnd();
    }
    
    public void handleSelectionChange() {
        updateCaretAndSelection();
        scrollCaretToVisible();
    }

    public void updateCaretAndSelection() {
        if (layout == null) {
            removeCaretAndSelection();
            return;
        }

        TextPos caret = control.getCaretPosition();
        if (caret == null) {
            removeCaretAndSelection();
            return;
        }
        
        TextPos anchor = control.getAnchorPosition();
        if(anchor == null) {
            anchor = caret;
        }
        
        //System.out.println("updateCaretAndSelection ca=" + caret + " an=" + anchor); // FIX
        
        // current line highlight
        if (control.isHighlightCurrentLine()) {
            FxPathBuilder caretLineBuilder = new FxPathBuilder();
            createCurrentLineHighlight(caretLineBuilder, caret);
            caretLineHighlight.getElements().setAll(caretLineBuilder.getPathElements());
        }

        // selection and caret
        FxPathBuilder selectionBuilder = new FxPathBuilder();
        FxPathBuilder caretBuilder = new FxPathBuilder();
        createSelectionHighlight(selectionBuilder, anchor, caret);
        createCaretPath(caretBuilder, caret);

        selectionHighlight.getElements().setAll(selectionBuilder.getPathElements());
        caretPath.getElements().setAll(caretBuilder.getPathElements());
    }

    protected void removeCaretAndSelection() {
        caretLineHighlight.getElements().clear();
        selectionHighlight.getElements().clear();
        caretPath.getElements().clear();
    }

    protected void createCaretPath(FxPathBuilder b, TextPos p) {
        CaretInfo c = getCaretInfo(p);
        if(c != null) {
            b.moveto(c.x(), c.y0());
            b.lineto(c.x(), c.y1());
        }
    }

    protected void createSelectionHighlight(FxPathBuilder b, TextPos start, TextPos end) {
        // probably unnecessary
        if ((start == null) || (end == null)) {
            return;
        }

        int eq = start.compareTo(end);
        if (eq == 0) {
            return;
        } else if (eq > 0) {
            TextPos p = start;
            start = end;
            end = p;
        }

        int topCellIndex = topCellIndex();
        if (end.index() < topCellIndex) {
            // selection is above visible area
            return;
        } else if (start.index() >= (topCellIndex + layout.getVisibleCellCount())) {
            // selection is below visible area
            return;
        }

        // get selection shapes for top and bottom segments,
        // translated to this VFlow coordinates.
        PathElement[] top;
        PathElement[] bottom;
        if (start.index() == end.index()) {
            top = getRangeShape(start.index(), start.offset(), end.offset());
            bottom = null;
        } else {
            top = getRangeShape(start.index(), start.offset(), -1);
            if (top == null) {
                top = getRangeTop();
            }

            bottom = getRangeShape(end.index(), 0, end.offset());
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

    protected void createCurrentLineHighlight(FxPathBuilder b, TextPos caret) {
        int ix = caret.index();
        TextCell cell = layout.getVisibleCell(ix);
        if(cell != null) {
            if(control.isWrapText()) {
                cell.addBoxOutline(b, snappedLeftInset(), snapPositionX(getWidth() - snappedLeftInset() - snappedRightInset()));
            } else {
                cell.addBoxOutline(b, snappedLeftInset(), snapPositionX(rightEdge()));
            }
        }
    }

    /** in vflow cooridinates */ 
    public TextPos getTextPos(double localX, double localY) {
        if (layout == null) {
            return null;
        }
        return layout.getTextPos(getOffsetX(), localX, localY);
    }

    protected CaretInfo getCaretInfo(TextPos p) {
        return layout.getCaretInfo(getOffsetX(), p);
    }

    /** returns caret sizing info, or null */
    public CaretInfo getCaretInfo() {
        TextPos p = control.getCaretPosition();
        if (p == null) {
            return null; // TODO check
        }

        return getCaretInfo(p);
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

    /** returns the shape if both ends are at the same line */
    protected PathElement[] getRangeShape(int line, int startOffset, int endOffset) {
        TextCell cell = layout.getVisibleCell(line);
        if (cell == null) {
            return null;
        }

        if (endOffset < 0) {
            // FIX to the edge?? but beware of RTL
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

    /** updates VSB in response to change in height, layout, or offsetY */ 
    protected void updateVerticalScrollBar() {
        double visible;
        double val;

        if (layout == null || (lineCount() == 0)) {
            visible = 1.0;
            val = 0.0;
        } else {
            double av = layout.averageHeight();
            double max = layout.estimatedMax();
            double h = getHeight();
            val = toScrollBarValue((topCellIndex() - layout.topCount()) * av + layout.topHeight(), h, max);
            visible = h / max;
        }

        handleScrollEvents = false;

        vscroll.setMin(0.0);
        vscroll.setMax(1.0);
        vscroll.setVisibleAmount(visible);
        vscroll.setValue(val);

        handleScrollEvents = true;
    }
    
    private void handleVScrollMouseEvent(MouseEvent ev) {
        EventType<? extends MouseEvent> t = ev.getEventType();
        if(t == MouseEvent.MOUSE_PRESSED) {
            vsbPressed = true;
        } else if(t == MouseEvent.MOUSE_RELEASED) {
            vsbPressed = false;
            updateVerticalScrollBar();
        }
    }

    /** handles user moving the vertical scroll bar */
    public void handleVerticalScroll() {
        if (handleScrollEvents) {
            if (lineCount() == 0) {
                return;
            }

            double val = vscroll.getValue();
            double visible = vscroll.getVisibleAmount();
            double max = vscroll.getMax();
            double pos = fromScrollBarValue(val, visible, max); // max is 1.0

            // FIX remove
            if(pos > 1.0) {
                System.err.println("* * ERR pos>1 " + pos);
                fromScrollBarValue(val, visible, max);
            }

            Origin p = layout.fromAbsolutePosition(pos);
            // FIX
//            System.err.println(
//                "handleVerticalScroll" +
//                " val=" + vscroll.getValue() +
//                " pos=" + pos +
//                " visible=" + visible +
//                " origin=" + p +
//                " lineCount=" + lineCount()
//            );

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
        double val = toScrollBarValue(off, w, max);

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
            
            double max = rightEdge() + Config.horizontalGuard;
            double visible = getWidth();
            double val = hscroll.getValue();
            double off = fromScrollBarValue(val, visible, max);

            setOffsetX(off);
            // no need to recompute the flow
            layoutNodes();
            updateCaretAndSelection();
        }
    }

    /**
     * javafx ScrollBar is weird in that the value has a range between [min,max] regardless of visible amount.
     * this method generates the value ScrollBar expects by renormalizing it to a [min,max-visible] range,
     * assuming min == 0.
     */
    private static double toScrollBarValue(double val, double visible, double max) {
        if (Math.abs(max - visible) < 1e-10) {
            return 0.0;
        } else {
            return val / (max - visible);
        }
    }

    /** inverse of {@link #toScrollBarValue}, returns the scroll bar value that takes into account visible amount */
    private static double fromScrollBarValue(double val, double visible, double max) {
        return val * (max - visible);
    }
    
    protected TextCell getCell(int modelIndex) {
        TextCell cell = cache.get(modelIndex);
        if (cell == null) {
            StyledParagraph p = control.getModel().getParagraph(modelIndex);
            cell = p.createTextCell();

            // a bit of a hack: avoid TextCells with an empty TextFlow,
            // as it makes the caret collapse to a single point
            {
                Region r = cell.getContent();
                if(r instanceof TextFlow f) {
                    if(f.getChildren().size() == 0) {
                        f.getChildren().add(new Text(""));
                    }
                }
            }

            cache.add(cell);
        }
        return cell;
    }

    public void invalidateLayout() {
        if (layout != null) {
            layout.removeNodesFrom(this);
            layout = null;
        }
    }

    @Override
    protected void layoutChildren() {
        if ((layout == null) || !layout.isValid(this)) {
            invalidateLayout();

            layout = new TextCellLayout(this);
            layoutCells();

            checkForExcessiveWhitespaceAtTheEnd();
            updateCaretAndSelection();

            // eliminate VSB during scrolling with a mouse
            // the VSB will finally get updated on mouse released event
            if (!vsbPressed) {
                updateVerticalScrollBar();
            }
        }
    }

    protected void layoutCells() {
        double width = getWidth();
        double height = getHeight();
        clip.setWidth(width);
        clip.setHeight(height);

        if(control.getModel() == null) {
            // TODO suppress scroll bars?
            return;
        }

        int tabSize = control.getTabSize();
        boolean wrap = control.isWrapText();
        double forWidth = (wrap ? width : -1);
        double maxWidth = (wrap ? width : 1_000_000_000); //Double.MAX_VALUE/2);
        int paragraphCount = lineCount();

        double y = -getOffsetY(); // TODO content padding
        double unwrappedWidth = 0;
        double margin = Config.slidingWindowMargin * height;
        int topMarginCount = 0;
        int bottomMarginCount = 0;
        int count = 0;
        boolean visible = true;
        // TODO if topCount < marginCount, increase bottomCount correspondingly
        // also, update Origin if layout hit the beginning/end of the document

        // populating visible part of the sliding window + bottom margin
        for (int i = topCellIndex(); i < paragraphCount; i++) {
            TextCell cell = getCell(i);
            // TODO skip computation if layout width is the same
            Region r = cell.getContent();
            getChildren().add(r);
            r.setMaxWidth(maxWidth);
            r.setMaxHeight(USE_COMPUTED_SIZE);
            if(r instanceof TextFlow f) {
                f.setTabSize(tabSize);
            }

            r.applyCss();

            layout.addCell(cell);

            // TODO actual box height might be different from h due to snapping?
            // TODO account for side components
            double h;
            try {
                h = r.prefHeight(forWidth);
            } catch(Exception e) {
                e.printStackTrace(); // FIX
                System.err.println("i=" + i + " cell=" + control.getPlainText(cell.getLineIndex())); // FIX
                h = 0;
            }
            cell.setComputedHeight(h, forWidth);
            cell.setLocationY(y);

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
                    layout.setVisibleCount(count);
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
            layout.setVisibleCount(count);
        }

        layout.setBottomCount(count);
        layout.setBottomHeight(y);
        layout.setUnwrappedWidth(unwrappedWidth);
        count = 0;
        y = -getOffsetY(); // TODO content padding
        
        // populate top margin, going backwards from topCellIndex
        // TODO populate more, if bottom ended prematurely
        for (int i = topCellIndex() - 1; i >= 0; i--) {
            TextCell cell = getCell(i);
            // TODO skip computation if layout width is the same
            Region r = cell.getContent();
            getChildren().add(r);
            r.setMaxWidth(maxWidth);
            r.setMaxHeight(USE_COMPUTED_SIZE);
            if(r instanceof TextFlow f) {
                f.setTabSize(tabSize);
            }

            r.applyCss();
            
            layout.addCell(cell);
            
            // TODO actual box height might be different from h due to snapping?
            // TODO account for side components
            double h = r.prefHeight(forWidth);
            y -= h;
            count++;

            cell.setComputedHeight(h, forWidth);
            cell.setLocationY(y);
            
            getChildren().remove(r);

            // stop populating the top part of the sliding window
            // when exceeded both pixel and line count margins
            if ((-y > margin) && (count > topMarginCount)) {
                break;
            }
        }
        
        layout.setTopHeight(-y);
        
        // lay out content nodes
        layoutNodes();

        if (wrap) {
            setRightEdge(width);
        } else {
            if(rightEdge() < unwrappedWidth) {
                setRightEdge(Math.max(unwrappedWidth, width));
            }
        }
    }
    
    protected void layoutNodes() {
        //System.err.println("layoutNodes"); // FIX
        boolean wrap = control.isWrapText();
        // TODO not sure why, but neither Double.MAX_VALUE nor 1e20 work
        double w = wrap ? getWidth() : 1_000_000_000.0;
        double x = -getOffsetX();

        int sz = layout.getVisibleCellCount();
        for (int i=0; i < sz; i++) {
            TextCell cell = layout.getCellAt(i);
            Region r = cell.getContent();
            
            double h = cell.getComputedHeight();
            double y = cell.getY();
            // FIX bug
            try {
                layoutInArea(r, x, y, w, h, 0, HPos.CENTER, VPos.CENTER);
            } catch(Exception e) {
                e.printStackTrace(); // FIX
                System.err.println("i=" + i + " cell=" + control.getPlainText(cell.getLineIndex())); // FIX
            }
        }
    }
    
    public double getViewHeight() {
        return getHeight(); // TODO padding
    }
    
    public void pageUp() {
        blockScroll(-getViewHeight());
    }
    
    public void pageDown() {
        blockScroll(getViewHeight());
    }
    
    public void scroll(double fractionOfHeight) {
        blockScroll(getViewHeight() * fractionOfHeight);
    }

    /** scroll by a number of pixels, delta must not exceed the view height in absolute terms */
    public void blockScroll(double delta) {
        Origin p = layout.computeOrigin(delta);
        if (p != null) {
            setOrigin(p);
        }
    }

    public void hscroll(double delta) {
        double off = getOffsetX() + delta * getWidth();
        if (off < 0.0) {
            off = 0.0;
        } else if (off + getWidth() > rightEdge()) {
            off = Math.max(0.0, rightEdge() - getWidth());
        }
        setOffsetX(off);
        // no need to recompute the flow
        layoutNodes();
        updateCaretAndSelection();
    }

    /** scrolls to visible area, using vflow coordinates */
    public void scrollToVisible(double x, double y) {
        if (y < 0.0) {
            // above viewport
            blockScroll(y);
        } else if (y >= getViewHeight()) {
            // below viewport
            blockScroll(y - getViewHeight());
        }
        
        scrollHorizontalToVisible(x);
    }

    public void scrollCaretToVisible() {
        CaretInfo c = getCaretInfo();
        if (c == null) {
            // caret is outside of the layout; let's set the origin first to the caret position
            // and then block scroll to avoid scrolling past the document end, if needed
            TextPos p = control.getCaretPosition();
            if (p != null) {
                int ix = p.index();
                Origin or = new Origin(ix, 0);
                setOrigin(or);
                checkForExcessiveWhitespaceAtTheEnd();
            }
        } else {
            // block scroll, if needed
            if (c.y0() < 0.0) {
                blockScroll(c.y0());
            } else if (c.y1() > getViewHeight()) {
                blockScroll(c.y1() - getViewHeight());
            }
            
            scrollHorizontalToVisible(c.x());
        }
    }

    /** x - vflow coordinate */
    private void scrollHorizontalToVisible(double x) {
        if (!control.isWrapText()) {
            double off;
            if (x < 0.0) {
                off = Math.max(getOffsetX() + x - 20.0, 0.0);
            } else if (x > getWidth()) {
                off = getOffsetX() + x - getWidth() + Config.horizontalGuard;
            } else {
                return;
            }

            setOffsetX(off);
            layoutNodes();
            updateCaretAndSelection();
        }
    }

    protected void checkForExcessiveWhitespaceAtTheEnd() {
        double delta = layout.bottomHeight() - getViewHeight();
        if(delta < 0) {
            blockScroll(delta);
        }
    }
    
    public void updateTabSize() {
        CaretInfo c = getCaretInfo();
        recomputeLayout();
        // TODO remember caret line position, do layout pass, block move to preserve the caret y position
        // as it might shift (only if wrapping is enabled)
        // also if wrap is off, might need a horizontal block scroll to keep caret in the same x position
    }

    // TODO this implementation might be more advanced to reduce the amount of re-computation and re-flow
    public void handleTextUpdated(TextPos start, TextPos end, int addedTop, int linesAdded, int addedBottom) {
        // change origin if start position is before the top line
        Origin origin = getOrigin();
        if(start.index() < origin.index()) {
            origin = new Origin(start.index(), 0);
            setOrigin(origin);
        }
        
        // TODO clear cache >= start, update layout
        cache.clear();
        // TODO rebuild from start.lineIndex()
        recomputeLayout();
    }

    // TODO this implementation might be more advanced to reduce the amount of re-computation and re-flow
    public void handleStyleUpdated(TextPos start, TextPos end) {
        // TODO clear cache >= start, update layout
        cache.clear();
        // TODO rebuild from start.lineIndex()
        recomputeLayout();
    }
    
    public WritableImage snapshot(Node n) {
        n.setManaged(false);
        getChildren().add(n);
        try {
            n.applyCss();
            if(n instanceof Region r) {
                // or layout?
                double w = getWidth(); // TODO padding
                double h = r.prefHeight(w);
                layoutInArea(r, 0, -h, w, h, 0, HPos.CENTER, VPos.CENTER);
            }
            SnapshotParameters p = new SnapshotParameters();
            // TODO parameters?
            return n.snapshot(p, null);
        } finally {
            getChildren().remove(n);
        }
    }
}
