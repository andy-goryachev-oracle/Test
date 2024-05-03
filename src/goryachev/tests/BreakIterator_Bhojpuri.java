/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
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

package goryachev.tests;

import java.text.BreakIterator;
import java.text.CharacterIterator;

public class BreakIterator_Bhojpuri {
    public static void main(String[] args) {
        //dump(" english english eng: end, eng: \n\n eng");
        //dump("𑂦𑂷𑂔𑂣𑂳𑂩𑂲");
        String text =
            "Braille ⠃⠗⠇ Burmese: မြန်မာ";
            //"aaa... 𑂦𑂷𑂔𑂣𑂳𑂩𑂲 bbb";
        dump(text);
        dumpCh(text);
    }
    
    private static void t() {
        for(char c: "𑂦𑂷𑂔𑂣𑂳𑂩𑂲".toCharArray()) {
            boolean letter = Character.isLetterOrDigit(c);
            System.err.println(String.format("char=%04x isLetterOrDigit=%s", (int)c, letter));
        }
    }
    
    static void test() {
        String text = "Bhojpuri: 𑂦𑂷𑂔𑂣𑂳𑂩𑂲 test";
        dump(text);
    }
    
    static void dump(String text) {
        System.err.println("dump " + text);
        BreakIterator b = BreakIterator.getWordInstance();
        b.setText(text);
        int start = 0;
        int off;
        while((off = b.next()) != BreakIterator.DONE) {
            String s = text.substring(start, off);
            int len = s.length();
            int cp = text.codePointAt(start);
            boolean letter = Character.isLetterOrDigit(cp);
            String type = getType(Character.getType(cp));
            System.err.println("start=" + start + " offset=" + off + " text='" + s + "' length=" + len + " letter=" + letter + " type=" + type);
            start = off;
        }
    }

    static void dumpCh(String text) {
        System.err.println("dumpCh " + text);
        BreakIterator b = BreakIterator.getCharacterInstance();
        b.setText(text);
        int start = 0;
        int off;
        while((off = b.next()) != BreakIterator.DONE) {
            String s = text.substring(start, off);
            int len = s.length();
            int cp = text.codePointAt(start);
            boolean letter = Character.isLetterOrDigit(cp);
            String type = getType(Character.getType(cp));
            System.err.println("start=" + start + " offset=" + off + " text='" + s + "' length=" + len + " letter=" + letter + " type=" + type);
            start = off;
        }
    }

    private static String getType(int t) {
        switch (t) {
        case Character.COMBINING_SPACING_MARK:
            return "COMBINING_SPACING_MARK";
        case Character.CONNECTOR_PUNCTUATION:
            return "CONNECTOR_PUNCTUATION";
        case Character.CONTROL:
            return "CONTROL";
        case Character.CURRENCY_SYMBOL:
            return "CURRENCY_SYMBOL";
        case Character.DASH_PUNCTUATION:
            return "DASH_PUNCTUATION";
        case Character.DECIMAL_DIGIT_NUMBER:
            return "DECIMAL_DIGIT_NUMBER";
        case Character.ENCLOSING_MARK:
            return "ENCLOSING_MARK";
        case Character.END_PUNCTUATION:
            return "END_PUNCTUATION";
        case Character.FINAL_QUOTE_PUNCTUATION:
            return "FINAL_QUOTE_PUNCTUATION";
        case Character.FORMAT:
            return "FORMAT";
        case Character.INITIAL_QUOTE_PUNCTUATION:
            return "INITIAL_QUOTE_PUNCTUATION";
        case Character.LETTER_NUMBER:
            return "LETTER_NUMBER";
        case Character.LINE_SEPARATOR:
            return "LINE_SEPARATOR";
        case Character.LOWERCASE_LETTER:
            return "LOWERCASE_LETTER";
        case Character.MATH_SYMBOL:
            return "MATH_SYMBOL";
        case Character.MODIFIER_LETTER:
            return "MODIFIER_LETTER";
        case Character.MODIFIER_SYMBOL:
            return "MODIFIER_SYMBOL";
        case Character.NON_SPACING_MARK:
            return "NON_SPACING_MARK";
        case Character.OTHER_LETTER:
            return "OTHER_LETTER";
        case Character.OTHER_NUMBER:
            return "OTHER_NUMBER";
        case Character.OTHER_PUNCTUATION:
            return "OTHER_PUNCTUATION";
        case Character.OTHER_SYMBOL:
            return "OTHER_SYMBOL";
        case Character.PARAGRAPH_SEPARATOR:
            return "PARAGRAPH_SEPARATOR";
        case Character.PRIVATE_USE:
            return "PRIVATE_USE";
        case Character.SPACE_SEPARATOR:
            return "SPACE_SEPARATOR";
        case Character.START_PUNCTUATION:
            return "START_PUNCTUATION";
        case Character.SURROGATE:
            return "SURROGATE";
        case Character.TITLECASE_LETTER:
            return "TITLECASE_LETTER";
        case Character.UNASSIGNED:
            return "UNASSIGNED";
        case Character.UPPERCASE_LETTER:
            return "UPPERCASE_LETTER";
        default:
            return String.valueOf(t);
        }
    }
}
