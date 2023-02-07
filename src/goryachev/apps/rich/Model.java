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
package goryachev.apps.rich;

import goryachev.rich.StyledTextModel;

public enum Model {
    DEMO,
    UNEVEN_SMALL,
    UNEVEN_LARGE,
    NULL,
    ZERO_LINES,
    ONE_LINE,
    TEN_LINES,
    THOUSAND_LINES,
    BILLION_LINES,
    LARGE_TEXT,
    LARGE_TEXT_LONG,
    MONOSPACED,
    NO_LAST_NEWLINE_SHORT,
    NO_LAST_NEWLINE_MEDIUM,
    NO_LAST_NEWLINE_LONG,
    ;

    public static StyledTextModel create(Model m) {
        if(m == null) {
            return null;
        }
        
        switch(m) {
        case BILLION_LINES:
            return new DemoStyledTextModel(1_000_000_000, false);
        case DEMO:
            return new RichTextAreaDemoModel();
        case MONOSPACED:
            return new DemoStyledTextModel(100_000, true);
        case NULL:
            return null;
        case ONE_LINE:
            return new DemoStyledTextModel(1, false);
        case TEN_LINES:
            return new DemoStyledTextModel(10, false);
        case THOUSAND_LINES:
            return new DemoStyledTextModel(1_000, false);
        case UNEVEN_SMALL:
            return new UnevenStyledTextModel(20);
        case UNEVEN_LARGE:
            return new UnevenStyledTextModel(2000);
        case ZERO_LINES:
            return new DemoStyledTextModel(0, false);
        case LARGE_TEXT:
            return new LargeTextModel(10);
        case LARGE_TEXT_LONG:
            return new LargeTextModel(5_000);
        case NO_LAST_NEWLINE_SHORT:
            return new NoLastNewlineModel(1);
        case NO_LAST_NEWLINE_MEDIUM:
            return new NoLastNewlineModel(5);
        case NO_LAST_NEWLINE_LONG:
            return new NoLastNewlineModel(300);
        default:
            throw new Error("?" + m);
        }
    }
}
