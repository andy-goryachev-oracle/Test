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

    public EditablePlaintextModel() {
        paragraphs.add("");
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
                return new TextCell(index, f);
            }
        };
    }
    
    // TODO go through import handler?  ImportHandler(PLAIN_TEXT)
    // TODO text might contain newlines
    // TODO possibly add styles
    @Override
    public void replace(TextPos start, TextPos end, String text, String directStyle, String[] css) {
        // TODO
        System.err.println("replace start=" + start + " end=" + end + " text=[" + text + "]");
        
        // update paragraphs
        // update markers
        // fire event
    }
}
