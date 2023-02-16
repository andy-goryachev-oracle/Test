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

import java.util.EnumSet;
import javafx.event.EventType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import goryachev.rich.InputMap2.Modifier;

// this class might be internal to InputMap2
public record KeyBinding2(KeyCode code, EnumSet<Modifier> modifiers) {
    private static final boolean isMac = isMac();
    private static final boolean isWin = isWin();

    public static KeyBinding2 from(KeyEvent ev) {
        EnumSet<Modifier> m = EnumSet.noneOf(Modifier.class);
        EventType<KeyEvent> t = ev.getEventType();
        if(t == KeyEvent.KEY_PRESSED) {
            m.add(Modifier.KEY_PRESS);
        } else if(t == KeyEvent.KEY_RELEASED) {
            m.add(Modifier.KEY_RELEASE);
        } else if(t == KeyEvent.KEY_TYPED) {
            m.add(Modifier.KEY_TYPED);
        } else {
            // FIX what is it?
            return null;
        }
        
        boolean ctrl = ev.isControlDown();
        boolean meta = ev.isMetaDown();
        boolean shortcut = ev.isShortcutDown();
        
        // TODO problem: shortcut on mac: shortcut + meta, on windows: control + meta; whereas of() would have
        // only one - shortcut
        
        // why shortcut key logic is not public is unclear
        if (isMac) {
            if (shortcut) {
                meta = false;
            }
        } else if (isWin) {
            if (ctrl) {
                shortcut = false;
            }
        }

        if (ev.isAltDown()) {
            m.add(Modifier.ALT);
        }

        if (ev.isShiftDown()) {
            m.add(Modifier.SHIFT);
        }

        if (shortcut) {
            m.add(Modifier.SHORTCUT);
        }

        if (ctrl) {
            m.add(Modifier.CTRL);
        }

        if (meta) {
            m.add(Modifier.META);
        }

        KeyCode code = ev.getCode();
        KeyBinding2 keyBinding = new KeyBinding2(code, m);
        System.err.println(ev + " kb=" + keyBinding); // FIX
        return keyBinding;
    }

    /** creates a key binding.  might return null if the specified modifiers refer to a different platform */
    public static KeyBinding2 of(KeyCode code, Modifier... modifiers) {
        EnumSet<Modifier> m = EnumSet.noneOf(Modifier.class);
        for (Modifier modifier : modifiers) {
            m.add(modifier);
        }

        // TODO mac-windows for now.  might rethink the logic to support more platforms
        if (isMac) {
            if (m.contains(Modifier.NOT_MAC)) {
                return null;
            } else if (m.contains(Modifier.WINDOWS)) {
                return null;
            }
        } else if (isWin) {
            if (m.contains(Modifier.NOT_WINDOWS)) {
                return null;
            } else if (m.contains(Modifier.MAC)) {
                return null;
            }
        }
        m.remove(Modifier.MAC);
        m.remove(Modifier.NOT_MAC);
        m.remove(Modifier.WINDOWS);
        m.remove(Modifier.NOT_WINDOWS);
        
        boolean pressed = m.contains(Modifier.KEY_PRESS);
        boolean released = m.contains(Modifier.KEY_PRESS);
        boolean typed = m.contains(Modifier.KEY_TYPED);
        
        int ct = 0;
        Modifier t = null;
        if (pressed) {
            ct++;
            t = Modifier.KEY_PRESS;
        }
        if (released) {
            ct++;
            t = Modifier.KEY_RELEASE;
        }
        if (typed) {
            ct++;
            t = Modifier.KEY_TYPED;
        }

        // validate event type
        if (ct > 1) {
            throw new IllegalArgumentException("more than one key event type is specified");
        }
        
        if(t == null) {
            t = Modifier.KEY_PRESS;
        }
        m.add(t);

        // TODO validate: shortcut and !(other shortcut modifier)
        return new KeyBinding2(code, m);
    }

    private static boolean isMac() {
        // PlatformUtil
        return System.getProperty("os.name").startsWith("Mac");
    }

    private static boolean isWin() {
        // PlatformUtil
        return System.getProperty("os.name").startsWith("Windows");
    }
}
