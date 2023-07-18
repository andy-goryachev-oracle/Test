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

import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.WritableValue;
import javafx.css.CssMetaData;
import javafx.css.FontCssMetaData;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.control.rich.RichTextArea;
import javafx.scene.control.rich.TextCell;
import javafx.scene.control.rich.skin.LineNumberDecorator;
import javafx.scene.control.util.Util;
import javafx.scene.text.Font;
import javafx.scene.text.TextFlow;

/**
 * CodeArea is a text component which supports styling (a.k.a. "syntax highlighting") of monospaced text.
 */
// TODO show line numbers - use font
public class CodeArea extends RichTextArea {
    private ObjectProperty<Font> font;
    private BooleanProperty lineNumbers;
    private String fontStyle;

    public CodeArea(CodeModel m) {
        super(m);
        modelProperty().addListener((s, prev, newValue) -> {
            // TODO perhaps even block any change of (already set CodeModel)
            if (newValue != null) {
                if (!(newValue instanceof CodeModel)) {
                    setModel(prev);
                    throw new IllegalArgumentException("model must be of type " + CodeModel.class);
                }
            }
        });
    }

    public CodeArea() {
        this(new CodeModel());
    }

    /**
     * The default font to use for text in the TextInputControl. If the TextInputControl's text is
     * rich text then this font may or may not be used depending on the font
     * information embedded in the rich text, but in any case where a default
     * font is required, this font will be used.
     * @return the font property
     */
    public final ObjectProperty<Font> fontProperty() {
        if (font == null) {
            font = new StyleableObjectProperty<Font>(getDefaultFont()) {
                private boolean fontSetByCss;

                @Override
                public void applyStyle(StyleOrigin newOrigin, Font value) {
                    // RT-20727 JDK-8127428 
                    // if CSS is setting the font, then make sure invalidate doesn't call NodeHelper.reapplyCSS
                    try {
                        // super.applyStyle calls set which might throw if value is bound.
                        // Have to make sure fontSetByCss is reset.
                        fontSetByCss = true;
                        super.applyStyle(newOrigin, value);
                    } catch (Exception e) {
                        throw e;
                    } finally {
                        fontSetByCss = false;
                    }
                }

                @Override
                public void set(Font f) {
                    Font oldValue = get();
                    if (f == null ? oldValue == null : f.equals(oldValue)) {
                        return;
                    }
                    super.set(f);
                }

                @Override
                protected void invalidated() {
                    // RT-20727 JDK-8127428 - if font is changed by calling setFont, then
                    // css might need to be reapplied since font size affects
                    // calculated values for styles with relative values
                    if (fontSetByCss == false) {
                        // FIX
                        // NodeHelper.reapplyCSS(CodeArea.this);
                        layoutChildren();
                    }
                    fontStyle = null;
                }

                @Override
                public CssMetaData<CodeArea, Font> getCssMetaData() {
                    return StyleableProperties.FONT;
                }

                @Override
                public Object getBean() {
                    return CodeArea.this;
                }

                @Override
                public String getName() {
                    return "font";
                }
            };
        }
        return font;
    }

    public final void setFont(Font value) {
        fontProperty().setValue(value);
    }

    public final Font getFont() {
        return font == null ? Font.getDefault() : font.getValue();
    }

    private static class StyleableProperties {
        private static final FontCssMetaData<CodeArea> FONT = new FontCssMetaData<>("-fx-font", Font.getDefault()) {

            @Override
            public boolean isSettable(CodeArea n) {
                return n.font == null || !n.font.isBound();
            }

            @Override
            public StyleableProperty<Font> getStyleableProperty(CodeArea n) {
                return (StyleableProperty<Font>)(WritableValue<Font>)n.fontProperty();
            }
        };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES =
            Util.initStyleables(RichTextArea.getClassCssMetaData(), FONT);
    }

    /**
     * Gets the {@code CssMetaData} associated with this class, which may include the
     * {@code CssMetaData} of its superclasses.
     * @return the {@code CssMetaData}
     */
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

    private CodeModel codeModel() {
        return (CodeModel)getModel();
    }

    // TODO another school of thought suggests to move the highlighter property here.
    public void setSyntaxHighlighter(SyntaxDecorator d) {
        var m = codeModel();
        if (m != null) {
            m.setDecorator(d);
        }
    }

    public SyntaxDecorator getSyntaxDecorator() {
        var m = codeModel();
        return (m == null) ? null : m.getDecorator();
    }

    private static Font getDefaultFont() {
        Font f = Font.getDefault();
        return Font.font("monospace", f.getSize());
    }

    protected String fontStyle() {
        if (fontStyle == null) {
            Font f = getFont();
            fontStyle = "-fx-font-family:'" + f.getFamily() + "'; -fx-font-size:" + f.getSize() + ";";
        }
        return fontStyle;
    }

    // sets direct font style on a TextFlow-based TextCell
    protected TextCell createTextCell(int modelIndex) {
        TextCell c = getModel().createTextCell(modelIndex);
        if (c.getContent() instanceof TextFlow f) {
            String st = fontStyle();
            f.setStyle(st);
        }
        return c;
    }

    public BooleanProperty lineNumbersEnabledProperty() {
        if (lineNumbers == null) {
            lineNumbers = new SimpleBooleanProperty() {
                @Override
                protected void invalidated() {
                    LineNumberDecorator d;
                    if(get()) {
                        d = new LineNumberDecorator() {
                            @Override
                            public Node getNode(int ix, boolean forMeasurement) {
                                Node n = super.getNode(ix, forMeasurement);
                                if(n instanceof Labeled t) {
                                    t.fontProperty().bind(fontProperty());
                                }
                                return n;
                            }
                        };
                    } else {
                        d = null;
                    }
                    setLeftDecorator(d);
                }
            };
        }
        return lineNumbers;
    }

    public boolean isLineNumbersEnabled() {
        return lineNumbers == null ? false : lineNumbers.get();
    }

    public void setLineNumbersEnabled(boolean on) {
        lineNumbersEnabledProperty().set(on);
    }
}
