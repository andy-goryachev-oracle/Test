/*
 * Copyright (c) 2023, Oracle and/or its affiliates. All rights reserved.
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
package goryachev.rich;

import java.util.HashMap;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class InputMap2 implements EventHandler<KeyEvent> {
    enum Modifier {
        ALT,
        CTRL,
        /** default event type */
        KEY_PRESS,
        KEY_RELEASE,
        KEY_TYPED,
        KEY_ANY,
        META,
        SHIFT,
        SHORTCUT,
    }
    private final HashMap<Object,Object> map = new HashMap<>();

    public InputMap2() {
    }

    // TODO or make KeyBinding2 class public with a bunch of factory methods
    // TODO should take additional FxAction argument instead of Runnable?
    public void add(Runnable r, KeyCode code, Modifier ... modifiers) {
        // TODO check for nulls
        KeyBinding2 k = KeyBinding2.of(code, modifiers);
        map.put(k, r);
        System.err.println("add " + k); // FIX
    }

    @Override
    public void handle(KeyEvent ev) {
        if (ev == null || ev.isConsumed()) {
            return;
        }

        KeyBinding2 k = KeyBinding2.from(ev);
        if (k != null) {
            System.err.println("handle ev=" + k); // FIX
            
            // this should return an FxAction which can be disabled
            Runnable r = getAction(k);
            if (r != null) {
                exec(r);
            }
        }
    }

    /** returns a Runnable object for the given Action.  Might return null. */
    // TODO this should return FxAction which app developer can enable/disable
    public Runnable getAction(KeyBinding2 k) {
        Object v = map.get(k);
        if (v instanceof Runnable r) {
            return r;
        }
        return null;
    }
    
    private void exec(Runnable r) {
        // TODO disable caret blinking - this belongs to behavior?
        r.run();
        // TODO enable caret blinking
    }
}
