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
package goryachev.rich.model;

import org.junit.Assert;
import org.junit.Test;
import goryachev.rich.TextPos;

public class TestStyledRuns {
    private final StyleAttrs PLAIN = mk();
    private final StyleAttrs BOLD = mk(StyleAttrs.BOLD);
    private final Object[] SEG1 = { "--------", PLAIN };
    private final Object[] SEG2 = { "--------", PLAIN, "--------", BOLD };
    private final Object[] SEG3 = { "--------", PLAIN, "--------", BOLD, "--------", PLAIN };
    
    @Test
    public void testApplyStyle() {
        t(SEG1, 0, 2, 0, 5, BOLD, new Object[] {
            0, PLAIN, 2, BOLD, 5, PLAIN
        });
    }
    
    private static StyleAttrs mk(Object... spec) {
        StyleAttrs rv = new StyleAttrs();
        for (int i = 0; i < spec.length;) {
            StyleAttribute a = (StyleAttribute)spec[i++];
            Object v;
            if (i < spec.length) {
                v = spec[i];
                if (v instanceof StyleAttribute) {
                    v = null;
                }
            } else {
                v = null;
            }

            if (v == null) {
                v = Boolean.TRUE;
            }
            rv.set(a, v);
        }
        return rv;
    }
    
    private void append(EditableRichTextModel m, Object[] items) {
        TStyledInput in = new TStyledInput(items);
        TextPos end = m.getEndTextPos();
        m.replace(TextPos.ZERO, end, in);
    }

    // FIX does not work (yet)
    private void t(Object[] initial, int ix1, int off1, int ix2, int off2, StyleAttrs a, Object[] expected) {
        EditableRichTextModel m = new EditableRichTextModel();
        append(m, initial);
        TextPos start = new TextPos(ix1, off1);
        TextPos end = new TextPos(ix2, off2);
        m.applyStyle(start, end, a);
        
        TStyledOutput out = new TStyledOutput();
        TextPos last = m.getEndTextPos();
        m.exportText(TextPos.ZERO, last, out);
        Object[] result = out.getResult();
        Assert.assertArrayEquals(expected, result);
    }
}
