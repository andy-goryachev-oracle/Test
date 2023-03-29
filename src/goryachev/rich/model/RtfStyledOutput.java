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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import javafx.scene.paint.Color;

/**
 * StyledOutput which generates RTF.
 * 
 * RTF 1.5 Spec:
 * www.biblioscape.com/rtf15_spec.htm
 */
public abstract class RtfStyledOutput implements StyledOutput {
    /** outputs ASCII-encoded string */
    protected abstract void write(String s) throws IOException;
    
    private String fontName = "Courier New";
    private String fontSize = "18"; // double the actual size
    private final ColorTable colorTable = new ColorTable();
    
    public RtfStyledOutput() {
    }

    public StyledOutput getColorTableBuilder() {
        return new StyledOutput() {
            @Override
            public void append(StyledSegment seg) throws IOException {
                if (seg.isText()) {
                    StyleAttrs a = seg.getStyleAttrs();
                    Object v = a.get(StyleAttrs.TEXT_COLOR);
                    if(v instanceof Color c) {
                        colorTable.add(c);
                    }
                    
                    // TODO background color
//                    c = mixBackground(st.getBackgroundColor());
//                    if (c != null) {
//                        colorTable.add(c);
//                    }
                }
            }
        };
    }

    @Override
    public void append(StyledSegment seg) throws IOException {
        // TODO
        if (seg.isLineBreak()) {
            writeNL();
        } else if (seg.isText()) {
            writeSegment(seg);
        }
    }

    public void setFont(String fontName, int fontSize) {
        this.fontName = fontName;
        this.fontSize = String.valueOf(2 * fontSize);
    }
    
    public void writePrologue() throws IOException {
        // preamble
        write("{\\rtf1\\ansi\\ansicpg1252\\uc1\\sl0\\sb0\\sa0\\deff0{\\fonttbl{\\f0\\fnil ");
        write(fontName);
        write(";}}\r\n");

        // color table
        write("{\\colortbl ;");
        for (Color c : colorTable.getColors()) {
            write("\\red");
            write(toInt255(c.getRed()));
            write("\\green");
            write(toInt255(c.getGreen()));
            write("\\blue");
            write(toInt255(c.getBlue()));
            write(";");
        }
        write("}\r\n");

        write("{\\f0\\fs");
        write(fontSize);
        write(" \\fi0\\ql ");
    }
    
    public void writeEpilogue() throws IOException {
        write("\r\n}}\r\n");
    }

    protected void writeSegment(StyledSegment seg) throws IOException {
        /* TODO
        // TODO checkCancelled();

        write("\\fi0\\ql ");

        TextCellStyle prevStyle = null;
        Color color = null;
        Color background = null;
        boolean bold = false;
        boolean italic = false;
        boolean under = false;
        boolean strike = false;

        String text = seg.getPlainText();
        for (int i = startPos; i < endPos; i++) {
            TextCellStyle st = t.getCellStyle(i);
            if (prevStyle != st) {
                Color col;
                Color bg;
                boolean bld;
                boolean ita;
                boolean und;
                boolean str;

                if (st == null) {
                    col = null;
                    bg = null;
                    bld = false;
                    ita = false;
                    und = false;
                    str = false;
                } else {
                    col = st.getTextColor();
                    bg = mixBackground(st.getBackgroundColor());
                    bld = st.isBold();
                    ita = st.isItalic();
                    und = st.isUnderscore();
                    str = st.isStrikeThrough();
                }

                prevStyle = st;

                // emit changes

                if (CKit.notEquals(col, color)) {
                    if (col == null) {
                        write("\\cf0 ");
                    } else {
                        String s = colorTable.getIndexFor(col);
                        if (s == null) {
                            s = "0";
                            //log.warn("no entry for " + col);
                        }

                        write("\\cf");
                        write(s);
                        write(" ");
                    }

                    color = col;
                }

                if (CKit.notEquals(bg, background)) {
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

            char ch = text.charAt(i);
            if (ch < 0x20) {
                switch (ch) {
                case '\n':
                case '\r':
                    break;
                case '\t':
                    sb.append(ch);
                    break;
                }
            } else if (ch < 0x80) {
                switch (ch) {
                case '\\':
                    write("\\\\");
                    break;
                case '{':
                    write("\\{");
                    break;
                case '}':
                    write("\\}");
                    break;
                default:
                    sb.append(ch);
                    break;
                }
            } else {
                write("\\u");
                write(String.valueOf((short)ch));
                write("?");
            }
        }

        if (color != null) {
            write("\\cf0 ");
        }

        if (background != null) {
            write("\\highlight0 ");
        }

        if (bold) {
            write("\\b0 ");
        }

        if (italic) {
            write("\\i0 ");
        }

        if (under) {
            write("\\ul0 ");
        }

        if (strike) {
            write("\\strike0 ");
        }
        */
    }
    
    private void writeNL() throws IOException {
        write("\\par\r\n");
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
    
    protected static class ColorTable {
        private final ArrayList<Color> colors = new ArrayList();
        private final HashMap<Color, String> indexes = new HashMap();

        public ColorTable() {
        }

        public void add(Color c) {
            if (!indexes.containsKey(c)) {
                colors.add(c);
                indexes.put(c, String.valueOf(colors.size()));
            }
        }

        public String getIndexFor(Color c) {
            return indexes.get(c);
        }

        public List<Color> getColors() {
            return colors;
        }
    }
}
