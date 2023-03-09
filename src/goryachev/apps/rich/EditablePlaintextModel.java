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
import goryachev.rich.TextCell;
import goryachev.rich.TextPos;
import goryachev.rich.model.EditableStyledTextModel;
import goryachev.rich.model.PlainTextFormatHandler;
import goryachev.rich.model.StringStyledText;
import goryachev.rich.model.StyledOutput;
import goryachev.rich.model.StyledParagraph;
import goryachev.rich.model.StyledText;

public class EditablePlaintextModel extends EditableStyledTextModel {
    private final ArrayList<String> paragraphs = new ArrayList();
    private static final String STYLE = "-fx-font-size:200%;";

    public EditablePlaintextModel() {
        paragraphs.add("");
        registerDataFormatHandler(new PlainTextFormatHandler());
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
    public int getParagraphCount() {
        return paragraphs.size();
    }

    @Override
    public String getPlainText(int index) {
        return paragraphs.get(index);
    }

    @Override
    protected int insertTextSegment(int index, int offset, StyledText segment) {
        String s = paragraphs.get(index);
        String text = segment.getText();

        String s2 = insertText(s, offset, text);
        paragraphs.set(index, s2);
        return text.length();
    }
    
    @Override
    protected void insertLineBreak(int index, int offset) {
        if(index >= getParagraphCount()) {
            paragraphs.add("");
        } else {
            String s = paragraphs.get(index);
            if(offset >= s.length()) {
                paragraphs.add(index + 1, "");
            } else {
                paragraphs.set(index, s.substring(0, offset));
                paragraphs.add(index + 1, s.substring(offset));
            }
        }
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

    @Override
    protected void insertParagraph(int index, StyledText segment) {
        // no-op
    }

    @Override
    protected void exportSegments(int index, int startOffset, int endOffset, StyledOutput out) {
        String text = getPlainText(index);
        if (endOffset < 0) {
            endOffset = text.length();
        }

        if ((startOffset != 0) || (endOffset != text.length())) {
            text = text.substring(startOffset, endOffset);
        }

        StringStyledText seg = new StringStyledText(text, null, null);
        out.append(seg);
    }

    @Override
    public void applyStyle(TextPos start, TextPos end, String direct, String[] css) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeStyle(TextPos start, TextPos end, String direct, String[] css) {
        throw new UnsupportedOperationException();
    }
}
