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

import java.util.ArrayList;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import goryachev.rich.StyledParagraph;
import goryachev.rich.StyledTextModel;
import goryachev.rich.TextCell;
import goryachev.rich.TextPos;

public class EditablePlaintextModel extends StyledTextModel {
    private final ArrayList<String> paragraphs = new ArrayList();
    private static final String STYLE = "-fx-font-size:200%;";

    public EditablePlaintextModel() {
        paragraphs.add("");
    }

    @Override
    public StyledParagraph getParagraph(int index) {
        return new StyledParagraph() {
            @Override
            public String getPlainText() {
                return EditablePlaintextModel.this.getPlainText(index);
            }

            @Override
            public int getIndex() {
                return index;
            }

            @Override
            public TextCell createTextCell() {
                String text = getPlainText();
                TextFlow f = new TextFlow(new Text(text));
                f.setStyle(STYLE);
                return new TextCell(index, f);
            }
        };
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public int getParagraphCount() {
        return paragraphs.size();
    }

    @Override
    public String getPlainText(int index) {
        return paragraphs.get(index);
    }

    @Override
    public void replace(TextPos start, TextPos end, String text) {
        System.out.println("replace start=" + start + " end=" + end + " text=[" + text + "]"); // FIX
        
        if(start.compareTo(end) > 0) {
            TextPos p = start;
            start = end;
            end = p;
        }

        int len = text.length();

        removeRegion(start, end);

        int ix = start.index();
        int cix = start.offset();
        String s = paragraphs.get(ix);

        String s2 = insertText(s, cix, text);
        paragraphs.set(ix, s2);

        fireChangeEvent(start, end, len, 0, 0);
    }
    
    @Override
    public void insertLineBreak(TextPos pos) {
        System.err.println("insertLineBreak pos=" + pos); // FIX
        int ix = pos.index();
        if(ix >= getParagraphCount()) {
            paragraphs.add("");
        } else {
            int cix = pos.offset();
            String s = paragraphs.get(ix);
            if(cix >= s.length()) {
                paragraphs.add(ix + 1, "");
            } else {
                paragraphs.set(ix, s.substring(0, cix));
                paragraphs.add(ix + 1, s.substring(cix));
            }
        }
        fireChangeEvent(pos, pos, 0, 1, 0);
    }

    private static String insertText(String text, int index, String toInsert) {
        if (index >= text.length()) {
            return text + toInsert;
        } else {
            return text.substring(0, index) + toInsert + text.substring(index);
        }
    }

    // the caller must ensure 'start' <= 'end'
    @Override
    protected void removeRegion(TextPos start, TextPos end) {
        int ix = start.index();
        String text = paragraphs.get(ix);
        String newText;

        if (ix == end.index()) {
            int len = text.length();
            if (end.offset() >= len) {
                newText = text.substring(0, start.offset());
            } else {
                newText = text.substring(0, start.offset()) + text.substring(end.offset());
            }
            paragraphs.set(ix, newText);
        } else {
            newText = text.substring(0, start.offset()) + paragraphs.get(end.index()).substring(end.offset());
            paragraphs.set(ix, newText);

            int ct = end.index() - ix;
            ix++;
            for (int i = 0; i < ct; i++) {
                paragraphs.remove(ix);
            }
        }
    }
}
