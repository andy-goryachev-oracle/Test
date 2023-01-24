/*
 * Copyright (c) 2022, 2023, Oracle and/or its affiliates. All rights reserved.
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
// This code borrows heavily from the following project, with permission from the author:
// https://github.com/andy-goryachev/FxEditor
package goryachev.rich.simple;

import java.io.InputStream;
import java.util.ArrayList;
import javafx.scene.image.Image;
import goryachev.rich.StyledParagraph;
import goryachev.rich.StyledTextModel;

public class SimpleStyledTextModel implements StyledTextModel {
    private final ArrayList<StyledParagraph> paragraphs = new ArrayList<>();

    public SimpleStyledTextModel() {
    }

    @Override
    public int getParagraphCount() {
        return paragraphs.size();
    }

    @Override
    public StyledParagraph getParagraph(int index) {
        return paragraphs.get(index);
    }

    public SimpleStyledTextModel addSegment(String text, String style, String... css) {
        if (paragraphs.size() == 0) {
            paragraphs.add(new SimpleStyledTextParagraph(0));
        }

        SimpleStyledTextParagraph p;
        StyledParagraph last = lastParagraph();
        if(last instanceof SimpleStyledTextParagraph ss) {
            p = ss;
        } else {
            int ix = paragraphs.size();
            p = new SimpleStyledTextParagraph(ix);
            paragraphs.add(p);
        }
        
        p.addSegment(text, style, css);
        return this;
    }
    
    protected StyledParagraph lastParagraph() {
        int sz = paragraphs.size();
        if (sz == 0) {
            return null;
        }
        return paragraphs.get(sz - 1);
    }

    public SimpleStyledTextModel addImage(InputStream in) {
        int ix = paragraphs.size();
        Image im = new Image(in);
        SimpleStyledImageParagraph p = new SimpleStyledImageParagraph(ix, im);
        paragraphs.add(p);
        return this;
    }

    public SimpleStyledTextModel nl() {
        return nl(1);
    }

    public SimpleStyledTextModel nl(int count) {
        for (int i = 0; i < count; i++) {
            int ix = paragraphs.size();
            paragraphs.add(new SimpleStyledTextParagraph(ix));
        }
        return this;
    }
}
