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
        dump(" english english eng: end, eng: \n\n eng");
    }
    
    private static void t() {
        for(char c: "𑂦𑂷𑂔𑂣𑂳𑂩".toCharArray()) {
            boolean letter = Character.isLetterOrDigit(c);
            System.err.println(String.format("char=%04x isLetterOrDigit=%s", (int)c, letter));
        }
    }
    
    static void test() {
        String text = "Bhojpuri: 𑂦𑂷𑂔𑂣𑂳𑂩𑂲 test";
        dump(text);
    }
    
    static void dump(String text) {
        BreakIterator b = BreakIterator.getWordInstance();
        b.setText(text);
        int start = 0;
        int off;
        while((off = b.next()) != BreakIterator.DONE) {
            String s = text.substring(start, off);
            int len = s.length();
            boolean letter = Character.isLetterOrDigit(s.charAt(0));
            System.err.println("start=" + start + " offset=" + off + " text='" + s + "' length=" + len + " letter=" + letter);
            start = off;
        }
    }
}
