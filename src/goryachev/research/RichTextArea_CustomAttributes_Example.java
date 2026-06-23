/*
 * Copyright (c) 2026, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package goryachev.research;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import jfx.incubator.scene.control.richtext.RichTextArea;
import jfx.incubator.scene.control.richtext.StyleHandlerRegistry;
import jfx.incubator.scene.control.richtext.TextPos;
import jfx.incubator.scene.control.richtext.model.StyleAttribute;
import jfx.incubator.scene.control.richtext.model.StyleAttributeMap;


/// Demonstrates a custom CSS Style attribute.
/// 
/// @author Andy Goryachev
/// 
public class RichTextArea_CustomAttributes_Example extends Application {

    /// Allows to store direct CSS styles in the model
    /// The value is a String representing a valid CSS style, for example:
    /// 
    /// `-fx-font-size:200%; -fx-underline:true;`
    public static final StyleAttribute<String> CSS_CHAR = StyleAttribute.character("CSS_CHAR", String.class);
    
    @Override
    public void start(Stage stage) throws Exception {
        RichTextArea r = new RichTextArea() {
            private static final StyleHandlerRegistry registry = init();

            private static StyleHandlerRegistry init() {
                // brings in the handlers from the base class
                StyleHandlerRegistry.Builder b = StyleHandlerRegistry.builder(RichTextArea.styleHandlerRegistry);
                // adds a handler for the new attribute
                b.setSegHandler(CSS_CHAR, (c, cx, style) -> {
                    cx.addStyle(style);
                });
                return b.build();
            }

            @Override
            public StyleHandlerRegistry getStyleHandlerRegistry() {
                return registry;
            }
        };

        r.appendText("""
            will disappear
            like the morning dew
            in the sunshine
            """);
        r.select(TextPos.ZERO);
        r.applyStyle(TextPos.ofLeading(1, 0), TextPos.ofLeading(1, 100), StyleAttributeMap.of(CSS_CHAR, "-fx-font-size:200%; -fx-underline:true;"));

        BorderPane pane = new BorderPane();
        pane.setCenter(r);
        stage.setScene(new Scene(pane));
        stage.show();
    }
}