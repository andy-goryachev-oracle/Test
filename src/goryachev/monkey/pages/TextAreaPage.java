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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.text.Font;

/**
 * TextArea Page
 */
public class TextAreaPage extends TestPaneBase {
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
    
    enum PromptChoice {
        NULL("null"),
        SHORT("Short"),
        LONG("Long"),
        ;
        private final String text;
        PromptChoice(String text) { this.text = text; }
        public String toString() { return text; }
    }
    
    enum FormatterChoice {
        NULL("null"),
        PREFIX("Prefix"),
        ;
        private final String text;
        FormatterChoice(String text) { this.text = text; }
        public String toString() { return text; }
    }
    
    private final ComboBox<String> fontChoice;
    private final ComboBox<Integer> fontSize;
    private final TextArea textArea;
    private Locale defaultLocale;

    public TextAreaPage() {
        textArea = new TextArea();
        textArea.setPromptText("<prompt>");
        
        ComboBox<TextChoice> textChoice = new ComboBox<>();
        textChoice.setId("textChoice");
        textChoice.getItems().setAll(TextChoice.values());
        textChoice.getSelectionModel().selectedItemProperty().addListener((s,p,c) -> {
            String text = getText(c);
            Locale loc = getLocale(c);
            textArea.setText(text);
            Locale.setDefault(loc);
        });
        
        fontChoice = new ComboBox<>();
        fontChoice.setId("fontChoice");
        fontChoice.getItems().setAll(collectFonts());
        fontChoice.getSelectionModel().selectedItemProperty().addListener((x) -> {
            updateFont();
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
            updateFont();
        });
        
        CheckBox wrap = new CheckBox("wrap text");
        wrap.setId("wrapText");
        wrap.selectedProperty().addListener((s,p,on) -> {
            textArea.setWrapText(on);
        });
        
        CheckBox editable = new CheckBox("editable");
        editable.setId("editable");
        editable.selectedProperty().bindBidirectional(textArea.editableProperty());
        
        ComboBox<PromptChoice> promptChoice = new ComboBox<>();
        promptChoice.setId("promptChoice");
        promptChoice.getItems().setAll(PromptChoice.values());
        promptChoice.getSelectionModel().selectedItemProperty().addListener((s,p,c) -> {
            String text = getPromptText(c);
            textArea.setPromptText(text);
        });
        
        ComboBox<FormatterChoice> formatterChoice = new ComboBox<>();
        formatterChoice.setId("formatterChoice");
        formatterChoice.getItems().setAll(FormatterChoice.values());
        formatterChoice.getSelectionModel().selectedItemProperty().addListener((s,p,c) -> {
            TextFormatter<?> f = getFormatter(c);
            textArea.setTextFormatter(f);
        });
        
        OptionPane p = new OptionPane();
        p.label("Text:");
        p.option(textChoice);
        p.label("Font:");
        p.option(fontChoice);
        p.label("Font Size:");
        p.option(fontSize);
        p.option(wrap);
        p.option(editable);
        p.label("Prompt:");
        p.option(promptChoice);
        p.label("Formatter: TODO");
        // TODO p.option(formatterChoice);
        
        setContent(textArea);
        setOptions(p);

        FX.select(fontChoice, "System Regular");
        FX.select(fontSize, 12);
        FX.select(textChoice, TextChoice.UNICODE);
        FX.select(promptChoice, PromptChoice.NULL);
    }
    
    protected void updateFont() {
        Font f = getFont();
        System.err.println(f); // FIX
        textArea.setFont(f);
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
            return generateTextForWritingSystems();
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
    
    protected TextFormatter<?> getFormatter(FormatterChoice ch) {
        switch (ch) {
        case NULL:
            return null;
        case PREFIX:
            // TODO converter, filter, too many options - code this later
//            return new TextFormatter<Object>() {
//            };
        default:
            throw new Error("?" + ch);
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
    
    private String generateTextForWritingSystems() {
        // better list https://en.wikipedia.org/wiki/List_of_writing_systems
        StringBuilder sb = new StringBuilder();
        t(sb, "Arabic", "العربية");
        t(sb, "Akkadian", "𒀝𒅗𒁺𒌑");
        t(sb, "Armenian", "հայերէն/հայերեն");
        t(sb, "Assamese", "অসমীয়া");
        t(sb, "Awadhi", "अवधी/औधी");
        t(sb, "Bagheli", "बघेली");
        t(sb, "Bagri", "बागड़ी, باگڑی");
        t(sb, "Bengali", "বাংলা");
        t(sb, "Bhojpuri", "𑂦𑂷𑂔𑂣𑂳𑂩𑂲");
        t(sb, "Braille", "⠃⠗⠇");
        t(sb, "Bundeli", "बुन्देली");
        t(sb, "Burmese", "မြန်မာ");
        t(sb, "Cherokee", "ᏣᎳᎩ ᎦᏬᏂᎯᏍᏗ");
        t(sb, "Chhattisgarhi", "छत्तीसगढ़ी, ଛତିଶଗଡ଼ି, ଲରିଆ");
        t(sb, "Chinese", "中文");
        t(sb, "Czech", "Čeština");
        t(sb, "Devanagari", "देवनागरी");
        t(sb, "Dhundhari", "ढूण्ढाड़ी/ઢૂણ્ઢાડ઼ી");
        t(sb, "Farsi", "فارسی");
        t(sb, "Garhwali", "गढ़वळि");
        t(sb, "Geʽez", "ግዕዝ");
        t(sb, "Greek", "Ελληνικά");
        t(sb, "Georgian", "ქართული");
        t(sb, "Gujarati", "ગુજરાતી");
        t(sb, "Harauti", "हाड़ौती, हाड़ोती");
        t(sb, "Haryanvi", "हरयाणवी");
        t(sb, "Hebrew", "עברית");
        t(sb, "Hindi", "हिन्दी");
        t(sb, "Inuktitut", "ᐃᓄᒃᑎᑐᑦ");
        t(sb, "Japanese", "日本語 かな カナ");
        t(sb, "Kangri", "कांगड़ी");
        t(sb, "Kannada", "ಕನ್ನಡ");
        t(sb, "Khmer", "ខ្មែរ");
        t(sb, "Khortha", "खोरठा");
        t(sb, "Korean", "한국어");
        t(sb, "Kumaoni", "कुमाऊँनी");
        t(sb, "Magahi", "𑂧𑂏𑂯𑂲/𑂧𑂏𑂡𑂲");
        t(sb, "Maithili", "मैथिली");
        t(sb, "Malayalam", "മലയാളം");
        t(sb, "Malvi", "माळवी भाषा / માળવી ભાષા");
        t(sb, "Marathi", "मराठी");
        t(sb, "Marwari,", "मारवाड़ी");
        t(sb, "Meitei", "ꯃꯩꯇꯩꯂꯣꯟ");
        t(sb, "Mewari", "मेवाड़ी/મેવ઼ાડ઼ી");
        t(sb, "Mongolian", "ᠨᠢᠷᠤᠭᠤ");
        t(sb, "Nimadi", "निमाड़ी");
        t(sb, "Odia", "ଓଡ଼ିଆ");
        t(sb, "Punjabi", "ਪੰਜਾਬੀپن٘جابی");
        t(sb, "Pahari", "पहाड़ी پہاڑی ");
        t(sb, "Rajasthani", "राजस्थानी");
        t(sb, "Russian", "Русский");
        t(sb, "Sanskrit", "संस्कृत-, संस्कृतम्");
        t(sb, "Santali", "ᱥᱟᱱᱛᱟᱲᱤ");
        t(sb, "Suret", "ܣܘܪܝܬ");
        t(sb, "Surgujia", "सरगुजिया");
        t(sb, "Surjapuri", "सुरजापुरी, সুরজাপুরী");
        t(sb, "Tamil", "Tamiḻ");
        t(sb, "Telugu", "తెలుగు");
        t(sb, "Thaana", "ދިވެހި");
        t(sb, "Thai", "ไทย");
        t(sb, "Tibetan", "བོད་");
        t(sb, "Tulu", "ತುಳು, ത‍ുള‍ു");
        t(sb, "Turoyo", "ܛܘܪܝܐ");
        t(sb, "Ukrainian", "Українська");
        t(sb, "Urdu", "اردو");
        t(sb, "Vietnamese", "Tiếng Việt");
        return sb.toString();
    }

    private void t(StringBuilder sb, String name, String text) {
        sb.append(name);
        sb.append(": ");
        sb.append(text);
        sb.append(" (");
        native2ascii(sb, text);
        sb.append(") \n");
    }

    protected static void native2ascii(StringBuilder sb, String text) {
        for (char c : text.toCharArray()) {
            if (c < 0x20) {
                escape(sb, c);
            } else if (c > 0x7f) {
                escape(sb, c);
            } else {
                sb.append(c);
            }
        }
    }

    protected static void escape(StringBuilder sb, char c) {
        sb.append("\\u");
        sb.append(h(c >> 12));
        sb.append(h(c >> 8));
        sb.append(h(c >> 4));
        sb.append(h(c));
    }

    protected static char h(int d) {
        return "0123456789abcdef".charAt(d & 0x000f);
    }
    
    protected static List<String> collectFonts() {
        ArrayList<String> rv = new ArrayList<>(Font.getFontNames());
        rv.add(0, null);
        return rv;
    }
}
