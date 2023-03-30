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
// This implementation is borrowed from
// https://github.com/andy-goryachev/FxTextEditor/blob/master/src/goryachev/fxtexteditor/internal/rtf/RtfWriter.java
// with permission from the author.
package goryachev.rich.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.scene.paint.Color;
import goryachev.rich.util.Util;

/**
 * StyledOutput which generates RTF.
 * 
 * RTF 1.5 Spec:
 * https://www.biblioscape.com/rtf15_spec.htm
 */
public abstract class RtfStyledOutput implements StyledOutput {
    /** outputs ASCII-encoded string */
    protected abstract void write(String s) throws IOException;
    
    private final LookupTable<Color> colorTable = new LookupTable<>(Color.BLACK);
    private final LookupTable<String> fontTable = new LookupTable<>("Helvetica");
    private boolean startOfLine = true;
    private StyleAttrs prevStyle;
    private Color color;
    private Color background;
    private boolean bold;
    private boolean italic;
    private boolean under;
    private boolean strike;
    private String fontFamily;
    private Integer fontSize;
    
    public RtfStyledOutput() {
    }

    public StyledOutput firstPassBuilder() {
        return new StyledOutput() {
            @Override
            public void append(StyledSegment seg) throws IOException {
                if (seg.isText()) {
                    StyleAttrs a = seg.getStyleAttrs();
                    if (a != null) {
                        // colors
                        Color c = a.getTextColor();
                        if (c != null) {
                            colorTable.add(c);
                        }

                        // TODO background color
                        //                    c = mixBackground(st.getBackgroundColor());
                        //                    if (c != null) {
                        //                        colorTable.add(c);
                        //                    }
                        
                        // TODO font table
                        String family = a.getFontFamily();
                        if (family != null) {
                            fontTable.add(family);
                        }
                    }
                }
            }
        };
    }
    
    public void writePrologue() throws IOException {
        // preamble
        write("{\\rtf1\\ansi\\ansicpg1252\\uc1\\sl0\\sb0\\sa0\\deff0");

        // font table
        write("{\\fonttbl{");
        int ix = 0;
        for (String family: fontTable.getItems()) {
            write("\\f");
            write(String.valueOf(ix++));
            write("\\fnil ");
            write(family);
            write(";}");
        }
        write("}\r\n");
        
        // color table
        write("{\\colortbl ;");
        for (Color c : colorTable.getItems()) {
            write("\\red");
            write(toInt255(c.getRed()));
            write("\\green");
            write(toInt255(c.getGreen()));
            write("\\blue");
            write(toInt255(c.getBlue()));
            write(";");
        }
        write("}\r\n");
        
        // TODO \deftab720 Default tab width in twips (the default is 720).  a twip is one-twentieth of a point
    }

    @Override
    public void append(StyledSegment seg) throws IOException {
        if (seg.isLineBreak()) {
            writeEndOfLine();
            writeNewLine();
        } else if (seg.isText()) {
            writeTextSegment(seg);
        }
    }

    public void writeEpilogue() throws IOException {
        writeEndOfLine();
        write("\r\n}\r\n");
    }

    private void writeEndOfLine() throws IOException {
        if (color != null) {
            write("\\cf0 ");
            color = null;
        }

        if (background != null) {
            write("\\highlight0 ");
            background = null;
        }

        if (bold) {
            write("\\b0 ");
            bold = false;
        }

        if (italic) {
            write("\\i0 ");
            italic = false;
        }

        if (under) {
            write("\\ul0 ");
            under = false;
        }

        if (strike) {
            write("\\strike0 ");
            strike = false;
        }
    }

    private void writeNewLine() throws IOException {
        write("\\par\r\n");
        startOfLine = true;
    }

    private void writeTextSegment(StyledSegment seg) throws IOException {
        // TODO checkCancelled();

        if (startOfLine) {
            // first line indent 0, left aligned
            write("\\fi0\\ql ");
            startOfLine = false;
        }

        StyleAttrs st = seg.getStyleAttrs();
        if (Util.notEquals(st, prevStyle)) {
            Color col;
            Color bg;
            boolean bld;
            boolean ita;
            boolean und;
            boolean str;
            String fam;
            Integer fsize;

            if (st == null) {
                col = null;
                bg = null;
                bld = false;
                ita = false;
                und = false;
                str = false;
                fam = null;
                fsize = null;
            } else {
                col = st.getTextColor();
                bg = null; // TODO mixBackground(st.getBackgroundColor());
                bld = st.isBold();
                ita = st.isItalic();
                und = st.isUnderline();
                str = st.isStrikeThrough();
                fam = st.getFontFamily();
                fsize = st.getFontSize();
            }

            prevStyle = st;

            // emit changes
            
            if (Util.notEquals(fontFamily, fam)) {
                String s = fontTable.getIndexFor(fam);
                if(s == null) {
                    s = "0";
                }
                write("\\f");
                write(s);

                fontFamily = fam;
            }

            if (Util.notEquals(fontSize, fsize)) {
                write("\\fs");
                double fs = 24.0; // twice the points
                if (fsize != null) {
                    fs = fs * (fsize / 100.0);
                }
                write(String.valueOf((int)Math.round(fs)));
                fontSize = fsize;
            }

            if (Util.notEquals(col, color)) {
                if (col == null) {
                    write("\\cf0 ");
                } else {
                    String s = colorTable.getIndexFor(col);
                    if (s == null) {
                        s = "0";
                    }

                    write("\\cf");
                    write(s);
                    write(" ");
                }

                color = col;
            }

            if (Util.notEquals(bg, background)) {
                if (bg == null) {
                    write("\\highlight0 ");
                } else {
                    String s = colorTable.getIndexFor(bg);

                    write("\\highlight");
                    write(s);
                    write(" ");
                }

                background = bg;
            }

            if (bld != bold) {
                write(bld ? "\\b " : "\\b0 ");
                bold = bld;
            }

            if (ita != italic) {
                write(ita ? "\\i " : "\\i0 ");
                italic = ita;
            }

            if (und != under) {
                write(und ? "\\ul " : "\\ul0 ");
                under = und;
            }

            if (str != strike) {
                write(str ? "\\strike " : "\\strike0 ");
                strike = str;
            }
        }

        String text = seg.getText();
        String encoded = encode(text);
        write(encoded);
    }

    // TODO unit test!
    private String encode(String text) {
        if (text == null) {
            return "";
        }

        int ix = indexOfSpecialChar(text);
        if (ix < 0) {
            return text;
        }

        int len = text.length();
        StringBuilder sb = new StringBuilder(len + 32);
        sb.append(text, 0, ix);

        for (int i = ix; i < len; i++) {
            char c = text.charAt(i);
            if (c < 0x20) {
                switch (c) {
                case '\n':
                case '\r':
                    break;
                case '\t':
                    sb.append(c);
                    break;
                }
            } else if (c < 0x80) {
                switch (c) {
                case '\\':
                    sb.append("\\\\");
                    break;
                case '{':
                    sb.append("\\{");
                    break;
                case '}':
                    sb.append("\\}");
                    break;
                default:
                    sb.append(c);
                    break;
                }
            } else {
                sb.append("\\u");
                sb.append(String.valueOf((short)c));
                sb.append("?");
            }
        }

        return sb.toString();
    }

    private int indexOfSpecialChar(String text) {
        int len = text.length();
        for (int i = 0; i < len; i++) {
            char c = text.charAt(i);
            if (c < 0x20) {
                switch (c) {
                case '\t':
                    continue;
                default:
                    return i;
                }
            } else if (c < 0x80) {
                switch (c) {
                case '\\':
                case '{':
                case '}':
                    return i;
                default:
                    continue;
                }
            }
            else {
                return i;
            }
        }
        return -1;
    }

    private static String toInt255(double x) {
        int v = (int)Math.round(255 * x);
        if (v < 0) {
            v = 0;
        } else if (v > 255) {
            v = 255;
        }
        return String.valueOf(v);
    }

    /** RTF is unable to specify colors inline it seems, needs a color lookup table */
    protected static class LookupTable<T> {
        private final ArrayList<T> items = new ArrayList<>();
        private final HashMap<T, String> indexes = new HashMap<>();

        public LookupTable(T initValue) {
            if(initValue != null) {
                add(initValue);
            }
        }

        public void add(T item) {
            if (!indexes.containsKey(item)) {
                items.add(item);
                indexes.put(item, String.valueOf(items.size()));
            }
        }

        public String getIndexFor(T c) {
            return indexes.get(c);
        }

        public List<T> getItems() {
            return items;
        }
    }
}
