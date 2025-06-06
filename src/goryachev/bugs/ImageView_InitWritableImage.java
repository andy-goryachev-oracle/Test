/*
 * Copyright (c) 2025, Oracle and/or its affiliates. All rights reserved.
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
package goryachev.bugs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Can we create an ImageView with an animated image or WritableImage in a background thread?
 */
public class ImageView_InitWritableImage extends Application {
    @Override
    public void start(Stage stage) {
        ImageView v = new ImageView(new Image("/img/animated.gif"));
        Scene scene = new Scene(new BorderPane(v), 300, 250);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("ImageView with WritableImage in BG Thread");

        new Thread() {
            @Override
            public void run() {
                ImageView v2 = new ImageView();
                for (;;) {
                    WritableImage im = new WritableImage(100, 100);
                    v2.setImage(im);
                    im = new WritableImage(200, 200);
                    v2.setImage(im);
                    v2.setImage(new Image("/img/animated.gif"));
                    
                    ImageView v3 = new ImageView(new Image("/img/animated.gif"));
                }
            }
        }.start();
    }
}
