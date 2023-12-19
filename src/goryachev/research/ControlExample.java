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

package goryachev.research;

import java.util.List;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.css.converter.SizeConverter;
import javafx.scene.text.Font;

public class ControlExample extends Control {
    private static final int DEFAULT_TAB_SIZE = 8;
    private StyleableProperty<Number> tabSize;
    private StyleableProperty<Font> font;
    private Styleable two;
    
    private List<CssMetaData<? extends Styleable, ?>> classMetadata = List.of(
        CssMetaData.<ControlExample,Number>of("-fx-tab-size", SizeConverter.getInstance(), DEFAULT_TAB_SIZE, (n) -> n.tabSize),
        CssMetaData.<ControlExample,Font>of("-fx-font", Font.getDefault(), (n) -> n.font)
    );
    
    private static String[] css = CssUtils.of(ControlExample.class);
    
    @Override
    protected List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return classMetadata;
    }

    @AStyleable
    public Styleable styleablePropertyTwo() {
        if(two == null) {
            two = new Styleable() {
                @Override
                public String getTypeSelector() {
                    return null;
                }
                
                @Override
                public Styleable getStyleableParent() {
                    return null;
                }
                
                @Override
                public ObservableList<String> getStyleClass() {
                    return null;
                }
                
                @Override
                public String getStyle() {
                    return null;
                }
                
                @Override
                public ObservableSet<PseudoClass> getPseudoClassStates() {
                    return null;
                }
                
                @Override
                public String getId() {
                    return null;
                }
                
                @Override
                public List<javafx.css.CssMetaData<? extends Styleable, ?>> getCssMetaData() {
                    return null;
                }
            };
        }
        return two;
    }
    
    public static void main(String[] args) {
        new ControlExample();
        System.exit(0);
    }
}
