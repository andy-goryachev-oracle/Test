/*
 * Copyright (c) 2026, Oracle and/or its affiliates. All rights reserved.
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
import javafx.scene.control.ScrollPane;
import javafx.scene.image.DrawingContext;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class DrawingContext_8390915 extends Application {

    private static final int SIZE = 2048;
    private static final Random random = new Random();

    @Override
    public void start(Stage stage) {
        WritableImage im = new WritableImage(SIZE, SIZE);
        DrawingContext cx = im.getDrawingContext();
        plop(cx);

        ImageView v = new ImageView(im);
        BorderPane bp = new BorderPane(new ScrollPane(v));

        Scene scene = new Scene(bp, 500, 500);
        stage.setScene(scene);
        stage.setTitle(getClass().getSimpleName());
        stage.show();

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    for (;;) {
                        Thread.sleep(10);
                        Platform.runLater(() -> {
                            plop(cx);
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.setDaemon(true);
        t.start();
    }

    private static Color color() {
        return Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble(), random.nextDouble());
    }

    private static void plop(DrawingContext cx) {
        double x = 2 * SIZE * random.nextDouble() - SIZE;
        double y = 2 * SIZE * random.nextDouble() - SIZE;
        double r = 50 * random.nextDouble();
        String text = String.valueOf(random.nextLong());
        cx.setFill(color());
        cx.setStroke(color());

        int op = random.nextInt(4);
        switch (op) {
        case 0:
            cx.fillOval(x, y, r, r);
            break;
        case 1:
            cx.fillText(text, x, y);
        case 2:
            cx.strokeText(text, x, y);
            break;
        case 3:
            cx.fillRect(x, y, r, r);
            break;
        }
    }
}
