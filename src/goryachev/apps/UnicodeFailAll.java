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
package goryachev.apps;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Many different Unicode scripts in TextArea/JTextArea.
 */
public class UnicodeFailAll extends Application {
    public static void main(String[] args) {
        Application.launch(UnicodeFailAll.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        String text = generateText();
        
        TextArea textField = new TextArea(text);
//        textField.setStyle("-fx-font-size:150%;");
        
        BorderPane bp = new BorderPane();

        SwingNode swingNode = new SwingNode();
        SwingUtilities.invokeLater(() -> {
            JTextArea t = new JTextArea(text);
            swingNode.setContent(new JScrollPane(t));
            
            Platform.runLater(() -> {
                bp.setCenter(swingNode);
            });
        });

        SplitPane split = new SplitPane(textField, bp);
        split.setOrientation(Orientation.HORIZONTAL);

        stage.setScene(new Scene(split));
        stage.setTitle("Unicode Failure 2 " + System.getProperty("java.version"));
        stage.setWidth(1200);
        stage.setHeight(1200);
        stage.show();
    }

    private String generateText() {
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
}
