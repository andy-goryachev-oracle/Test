/*
 * Copyright (c) 2022, Oracle and/or its affiliates. All rights reserved.
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

import goryachev.monkey.util.OptionPane;
import goryachev.monkey.util.ToolPane;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/**
 * TextField Page
 */
public class TextFieldPage extends ToolPane {
    enum TextChoice {
        NULL,
        SHORT,
        LONG
    }
    
    private TextField textField;

    public TextFieldPage() {
        textField = new TextField();
        textField.setAlignment(Pos.BASELINE_RIGHT);
        textField.setPromptText("<prompt>");
        
        ComboBox<TextChoice> textChoice = new ComboBox<>();
        textChoice.getItems().setAll(TextChoice.values());
        textChoice.getSelectionModel().selectedItemProperty().addListener((s,p,c) -> {
            String text = getText(c);
            textField.setText(text);
        });
        
        ComboBox<Pos> posChoice = new ComboBox<>();
        posChoice.getItems().setAll(Pos.values());
        posChoice.getSelectionModel().selectedItemProperty().addListener((s,p,c) -> {
            Pos a = posChoice.getSelectionModel().getSelectedItem();
            textField.setAlignment(a);
        });
        
        OptionPane p = new OptionPane();
        p.label("Text:");
        p.option(textChoice);
        p.label("Alignment:");
        p.option(posChoice);
        
        setContent(textField);
        setOptions(p);
        
        posChoice.getSelectionModel().select(Pos.BASELINE_RIGHT);
    }
    
    protected String getText(TextChoice ch) {
        switch(ch) {
        case LONG:
            return "<beg-01234567890123456789012345678901234567890123456789012345678901234567890123456789-end>";
        case SHORT:
            return "yo";
        case NULL:
            return null;
        default:
            return "?" + ch;
        }
    }
}
