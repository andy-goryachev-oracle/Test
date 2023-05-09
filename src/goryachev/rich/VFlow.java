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
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import goryachev.rich.impl.FastCache;
import goryachev.rich.impl.RPane;
import goryachev.rich.impl.SelectionHelper;
import goryachev.rich.model.StyledTextModel;
import goryachev.rich.util.FxPathBuilder;
import goryachev.rich.util.ListenerHelper;
import goryachev.rich.util.Util;

/**
 * Virtual text flow deals with TextCells, scroll bars, and conversion
 * between the model and the screen coordinates.
 */
public class VFlow extends Pane {
    /** maximum width for unwrapped TextFlow layout. Neither Double.MAX_VALUE nor 1e20 work */
    private static final double MAX_WIDTH_FOR_LAYOUT = 1_000_000_000.0;
    private final RichTextArea control;
    private final Config config;
    private final ScrollBar vscroll;
    private final ScrollBar hscroll;
    private final RPane leftGutter;
    private final RPane rightGutter;
    private final RPane content;
    protected final FastCache<TextCell> cellCache;
    private TextCellLayout layout; // FIX rename?
    private final Path caretPath;
    private final Path caretLineHighlight;
    private final Path selectionHighlight;
    protected final ReadOnlyObjectWrapper<Origin> origin = new ReadOnlyObjectWrapper(Origin.ZERO);
    protected final SimpleBooleanProperty caretVisible = new SimpleBooleanProperty(true);
    protected final SimpleBooleanProperty suppressBlink = new SimpleBooleanProperty(false);
    protected final SimpleDoubleProperty offsetX = new SimpleDoubleProperty(0.0);
    protected final SimpleDoubleProperty contentWidth = new SimpleDoubleProperty(0.0);
    protected final Timeline caretAnimation;
    private boolean handleScrollEvents = true;
    private boolean vsbPressed;
    private double topPadding;
    private double bottomPadding;
    private double leftPadding;
    private double rightPadding;
    private double lineSpacing;
    private boolean inReflow;

    public VFlow(RichTextAreaSkin skin, Config c, ListenerHelper lh, ScrollBar vscroll, ScrollBar hscroll) {
        this.control = skin.getSkinnable();
        this.config = c;
        this.vscroll = vscroll;
        this.hscroll = hscroll;

        getStyleClass().add("flow");

        cellCache = new FastCache(config.cellCacheSize);

        // TODO consider creating upond demand
        leftGutter = new RPane("left-side");
        // TODO consider creating upond demand
        rightGutter = new RPane("right-side");

        content = new RPane("content");

        caretPath = new Path();
        caretPath.getStyleClass().add("caret");
        caretPath.setManaged(false);
        caretPath.setStroke(Color.BLACK);
        caretPath.setStrokeWidth(1.0);

        caretLineHighlight = new Path();
        caretLineHighlight.getStyleClass().add("caret-line");
        caretLineHighlight.setFill(Color.rgb(255, 0, 255, 0.02));
        caretLineHighlight.setManaged(false);

        selectionHighlight = new Path();
        selectionHighlight.getStyleClass().add("selection-highlight");
        selectionHighlight.setManaged(false);

        getChildren().addAll(content, leftGutter, rightGutter);
        content.getChildren().addAll(caretLineHighlight, selectionHighlight, caretPath);

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

        lh.addInvalidationListener(this::handleModelChange, control.modelProperty());
        lh.addInvalidationListener(this::handleWrapText, control.wrapTextProperty());
        
        lh.addChangeListener(
            this::updateHorizontalScrollBar,
            true,
            contentWidth,
            offsetX
        );
        
        lh.addChangeListener(
            this::handleOrigin,
            true,
            origin
        );
        
        widthProperty().addListener((p) -> updateWidth());
        heightProperty().addListener((p) -> updateHeight());
        
        vscroll.addEventFilter(MouseEvent.ANY, this::handleVScrollMouseEvent);
    }

    public void dispose() {
        caretPath.visibleProperty().unbind();
    }

    public Pane getContentPane() {
        return content;
    }

    public void handleModelChange() {
        control.clearSelection();
        setContentWidth(0.0);
        setOrigin(new Origin(0, -topPadding));
        setOffsetX(-leftPadding);
        cellCache.clear();
        requestLayout();
    }

    protected double wrappedWidth() {
        double w = content.getWidth() - leftPadding - rightPadding;
        w = Math.max(w, 0.0);
        return snapSpaceX(w);
    }

    public void handleWrapText() {
        if (control.isWrapText()) {
            double w = wrappedWidth();
            setContentWidth(w);
        } else {
            setContentWidth(0.0);
        }
        setOffsetX(-leftPadding);
        cellCache.clear();
        requestLayout();
        updateHorizontalScrollBar();
        updateVerticalScrollBar();
    }

    public void handleDecoratorChange() {
        // TODO clear left/right side caches
        requestLayout();
    }

    public void handleLineSpacing() {
        requestLayout();
        updateHorizontalScrollBar();
        updateVerticalScrollBar();
    }

    public void handleContentPadding() {
        updateContentPadding();
        
        setOffsetX(-leftPadding);

        if (getOrigin().index() == 0) {
            if (getOrigin().offset() < -topPadding) {
                setOrigin(new Origin(0, -topPadding));
            }
        }

        requestLayout();
        updateHorizontalScrollBar();
        updateVerticalScrollBar();
    }

    public void updateContentPadding() {
        Insets m = control.getContentPadding();
        if (m == null) {
            leftPadding = 0.0;
            rightPadding = 0.0;
            topPadding = 0.0;
            bottomPadding = 0.0;
        } else {
            leftPadding = snapPositionX(m.getLeft());
            rightPadding = snapPositionX(m.getRight());
            topPadding = snapPositionY(m.getTop());
            bottomPadding = snapPositionY(m.getBottom());
        }
    }

    public double leftPadding() {
        return leftPadding;
    }

    public Origin getOrigin() {
        return origin.get();
    }

    public void setOrigin(Origin p) {
        if (p == null) {
            throw new NullPointerException();
        }
        origin.set(p);
    }
    
    public ReadOnlyProperty<Origin> originProperty() {
        return origin.getReadOnlyProperty();
    }
    
    protected void handleOrigin() {
        if(!inReflow) {
            requestLayout();
        }
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

    /** horizontal scroll offset */
    public DoubleProperty offsetXProperty() {
        return offsetX;
    }
    
    /** width of the area allocated for content (text cells) */
    public double getContentWidth() {
        return contentWidth.get();
    }
    
    public void setContentWidth(double w) {
        contentWidth.set(w);
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
            double w = wrappedWidth();
            setContentWidth(w);
        } else {
            double w = getOffsetX() + content.getWidth();
            if (layout != null) {
                if (layout.getUnwrappedWidth() > w) {
                    w = layout.getUnwrappedWidth();
                }
            }

            if (w > getContentWidth()) {
                setContentWidth(w);
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
        
        // current line highlight
        if (control.isHighlightCurrentLine()) {
            FxPathBuilder b = new FxPathBuilder();
            createCurrentLineHighlight(b, caret);
            caretLineHighlight.getElements().setAll(b.getPathElements());
        } else {
            caretLineHighlight.getElements().clear();
        }
        
        // selection
        FxPathBuilder b = new FxPathBuilder();
        createSelectionHighlight(b, anchor, caret);
        selectionHighlight.getElements().setAll(b.getPathElements());
        selectionHighlight.setTranslateX(leftPadding);

        // caret
        b = new FxPathBuilder();
        createCaretPath(b, caret);
        caretPath.getElements().setAll(b.getPathElements());
        caretPath.setTranslateX(leftPadding);
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
        } else if (start.index() >= (topCellIndex + textCellLayout().getVisibleCellCount())) {
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
        double left = -leftPadding;
        double right = getContentWidth() + leftPadding + rightPadding;
        // TODO
        boolean topLTR = true;
        boolean bottomLTR = true;

        new SelectionHelper(b, left, right).generate(top, bottom, topLTR, bottomLTR, leftPadding, lineSpacing);
    }

    protected void createCurrentLineHighlight(FxPathBuilder b, TextPos caret) {
        int ix = caret.index();
        TextCell cell = textCellLayout().getVisibleCell(ix);
        if(cell != null) {
            double w;
            if(control.isWrapText()) {
                w = getWidth();
            } else {
                w = getContentWidth() + leftPadding + rightPadding;
            }
            cell.addBoxOutline(b, 0.0, snapPositionX(w), cell.getCellHeight());
        }
    }

    /** uses vflow.content cooridinates */ 
    public TextPos getTextPosLocal(double localX, double localY) {
        return textCellLayout().getTextPos(getOffsetX(), localX, localY);
    }

    /** uses vflow.content coordinates */
    protected CaretInfo getCaretInfo(TextPos p) {
        return textCellLayout().getCaretInfo(getOffsetX() + leftPadding, p);
    }

    /** returns caret sizing info using vflow.content coordinates, or null */
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
        TextCell cell = textCellLayout().getVisibleCell(line);
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
            return Util.translatePath(-leftPadding, content, cell.getContent(), pe);
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
    
    public int getParagraphCount() {
        return control.getParagraphCount();
    }
    
    public double lineSpacing() {
        return lineSpacing;
    }

    public Insets contentPadding() {
        return control.getContentPadding();
    }

    private void handleVScrollMouseEvent(MouseEvent ev) {
        EventType<? extends MouseEvent> t = ev.getEventType();
        if (t == MouseEvent.MOUSE_PRESSED) {
            vsbPressed = true;
        } else if (t == MouseEvent.MOUSE_RELEASED) {
            vsbPressed = false;
            updateVerticalScrollBar();
        }
    }

    /** updates VSB in response to change in height, layout, or offsetY */ 
    protected void updateVerticalScrollBar() {
        double visible;
        double val;
        if (layout == null || (getParagraphCount() == 0)) {
            visible = 1.0;
            val = 0.0;
        } else {
            TextCellLayout la = textCellLayout();
            double av = la.averageHeight();
            double max = la.estimatedMax();
            double h = getHeight();
            val = toScrollBarValue((topCellIndex() - la.topCount()) * av + la.topHeight(), h, max);
            visible = h / max;
        }

        handleScrollEvents = false;

        vscroll.setMin(0.0);
        vscroll.setMax(1.0);
        vscroll.setUnitIncrement(config.scrollBarsUnitIncrement);
        vscroll.setVisibleAmount(visible);
        vscroll.setValue(val);

        handleScrollEvents = true;
    }

    /** handles user moving the vertical scroll bar */
    public void handleVerticalScroll() {
        if (handleScrollEvents) {
            if (getParagraphCount() == 0) {
                return;
            }

            double max = vscroll.getMax();
            double val = vscroll.getValue();
            double visible = vscroll.getVisibleAmount();
            double pos = fromScrollBarValue(val, visible, max); // max is 1.0

            Origin p = textCellLayout().fromAbsolutePosition(pos);
            setOrigin(p);
        }
    }

    /** updates HSB in response to change in width, layout, or offsetX */ 
    protected void updateHorizontalScrollBar() {
        boolean wrap = control.isWrapText();
        if (wrap) {
            return;
        }

        double max = getContentWidth() + leftPadding + rightPadding;
        double w = content.getWidth();
        double off = getOffsetX();
        double vis = w / max;
        double val = toScrollBarValue(off, w, max);

        handleScrollEvents = false;

        hscroll.setMin(0.0);
        hscroll.setMax(1.0);
        hscroll.setUnitIncrement(config.scrollBarsUnitIncrement);
        hscroll.setVisibleAmount(vis);
        hscroll.setValue(val);

        handleScrollEvents = true;
    }

    /** handles user moving the scroll bar */
    public void handleHorizontalScroll() {
        if (handleScrollEvents) {
            if (layout == null) {
                return;
            } else if (control.isWrapText()) {
                return;
            }
            
            double max = getContentWidth() + leftPadding + rightPadding;
            double visible = content.getWidth();
            double val = hscroll.getValue();
            double off = fromScrollBarValue(val, visible, max) - leftPadding;

            setOffsetX(snapPositionX(off));
            // no need to recompute the flow
            placeNodes();
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
        TextCell cell = cellCache.get(modelIndex);
        if (cell == null) {
            cell = control.getModel().createTextCell(modelIndex);

            // a bit of a hack: avoid TextCells with an empty TextFlow,
            // as it makes the caret collapse to a single point
            {
                Region r = cell.getContent();
                if (r instanceof TextFlow f) {
                    if (f.getChildren().size() == 0) {
                        f.getChildren().add(new Text(""));
                    }
                }
            }

            cellCache.add(cell.getIndex(), cell);
        }
        return cell;
    }

    private double computeSideWidth(SideDecorator d) {
        if (d != null) {
            double w = d.getPrefWidth(getWidth());
            if (w <= 0.0) {
                int top = topCellIndex();
                Node n = d.getNode(top, true);
                n.setManaged(false);

                content.getChildren().add(n);
                try {
                    n.applyCss();
                    w = n.prefWidth(-1);
                } finally {
                    content.getChildren().remove(n);
                }
            }
            // introducing some granularity in order to avoid left boundary moving back and forth when scrolling
            double granularity = 15;
            w = (Math.round((w + 1.0) / granularity) + 1.0) * granularity;
            return snapSizeX(w);
        }
        return 0.0;
    }

    @Override
    protected void layoutChildren() {
        layout = reflow();
    }

    protected TextCellLayout reflow() {
        inReflow = true;
        try {
            removeCells();
    
            TextCellLayout la = new TextCellLayout(this);
            layout = la;
            layoutCells();
    
            checkForExcessiveWhitespaceAtTheEnd();
            updateCaretAndSelection();
    
            // eliminate VSB during scrolling with a mouse
            // the VSB will finally get updated on mouse released event
            if (!vsbPressed) {
                updateVerticalScrollBar();
            }
    
            // layout might get invalidated in the process, but we must return a non-null value
            return la;
        } finally {
            inReflow = false;
        }
    }

    protected void removeCells() {
        if (layout != null) {
            layout.removeNodesFrom(content);
            layout = null;
        }
    }

    /** returns a non-null layout, laying out cells if necessary */
    protected TextCellLayout textCellLayout() {
        if(layout == null) {
            layoutChildren();
        }
        return layout;
    }

    /** recomputes sliding window */
    protected void layoutCells() {
        if (control.getModel() == null) {
            leftGutter.setVisible(false);
            rightGutter.setVisible(false);
            return;
        }

        double width = getWidth();
        double height = getHeight();

        // sides
        SideDecorator leftDecorator = control.getLeftDecorator();
        SideDecorator rightDecorator = control.getRightDecorator();
        double leftSide = computeSideWidth(leftDecorator);
        double rightSide = computeSideWidth(rightDecorator);

        if (leftDecorator == null) {
            leftGutter.setVisible(false);
        } else {
            leftGutter.setVisible(true);
            layoutInArea(leftGutter, 0.0, 0.0, leftSide, height, 0.0, HPos.CENTER, VPos.CENTER);
        }
        
        if (rightGutter == null) {
            rightGutter.setVisible(false);
        } else {
            rightGutter.setVisible(true);
            layoutInArea(rightGutter, width - rightSide, 0.0, rightSide, height, 0.0, HPos.CENTER, VPos.CENTER);
        }
        
        layoutInArea(content, leftSide, 0.0, width - leftSide - rightSide, height, 0.0, HPos.CENTER, VPos.CENTER);

        int paragraphCount = getParagraphCount();
        int tabSize = control.getTabSize();
        lineSpacing = control.getLineSpacing();
        boolean wrap = control.isWrapText();
        double forWidth;
        double maxWidth;
        if (wrap) {
            forWidth = wrappedWidth();
            maxWidth = forWidth;
        } else {
            forWidth = -1.0;
            maxWidth = MAX_WIDTH_FOR_LAYOUT;
        }

        double ytop = snapPositionY(-getOrigin().offset());
        double y = ytop;
        double unwrappedWidth = 0;
        double margin = config.slidingWindowMargin * height;
        int topMarginCount = 0;
        int bottomMarginCount = 0;
        int count = 0;
        boolean visible = true;
        // TODO if topCount < marginCount, increase bottomCount correspondingly
        // also, update Origin if layout hit the beginning/end of the document
        
        // populating visible part of the sliding window + bottom margin
        int i = topCellIndex();
        for ( ; i < paragraphCount; i++) {
            TextCell cell = getCell(i);
            // TODO skip computation if layout width is the same
            Region r = cell.getContent();
            content.getChildren().add(r);
            r.setMaxWidth(maxWidth);
            r.setMaxHeight(USE_COMPUTED_SIZE);
            if(r instanceof TextFlow f) {
                f.setTabSize(tabSize);
                f.setLineSpacing(lineSpacing);
            }

            r.applyCss();

            layout.addCell(cell);

            double h = r.prefHeight(forWidth) + lineSpacing;
            h = snapSizeY(h); // is this right?  or snap(y + h) - snap(y) ?
            cell.setCellHeight(h, forWidth);
            cell.setLocationY(y);

            if (!wrap) {
                if (visible) {
                    double w = r.prefWidth(-1);
                    if (unwrappedWidth < w) {
                        unwrappedWidth = w;
                    }
                }
            }

            y = snapPositionY(y + h);
            count++;

            // stop populating the bottom part of the sliding window
            // when exceeded both pixel and line count margins
            if (visible) {
                if (y > height) {
                    topMarginCount = (int)Math.ceil(count * config.slidingWindowMargin);
                    bottomMarginCount = count + topMarginCount;
                    layout.setVisibleCount(count);
                    visible = false;
                }
            } else {
                // remove invisible cell from layout after sizing
                content.getChildren().remove(r);

                if ((y > (height + margin)) && (count > bottomMarginCount)) {
                    break;
                }
            }
        }

        // less paragraphs than can fit in the view
        if (visible) {
            layout.setVisibleCount(count);
        }
        
        if (i == paragraphCount) {
            y += bottomPadding;
        }

        layout.setBottomCount(count);
        layout.setBottomHeight(y);
        layout.setUnwrappedWidth(unwrappedWidth);
        count = 0;
        y = ytop;
        
        // populate top margin, going backwards from topCellIndex
        // TODO populate more, if bottom ended prematurely
        for (i = topCellIndex() - 1; i >= 0; i--) {
            TextCell cell = getCell(i);
            // TODO maybe skip computation if layout width is the same
            Region r = cell.getContent();
            content.getChildren().add(r);
            r.setMaxWidth(maxWidth);
            r.setMaxHeight(USE_COMPUTED_SIZE);
            if(r instanceof TextFlow f) {
                f.setTabSize(tabSize);
                f.setLineSpacing(lineSpacing);
            }

            r.applyCss();
            
            layout.addCell(cell);
            
            double h = r.prefHeight(forWidth) + lineSpacing;
            h = snapSizeY(h); // is this right?  or snap(y + h) - snap(y) ?
            y = snapPositionY(y - h);
            count++;

            cell.setCellHeight(h, forWidth);
            cell.setLocationY(y);
            
            content.getChildren().remove(r);

            // stop populating the top part of the sliding window
            // when exceeded both pixel and line count margins
            if ((-y > margin) && (count > topMarginCount)) {
                break;
            }
        }
        
        layout.setTopHeight(-y);

        // lay out content nodes
        placeNodes();

        if (wrap) {
            double w = wrappedWidth();
            setContentWidth(w);
        } else {
            if (getContentWidth() < unwrappedWidth) {
                setContentWidth(Math.max(unwrappedWidth, width));
            }
        }
    }

    protected void placeNodes() {
        boolean wrap = control.isWrapText();
        double w = wrap ? getContentWidth() : MAX_WIDTH_FOR_LAYOUT;
        double x = snapPositionX(-getOffsetX());

        leftGutter.getChildren().clear();
        rightGutter.getChildren().clear();

        int sz = layout.getVisibleCellCount();
        for (int i=0; i < sz; i++) {
            TextCell cell = layout.getCellAt(i);
            Region r = cell.getContent();
            double h = cell.getCellHeight();
            double y = cell.getY();
            content.layoutInArea(r, x, y, w, h);

            // place side nodes
            // in theory, these can be cached
            SideDecorator leftDecorator = control.getLeftDecorator();
            if (leftDecorator != null) {
                Node n = leftDecorator.getNode(cell.getIndex(), false);
                if (n != null) {
                    n.setManaged(false);
                    leftGutter.getChildren().add(n);
                    leftGutter.layoutInArea(n, 0.0, y, leftGutter.getWidth(), h);
                }
            }

            SideDecorator rightDecorator = control.getRightDecorator();
            if (rightDecorator != null) {
                Node n = rightDecorator.getNode(cell.getIndex(), false);
                if (n != null) {
                    n.setManaged(false);
                    rightGutter.getChildren().add(n);
                    rightGutter.layoutInArea(n, 0.0, y, rightGutter.getWidth(), h);
                }
            }
        }
    }

    public double getViewHeight() {
        return content.getHeight(); // TODO padding
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
        Origin p = textCellLayout().computeOrigin(delta);
        if (p != null) {
            setOrigin(p);
        }
    }

    public void hscroll(double delta) {
        double cw = content.getWidth();
        double off = getOffsetX() + delta * cw;
        if (off < 0.0) {
            off = 0.0;
        } else if (off + cw > (getContentWidth() + leftPadding)) {
            off = Math.max(0.0, getContentWidth() + leftPadding - cw);
        }
        setOffsetX(off - leftPadding);
        // no need to recompute the flow
        placeNodes();
        updateCaretAndSelection();
    }

    /** scrolls to visible area, using vflow.content coordinates */
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

    /** x - vflow.content coordinate */
    private void scrollHorizontalToVisible(double x) {
        if (!control.isWrapText()) {
            x += leftPadding;
            double cw = content.getWidth();
            double off;
            if (x < 0.0) {
                off = Math.max(getOffsetX() + x - 20.0, 0.0);
            } else if (x > cw) {
                off = getOffsetX() + x - cw + config.horizontalGuard;
            } else {
                return;
            }

            setOffsetX(off);
            placeNodes();
            updateCaretAndSelection();
        }
    }

    protected void checkForExcessiveWhitespaceAtTheEnd() {
        double delta = textCellLayout().bottomHeight() - getViewHeight();
        if (delta < 0) {
            if (getOrigin().index() == 0) {
                if (getOrigin().offset() <= -topPadding) {
                    return;
                }
            }
            blockScroll(delta);
        }
    }

    public void updateTabSize() {
        CaretInfo c = getCaretInfo();
        requestLayout();
        // TODO remember caret line position, do layout pass, block move to preserve the caret y position
        // as it might shift (only if wrapping is enabled)
        // also if wrap is off, might need a horizontal block scroll to keep caret in the same x position
    }

    // TODO this implementation might be more advanced to reduce the amount of re-computation and re-flow
    public void handleTextUpdated(TextPos start, TextPos end, int addedTop, int linesAdded, int addedBottom) {
        // change origin if start position is before the top line
        Origin origin = getOrigin();
        if (start.index() < origin.index()) {
            origin = new Origin(start.index(), 0);
            setOrigin(origin);
        }

        // TODO causes flicker in line number nodes

        // TODO clear cache >= start, update layout
        cellCache.clear();
        // TODO rebuild from start.lineIndex()
        requestLayout();
    }

    // TODO this implementation might be more advanced to reduce the amount of re-computation and re-flow
    public void handleStyleUpdated(TextPos start, TextPos end) {
        // TODO clear cache >= start, update layout
        cellCache.clear();
        // TODO rebuild from start.lineIndex()
        requestLayout();
    }
    
    public WritableImage snapshot(Node n) {
        n.setManaged(false);
        getChildren().add(n);
        try {
            n.applyCss();
            if(n instanceof Region r) {
                double w = getContentWidth();
                double h = r.prefHeight(w);
                layoutInArea(r, 0, -h, w, h, 0, HPos.CENTER, VPos.CENTER);
            }
            return n.snapshot(null, null);
        } finally {
            getChildren().remove(n);
        }
    }
}
