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

package goryachev.rich.code;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.rich.model.BasePlainTextModel;
import javafx.scene.control.rich.model.RichParagraph;

public class CodeModel extends BasePlainTextModel {
    private final SimpleObjectProperty<SyntaxDecorator> decorator = new SimpleObjectProperty<>();

    public CodeModel() {
    }

    @Override
    public final RichParagraph getParagraph(int index) {
        SyntaxDecorator d = getDecorator();
        if (d == null) {
            return super.getParagraph(index);
        } else {
            String text = getPlainText(index);
            return d.createRichParagraph(text);
        }
    }
    
    public final SyntaxDecorator getDecorator() {
        return decorator.get();
    }

    public final void setDecorator(SyntaxDecorator d) {
        decorator.set(d);
    }

    public final ObjectProperty<SyntaxDecorator> decoratorProperty() {
        return decorator;
    }
}
