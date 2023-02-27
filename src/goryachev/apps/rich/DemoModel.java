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
package goryachev.apps.rich;
import java.util.Arrays;
import java.util.Random;
import javafx.scene.control.Button;
import goryachev.rich.simple.SegmentStyledTextModel;

/**
 * RichTextArea demo model.
 */
public class DemoModel extends SegmentStyledTextModel {
    public DemoModel() {
        String ARABIC = "arabic";
        String CODE = "code";
        String RED = "red";
        String GREEN = "green";
        String UNDER = "underline";
        String GRAY = "gray";
        String LARGE = "large";

        addSegment("RichTextArea Control", "-fx-font-size:200%;", UNDER);
        //nl();
        addImage(DemoModel.class.getResourceAsStream("image.png"));
        addSegment("*** This text is styled with inline style ***", "-fx-font-size:100%; -fx-font-style:italic; -fx-fill:gray;");
        nl();
        addSegment("The following text is styled with a CSS stylesheet:");
        nl().nl();
        addSegment("/**", null, RED, CODE);
        nl();
        addSegment(" * RichTextArea demo.", null, RED, CODE);
        nl();
        addSegment(" */", null, RED, CODE);
        nl();
        addSegment("public class ", null, GREEN, CODE);
        addSegment("RichTextAreaDemo ", null, CODE);
        addSegment("extends ", null, GREEN, CODE);
        addSegment("Application {", null, CODE);
        nl();
        addSegment("}", null, CODE);
        nl(3);
        addSegment("        leading and trailing whitespace         ", null, CODE).nl();
        addSegment("A regular Arabic verb, كَتَبَ‎ kataba (to write).", null, ARABIC).nl();
        addSegment("Tibetan ཨོཾ་མ་ཎི་པདྨེ་ཧཱུྃ", null, LARGE).nl();
        addSegment("Double diacritics: a\u0360b a\u0361b a\u0362b a\u035cb").nl();
        addSegment("Emojis: [🇺🇦❤️🏁🇺🇸🔥🦋😀😃😄😁😆😅🤣😂🙂🙃😉😊😇]", null, LARGE).nl();
        nl();
        addSegment("Halfwidth and FullWidth Forms", null, UNDER).nl();
        addSegment("ＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯ", "-fx-font-family:monospaced;").nl();
        addSegment("ABCDEFGHIJKLMNO", "-fx-font-family:monospaced;").nl();
        // inline node segments break navigation, probably because of caret shape?
        addSegment("Inline Node [").addNodeSegment(() -> new Button("Button")).addSegment("].").nl();
        nl(2);

        // TODO unicode codepoints
//        addSegment("Mongolian ᠨᠢᠷᠤᠭᠤ niruγu (нуруу nuruu)", null, null).nl();
//        addSegment("Arabic العربية", null, null).nl();
//        addSegment("Japanese 日本語", null, null).nl();
//        addSegment("Mongolian \u1828\u1822\u1837\u1824\u182d\u1824 niru\u03b3u (\u043d\u0443\u0440\u0443\u0443 nuruu)", null, null).nl();
//        addSegment("Arabic \u0627\u0644\u0639\u0631\u0628\u064a\u0629", null, null).nl();
//        addSegment("Japanese \u65e5\u672c\u8a9e", null, null).nl();
        
        Random r = new Random();
        for(int line=0; line<100; line++) {
            int ct = r.nextInt(10);
            for (int word = 0; word < ct; word++) {
                int len = 1 + r.nextInt(7);
                char c = '*';

                if (word == 0) {
                    addSegment("L" + (getParagraphCount() + 1), null, GRAY);
                }
                
                addSegment(" ", null);

                if (r.nextFloat() < 0.1) {
                    addSegment(word + "." + word(c, len), null, RED);
                } else {
                    addSegment(word + "." + word(c, len), null);
                }
            }
            nl();
        }
        
        nl();
        addSegment("The End.", null);
        nl();
    }
    
    private String word(char c, int len) {
        char[] cs = new char[len];
        Arrays.fill(cs, c);
        return new String(cs);
    }
}
