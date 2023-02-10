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
 * FIX BehaviorBase and InputMap are not public!
 */
public class RichTextAreaBehavior {
    private final RichTextAreaSkin skin;
    private final RichTextArea control;
    private final InputMap2 inputMap;
    private final EventHandler<KeyEvent> keyHandler;
    private final Timeline autoScrollTimer;
    private boolean autoScrollUp;
    private boolean fastAutoScroll;
    // TODO set phantom x from cursor, add to mouse handler
    private double phantomX = -1.0;
    private static final Duration autoScrollPeriod = Duration.millis(100); // TODO config?
    private static final double fastAutoScrollThreshold  = 100; // arbitrary number TODO config?
    private static final double autoScrollStepFast  = 200; // arbitrary number TODO config?
    private static final double autoStopStepSlow  = 20; // arbitrary number TODO config?

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
    private TextPos getTextPos(double localX, double localY) {
        return vflow().getTextPos(localX, localY);
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
        //control.commitselection TODO
    }

    protected void handleMouseDragged(MouseEvent ev) {
        if (!(ev.getButton() == MouseButton.PRIMARY)) {
            return;
        }

        double y = ev.getY();
        System.err.println("    handleMouseDragged y=" + y); // FIX
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
        System.err.println("    handleMouseDragged pos=" + pos); // FIX
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

    protected void stopAutoScroll() {
        autoScrollTimer.stop();
    }
    
    protected void autoScroll(double delta) {
        autoScrollUp = (delta < 0.0);
        fastAutoScroll = Math.abs(delta) > fastAutoScrollThreshold;
        autoScrollTimer.play();
    }
    
    protected void autoScroll() {
        double delta = fastAutoScroll ? autoScrollStepFast : autoStopStepSlow;
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
        // TODO block scroll - getViewHeight(),
        // TODO then change caret position (use phantom x)
        System.err.println("pageUp"); // FIX
    }

    public void pageDown() {
        // TODO block scroll + getViewHeight(),
        // TODO then change caret position (use phantom x)
        System.err.println("pageDown"); // FIX
    }
    
    public void moveRight() {
        // TODO
        System.err.println("moveRight"); // FIX
    }

    public void moveLeft() {
        // TODO
        System.err.println("moveLeft"); // FIX
    }
    
    public void moveHome() {
        // TODO
        System.err.println("moveHome"); // FIX
    }
    
    public void moveEnd() {
        // TODO
        System.err.println("moveEnd"); // FIX
    }
    
    public void moveUp() {
        // TODO
        System.err.println("moveUp"); // FIX
    }
    
    public void moveDown() {
        // TODO
        System.err.println("moveDown " + control.getCaretPosition()); // FIX
        CaretSize c = vflow().getCaretSize();
        double x = c.x();
        double y = c.y1() + 1; // TODO line spacing
        
        if(phantomX < 0) {
            phantomX = x;
        } else {
            x = phantomX;
        }
        
        TextPos p = getTextPos(x, y);
        System.err.println("    moveDown p=" + p); // FIX
        if(p == null) {
            // TODO check
            return;
        }

        // FIX does not move the caret!
        vflow().moveCaret(p, false);
    }

    public void selectAll() {
        System.err.println("selectAll"); // FIX
        StyledTextModel m = control.getModel();
        if(m != null) {
            int ix = m.getParagraphCount() - 1;
            if (ix >= 0) {
                String text = m.getPlainText(ix);
                int cix = (text == null ? 0 : Math.max(0, text.length() - 1));
                Marker end = control.newMarker(ix, cix, false);
                control.getSelectionModel().setSelection(Marker.ZERO, end);
            }
        }
    }
}
