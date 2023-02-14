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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import goryachev.monkey.util.FX;
import goryachev.monkey.util.OptionPane;
import goryachev.monkey.util.TestPaneBase;
import goryachev.monkey.util.WritingSystemsDemo;
import javafx.scene.Group;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Text Page
 */
public class TextPage extends TestPaneBase {
    enum TextChoice {
        NULL("null"),
        SHORT("Short"),
        LONG("Long"),
        RIGHT_TO_LEFT("Right-to-Left"),
        UNICODE("Unicode"),
        COMBINING("Combining Characters"),
        FAIL_NAV("Navigation Fails"),
        ;
        private final String text;
        TextChoice(String text) { this.text = text; }
        public String toString() { return text; }
    }
    
    private final ComboBox<TextChoice> textChoice;
    private final ComboBox<String> fontChoice;
    private final ComboBox<Integer> fontSize;
    private final Group textGroup;
    private Locale defaultLocale;

    public TextPage() {
        textGroup = new Group();
        
        textChoice = new ComboBox<>();
        textChoice.setId("textChoice");
        textChoice.getItems().setAll(TextChoice.values());
        textChoice.getSelectionModel().selectedItemProperty().addListener((c) -> {
            updateTextFlow();
        });
        
        fontChoice = new ComboBox<>();
        fontChoice.setId("fontChoice");
        fontChoice.getItems().setAll(collectFonts());
        fontChoice.getSelectionModel().selectedItemProperty().addListener((x) -> {
            updateTextFlow();
        });
        
        fontSize = new ComboBox<>();
        fontSize.setId("fontSize");
        fontSize.getItems().setAll(
            8,
            12,
            24,
            48
        );
        fontSize.getSelectionModel().selectedItemProperty().addListener((x) -> {
            updateTextFlow();
        });
        
        OptionPane p = new OptionPane();
        p.label("Text:");
        p.option(textChoice);
        p.label("Font:");
        p.option(fontChoice);
        p.label("Font Size:");
        p.option(fontSize);
        
        setContent(textGroup);
        setOptions(p);

        FX.select(fontChoice, "System Regular");
        FX.select(fontSize, 12);
        FX.select(textChoice, TextChoice.UNICODE);
    }
    
    protected void updateTextFlow() {
        TextChoice c = FX.getSelectedItem(textChoice);
        if(c == null) {
            return;
        }
        
        Font f = getFont();
        String text = getText(c);
        Text t = new Text(text);
        t.setFont(f);
        textGroup.getChildren().setAll(t);
        Locale loc = getLocale(c);
        Locale.setDefault(loc);
    }
    
    protected Font getFont() {
        String name = fontChoice.getSelectionModel().getSelectedItem();
        if(name == null) {
            return null;
        }
        Integer size = fontSize.getSelectionModel().getSelectedItem();
        if(size == null) {
            size = 12;
        }
        return new Font(name, size);
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
            return "العربية" + "העברעאיש (עברית) איז אַ סעמיטישע שפּראַך. מען שרייבט העברעאיש מיט די 22 אותיות פונעם אלף בית לשון קודש. די";
        case UNICODE:
            return WritingSystemsDemo.getText();
        case COMBINING:
            return
                "Tibetan HAKṢHMALAWARAYAṀ: \u0f67\u0f90\u0fb5\u0fa8\u0fb3\u0fba\u0fbc\u0fbb\u0f82\n(U+0f67 U+0f90 U+0fb5 U+0fa8 U+0fb3 U+0fba U+0fbc U+0fbb U+0f82)\n" +
                "Double diacritics: a\u0360b a\u0361b a\u0362b a\u035cb";
        case FAIL_NAV:
            return "Arabic: \u0627\u0644\u0639\u0631\u0628\u064a\u0629";
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
    
    protected static List<String> collectFonts() {
        ArrayList<String> rv = new ArrayList<>(Font.getFontNames());
        return rv;
    }
}
