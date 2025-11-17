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

import java.util.Random;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Does writing to an image invalidates its dimensions and requests layout?
 */
public class ImageView_Pixels_8372007 extends Application {
    @Override
    public void start(Stage stage) {
        WritableImage im = new WritableImage(100, 100);
        PixelWriter wr = im.getPixelWriter();
        ImageView v = new ImageView(im);
        BorderPane bp = new BorderPane(v) {
            @Override
            public void requestLayout() {
                System.out.println("requestLayout");
                super.requestLayout();
            };
        };
        Scene scene = new Scene(bp, 300, 250);
        stage.setScene(scene);
        stage.setTitle(getClass().getSimpleName());
        stage.show();

        Random r = new Random();
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    for (;;) {
                        Thread.sleep(500);
                        Platform.runLater(() -> {
                            System.out.println("writing pixels");
                            wr.setColor(10, 10, Color.color(r.nextDouble(), r.nextDouble(), r.nextDouble()));
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.setDaemon(true);
        t.start();
    }
}
