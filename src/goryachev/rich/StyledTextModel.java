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
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.scene.input.DataFormat;
import goryachev.rich.impl.Markers;

/**
 * Base class for a styled text model for use with {@link RichTextArea}.
 * The text is considered to be a collection of paragraphs, represented by {@link StyledParagraph} class.
 * 
 * TODO events
 * TODO printing
 */
public abstract class StyledTextModel {
    public interface ChangeListener {
        /**
         * Indicates a change in the model text.
         * The listeners are updated *after* the corresponding changes have been made to the model.
         * 
         * @param start start of the text block
         * @param end end of the affected text block
         * @param charsAddedTop number of characters inserted on the same line as start
         * @param linesAdded the number of paragraphs inserted between start and end
         * @param charsAddedBottom number of characters inserted on the same line as end
         */
        public void eventTextUpdated(TextPos start, TextPos end, int charsAddedTop, int linesAdded, int charsAddedBottom);
        // TODO or another Change class?
    }
    
    /**
     * Indicates whether the model is editable.
     */
    public boolean isEditable() { return false; }

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
    
    private final CopyOnWriteArrayList<ChangeListener> listeners = new CopyOnWriteArrayList();
    private final HashMap<DataFormat,ImportHandler> importHandlers = new HashMap<>(4);
    private final HashMap<DataFormat,ExportHandler> exportHandlers = new HashMap<>(4);
    private final Markers markers = new Markers();
    // TODO special BEGIN/END markers? especially END?

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
        listeners.add(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        listeners.remove(listener);
    }

    public ExportHandler registerExportHandler(ExportHandler handler) {
        return exportHandlers.put(handler.getDataFormat(), handler);
    }

    /** returns data formats supported by {@link export()} operation */
    public DataFormat[] getSupportedExportFormats() {
        Set<DataFormat> formats = exportHandlers.keySet();
        return formats.toArray(new DataFormat[formats.size()]);
    }

    protected ExportHandler getExportHandler(DataFormat format) {
        return exportHandlers.get(format);
    }

    // TODO writer or OutputStream ?
    // plain text, rtf: Writer
    // html: ?? (html, css, images)
    // also, need to convert CSS into whatever form the export format supports.
    // Perhaps, it should emit a sequence of StyledText's.
    public void export(DataFormat format, TextPos start, TextPos end, Writer wr) {
        ExportHandler h = getExportHandler(format);
        if (h == null) {
            throw new IllegalArgumentException("Data format is not supported: " + format);
        }

        // TODO
    }

    public ImportHandler registerImportHandler(ImportHandler handler) {
        return importHandlers.put(handler.getDataFormat(), handler);
    }

    /** returns data formats supported by {@link import()} operation */
    public DataFormat[] getSupportedImportFormats() {
        Set<DataFormat> formats = importHandlers.keySet();
        return formats.toArray(new DataFormat[formats.size()]);
    }

    protected ImportHandler getImportHandler(DataFormat format) {
        return importHandlers.get(format);
    }

    // TODO import from InputStream, String (multi-line), copy
    public void importText(DataFormat format, TextPos start, TextPos end, Object input) {
        ImportHandler h = getImportHandler(format);
        if (h == null) {
            throw new IllegalArgumentException("Data format is not supported: " + format);
        }

        // TODO
    }
    
    /**
     * Equivalent of the user typing text.
     * The style of the inserted text is determined by the model based on the surrounding text.
     * After the model makes necessary changes, the model emits an event to all registered ChangeListeners.
     * 
     * Inserted text cannot include any control characters (< 0x20) such as newlines, with a single exception
     * of the TAB character.
     * 
     * @param start
     * @param end
     * @param text text to be inserted.
     */
    @Deprecated // use the new method
    public void replace(TextPos start, TextPos end, String text) {
        // no-op in read-only model

        // TODO or assume: removeRegion + insert from (string, styled string, styled string iterator)
        // TODO via input handler?
        // TODO input: typed text; clipboard
        // TODO replace with sequence: remove range + insert(text, directStyle, css[])
        // TODO must work with multi-line text (separated by cr/crlf/lf)
        // TODO make sure start < pos
    }
    
    /**
     * Replaces given range with the provided styled text.
     * When inserting a plain text, the style is taken from preceding text segment, or, if the text is being
     * inserted into the beginning of the document, the style is taken from the following text segment.
     * 
     * @param start start position
     * @param end end position
     * @param input StyledInput
     * @return text position at the end of the inserted text, or null if the model is read only
     */
    public TextPos replace_NEW(TextPos start, TextPos end, StyledInput input) {
        if (isEditable()) {
            int cmp = start.compareTo(end);
            if (cmp > 0) {
                // make sure start < end
                TextPos p = start;
                start = end;
                end = p;
            }
            
            if(cmp != 0) {
                removeRegion(start, end);
            }

            int index = start.index();
            int offset = start.offset();
            int top = 0;
            int btm = 0;
            
            StyledText seg;
            while ((seg = input.nextSegment()) != null) {
                if(seg.isParagraph()) {
                    offset = 0;
                    btm = 0;
                    index++;
                }
                
                int len = insertSegment(index, offset, seg);
                offset += len;
                
                if(index == start.index()) {
                    top += len;
                }

                if (seg.isLineBreak()) {
                    index++;
                    btm = 0;
                } else {
                    btm += len;
                }
            }

            int lines = index - start.index();
            fireChangeEvent(start, end, top, lines, btm);
            
            return new TextPos(index, offset);
        }
        return null;
    }

    protected void removeRegion(TextPos start, TextPos end) {
        throw new UnsupportedOperationException();
    }

    /** returns the character count of the inserted text */
    protected int insertSegment(int index, int offset, StyledText text) {
        throw new UnsupportedOperationException();
    }

    // TODO remove, use multi-line replace()
    @Deprecated
    protected void insertLineBreak(TextPos pos) {
        throw new UnsupportedOperationException();
    }
    
    // TODO protected void insertText(TextPos, String);
    
    public void applyStyle(TextPos start, TextPos end, String direct, String[] css) {
        // no-op in read-only model
        // TODO the problem of applying a direct style is that it has to be intelligently merged, which
        // entails a lot of non-trivial string processing
    }
    
    public void removeStyle(TextPos start, TextPos end, String direct, String[] css) {
        // no-op in read-only model
        // TODO same problem in removing a direct style is that it has to be intelligently merged, which
        // entails a lot of non-trivial string processing
    }
    
    protected void fireChangeEvent(TextPos start, TextPos end, int charsTop, int linesAdded, int charsBottom) {
        System.out.println("fireChangeEvent start=" + start + " end=" + end + " top=" + charsTop + " lines=" + linesAdded + " btm=" + charsBottom); // FIX
        markers.update(start, end, charsTop, linesAdded, charsBottom);

        for (ChangeListener li : listeners) {
            li.eventTextUpdated(start, end, charsTop, linesAdded, charsBottom);
        }
    }

    public Marker newMarker(TextPos pos) {
        TextPos p = clamp(pos);
        return markers.newMarker(p);
    }

    private TextPos clamp(TextPos p) {
        int ct = getParagraphCount();
        int ix = p.index();
        if (ix < 0) {
            return TextPos.ZERO;
        } else if (ix < ct) {
            return p;
        } else {
            if (ct == 0) {
                return new TextPos(0, 0);
            } else {
                ix = ct - 1;
                String s = getPlainText(ix);
                int len = (s == null) ? 0 : s.length();
                return new TextPos(ct - 1, len);
            }
        }
    }
}
