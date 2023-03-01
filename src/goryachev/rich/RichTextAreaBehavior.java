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
import javafx.geometry.NodeOrientation;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;
import goryachev.rich.RichTextArea.Action;
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

        map(Action.MOVE_LEFT, this::moveLeft, KeyCode.LEFT);
        map(Action.MOVE_RIGHT, this::moveRight, KeyCode.RIGHT);
        map(Action.MOVE_UP, this::moveUp, KeyCode.UP);
        map(Action.MOVE_DOWN, this::moveDown, KeyCode.DOWN);
        map(Action.MOVE_HOME, this::moveHome, KeyCode.HOME);
        map(Action.MOVE_END, this::moveEnd, KeyCode.END);
        map(Action.PAGE_DOWN, this::pageDown, KeyCode.PAGE_DOWN);
        map(Action.PAGE_UP, this::pageUp, KeyCode.PAGE_UP);
        map(Action.SELECT_ALL, this::selectAll, KeyCode.A, KCondition.SHORTCUT);
        map(Action.MOVE_DOCUMENT_START, this::moveDocumentStart);
        map(Action.MOVE_DOCUMENT_START, KeyCode.HOME, KCondition.CTRL, KCondition.NOT_MAC);
        map(Action.MOVE_DOCUMENT_START, KeyCode.UP, KCondition.SHORTCUT, KCondition.MAC);
        map(Action.MOVE_DOCUMENT_END, this::moveDocumentEnd);
        map(Action.MOVE_DOCUMENT_END, KeyCode.END, KCondition.CTRL, KCondition.NOT_MAC);
        map(Action.MOVE_DOCUMENT_END, KeyCode.DOWN, KCondition.SHORTCUT, KCondition.MAC);
        map(Action.SELECT_LEFT, this::selectLeft, KeyCode.LEFT, KCondition.SHIFT);
        map(Action.SELECT_RIGHT, this::selectRight, KeyCode.RIGHT, KCondition.SHIFT);
        map(Action.SELECT_UP, this::selectUp, KeyCode.UP, KCondition.SHIFT);
        map(Action.SELECT_DOWN, this::selectDown, KeyCode.DOWN, KCondition.SHIFT);
        map(Action.SELECT_PAGE_UP, this::selectPageUp, KeyCode.PAGE_UP, KCondition.SHIFT);
        map(Action.SELECT_PAGE_DOWN, this::selectPageDown, KeyCode.PAGE_DOWN, KCondition.SHIFT);
        map(Action.SELECT_DOCUMENT_START, this::selectDocumentStart, KeyCode.HOME, KCondition.SHIFT, KCondition.CTRL, KCondition.NOT_MAC);
        map(Action.SELECT_DOCUMENT_START, this::selectDocumentStart, KeyCode.UP, KCondition.SHIFT, KCondition.SHORTCUT, KCondition.MAC);
        map(Action.SELECT_DOCUMENT_END, this::selectDocumentEnd, KeyCode.END, KCondition.SHIFT, KCondition.CTRL, KCondition.NOT_MAC);
        map(Action.SELECT_DOCUMENT_END, this::selectDocumentEnd, KeyCode.DOWN, KCondition.SHIFT, KCondition.SHORTCUT, KCondition.MAC);
        map(Action.SELECT_LINE, this::selectLine);
        map(Action.SELECT_WORD, this::selectWord);

        this.textChangeListener = new StyledTextModel.ChangeListener() {
            @Override
            public void eventTextUpdated(TextPos start, TextPos end, int top, int ins, int btm) {
                handleTextUpdated(start, end, top, ins, btm);
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

    protected void handleModel(Object src, StyledTextModel old, StyledTextModel m) {
        if (old != null) {
            old.removeChangeListener(textChangeListener);
        }

        if (m != null) {
            m.addChangeListener(textChangeListener);
        }
    }

    // TODO possibly move to the inputMap
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
            ev.consume();
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

        TextPos ca = control.getCaretPosition();
        if(ca != null) {
            TextPos an = control.getAnchorPosition();
            if(an == null) {
                an = ca;
            }
            m.replace(an, ca, character);

            TextPos p = TextPos.min(an, ca);
            TextPos p2 = new TextPos(p.lineIndex(), p.charIndex() + character.length(), p.leading());
            control.moveCaret(p2, false);
        }
    }

    protected void handleMouseClicked(MouseEvent ev) {
        if (ev.getButton() == MouseButton.PRIMARY) {
            int clicks = ev.getClickCount();
            switch (clicks) {
            case 2:
                control.selectWord();
                break;
            case 3:
                control.selectLine();
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

        TextPos pos = getTextPosition(ev);
        if (pos == null) {
            return;
        }

        vflow().setSuppressBlink(true);

        if (ev.isShiftDown()) {
            // expand selection from the anchor point to the current position
            // clearing existing (possibly multiple) selection
            control.extendSelection(pos);
        } else {
            control.select(pos, pos);
        }

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

        TextPos pos = getTextPosition(ev);
        control.extendSelection(pos);
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

    protected TextPos getTextPosition(MouseEvent ev) {
        double x = ev.getScreenX();
        double y = ev.getScreenY();
        return getTextPosition(x, y);
    }

    protected TextPos getTextPosition(double screenX, double screenY) {
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
        if (autoScrollUp) {
            delta = -delta;
        }
        vflow().blockScroll(delta);

        double x = 0.0;
        double y;
        if (autoScrollUp) {
            y = 0.0;
        } else {
            y = vflow().getViewHeight();
        }

        vflow().scrollToVisible(x, y);

        TextPos p = getTextPos(x, y);
        control.extendSelection(p);
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
        TextPos p = control.getCaretPosition();
        if (p != null) {
            TextPos p2 = new TextPos(p.lineIndex(), 0, true);
            control.moveCaret(p2, false);
            clearPhantomX();
        }
    }
    
    public void moveEnd() {
        TextPos p = control.getCaretPosition();
        if (p != null) {
            String s = getPlainText(p.lineIndex());
            int len = (s == null ? 0 : s.length());
            TextPos p2 = new TextPos(p.lineIndex(), len, false);
            control.moveCaret(p2, false);
            clearPhantomX();
        }
    }

    public void moveUp() {
        moveLine(-1.0, false); // TODO line spacing
    }

    public void moveDown() {
        moveLine(1.0, false); // TODO line spacing
    }
    
    /**
     * Moves the caret to before the first character of the text, also clearing the selection.
     */
    public void moveDocumentStart() {
        control.select(TextPos.ZERO);
    }

    /**
     * Moves the caret to after the last character of the text, also clearing the selection.
     */
    public void moveDocumentEnd() {
        TextPos pos = getEndOfDocument();
        if (pos != null) {
            control.select(pos);
        }
    }

    /** returns TextPos at the end of the document, or null if no document is present */
    private TextPos getEndOfDocument() {
        int line = control.getParagraphCount();
        if (line > 0) {
            --line;
            String text = getPlainText(line);
            int cix = (text == null) ? 0 : text.length();
            return new TextPos(line, cix, false);
        }
        return null;
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

        control.moveCaret(p, extendSelection);
    }

    protected void nextCharacterVisually(boolean moveRight, boolean extendSelection) {
        clearPhantomX();

        TextPos caret = control.getCaretPosition();
        if (caret == null) {
            return; // TODO
        }

        if (isRTL()) {
            moveRight = !moveRight;
        }

        TextCell cell = vflow().getCell(caret.lineIndex());
        int cix = caret.charIndex();
        if (moveRight) {
            cix++;
            if (cix >= cell.getTextLength()) {
                int line = cell.getLineIndex() + 1;
                if (line < vflow().lineCount()) {
                    // next line
                    TextPos pos = new TextPos(line, 0, true);
                    control.moveCaret(pos, extendSelection);
                }
                return;
            }
        } else {
            if (caret.charIndex() == 0) {
                int line = cell.getLineIndex() - 1;
                if (line >= 0) {
                    // prev line
                    TextCell prevCell = vflow().getCell(line);
                    cix = Math.max(0, prevCell.getTextLength() - 1);
                    TextPos pos = new TextPos(line, cix, false);
                    control.moveCaret(pos, extendSelection);
                }
                return;
            }
        }

        // using default locale, same as TextInputControl.backward() for example
        BreakIterator br = BreakIterator.getCharacterInstance();
        String text = getPlainText(cell.getLineIndex());
        br.setText(text);
        int off = caret.charIndex();
        int ix = moveRight ? br.following(off) : br.preceding(off);
        if (ix == BreakIterator.DONE) {
            System.err.println(" --- SHOULD NOT HAPPEN: BreakIterator.DONE off=" + off); // FIX
            return;
        }

        TextPos pos = new TextPos(caret.lineIndex(), ix, caret.leading());
        control.moveCaret(pos, extendSelection);
        return;
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
    
    public void selectAll() {
        StyledTextModel m = control.getModel();
        if(m != null) {
            int ix = m.getParagraphCount() - 1;
            if (ix >= 0) {
                // TODO create a method (getLastTextPos)
                // TODO add a special END_OF_DOCUMENT marker?
                String text = m.getPlainText(ix);
                int cix = (text == null ? 0 : Math.max(0, text.length() - 1));
                TextPos end = new TextPos(ix, cix, false);
                control.select(TextPos.ZERO, end);
                clearPhantomX();
            }
        }
    }

    /** selects from the anchor position to the document start */
    public void selectDocumentStart() {
        control.extendSelection(TextPos.ZERO);
    }

    /** selects from the anchor position to the document end */
    public void selectDocumentEnd() {
        TextPos pos = getEndOfDocument();
        if (pos != null) {
            control.extendSelection(pos);
        }
    }
    
    public void selectWord() {
        // TODO
    }
    
    public void selectLine() {
        TextPos p = control.getCaretPosition();
        if(p != null) {
            int ix = p.lineIndex();
            TextPos an = new TextPos(ix, 0, true);
            TextPos ca = new TextPos(ix + 1, 0, true);
            control.select(an, ca);
        }
    }

    protected void handleTextUpdated(TextPos start, TextPos end, int addedTop, int linesAdded, int addedBottom) {
        vflow().handleTextUpdated(start, end, addedTop, linesAdded, addedBottom);
    }
}
