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
package goryachev.rich;

public class StringStyledInput extends StyledInput {
    private final String text;
    private final String direct;
    private final String[] css;
    private int offset;

    // TODO check for illegal chars (<0x20 except for \r \n \t)
    public StringStyledInput(String text, String direct, String[] css) {
        this.text = text;
        this.direct = direct;
        this.css = css;
    }

    @Override
    public StyledText nextSegment() {
        if (offset < text.length()) {
            int c = text.charAt(offset);
            // is it a line break;?
            switch(c) {
            case '\n':
                offset++;
                return StyledText.LINEBREAK;
            case '\r':
                c = charAt(++offset);
                switch(c) {
                case '\n':
                    offset++;
                    break;
                }
                return StyledText.LINEBREAK;
            }

            int ix = indexOfLineBreak(offset);
            if (ix < 0) {
                String s = text.substring(offset);
                offset = text.length();
                return new StringStyledText(s, direct, css);
            } else {
                String s = text.substring(offset, ix);
                offset = ix;
                return new StringStyledText(s, direct, css);
            }
        }
        return null;
    }
    
    private int charAt(int ix) {
        if(ix < text.length()) {
            return text.charAt(ix);
        }
        return -1;
    }

    private int indexOfLineBreak(int start) {
        int len = text.length();
        for(int i=start; i<len; i++) {
            char c = text.charAt(i);
            switch(c) {
            case '\r':
            case '\n':
                return i;
            // TODO we can check for invalid ctrl characters here,
            // or use a string builder to filter out unwanted chars
            }
        }
        return -1;
    }
}
