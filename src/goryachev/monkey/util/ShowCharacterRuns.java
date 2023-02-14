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
package goryachev.monkey.util;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.text.HitInfo;
import javafx.scene.text.Text;

/**
 * Shows character boundaries using navigation code similar to TextArea.nextCharacterVisually()
 */
public class ShowCharacterRuns extends Group {
    public ShowCharacterRuns() {
        setManaged(false);
    }

    public static Group createFor(Text text) {
        ShowCharacterRuns r = new ShowCharacterRuns();
        int len = text.getText().length();
        for (int i = 0; i < len; i++) {
            PathElement[] caret = text.caretShape(i, true);
            if (caret.length == 4) {
                caret = new PathElement[] {
                    caret[0],
                    caret[1]
                };
            }

            Bounds caretBounds = new Path(caret).getLayoutBounds();
            double x = caretBounds.getMaxX();
            double y = (caretBounds.getMinY() + caretBounds.getMaxY()) / 2;
            HitInfo hit = text.hitTest(new Point2D(x, y));
            Path cs = new Path(text.rangeShape(hit.getCharIndex(), hit.getCharIndex() + 1));
            System.err.println(i + " " + cs); // FIX
            Color c = ((i % 2) == 0) ? Color.rgb(255, 0, 0, 0.5) : Color.rgb(0, 255, 0, 0.5);
            cs.setFill(c);
            cs.setStroke(c);
            r.getChildren().add(cs);
        }
        return r;
    }
    
    /*
    private void nextCharacterVisually(boolean moveRight) {
        Text textNode = getTextNode();
        Bounds caretBounds = caretPath.getLayoutBounds();
        if (caretPath.getElements().size() == 4) {
            // The caret is split
            // TODO: Find a better way to get the primary caret position
            // instead of depending on the internal implementation.
            // See RT-25465.
            caretBounds = new Path(caretPath.getElements().get(0), caretPath.getElements().get(1)).getLayoutBounds();
        }
        double hitX = moveRight ? caretBounds.getMaxX() : caretBounds.getMinX();
        double hitY = (caretBounds.getMinY() + caretBounds.getMaxY()) / 2;
        HitInfo hit = textNode.hitTest(new Point2D(hitX, hitY));
        boolean leading = hit.isLeading();
        Path charShape = new Path(textNode.rangeShape(hit.getCharIndex(), hit.getCharIndex() + 1));
        if ((moveRight && charShape.getLayoutBounds().getMaxX() > caretBounds.getMaxX()) ||
                (!moveRight && charShape.getLayoutBounds().getMinX() < caretBounds.getMinX())) {
            leading = !leading;
            positionCaret(hit.getInsertionIndex(), leading, false, false);
        } else {
            // We're at beginning or end of line. Try moving up / down.
            int dot = textArea.getCaretPosition();
            targetCaretX = moveRight ? 0 : Double.MAX_VALUE;
            // TODO: Use Bidi sniffing instead of assuming right means forward here?
            downLines(moveRight ? 1 : -1, false, false);
            targetCaretX = -1;
            if (dot == textArea.getCaretPosition()) {
                if (moveRight) {
                    textArea.forward();
                } else {
                    textArea.backward();
                }
            }
        }
    }
    */
}
