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

package goryachev.research;

import java.io.File;
import java.text.MessageFormat;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.stage.Stage;

public class ImageImportResearch extends Application {
    
    public static void main(String[] args) throws Exception {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        
        // TODO: compress both, check against the detector
        // also return an info object
        // also a histogram
        String path = "/Users/angorya/Work/ImageImport";
        for (File f : new File(path).listFiles()) {
            Stats s = detectPhoto(new Image(f.toURI().toURL().toString()));
            IO.println((s.photo() ? "JPG" : "PNG") + " - " + s.cause() + " - " + f.getName());
        }
        System.exit(0);
    }

    private static int diff(int a, int b) {
        int d = diffChannel(a, b);
        a >>= 8;
        b >>= 8;
        d += diffChannel(a, b);
        a >>= 8;
        b >>= 8;
        d += diffChannel(a, b);
        a >>= 8;
        b >>= 8;
        d += diffChannel(a, b);
        return d;
    }

    private static int diffChannel(int a, int b) {
        int d = (a & 0xff) - (b & 0xff);
        return d < 0 ? -d : d;
    }

    private static String list(int[] a) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i=0; i<a.length; i++) {
            if(i > 0) {
                sb.append(",");
            }
            sb.append(a[i]);
        }
        sb.append("]");
        return sb.toString();
    }

    private Stats detectPhoto(Image im) {
        int SMALL_SIZE = 128;
        int MAX_DIFF = 255 * 4;
        
        int w = (int)im.getWidth();
        int h = (int)im.getHeight();

        // small image -> png
        if ((w < SMALL_SIZE) && (h < SMALL_SIZE)) {
            return new Stats(false, "small");
        }

        // alpha channel -> png
        PixelReader rd = im.getPixelReader();

        int parts = 4;
        int stepx = w / parts;
        int stepy = h / parts;
        int x;
        int y;

        int total = 0;
        int same = 0;
        long diff = 0;

        int jumpBands = 4;
        int[] jumps = new int[jumpBands];
        int[] thresholds = new int[jumpBands + 1];
        for(int i=0; i<jumpBands; i++) {
            // linear
            int t = (MAX_DIFF * (i + 1)) / jumpBands;
            thresholds[i + 1] = t;
        }
        
        long start = System.nanoTime();

        x = stepx;
        for (int ix = 1; ix < parts; ix++) {
            int prev = 0;
            for (y = 0; y < h; y++) {
                int argb = rd.getArgb(x, y);

                // alpha
                if((argb & 0xff000000) != 0xff000000) {
                    return new Stats(false, "alpha");
                }

                total++;
                if (prev == argb) {
                    same++;
                }
                
                int d = diff(argb, prev);
                for (int i = 0; i < jumpBands; i++) {
                    if (d <= thresholds[i]) {
                        jumps[i]++;
                        break;
                    }
                }
                diff += d;
                
                prev = argb;
            }
            x += stepx;
        }

        y = stepy;
        for (int iy = 1; iy < parts; iy++) {
            int prev = 0;
            for (x = 0; x < w; x++) {
                int argb = rd.getArgb(x, y);

                // alpha
                if((argb & 0xff000000) != 0xff000000) {
                    return new Stats(false, "alpha");
                }

                total++;
                if (prev == argb) {
                    same++;
                }
                
                int d = diff(argb, prev);
                for (int i = 0; i < jumpBands; i++) {
                    if (d <= thresholds[i]) {
                        jumps[i]++;
                        break;
                    }
                }
                diff += d;
                
                prev = argb;
            }
            y += stepy;
        }

        long end = System.nanoTime();

        // TODO
        // possible cases where heuristics might fail:
        // - a photo with large solid color

        // sharp edges dominate -> png
        String s = (MessageFormat.format("same={0}% jumps={1} diff={2,number,#0.0}% time={3,number,#0.0} ms",
            (int)((100L * same) / total),
            list(jumps),
            ((100.0 * diff / MAX_DIFF) / total),
            (end - start)/1_000_000.0
            ));
        return new Stats(false, s);
    }

    static record Stats(boolean photo, String cause) { }
}
