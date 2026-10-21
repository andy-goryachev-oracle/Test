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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class DrawingContext_8390915 extends Application {

    private static final int SIZE = 2048;
    private static final Random random = new Random();

    @Override
    public void start(Stage stage) {
        /*
         * A WritableImage is a 1x bitmap. Rendering it at the screen's output
         * scale and displaying it at its logical size avoids the ImageView
         * smoothing (and blurring) the image on a HiDPI display.
         */
        double outputScale = Math.max(
            Screen.getPrimary().getOutputScaleX(),
            Screen.getPrimary().getOutputScaleY());
        int imageSize = (int)Math.ceil(SIZE * outputScale);

        WritableImage im = new WritableImage(imageSize, imageSize);
        DrawingContext cx = im.getDrawingContext();
        cx.scale(outputScale, outputScale); // Undoes the larger images size by scaling all operations back down
        plop(cx);

        ImageView v = new ImageView(im);
        v.setFitWidth(SIZE);
        v.setFitHeight(SIZE);
        v.setSmooth(false);

        BorderPane bp = new BorderPane(new ScrollPane(v));

        Scene scene = new Scene(bp, 1200, 800);
        stage.setScene(scene);
        stage.setTitle(getClass().getSimpleName() + " (scale " + outputScale + ")");
        stage.show();

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    for (;;) {
                        Thread.sleep(1);
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
    
    private static Color color(boolean withNull) {
        if(withNull) {
            if (random.nextDouble() < 0.1) {
                return null;
            }
        }
        return color();
    }

    private static double rnd(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
    
    private static double rnd(double max) {
        return max * random.nextDouble();
    }

    private static <T extends Enum> T rnd(Class<T> type) {
        T[] vals = type.getEnumConstants();
        int ix = random.nextInt(vals.length);
        return vals[ix];
    }
    
    private static double[] rndArr(int sz) {
        double[] a = new double[sz];
        double v = SIZE * random.nextDouble();
        for (int i = 0; i < sz; i++) {
            if (i > 0) {
                v += rnd(-50, 50);
            }
            a[i] = v;
        }
        return a;
    }

    private static void plop(DrawingContext cx) {
        double x = 2 * SIZE * random.nextDouble() - SIZE;
        double y = 2 * SIZE * random.nextDouble() - SIZE;
        double r = 50 * random.nextDouble();
        String text = String.valueOf(random.nextLong());
        cx.setFill(color());
        cx.setStroke(color());

        int op = random.nextInt(20);
        switch (op) {
        case 0:
            cx.fillOval(x, y, r, r);
            break;
        case 1:
            cx.fillText(text, x, y);
            break;
        case 2:
            cx.strokeText(text, x, y);
            break;
        case 3:
            cx.fillRect(x, y, r, r);
            break;
        case 4:
            cx.beginPath();
            cx.arc(x, y, r, r, rnd(0, 360), rnd(100));
            break;
        case 5:
            cx.beginPath();
            cx.arcTo(x, y, x + rnd(-100, 100), y + rnd(-100, 100), r);
            break;
        case 6:
            int n = random.nextInt(7);
            cx.fillPolygon(rndArr(n), rndArr(n), n);
            break;
        case 7:
            cx.beginPath();
            cx.bezierCurveTo(x, y, x + rnd(-100, 100), y + rnd(-100, 100), x + rnd(-100, 100), y + rnd(-100, 100));
            break;
        case 8:
            cx.clearRect(x, y, r, r);
            break;
        case 9:
            cx.closePath();
            break;
        case 10:
            Rectangle rc = new Rectangle(0, 1 + rnd(100));
            rc.setFill(color(false));
            Image im = rc.snapshot(null, null);
            cx.drawImage(im, x, y);
            break;
        case 11:
            cx.fill();
            break;
        case 12:
            cx.beginPath();
            cx.fillArc(x, y, r, r, rnd(0, 360), rnd(0, 360), rnd(ArcType.class));
            break;
        case 13:
            cx.fillRoundRect(x, y, r, r, rnd(10), rnd(10));
            break;
        }
    }
}
