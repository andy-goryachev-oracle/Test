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

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;

/**
 * RichTextArea Behavior.
 *
 * BehaviorBase and InputMap are not public, so had to invent my own.
 */
public class RichTextAreaBehavior {
    private final RichTextAreaSkin skin;
    private final RichTextArea control;
    private final InputMap2 inputMap;
    private final EventHandler<KeyEvent> keyHandler;
    private final Timeline autoScrollTimer;
    private boolean autoScrollUp;
    private boolean fastAutoScroll;
    private double phantomX = -1.0;
    private static final Duration autoScrollPeriod = Duration.millis(Config.autoScrollPeriod);

    public RichTextAreaBehavior(RichTextAreaSkin skin) {
        this.skin = skin;
        this.control = skin.getSkinnable();
        
        this.inputMap = createInputMap();
        this.keyHandler = this::handleKeyEvent;
        
        autoScrollTimer = new Timeline(new KeyFrame(autoScrollPeriod, (ev) -> {
            autoScroll();
        }));
        autoScrollTimer.setCycleCount(Timeline.INDEFINITE);
    }

    // TODO alternatively, can expose addKeyBinding() and removeKeyBinding(),
    // or better make InputMap and KeyBinding2 public
    protected InputMap2 createInputMap() {
        InputMap2 m = new InputMap2();
        m.add(this::moveLeft, KeyCode.LEFT);
        m.add(this::moveRight, KeyCode.RIGHT);
        m.add(this::moveUp, KeyCode.UP);
        m.add(this::moveDown, KeyCode.DOWN);
        m.add(this::moveHome, KeyCode.HOME);
        m.add(this::moveEnd, KeyCode.END);
        m.add(this::pageDown, KeyCode.PAGE_DOWN);
        m.add(this::pageUp, KeyCode.PAGE_UP);
        m.add(this::selectAll, KeyCode.A, InputMap2.Modifier.SHORTCUT);
        return m;
    }

    public void install() {
        VFlow f = vflow();
        f.addEventFilter(MouseEvent.MOUSE_CLICKED, this::handleMouseClicked);
        f.addEventFilter(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);
        f.addEventFilter(MouseEvent.MOUSE_RELEASED, this::handleMouseReleased);
        f.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::handleMouseDragged);
        f.addEventFilter(ScrollEvent.ANY, this::handleScrollEvent);

        control.addEventHandler(KeyEvent.ANY, keyHandler);
    }

    public void dispose() {
        control.removeEventHandler(KeyEvent.ANY, keyHandler);
    }

    protected VFlow vflow() {
        return skin.getVFlow();
    }

    /** accepting VFlow coordinates */
    protected TextPos getTextPos(double localX, double localY) {
        return vflow().getTextPos(localX, localY);
    }

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

    public void handleKeyEvent(KeyEvent ev) {
        if (ev == null || ev.isConsumed()) {
            return;
        }

        KeyBinding2 k = KeyBinding2.from(ev);
        if (k != null) {
            // this should return an FxAction which can be disabled
            Runnable r = inputMap.getAction(k);
            if (r != null) {
                vflow().setSuppressBlink(true);
                r.run();
                vflow().setSuppressBlink(false);
                ev.consume();
            }
        }
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
            sm.clearAndExtendLastSegment(m);
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
        vflow().setSuppressBlink(false);
        vflow().scrollCaretToVisible();
        phantomX = -1.0;
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
    
    protected String getText(int modelIndex) {
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

    public void pageUp() {
        moveLine(-vflow().getViewHeight());
    }

    public void pageDown() {
        moveLine(vflow().getViewHeight());
    }
    
    public void moveRight() {
        nextCharacterVisually(true);
    }

    public void moveLeft() {
        nextCharacterVisually(false);
    }
    
    public void moveHome() {
        TextPos p = getCaret();
        if (p != null) {
            TextPos p2 = new TextPos(p.lineIndex(), 0, true);
            vflow().moveCaret(p2, false);
            phantomX = -1.0;
        }
    }
    
    public void moveEnd() {
        TextPos p = getCaret();
        if (p != null) {
            String s = getText(p.lineIndex());
            int len = (s == null ? 0 : s.length());
            TextPos p2 = new TextPos(p.lineIndex(), len, false);
            vflow().moveCaret(p2, false);
            phantomX = -1.0;
        }
    }
    
    public void moveUp() {
        moveLine(-1.0); // TODO line spacing
    }
    
    public void moveDown() {
        moveLine(1.0); // TODO line spacing
    }
    
    public void selectAll() {
        StyledTextModel m = control.getModel();
        if(m != null) {
            int ix = m.getParagraphCount() - 1;
            if (ix >= 0) {
                String text = m.getPlainText(ix);
                int cix = (text == null ? 0 : Math.max(0, text.length() - 1));
                Marker end = control.newMarker(ix, cix, false);
                control.getSelectionModel().setSelection(Marker.ZERO, end);
                phantomX = -1.0;
            }
        }
    }
    
    protected void moveLine(double delta) {
        CaretInfo c = vflow().getCaretInfo();
        double x = c.x();
        double y = (delta < 0) ? c.y0() + delta : c.y1() + delta;
        
        if(phantomX < 0) {
            phantomX = x;
        } else {
            x = phantomX;
        }
        
        TextPos p = getTextPos(x, y);
        if(p == null) {
            // TODO check
            return;
        }

        vflow().moveCaret(p, false);
    }

    protected void nextCharacterVisually(boolean moveRight) {
        // TODO
        System.err.println("nextCharacterVisually " + moveRight); // FIX
    }
}
