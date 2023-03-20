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
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Screen;
import javafx.util.Duration;
import goryachev.rich.RichTextArea.Cmd;
import goryachev.rich.model.DataFormatHandler;
import goryachev.rich.model.StyledInput;
import goryachev.rich.model.StyledOutput;
import goryachev.rich.model.StyledTextModel;
import goryachev.rich.util.BehaviorBase2;
import goryachev.rich.util.KCondition;
import goryachev.rich.util.KeyBinding2;
import goryachev.rich.util.NewAPI;
import goryachev.rich.util.Util;

/**
 * RichTextArea Behavior.
 *
 * BehaviorBase and InputMap are not public, so had to invent my own.
 * 
 * Behavior:
 * - maps key bindings to action tags
 * - maps action tags to functions (default implementation)
 * - function: provides default implementation
 * Control:
 * - allows for re-mapping of an action tag to an alternative implementation
 * - executes code associated with an action tag (default or alternative)
 * - TODO manages Action instances upon demand
 * - TODO allows for restoring the default mapping
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
    private ContextMenu contextMenu = new ContextMenu();

    public RichTextAreaBehavior(RichTextAreaSkin skin) {
        this.skin = skin;
        this.control = skin.getSkinnable();
        this.keyHandler = this::handleKeyEvent;
        this.modelListener = this::handleModel;

        map(Cmd.BACKSPACE, this::backspace, KeyCode.BACK_SPACE);
        map(Cmd.COPY, this::copy, KeyCode.C, KCondition.SHORTCUT);
        map(Cmd.CUT, this::cut, KeyCode.X, KCondition.SHORTCUT);
        map(Cmd.DELETE, this::delete, KeyCode.DELETE);
        map(Cmd.INSERT_LINE_BREAK, this::insertLineBreak, KeyCode.ENTER);
        map(Cmd.INSERT_TAB, this::insertTab, KeyCode.TAB);
        map(Cmd.MOVE_LEFT, this::moveLeft, KeyCode.LEFT);
        map(Cmd.MOVE_RIGHT, this::moveRight, KeyCode.RIGHT);
        map(Cmd.MOVE_UP, this::moveUp, KeyCode.UP);
        map(Cmd.MOVE_DOWN, this::moveDown, KeyCode.DOWN);
        map(Cmd.MOVE_HOME, this::moveHome, KeyCode.HOME);
        map(Cmd.MOVE_END, this::moveEnd, KeyCode.END);
        map(Cmd.MOVE_DOCUMENT_START, this::moveDocumentStart);
        map(Cmd.MOVE_DOCUMENT_START, KeyCode.HOME, KCondition.CTRL, KCondition.NOT_MAC);
        map(Cmd.MOVE_DOCUMENT_START, KeyCode.UP, KCondition.SHORTCUT, KCondition.MAC);
        map(Cmd.MOVE_DOCUMENT_END, this::moveDocumentEnd);
        map(Cmd.MOVE_DOCUMENT_END, KeyCode.END, KCondition.CTRL, KCondition.NOT_MAC);
        map(Cmd.MOVE_DOCUMENT_END, KeyCode.DOWN, KCondition.SHORTCUT, KCondition.MAC);
        map(Cmd.MOVE_WORD_NEXT, this::nextWord);
        map(Cmd.MOVE_WORD_NEXT, KeyCode.RIGHT, KCondition.CTRL, KCondition.NOT_MAC);
        map(Cmd.MOVE_WORD_NEXT, KeyCode.RIGHT, KCondition.OPTION, KCondition.MAC);
        map(Cmd.MOVE_WORD_NEXT_END, this::endOfNextWord);
        map(Cmd.MOVE_WORD_PREVIOUS, this::previousWord);
        map(Cmd.MOVE_WORD_PREVIOUS, KeyCode.LEFT, KCondition.CTRL, KCondition.NOT_MAC);
        map(Cmd.MOVE_WORD_PREVIOUS, KeyCode.LEFT, KCondition.OPTION, KCondition.MAC);
        map(Cmd.PAGE_DOWN, this::pageDown, KeyCode.PAGE_DOWN);
        map(Cmd.PAGE_UP, this::pageUp, KeyCode.PAGE_UP);
        map(Cmd.PASTE, this::paste, KeyCode.V, KCondition.SHORTCUT);
        map(Cmd.SELECT_ALL, this::selectAll, KeyCode.A, KCondition.SHORTCUT);
        map(Cmd.SELECT_LEFT, this::selectLeft, KeyCode.LEFT, KCondition.SHIFT);
        map(Cmd.SELECT_RIGHT, this::selectRight, KeyCode.RIGHT, KCondition.SHIFT);
        map(Cmd.SELECT_UP, this::selectUp, KeyCode.UP, KCondition.SHIFT);
        map(Cmd.SELECT_DOWN, this::selectDown, KeyCode.DOWN, KCondition.SHIFT);
        map(Cmd.SELECT_PAGE_UP, this::selectPageUp, KeyCode.PAGE_UP, KCondition.SHIFT);
        map(Cmd.SELECT_PAGE_DOWN, this::selectPageDown, KeyCode.PAGE_DOWN, KCondition.SHIFT);
        map(Cmd.SELECT_DOCUMENT_START, this::selectDocumentStart, KeyCode.HOME, KCondition.SHIFT, KCondition.CTRL, KCondition.NOT_MAC);
        map(Cmd.SELECT_DOCUMENT_START, this::selectDocumentStart, KeyCode.UP, KCondition.SHIFT, KCondition.SHORTCUT, KCondition.MAC);
        map(Cmd.SELECT_DOCUMENT_END, this::selectDocumentEnd, KeyCode.END, KCondition.SHIFT, KCondition.CTRL, KCondition.NOT_MAC);
        map(Cmd.SELECT_DOCUMENT_END, this::selectDocumentEnd, KeyCode.DOWN, KCondition.SHIFT, KCondition.SHORTCUT, KCondition.MAC);
        map(Cmd.SELECT_LINE, this::selectLine);
        map(Cmd.SELECT_WORD, this::selectWord);
        map(Cmd.SELECT_WORD_NEXT, this::selectNextWord);
        map(Cmd.SELECT_WORD_NEXT_END, this::selectEndOfNextWord);
        map(Cmd.SELECT_WORD_PREVIOUS, this::selectPreviousWord);

        textChangeListener = new StyledTextModel.ChangeListener() {
            @Override
            public void eventTextUpdated(TextPos start, TextPos end, int top, int ins, int btm) {
                handleTextUpdated(start, end, top, ins, btm);
            }

            @Override
            public void eventStyleUpdated(TextPos start, TextPos end) {
                handleStyleUpdated(start, end);
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
        
        // TODO there is no way to override the default behavior, such as clear selection or select word under cursor
        control.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, this::contextMenuRequested);

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

    /** returns text length.  the caller must ensure the index is valid. */
    protected int getTextLength(int index) {
        String text = getPlainText(index);
        return text == null ? 0 : text.length();
    }
    
    protected String getPlainText(int modelIndex) {
        StyledTextModel m = control.getModel();
        return (m == null) ? null : m.getPlainText(modelIndex);
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
        //System.out.println("handleKeyEvent: " + ev); // FIX
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
        
        String character = getValidKeyTyped(ev);
        if (character != null) {
            vflow().setSuppressBlink(true);
            handleKeyTyped(character);
            vflow().setSuppressBlink(false);
            ev.consume();
        }
    }

    protected String getValidKeyTyped(KeyEvent ev) {
        if (ev.getEventType() == KeyEvent.KEY_TYPED) {
            String ch = ev.getCharacter();
            if (ch.length() > 0) {
                // see TextInputControlBehavior:395
                // Filter out control keys except control+Alt on PC or Alt on Mac
                if (ev.isControlDown() || ev.isAltDown() || (Util.isMac() && ev.isMetaDown())) {
                    if (!((ev.isControlDown() || Util.isMac()) && ev.isAltDown()))
                        return null;
                }

                // Ignore characters in the control range and the ASCII delete
                // character as well as meta key presses
                if (ch.charAt(0) > 0x1F && ch.charAt(0) != 0x7F && !ev.isMetaDown()) {
                    // Not sure about this one (original comment, not sure about it either)
                    return ch;
                }
            }
        }
        return null;
    }

    /** returns true if both control and model are editable */
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
        TextPos ca = control.getCaretPosition();
        if(ca != null) {
            TextPos an = control.getAnchorPosition();
            if(an == null) {
                an = ca;
            }
            m.replace(an, ca, character);

            TextPos p = TextPos.min(an, ca);
            TextPos p2 = new TextPos(p.index(), p.offset() + character.length());
            control.moveCaret(p2, false);
            clearPhantomX();
        }
    }
    
    public void insertTab() {
        handleKeyTyped("\t");
    }

    public void insertLineBreak() {
        if (!isEditable()) {
            return;
        }

        StyledTextModel m = control.getModel();
        TextPos pos = control.getCaretPosition();
        if(pos != null) {
            TextPos an = control.getAnchorPosition();
            TextPos p2 = m.replace(an, pos, StyledInput.of("\n", null, null));
            control.moveCaret(p2, false);
            clearPhantomX();
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
        if (ev.isPopupTrigger() || (ev.getButton() != MouseButton.PRIMARY)) {
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

        if (contextMenu.isShowing()) {
            contextMenu.hide();
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
            if (!control.isWrapText()) {
                // horizontal scroll
                double f = Config.scrollWheelBlockSizeHorizontal;
                if (ev.getDeltaX() >= 0) {
                    f = -f;
                }
                vflow().hscroll(f);
                ev.consume();
            }
        } else if (ev.isShortcutDown()) {
            // page up / page down
            if (ev.getDeltaY() >= 0) {
                vflow().pageUp();
            } else {
                vflow().pageDown();
            }
            ev.consume();
        } else {
            // block scroll
            double f = Config.scrollWheelBlockSizeVertical;
            if (ev.getDeltaY() >= 0) {
                f = -f;
            }
            vflow().scroll(f);
            ev.consume();
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
        moveCharacter(true, false);
    }

    public void moveLeft() {
        moveCharacter(false, false);
    }
    
    public void moveHome() {
        TextPos p = control.getCaretPosition();
        if (p != null) {
            TextPos p2 = new TextPos(p.index(), 0);
            control.moveCaret(p2, false);
            clearPhantomX();
        }
    }
    
    public void moveEnd() {
        TextPos p = control.getCaretPosition();
        if (p != null) {
            String s = getPlainText(p.index());
            int len = (s == null ? 0 : s.length());
            TextPos p2 = new TextPos(p.index(), len);
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
            return new TextPos(line, cix);
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

    protected void moveCharacter(boolean moveRight, boolean extendSelection) {
        TextPos caret = control.getCaretPosition();
        if (caret == null) {
            return;
        }

        clearPhantomX();

        if(!extendSelection) {
            TextPos ca = control.getCaretPosition();
            TextPos an = control.getAnchorPosition();
            int d = ca.compareTo(an);
            // jump over selection if it exists
            if (d < 0) {
                control.moveCaret(moveRight ? an : ca, extendSelection);
                return;
            } else if(d > 0) {
                control.moveCaret(moveRight ? ca : an, extendSelection);
                return;
            }
        }

        TextPos p = nextCharacterVisually(caret, moveRight);
        if (p != null) {
            control.moveCaret(p, extendSelection);
        }
    }
    
    @Deprecated // FIX remove
    protected void moveWord(boolean moveRight, boolean extendSelection) {
        // TODO
//        TextPos caret = control.getCaretPosition();
//        if (caret == null) {
//            return;
//        }
//
//        clearPhantomX();
//
//        if(!extendSelection) {
//            TextPos ca = control.getCaretPosition();
//            TextPos an = control.getAnchorPosition();
//            int d = ca.compareTo(an);
//            // jump over selection if it exists
//            if (d < 0) {
//                control.moveCaret(moveRight ? an : ca, extendSelection);
//                return;
//            } else if(d > 0) {
//                control.moveCaret(moveRight ? ca : an, extendSelection);
//                return;
//            }
//        }
//
//        TextPos p = moveWord2(caret, moveRight);
//        if (p != null) {
//            control.moveCaret(p, extendSelection);
//        }
    }
    
    protected TextPos moveWord2(TextPos start, boolean moveRight) {
        if (isRTL()) {
            moveRight = !moveRight;
        }

        TextCell cell = vflow().getCell(start.index());
        int cix = start.offset();
        if (moveRight) {
            cix++;
            if (cix > cell.getTextLength()) {
                int line = cell.getLineIndex() + 1;
                TextPos p;
                if (line < vflow().lineCount()) {
                    // next line
                    p = new TextPos(line, 0);
                } else {
                    // end of last paragraph w/o newline
                    p = new TextPos(cell.getLineIndex(), cell.getTextLength());
                }
                return p;
            }
        } else {
            if (start.offset() == 0) {
                int line = cell.getLineIndex() - 1;
                if (line >= 0) {
                    // prev line
                    TextCell prevCell = vflow().getCell(line);
                    cix = prevCell.getTextLength();
                    return new TextPos(line, cix);
                }
                return null;
            }
        }

        // using default locale, same as TextInputControl.backward() for example
        BreakIterator br = BreakIterator.getCharacterInstance();
        String text = getPlainText(cell.getLineIndex());
        br.setText(text);
        int off = start.offset();
        try {
            int ix = moveRight ? br.following(off) : br.preceding(off);
            if (ix == BreakIterator.DONE) {
                System.err.println(" --- SHOULD NOT HAPPEN: BreakIterator.DONE off=" + off); // FIX
                return null;
            }
            return new TextPos(start.index(), ix);
        } catch(Exception e) {
            // TODO need to use a logger!
            System.err.println("offset=" + off + " text=[" + text + "]");
            e.printStackTrace();
            return null;
        }
    }

    protected TextPos nextCharacterVisually(TextPos start, boolean moveRight) {
        if (isRTL()) {
            moveRight = !moveRight;
        }

        TextCell cell = vflow().getCell(start.index());
        int cix = start.offset();
        if (moveRight) {
            cix++;
            if (cix > cell.getTextLength()) {
                int line = cell.getLineIndex() + 1;
                TextPos p;
                if (line < vflow().lineCount()) {
                    // next line
                    p = new TextPos(line, 0);
                } else {
                    // end of last paragraph w/o newline
                    p = new TextPos(cell.getLineIndex(), cell.getTextLength());
                }
                return p;
            }
        } else {
            if (start.offset() == 0) {
                int line = cell.getLineIndex() - 1;
                if (line >= 0) {
                    // prev line
                    TextCell prevCell = vflow().getCell(line);
                    cix = prevCell.getTextLength();
                    return new TextPos(line, cix);
                }
                return null;
            }
        }

        // using default locale, same as TextInputControl.backward() for example
        BreakIterator br = BreakIterator.getCharacterInstance();
        String text = getPlainText(cell.getLineIndex());
        br.setText(text);
        int off = start.offset();
        try {
            int ix = moveRight ? br.following(off) : br.preceding(off);
            if (ix == BreakIterator.DONE) {
                System.err.println(" --- SHOULD NOT HAPPEN: BreakIterator.DONE off=" + off); // FIX
                return null;
            }
            return new TextPos(start.index(), ix);
        } catch(Exception e) {
            // TODO need to use a logger!
            System.err.println("offset=" + off + " text=[" + text + "]");
            e.printStackTrace();
            return null;
        }
    }

    public void clearPhantomX() {
        phantomX = -1.0;
    }
    
    public void selectLeft() {
        moveCharacter(false, true);
    }
    
    public void selectRight() {
        moveCharacter(true, true);
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
                int off = getTextLength(ix);
                TextPos end = new TextPos(ix, off);
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
        TextPos caret = control.getCaretPosition();
        if(caret == null) {
            return;
        }

        int index = caret.index();
        String text = getPlainText(index);
        if(text == null) {
            return;
        }
        
        // using default locale, same as TextInputControl.backward() for example
        BreakIterator br = BreakIterator.getWordInstance();
        br.setText(text);
        int off = caret.offset();
        try {
            int off0 = br.preceding(off);
            if (off0 == BreakIterator.DONE) {
                System.err.println(" --- no previous word off=" + off); // FIX
                return;
            }
            
            int off1 = br.following(off);
            if (off1 == BreakIterator.DONE) {
                System.err.println(" --- no following word off=" + off); // FIX
                return;
            }
            
            TextPos p0 = new TextPos(index, off0);
            TextPos p1 = new TextPos(index, off1);
            control.select(p0, p1);
        } catch(Exception e) {
            // TODO need to use a logger!
            System.err.println("offset=" + off + " text=[" + text + "]");
            e.printStackTrace();
        }
    }
    
    public void selectWord2() {
        control.previousWord();
        if (Util.isWindows()) {
            control.selectNextWord();
        } else {
            control.selectEndOfNextWord();
        }
    }

    public void selectLine() {
        TextPos p = control.getCaretPosition();
        if (p != null) {
            int ix = p.index();
            TextPos an = new TextPos(ix, 0);
            TextPos ca = new TextPos(ix + 1, 0);
            control.select(an, ca);
        }
    }

    protected void handleTextUpdated(TextPos start, TextPos end, int addedTop, int linesAdded, int addedBottom) {
        vflow().handleTextUpdated(start, end, addedTop, linesAdded, addedBottom);
    }
    
    protected void handleStyleUpdated(TextPos start, TextPos end) {
        vflow().handleStyleUpdated(start, end);
    }

    public void backspace() {
        if (!isEditable()) {
            return;
        }

        if (hasSelection()) {
            deleteSelection();
        } else {
            TextPos end = control.getCaretPosition();
            if (end == null) {
                return;
            }

            TextPos start = nextCharacterVisually(end, false);
            if (start != null) {
                control.getModel().replace(start, end, "");
                control.moveCaret(start, false);
                clearPhantomX();
            }
        }
    }

    public void delete() {
        if (!isEditable()) {
            return;
        }

        if (hasSelection()) {
            deleteSelection();
        } else {
            TextPos start = control.getCaretPosition();
            TextPos end = nextCharacterVisually(start, true);
            if (end != null) {
                control.getModel().replace(start, end, "");
                control.moveCaret(start, false);
                clearPhantomX();
            }
        }
    }

    // TODO move to control?
    protected boolean hasSelection() {
        TextPos ca = control.getCaretPosition();
        if (ca != null) {
            TextPos an = control.getAnchorPosition();
            if (an != null) {
                return !ca.isSameIndexAndOffset(an);
            }
        }
        return false;
    }

    protected void deleteSelection() {
        TextPos start = control.getCaretPosition();
        TextPos end = control.getAnchorPosition();
        if (start.compareTo(end) > 0) {
            TextPos p = start;
            start = end;
            end = p;
        }
        control.getModel().replace(start, end, "");
        control.moveCaret(start, false);
        clearPhantomX();
    }

    // see TextAreaBehavior:338
    public void contextMenuRequested(ContextMenuEvent ev) {
        if (contextMenu.isShowing()) {
            contextMenu.hide();
        } else if (control.getContextMenu() == null && control.getOnContextMenuRequested() == null) {
            double screenX = ev.getScreenX();
            double screenY = ev.getScreenY();
            double sceneX = ev.getSceneX();

            if (NewAPI.isTouchSupported()) {
                /* TODO
                Point2D menuPos;
                if (control.getSelection().getLength() == 0) {
                    skin.positionCaret(skin.getIndex(ev.getX(), ev.getY()), false);
                    menuPos = skin.getMenuPosition();
                } else {
                    menuPos = skin.getMenuPosition();
                    if (menuPos != null && (menuPos.getX() <= 0 || menuPos.getY() <= 0)) {
                        skin.positionCaret(skin.getIndex(ev.getX(), ev.getY()), false);
                        menuPos = skin.getMenuPosition();
                    }
                }

                if (menuPos != null) {
                    Point2D p = control.localToScene(menuPos);
                    Scene scene = control.getScene();
                    Window window = scene.getWindow();
                    Point2D location = new Point2D(window.getX() + scene.getX() + p.getX(),
                        window.getY() + scene.getY() + p.getY());
                    screenX = location.getX();
                    sceneX = p.getX();
                    screenY = location.getY();
                }
                */
            }

            populateContextMenu();

            double menuWidth = contextMenu.prefWidth(-1);
            double menuX = screenX - (NewAPI.isTouchSupported() ? (menuWidth / 2) : 0);
            Screen currentScreen = NewAPI.getScreenForPoint(screenX, 0);
            Rectangle2D bounds = currentScreen.getBounds();

            // what is this??
            if (menuX < bounds.getMinX()) {
                control.getProperties().put("CONTEXT_MENU_SCREEN_X", screenX);
                control.getProperties().put("CONTEXT_MENU_SCENE_X", sceneX);
                contextMenu.show(control, bounds.getMinX(), screenY);
            } else if (screenX + menuWidth > bounds.getMaxX()) {
                double leftOver = menuWidth - (bounds.getMaxX() - screenX);
                control.getProperties().put("CONTEXT_MENU_SCREEN_X", screenX);
                control.getProperties().put("CONTEXT_MENU_SCENE_X", sceneX);
                contextMenu.show(control, screenX - leftOver, screenY);
            } else {
                control.getProperties().put("CONTEXT_MENU_SCREEN_X", 0);
                control.getProperties().put("CONTEXT_MENU_SCENE_X", 0);
                contextMenu.show(control, menuX, screenY);
            }
        }

        ev.consume();
    }

    protected void populateContextMenu() {
        boolean sel = hasSelection();
        boolean paste = (findFormatForPaste() != null);
        
        ObservableList<MenuItem> items = contextMenu.getItems();
        items.clear();

        MenuItem m;
        items.add(m = new MenuItem("Undo"));
        m.setOnAction((ev) -> control.undo());
        m.setDisable(!control.isUndoable());

        items.add(m = new MenuItem("Redo"));
        m.setOnAction((ev) -> control.redo());
        m.setDisable(!control.isRedoable());

        items.add(new SeparatorMenuItem());

        items.add(m = new MenuItem("Cut"));
        m.setOnAction((ev) -> control.cut());
        m.setDisable(!sel);

        items.add(m = new MenuItem("Copy"));
        m.setOnAction((ev) -> control.copy());
        m.setDisable(!sel);

        items.add(m = new MenuItem("Paste"));
        m.setOnAction((ev) -> control.paste());
        m.setDisable(!paste);

        items.add(new SeparatorMenuItem());

        items.add(m = new MenuItem("Select All"));
        m.setOnAction((ev) -> control.selectAll());
    }

    public void copy() {
        copy(false);
    }

    public void cut() {
        copy(true);
    }

    public void paste() {
        DataFormat f = findFormatForPaste();
        if (f != null) {
            if (hasSelection()) {
                deleteSelection();
            }

            Object src = Clipboard.getSystemClipboard().getContent(f);
            TextPos caret = control.getCaretPosition();
            TextPos anchor = control.getAnchorPosition();
            StyledTextModel m = control.getModel();
            DataFormatHandler h = m.getDataFormatHandler(f);
            StyledInput in = h.getStyledInput(src);
            TextPos p = m.replace(caret, anchor, in);
            control.moveCaret(p, false);
        }
    }

    /** 
     * returns a format that can be imported by a model, based on the clipboard content and model being editable.
     */
    protected DataFormat findFormatForPaste() {
        if (isEditable()) {
            StyledTextModel m = control.getModel();
            DataFormat[] fs = m.getSupportedDataFormats();
            if (fs.length > 0) {
                for (DataFormat f : fs) {
                    if (Clipboard.getSystemClipboard().hasContent(f)) {
                        return f;
                    }
                }
            }
        }
        return null;
    }

    protected void copy(boolean cut) {
        if (hasSelection()) {
            StyledTextModel m = control.getModel();
            DataFormat[] fs = m.getSupportedDataFormats();
            if (fs.length > 0) {
                TextPos start = control.getAnchorPosition();
                TextPos end = control.getCaretPosition();
                if (start.compareTo(end) > 0) {
                    TextPos p = start;
                    start = end;
                    end = p;
                }

                try {
                    ClipboardContent c = new ClipboardContent();
                    for (DataFormat f : fs) {
                        DataFormatHandler h = m.getDataFormatHandler(f);
                        // problem: styled output depends on the export options
                        StyledOutput out = h.getStyledOutput(null);
                        m.exportText(start, end, out);
                        Object v = out.getOutput();
                        if (v != null) {
                            c.put(f, v);
                        }
                    }
                    Clipboard.getSystemClipboard().setContent(c);
    
                    if (isEditable() && cut) {
                        deleteSelection();
                    }
                } catch(Exception | OutOfMemoryError e) {
                    // TODO log exception
                    NewAPI.provideErrorFeedback(control);
                }
            }
        }
    }

    /**
     * Moves the caret to the beginning of previous word. This function
     * also has the effect of clearing the selection.
     */
    public void previousWord() {
        previousWord(false);
    }

    /**
     * Moves the caret to the beginning of next word. This function
     * also has the effect of clearing the selection.
     */
    public void nextWord() {
        nextWord(false);
    }

    /**
     * Moves the caret to the end of the next word. This function
     * also has the effect of clearing the selection.
     */
    public void endOfNextWord() {
        endOfNextWord(false);
    }

    /**
     * Moves the caret to the beginning of previous word. This does not cause
     * the selection to be cleared. Rather, the anchor stays put and the caretPosition is
     * moved to the beginning of previous word.
     */
    public void selectPreviousWord() {
        previousWord(true);
    }

    /**
     * Moves the caret to the beginning of next word. This does not cause
     * the selection to be cleared. Rather, the anchor stays put and the caretPosition is
     * moved to the beginning of next word.
     */
    public void selectNextWord() {
        nextWord(true);
    }

    /**
     * Moves the caret to the end of the next word. This does not cause
     * the selection to be cleared.
     */
    public void selectEndOfNextWord() {
        endOfNextWord(true);
    }

    protected void previousWord(boolean extendSelection) {
        TextPos caret = control.getCaretPosition();
        if (caret != null) {
            clearPhantomX();

            TextPos p = previousWordFrom(caret);
            if (p != null) {
                control.moveCaret(p, extendSelection);
            }
        }
    }
    
    protected void nextWord(boolean extendSelection) {
        TextPos caret = control.getCaretPosition();
        if (caret != null) {
            clearPhantomX();

            TextPos p = nextWordFrom(caret);
            if (p != null) {
                control.moveCaret(p, extendSelection);
            }
        }
    }
    
    protected void endOfNextWord(boolean extendSelection) {
        TextPos caret = control.getCaretPosition();
        if (caret != null) {
            clearPhantomX();

            TextPos p = endOfNextWordFrom(caret);
            if (p != null) {
                control.moveCaret(p, extendSelection);
            }
        }
    }

    protected TextPos previousWordFrom(TextPos caret) {
        int index = caret.index();
        String text = getPlainText(index);
        if ((text == null) || (text.length() == 0)) {
            return null;
        }

        BreakIterator br = BreakIterator.getWordInstance();
        br.setText(text);

        int len = text.length();
        int offset = caret.offset();
        int off = br.preceding(Util.clamp(0, offset, len));

        // Skip the non-word region, then move/select to the beginning of the word.
        while (off != BreakIterator.DONE && !Character.isLetterOrDigit(text.charAt(Util.clamp(0, off, len - 1)))) {
            off = br.preceding(Util.clamp(0, off, len));
        }

        return new TextPos(index, off);
    }

    protected TextPos nextWordFrom(TextPos caret) {
        int index = caret.index();
        String text = getPlainText(index);
        if ((text == null) || (text.length() == 0)) {
            return null;
        }

        BreakIterator br = BreakIterator.getWordInstance();
        br.setText(text);

        int len = text.length();
        int offset = caret.offset();

        int last = br.following(Util.clamp(0, offset, len - 1));
        int current = br.next();

        // Skip whitespace characters to the beginning of next word, but
        // stop at newline. Then move the caret or select a range.
        while (current != BreakIterator.DONE) {
            for (int off = last; off <= current; off++) {
                char ch = text.charAt(Util.clamp(0, off, len - 1));
                // Avoid using Character.isSpaceChar() and Character.isWhitespace(),
                // because they include LINE_SEPARATOR, PARAGRAPH_SEPARATOR, etc.
                if (ch != ' ' && ch != '\t') {
                    return new TextPos(index, off);
                }
            }
            last = current;
            current = br.next();
        }

        return new TextPos(index, len);
    }

    protected TextPos endOfNextWordFrom(TextPos caret) {
        int index = caret.index();
        String text = getPlainText(index);
        if ((text == null) || (text.length() == 0)) {
            return null;
        }

        BreakIterator br = BreakIterator.getWordInstance();
        br.setText(text);

        int textLength = text.length();
        int offset = caret.offset();
        int last = br.following(Util.clamp(0, offset, textLength));
        int current = br.next();

        // skip the non-word region, then move/select to the end of the word.
        while (current != BreakIterator.DONE) {
            for (int p = last; p <= current; p++) {
                if (!Character.isLetterOrDigit(text.charAt(Util.clamp(0, p, textLength - 1)))) {
                    return new TextPos(index, p);
                }
            }
            last = current;
            current = br.next();
        }

        return new TextPos(index, textLength);
    }
}
