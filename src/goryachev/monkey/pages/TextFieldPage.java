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

import goryachev.monkey.pages.TextAreaPage.PromptChoice;
import goryachev.monkey.util.OptionPane;
import goryachev.monkey.util.TestPaneBase;
import java.util.Locale;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/**
 * TextField Page
 */
public class TextFieldPage extends TestPaneBase {
    enum TextChoice {
        NULL,
        SHORT,
        LONG,
        RIGHT_TO_LEFT,
    }
    
    enum PromptChoice {
        NULL("null"),
        SHORT("Short"),
        LONG("Long"),
        ;
        private final String text;
        PromptChoice(String text) { this.text = text; }
        public String toString() { return text; }
    }
    
    private TextField control;
    private Locale defaultLocale;

    public TextFieldPage() {
        setId("TextFieldPage");
        
        control = new TextField();
        control.setAlignment(Pos.BASELINE_RIGHT);
        
        ComboBox<TextChoice> textChoice = new ComboBox<>();
        textChoice.setId("textChoice");
        textChoice.getItems().setAll(TextChoice.values());
        textChoice.getSelectionModel().selectedItemProperty().addListener((s,p,c) -> {
            String text = getText(c);
            Locale loc = getLocale(c);
            control.setText(text);
            Locale.setDefault(loc);
        });
        
        ComboBox<Pos> posChoice = new ComboBox<>();
        posChoice.setId("posChoice");
        posChoice.getItems().setAll(Pos.values());
        posChoice.getSelectionModel().selectedItemProperty().addListener((s,p,c) -> {
            Pos a = posChoice.getSelectionModel().getSelectedItem();
            control.setAlignment(a);
        });
        
        ComboBox<PromptChoice> promptChoice = new ComboBox<>();
        promptChoice.setId("promptChoice");
        promptChoice.getItems().setAll(PromptChoice.values());
        promptChoice.getSelectionModel().selectedItemProperty().addListener((s,p,c) -> {
            String text = getPromptText(c);
            control.setPromptText(text);
        });
        
        ComboBox<Integer> prefColumnCount = new ComboBox<>();
        prefColumnCount.setId("prefColumnCount");
        prefColumnCount.getItems().setAll(
            null,
            1,
            10,
            100,
            1000
        );
        prefColumnCount.getSelectionModel().selectedItemProperty().addListener((s,p,c) -> {
            Integer ct = prefColumnCount.getSelectionModel().getSelectedItem();
            int count = ct == null ? TextField.DEFAULT_PREF_COLUMN_COUNT : ct;
            control.setPrefColumnCount(count);
        });
        
        OptionPane p = new OptionPane();
        p.label("Text:");
        p.option(textChoice);
        p.label("Alignment:");
        p.option(posChoice);
        p.label("Prompt:");
        p.option(promptChoice);
        p.label("Preferred Column Count:");
        p.option(prefColumnCount);
        // TODO editable
        // TODO font
        
        setContent(control);
        setOptions(p);
        
        posChoice.getSelectionModel().select(Pos.BASELINE_RIGHT);
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
    
    protected String getPromptText(PromptChoice ch) {
        switch (ch) {
        case LONG:
            return "<beg-0123456789012345678901234567890123456789-|-0123456789012345678901234567890123456789-end>";
        case SHORT:
            return "yo";
        case NULL:
            return null;
        default:
            return "?" + ch;
        }
    }
}
