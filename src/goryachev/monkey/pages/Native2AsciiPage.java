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
package goryachev.monkey.pages;

import goryachev.monkey.util.TestPaneBase;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 *
 */
public class Native2AsciiPage extends TestPaneBase {
    private final TextArea nat;
    private final TextArea ascii;
    private boolean ignoreEvent;
    
    public Native2AsciiPage() {
        nat = new TextArea();
        nat.textProperty().addListener((x) -> convert(true));
        
        ascii = new TextArea();
        ascii.textProperty().addListener((x) -> convert(false));
        
        GridPane p = new GridPane();
        p.add(new Label("Native"), 0, 0);
        p.add(nat, 0, 1);
        // talk about ceremony!
        GridPane.setFillHeight(nat, Boolean.TRUE);
        GridPane.setFillWidth(nat, Boolean.TRUE);
        p.setHgrow(nat, Priority.ALWAYS);
        p.setVgrow(nat, Priority.ALWAYS);
        p.add(new Label("ASCII"), 0, 2);
        p.add(ascii, 0, 3);
        GridPane.setFillHeight(ascii, Boolean.TRUE);
        GridPane.setFillWidth(ascii, Boolean.TRUE);
        p.setHgrow(ascii, Priority.ALWAYS);
        p.setVgrow(ascii, Priority.ALWAYS);
        setContent(new BorderPane(p));
    }

    protected void convert(boolean fromNative) {
        if (ignoreEvent) {
            return;
        }

        ignoreEvent = true;

        if (fromNative) {
            String s = nat.getText();
            String text = native2ascii(s);
            ascii.setText(text);
        } else {
            String s = ascii.getText();
            String text = ascii2native(s);
            nat.setText(text);
        }
        ignoreEvent = false;
    }

    private String ascii2native(String text) {
        if (text == null) {
            return null;
        }

        int sz = text.length();
        StringBuilder sb = new StringBuilder(sz);
        for (int i = 0; i < sz; i++) {
            char c = text.charAt(i);
            switch (c) {
            case '\\':
                int u = toUnicode(text, i + 1);
                if (u < 0) {
                    sb.append(c);
                } else {
                    sb.append((char)u);
                    i += 5;
                }
                break;
            default:
                sb.append(c);
                break;
            }
        }
        return sb.toString();
    }

    private String native2ascii(String text) {
        if (text == null) {
            return null;
        }

        int sz = text.length();
        StringBuilder sb = new StringBuilder(sz + 256);
        for (int i = 0; i < sz; i++) {
            char c = text.charAt(i);
            switch (c) {
            case ' ':
            case '\n':
                sb.append(c);
                break;
            default:
                if ((c > ' ') && (c < 0x7f)) {
                    sb.append(c);
                } else {
                    sb.append("\\u");
                    sb.append(Integer.toString(c, 16));
                }
            }
        }
        return sb.toString();
    }

    private int toUnicode(String text, int ix) {
        if (text.length() < (ix + 5)) {
            return -1;
        }

        char c = text.charAt(ix++);
        switch (c) {
        case 'u':
        case 'U':
            break;
        default:
            return -1;
        }

        try {
            String s = text.substring(ix, ix + 4);
            int v = Integer.parseInt(s, 16);
            return v;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
