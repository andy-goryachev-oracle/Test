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
package goryachev.rich.util;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * These APIs should be added to JavaFX.
 */
public class NewAPI {
    /**
     * may or may not be a good idea: what if TextFlow contains non-text Nodes?
     * this logic belongs to the model.
     */
    public static String getText(TextFlow f) {
        StringBuilder sb = new StringBuilder();
        for(Node n: f.getChildrenUnmodifiable()) {
            if(n instanceof Text t) {
                sb.append(t.getText());
            }
        }
        return sb.toString();
    }

    /** TextFlow.getTextLength() */
    public static int getTextLength(TextFlow f) {
        int len = 0;
        for(Node n: f.getChildrenUnmodifiable()) {
            if(n instanceof Text t) {
                len += t.getText().length();
            }
        }
        return len;
    }
    
    /** adds a change listener via ListenerHelper which should be made public */
    public static void addChangeListener(Runnable onChange, boolean fireImmediately, ObservableValue<?>... props) {
        ChangeListener li = new ChangeListener() {
            @Override
            public void changed(ObservableValue prop, Object oldValue, Object newValue) {
                onChange.run();
            }
        };

        for (ObservableValue p : props) {
            p.addListener(li);
        }

        if (fireImmediately) {
            onChange.run();
        }
    }

    /** TODO need com.sun.javafx.scene.control.ListenerHelper to be public
    public static ListenerHelper listenerHelper() {
    } */
}
