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
import goryachev.rich.simple.SimpleStyledTextModel;

/**
 * RichTextArea demo.
 */
public class RichTextAreaDemoModel extends SimpleStyledTextModel {
    public RichTextAreaDemoModel() {
        String CODE = "code";
        String RED = "red";
        String GREEN = "green";
        String UNDER = "underline";

        addSegment("RichTextArea Control", "-fx-font-size:200%;", UNDER);
        nl();
        addSegment("This text is styled with inline style.", "-fx-font-size:100%; -fx-font-style:italic;");
        nl();
        addSegment("The following text is styled with a CSS stylesheet:", null, null);
        nl().nl();
        addSegment("/**", null, RED, CODE);
        addSegment(" * RichTextArea demo.", null, RED, CODE);
        addSegment(" */", null, RED, CODE).nl();
        addSegment("public class ", null, GREEN, CODE);
        addSegment("RichTextAreaDemo ", null, CODE);
        addSegment("extends ", null, GREEN, CODE);
        addSegment("Application {", null, CODE).nl();
        addSegment("}", null, CODE).nl();
        nl(2);
        // TODO unicode codepoints
//        addSegment("Mongolian ᠨᠢᠷᠤᠭᠤ niruγu (нуруу nuruu)", null, null).nl();
//        addSegment("Arabic العربية", null, null).nl();
//        addSegment("Japanese 日本語", null, null).nl();
        addSegment("Mongolian \u1828\u1822\u1837\u1824\u182d\u1824 niru\u03b3u (\u043d\u0443\u0440\u0443\u0443 nuruu)", null, null).nl();
        addSegment("Arabic \u0627\u0644\u0639\u0631\u0628\u064a\u0629", null, null).nl();
        addSegment("Japanese \u65e5\u672c\u8a9e", null, null).nl();
    }
}
