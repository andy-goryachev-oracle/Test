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

import java.text.BreakIterator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Region;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.text.HitInfo;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import goryachev.rich.util.BehaviorBase2;
import goryachev.rich.util.KCondition;
import goryachev.rich.util.KeyBinding2;

/**
 * RichTextArea Behavior.
 *
 * BehaviorBase and InputMap are not public, so had to invent my own.
 * 
 * TODO
 * Behavior:
 * - maps key bindings to actions
 * - action: invokes methods of control
 * Control:
 * - delegates to skin/behavior methods
 */
public class RichTextAreaBehavior extends BehaviorBase2 {
    private final RichTextAreaSkin skin;
    private final RichTextArea control;
    private final EventHandler<KeyEvent> keyHandler;
    private final ChangeListener<StyledTextModel> modelListener;
    private final StyledTextModel.ChangeListener textChangeListener;
    private final Timeline autoScrollTimer;
    private boolean autoScrollUp;
    private boolean fastAutoScroll;
    private double phantomX = -1.0;
    private static final Duration autoScrollPeriod = Duration.millis(Config.autoScrollPeriod);

    public RichTextAreaBehavior(RichTextAreaSkin skin) {
        this.skin = skin;
        this.control = skin.getSkinnable();

        this.keyHandler = this::handleKeyEvent;
        this.modelListener = this::handleModel;
        
        map(this::moveLeft, KeyCode.LEFT);
        map(this::moveRight, KeyCode.RIGHT);
        map(this::moveUp, KeyCode.UP);
        map(this::moveDown, KeyCode.DOWN);
        map(this::moveHome, KeyCode.HOME);
        map(this::moveEnd, KeyCode.END);
        map(this::pageDown, KeyCode.PAGE_DOWN);
        map(this::pageUp, KeyCode.PAGE_UP);
        // FIX move control:: methods back to behavior
        map(control::selectAll, KeyCode.A, KCondition.SHORTCUT);
        map(control::moveDocumentStart, KeyCode.HOME, KCondition.CTRL, KCondition.NOT_MAC);
        map(control::moveDocumentStart, KeyCode.UP, KCondition.SHORTCUT, KCondition.MAC);
        map(control::moveDocumentEnd, KeyCode.END, KCondition.CTRL, KCondition.NOT_MAC);
        map(control::moveDocumentEnd, KeyCode.DOWN, KCondition.SHORTCUT, KCondition.MAC);
        map(this::selectLeft, KeyCode.LEFT, KCondition.SHIFT);
        map(this::selectRight, KeyCode.RIGHT, KCondition.SHIFT);
        map(this::selectUp, KeyCode.UP, KCondition.SHIFT);
        map(this::selectDown, KeyCode.DOWN, KCondition.SHIFT);
        map(this::selectPageUp, KeyCode.PAGE_UP, KCondition.SHIFT);
        map(this::selectPageDown, KeyCode.PAGE_DOWN, KCondition.SHIFT);
        // FIX move control:: methods back to behavior
        map(control::selectDocumentStart, KeyCode.HOME, KCondition.SHIFT, KCondition.CTRL, KCondition.NOT_MAC);
        map(control::selectDocumentStart, KeyCode.UP, KCondition.SHIFT, KCondition.SHORTCUT, KCondition.MAC);
        map(control::selectDocumentEnd, KeyCode.END, KCondition.SHIFT, KCondition.CTRL, KCondition.NOT_MAC);
        map(control::selectDocumentEnd, KeyCode.DOWN, KCondition.SHIFT, KCondition.SHORTCUT, KCondition.MAC);

        this.textChangeListener = new StyledTextModel.ChangeListener() {
            @Override
            public void eventTextUpdated(TextPos start, TextPos end, int top, int ins, int btm) {
                handleTextUpdated(start, end, top, ins, btm);
            }

            @Override
            public void eventAllTextReplaced() {
                // TODO
            }
        };

        autoScrollTimer = new Timeline(new KeyFrame(autoScrollPeriod, (ev) -> {
            autoScroll();
        }));
        autoScrollTimer.setCycleCount(Timeline.INDEFINITE);
    }

    public void install() {
        VFlow f = vflow();
        f.addEventFilter(MouseEvent.MOUSE_CLICKED, this::handleMouseClicked);
        f.addEventFilter(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);
        f.addEventFilter(MouseEvent.MOUSE_RELEASED, this::handleMouseReleased);
        f.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::handleMouseDragged);
        f.addEventFilter(ScrollEvent.ANY, this::handleScrollEvent);

        control.addEventHandler(KeyEvent.ANY, keyHandler);

        // TODO ListenerHelper with fireImmediately flag would work well here
        control.modelProperty().addListener(modelListener);
        if(control.getModel() != null) {
            control.getModel().addChangeListener(textChangeListener);
        }
    }

    @Override
    public void dispose() {
        if(control.getModel() != null) {
            control.getModel().removeChangeListener(textChangeListener);
        }
        control.modelProperty().removeListener(modelListener);
        control.removeEventHandler(KeyEvent.ANY, keyHandler);
        
        super.dispose();
    }

    protected VFlow vflow() {
        return skin.getVFlow();
    }
    
    protected boolean isRTL() {
        return (control.getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT);
    }

    /** accepting VFlow coordinates */
    protected TextPos getTextPos(double localX, double localY) {
        return vflow().getTextPos(localX, localY);
    }

    /** returns caret position or null */
    protected TextPos getCaret() {
        SelectionModel sm = control.getSelectionModel();
        if (sm == null) {
            return null;
        }

        SelectionSegment sel = sm.getSelectionSegment();
        if (sel == null) {
            return null;
        }

        return sel.getCaret().getTextPos();
    }
    
    protected void handleModel(Object src, StyledTextModel old, StyledTextModel m) {
        if (old != null) {
            old.removeChangeListener(textChangeListener);
        }

        if (m != null) {
            m.addChangeListener(textChangeListener);
        }
    }

    public void handleKeyEvent(KeyEvent ev) {
        if (ev == null || ev.isConsumed()) {
            return;
        }

        KeyBinding2 k = KeyBinding2.from(ev);
        if (k != null) {
            Runnable r = inputMap.getFunction(k);
            if (r != null) {
                vflow().setSuppressBlink(true);
                r.run();
                vflow().setSuppressBlink(false);
                ev.consume();
            }
        }

        // TODO possibly onKeyTyped in inputMap?
        // TODO something about consuming all key presses (yes) and key releases (not really)
        // in TextInputControlBehavior:194
        if (ev.getEventType() == KeyEvent.KEY_TYPED) {
            String ch = ev.getCharacter();
            handleKeyTyped(ch);
        }
    }

    protected boolean isEditable() {
        if (control.isEditable()) {
            StyledTextModel m = control.getModel();
            if (m != null) {
                return m.isEditable();
            }
        }
        return false;
    }

    protected void handleKeyTyped(String character) {
        if (!isEditable()) {
            return;
        }

        StyledTextModel m = control.getModel();
        if (m == null) {
            return;
        }

        SelectionModel sm = control.getSelectionModel();
        if (sm == null) {
            return;
        }

        // TODO utility method
        SelectionSegment sel = sm.getSelectionSegment();
        if (sel == null) {
            return;
        }

        TextPos an = sel.getAnchor().getTextPos();
        TextPos ca = sel.getCaret().getTextPos();
        m.replace(an, ca, character);
    }

    protected void handleMouseClicked(MouseEvent ev) {
        if (ev.getButton() == MouseButton.PRIMARY) {
            int clicks = ev.getClickCount();
            switch (clicks) {
            case 2:
                control.selectWord(getTextPosition(ev));
                break;
            case 3:
                control.selectLine(getTextPosition(ev));
                break;
            }
        }
    }

    protected void handleMousePressed(MouseEvent ev) {
        // TODO
        if (ev.isPopupTrigger()) {
            // TODO use onContextMenu ?
            // TODO clear selection if click happened outside of said selection?
            return;
        }

        SelectionModel sm = control.getSelectionModel();
        if (sm == null) {
            return;
        }

        Marker m = getTextPosition(ev);
        if (m == null) {
            return;
        }

        vflow().setSuppressBlink(true);

        if (ev.isShiftDown()) {
            // expand selection from the anchor point to the current position
            // clearing existing (possibly multiple) selection
            sm.extendSelection(m);
        } else {
            sm.setSelection(m, m);
            sm.setAnchor(m);
        }

        TextPos pos = m.getTextPos();
        control.setCaretPosition(pos);
        control.requestFocus();
    }

    protected void handleMouseReleased(MouseEvent ev) {
        stopAutoScroll();
        vflow().scrollCaretToVisible();
        vflow().setSuppressBlink(false);
        clearPhantomX();
    }

    protected void handleMouseDragged(MouseEvent ev) {
        if (!(ev.getButton() == MouseButton.PRIMARY)) {
            return;
        }

        double y = ev.getY();
        if (y < 0.0) {
            // above visible area
            autoScroll(y);
            return;
        } else if (y > vflow().getViewHeight()) {
            // below visible area
            autoScroll(y - vflow().getViewHeight());
            return;
        } else {
            stopAutoScroll();
        }

        Marker pos = getTextPosition(ev);
        control.getSelectionModel().extendSelection(pos);
    }

    protected void handleScrollEvent(ScrollEvent ev) {
        if (ev.isShiftDown()) {
            // TODO horizontal scroll
        } else if (ev.isShortcutDown()) {
            // page up / page down
            if (ev.getDeltaY() >= 0) {
                vflow().pageUp();
            } else {
                vflow().pageDown();
            }
        } else {
            // block scroll
            double f = Config.scrollWheelBlockSize;
            if (ev.getDeltaY() >= 0) {
                f = -f;
            }
            vflow().scroll(f);
        }
    }

    protected Marker getTextPosition(MouseEvent ev) {
        double x = ev.getScreenX();
        double y = ev.getScreenY();
        return getTextPosition(x, y);
    }

    protected Marker getTextPosition(double screenX, double screenY) {
        return control.getTextPosition(screenX, screenY);
    }
    
    protected String getPlainText(int modelIndex) {
        StyledTextModel m = control.getModel();
        return (m == null) ? null : m.getPlainText(modelIndex);
    }

    protected void stopAutoScroll() {
        autoScrollTimer.stop();
    }
    
    protected void autoScroll(double delta) {
        autoScrollUp = (delta < 0.0);
        fastAutoScroll = Math.abs(delta) > Config.fastAutoScrollThreshold;
        autoScrollTimer.play();
    }
    
    protected void autoScroll() {
        double delta = fastAutoScroll ? Config.autoScrollStepFast : Config.autoStopStepSlow;
        if(autoScrollUp) {
            delta = -delta;
        }
        vflow().blockScroll(delta);
        
        double x = 0.0;
        double y;
        if(autoScrollUp) {
            y = 0.0;
        } else {
            y = vflow().getViewHeight();
        }
        
        vflow().scrollToVisible(x, y);
        
        TextPos p = getTextPos(x, y);
        
        Marker m = control.newMarker(p);
        control.getSelectionModel().extendSelection(m);
    }

    public void pageDown() {
        moveLine(vflow().getViewHeight(), false);
    }
    
    public void pageUp() {
        moveLine(-vflow().getViewHeight(), false);
    }

    public void moveRight() {
        nextCharacterVisually(true, false);
    }

    public void moveLeft() {
        nextCharacterVisually(false, false);
    }
    
    public void moveHome() {
        TextPos p = getCaret();
        if (p != null) {
            TextPos p2 = new TextPos(p.lineIndex(), 0, true);
            vflow().moveCaret(p2, false);
            clearPhantomX();
        }
    }
    
    public void moveEnd() {
        TextPos p = getCaret();
        if (p != null) {
            String s = getPlainText(p.lineIndex());
            int len = (s == null ? 0 : s.length());
            TextPos p2 = new TextPos(p.lineIndex(), len, false);
            vflow().moveCaret(p2, false);
            clearPhantomX();
        }
    }

    public void moveUp() {
        moveLine(-1.0, false); // TODO line spacing
    }

    public void moveDown() {
        moveLine(1.0, false); // TODO line spacing
    }

    protected void moveLine(double deltaPixels, boolean extendSelection) {
        CaretInfo c = vflow().getCaretInfo();
        double x = c.x();
        double y = (deltaPixels < 0) ? c.y0() + deltaPixels : c.y1() + deltaPixels;

        if (phantomX < 0) {
            phantomX = x;
        } else {
            x = phantomX;
        }

        TextPos p = getTextPos(x, y);
        if (p == null) {
            // TODO check
            return;
        }

        vflow().moveCaret(p, extendSelection);
    }

    protected void nextCharacterVisually(boolean moveRight, boolean extendSelection) {
        clearPhantomX();

        TextPos caretPos = getCaret();
        if (caretPos == null) {
            return; // TODO
        }

        if (isRTL()) {
            moveRight = !moveRight;
        }
        
        TextCell cell = vflow().getCell(caretPos.lineIndex());
        int cix = caretPos.charIndex();
        if(moveRight) {
            cix++;
            if(cix >= cell.getTextLength()) {
                int line = cell.getLineIndex() + 1;
                if(line < vflow().lineCount()) {
                    // next line
                    TextPos pos = new TextPos(line, 0, true);
                    vflow().moveCaret(pos, extendSelection);
                }
                return;
            }
        } else {
            if(caretPos.charIndex() == 0) {
                int line = cell.getLineIndex() - 1;
                if(line >= 0) {
                    // prev line
                    TextCell prevCell = vflow().getCell(line);
                    cix = Math.max(0, prevCell.getTextLength() - 1);
                    TextPos pos = new TextPos(line, cix, false);
                    vflow().moveCaret(pos, extendSelection);
                }
                return;
            }
        }

        boolean useBreakIterator = true;
        if (useBreakIterator) {
            nextCharacterVisually_breakIterator(cell, caretPos, moveRight, extendSelection);
        } else {
            nextCharacterVisually_textArea(cell, caretPos, moveRight, extendSelection);
        }
    }
    
    // TODO combine with previous method
    private void nextCharacterVisually_breakIterator(TextCell cell, TextPos caretPos, boolean moveRight, boolean extendSelection) {
        // using default locale, same as TextInputControl.backward() for example
        BreakIterator br = BreakIterator.getCharacterInstance();
        String text = getPlainText(cell.getLineIndex());
        br.setText(text);
        int off = caretPos.charIndex();
        int ix = moveRight ? br.following(off) : br.preceding(off);
        if(ix == BreakIterator.DONE) {
            System.err.println(" --- SHOULD NOT HAPPEN: BreakIterator.DONE off=" + off); // FIX
            return;
        }
        
        TextPos pos = new TextPos(caretPos.lineIndex(), ix, caretPos.leading());
        vflow().moveCaret(pos, extendSelection);
        return;
    }
    
    @Deprecated // FIX remove, does not work correctly due to other bugs
    private void nextCharacterVisually_textArea(TextCell cell, TextPos caretPos, boolean moveRight, boolean extendSelection) { // FIX
        Region r = cell.getContent();
        if(r instanceof TextFlow /* TODO eclipse autocompletion f */) {
            TextFlow f = (TextFlow)r;
            PathElement[] caretShape = f.caretShape(caretPos.charIndex(), caretPos.leading());
            if(caretShape.length == 4) {
                System.err.println(" --- Split caret"); // FIX
                caretShape = new PathElement[] {
                    caretShape[0],
                    caretShape[1]
                };
            }
            
            Bounds caretBounds = new Path(caretShape).getLayoutBounds();
            double hitX = moveRight ? caretBounds.getMaxX() : caretBounds.getMinX();
            double hitY = (caretBounds.getMinY() + caretBounds.getMaxY()) / 2;
            HitInfo hit = f.hitTest(new Point2D(hitX, hitY));
            Path charShape = new Path(f.rangeShape(hit.getCharIndex(), hit.getCharIndex() + 1));
            if ((moveRight && charShape.getLayoutBounds().getMaxX() > caretBounds.getMaxX()) || 
                (!moveRight && charShape.getLayoutBounds().getMinX() < caretBounds.getMinX())) {
                TextPos pos = new TextPos(caretPos.lineIndex(), hit.getInsertionIndex(), !hit.isLeading());
                vflow().moveCaret(pos, extendSelection);
                return;
            }
        }
        
        System.err.println("* * * ERR failed to navigate within the TextFlow"); // FIX
    }
    
    public void clearPhantomX() {
        phantomX = -1.0;
    }
    
    public void selectLeft() {
        nextCharacterVisually(false, true);
    }
    
    public void selectRight() {
        nextCharacterVisually(true, true);
    }
    
    public void selectDown() {
        moveLine(1.0, true);
    }
    
    public void selectUp() {
        moveLine(-1.0, true);
    }
    
    public void selectPageDown() {
        moveLine(vflow().getViewHeight(), true);
    }
    
    public void selectPageUp() {
        moveLine(-vflow().getViewHeight(), true);
    }
    
    protected void handleTextUpdated(TextPos start, TextPos end, int charsAddedTop, int linesAdded, int charsAddedBottom) {
        // TODO
    }
    
    protected void handleAllTextReplaced() {
        // TODO
    }
}
