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

/**
 * Represents a styled text model.
 * The text is considered to be a collection of paragraphs, represented by {@link StyledParagraph} class.
 * 
 * TODO events
 * TODO listeners
 * TODO editing
 * TODO is read only
 * TODO isModified()
 */
public interface StyledTextModel {
    /**
     * Returns the number of paragraphs in the model.
     */
    public int getParagraphCount();

    /**
     * Returns the specified paragraph.  The caller should never attempt to ask for a paragraph outside of the
     * valid range.
     *
     * @param index paragraph index in the range (0...{@link getParagraphCount()})
     */
    public StyledParagraph getParagraph(int index);

    /**
     * Returns the plain text string for the specified paragraph.
     * The caller should never attempt to ask for a paragraph outside of the valid range.
     *
     * @param index paragraph index in the range (0...{@link getParagraphCount()})
     */
    public String getPlainText(int index);
}
