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

import java.util.Locale;
import goryachev.monkey.util.OptionPane;
import goryachev.monkey.util.ToolPane;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

/**
 * TextArea Page
 */
public class TextAreaPage extends ToolPane {
    enum TextChoice {
        NULL,
        SHORT,
        LONG,
        RIGHT_TO_LEFT,
    }
    
    private TextArea textArea;
    private Locale defaultLocale;

    public TextAreaPage() {
        textArea = new TextArea();
        textArea.setPromptText("<prompt>");
        
        ComboBox<TextChoice> textChoice = new ComboBox<>();
        textChoice.getItems().setAll(TextChoice.values());
        textChoice.getSelectionModel().selectedItemProperty().addListener((s,p,c) -> {
            String text = getText(c);
            Locale loc = getLocale(c);
            textArea.setText(text);
            Locale.setDefault(loc);
        });
        
        CheckBox wrap = new CheckBox("wrap text");
        wrap.selectedProperty().addListener((s,p,on) -> {
            textArea.setWrapText(on);
        });
        
        OptionPane p = new OptionPane();
        p.label("Text:");
        p.option(textChoice);
        p.option(wrap);
        
        setContent(textArea);
        setOptions(p);
    }
    
    protected String getText(TextChoice ch) {
        switch (ch) {
        case LONG:
            return "<beg-0123456789012345678901234567890123456789-|-0123456789012345678901234567890123456789-end>";
        case SHORT:
            return "yo";
        case NULL:
            return null;
        case RIGHT_TO_LEFT:
            return "העברעאיש (עברית) איז אַ סעמיטישע שפּראַך. מען שרייבט העברעאיש מיט די 22 אותיות פונעם אלף בית לשון קודש. די";
        default:
            return "?" + ch;
        }
    }

    protected Locale getLocale(TextChoice ch) {
        if (defaultLocale == null) {
            defaultLocale = Locale.getDefault();
        }

        switch (ch) {
        case RIGHT_TO_LEFT:
            return Locale.forLanguageTag("he");
        default:
            return defaultLocale;
        }
    }
}
