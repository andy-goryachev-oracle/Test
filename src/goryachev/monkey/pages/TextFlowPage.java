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
package goryachev.monkey.pages;

import java.util.Locale;
import goryachev.monkey.util.FontSelector;
import goryachev.monkey.util.OptionPane;
import goryachev.monkey.util.ShowCharacterRuns;
import goryachev.monkey.util.TestPaneBase;
import goryachev.monkey.util.TextSelector;
import goryachev.monkey.util.Utils;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * TextFlow Page
 */
public class TextFlowPage extends TestPaneBase {
    private final TextSelector textSelector;
    private final FontSelector fontSelector;
    private final CheckBox showChars;
    private final TextFlow control;
    private Locale defaultLocale;
    private static final String INLINE = "$INLINE";

    public TextFlowPage() {
        setId("TextFlowPage");
        
        control = new TextFlow();
        
        textSelector = TextSelector.fromPairs(
            "textSelector", 
            (t) -> updateControl(),
            Utils.combine(
                Templates.multiLineTextPairs(),
                "Inline Nodes", INLINE
            )
        );
        
        fontSelector = new FontSelector("font", (f) -> updateControl());
        
        showChars = new CheckBox("show characters");
        showChars.setId("showChars");
        showChars.selectedProperty().addListener((p) -> {
            updateControl();
        });

        OptionPane p = new OptionPane();
        p.label("Text:");
        p.option(textSelector.node());
        p.label("Font:");
        p.option(fontSelector.fontNode());
        p.label("Font Size:");
        p.option(fontSelector.sizeNode());
        p.option(showChars);
        
        setContent(control);
        setOptions(p);

        fontSelector.selectSystemFont();
        textSelector.selectFirst();
    }

    protected void updateControl() {
        Font f = fontSelector.getFont();
        String text = textSelector.getSelectedText();
        Node[] ts = createTextArray(text, f);
        control.getChildren().setAll(ts);

        if (showChars.isSelected()) {
            Group g = ShowCharacterRuns.createFor(control);
            control.getChildren().add(g);
        }
    }

    protected Node[] createTextArray(String text, Font f) {
        if (INLINE.equals(text)) {
            return new Node[] {
                t("Inline Nodes:", f),
                new Button("Left"),
                t(" ", f),
                new Button("Right"),
                t("trailing", f)
            };
        } else {
            return new Node[] { t(text, f) };
        }
    }
    
    protected static Text t(String text, Font f) {
        Text t = new Text(text);
        t.setFont(f);
        return t;
    }
}
