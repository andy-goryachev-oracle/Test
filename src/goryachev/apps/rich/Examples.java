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

import goryachev.rich.RichTextArea;
import goryachev.rich.model.EditableRichTextModel;
import goryachev.rich.model.SimpleReadOnlyStyledModel;

/**
 * Code Examples to be Inlcuded in the Design Document
 */
public class Examples {
    /** creates an editable rich text control */
    public void editableControl() {
        RichTextArea t = new RichTextArea();
        t.setModel(new EditableRichTextModel());
    }

    /** creates an editable rich text control */
    public void readOnlyControl() {
        SimpleReadOnlyStyledModel m = new SimpleReadOnlyStyledModel();
        // add text segment using CSS style name (requires a style sheet)
        m.addSegment("Demo ", null, "HEADER");
        // add text segment using direct style
        m.addSegment("Demo ", "-fx-font-size:200%;", null);
        // newline
        m.nl();

        RichTextArea t = new RichTextArea();
        t.setModel(m);
    }
}
