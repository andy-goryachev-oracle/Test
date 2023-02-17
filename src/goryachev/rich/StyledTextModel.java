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
package goryachev.rich;

import java.io.Writer;
import javafx.scene.input.DataFormat;

/**
 * Base class for a styled text model for use with {@link RichTextArea}.
 * The text is considered to be a collection of paragraphs, represented by {@link StyledParagraph} class.
 * 
 * TODO events
 * TODO listeners
 * TODO editing
 * TODO is read only
 * TODO isModified()
 * TODO use properties?  r/o: line count, r/w: editable
 * 
 * TODO rename StyledTextModelBase?
 */
public abstract class StyledTextModel {
    public interface ChangeListener {
        // TODO
    }

    /**
     * Returns the number of paragraphs in the model.
     */
    public abstract int getParagraphCount();

    /**
     * Returns the specified paragraph.  The caller should never attempt to ask for a paragraph outside of the
     * valid range.
     *
     * @param index paragraph index in the range (0...{@link getParagraphCount()})
     */
    public abstract StyledParagraph getParagraph(int index);

    public StyledTextModel() {
    }

    /**
     * Returns the plain text string for the specified paragraph.
     * The caller should never attempt to ask for a paragraph outside of the valid range.
     * 
     * The default implementation requests a plain text string from StyledParagraph;
     * models that have a cheaper access to the plain text should override this method. 
     *
     * @param index paragraph index in the range (0...{@link getParagraphCount()})
     */
    public String getPlainText(int index) {
        StyledParagraph p = getParagraph(index);
        return p.getPlainText();
    }
    
    public void addChangeListener(ChangeListener listener) {
        // TODO
    }
    
    public void removeChangeListener(ChangeListener listener) {
        // TODO
    }

    /** returns data formats supported by {@link export()} operation */
    public DataFormat[] getSupportedFormats() {
        return new DataFormat[] { DataFormat.PLAIN_TEXT };
    }
    
    public boolean isFormatSupported(DataFormat format) {
        for(DataFormat f: getSupportedFormats()) {
            if(f.equals(format)) {
                return true;
            }
        }
        return false;
    }

    // TODO writer or OutputStream ?
    public void export(DataFormat format, TextPos start, TextPos end, Writer wr) {
        if(!isFormatSupported(format)) {
            throw new IllegalArgumentException("Data format is not supported: " + format);
        }
        
        // TODO
    }
    
    // TODO replace from external source
    
    // TODO replace from string
}
