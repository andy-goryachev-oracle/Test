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

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.io.ByteArrayOutputStream;
import java.nio.IntBuffer;
import java.util.Base64;
import javax.imageio.ImageIO;
import javafx.application.Application;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Canvas_CreateImage extends Application {

    @Override
    public void start(Stage stage) {
        try {
            int w = 32;
            int h = 32;
            Canvas c = new Canvas(w, h);
            GraphicsContext g = c.getGraphicsContext2D();
            g.setFill(Color.BLUE);
            g.fillRect(0, 0, w, h);
//            g.fillRect(0, 0, w, h / 2.0);
//            g.setFill(Color.YELLOW);
//            g.fillRect(0, h / 2.0, w, h / 2.0);
            WritableImage im = c.snapshot(null, null);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.setUseCache(false);
            ImageIO.write(fromFXImage(im), "PNG", out);
            byte[] b = out.toByteArray();
            String b64 = Base64.getEncoder().encodeToString(b);
            System.out.println("data:image/png;base64," + b64 + "\n");
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(-1);
        }
        System.exit(0);
    }

    public static BufferedImage fromFXImage(Image img) {
        PixelReader pr = img.getPixelReader();
        int iw = (int)img.getWidth();
        int ih = (int)img.getHeight();
        PixelFormat<?> fxFormat = pr.getPixelFormat();
        boolean srcPixelsAreOpaque = false;
        switch (fxFormat.getType()) {
        case INT_ARGB_PRE:
        case INT_ARGB:
        case BYTE_BGRA_PRE:
        case BYTE_BGRA:
            break;
        case BYTE_RGB:
            srcPixelsAreOpaque = true;
            break;
        }
        int prefBimgType = getBestBufferedImageType(pr.getPixelFormat(), srcPixelsAreOpaque);
        BufferedImage bimg = new BufferedImage(iw, ih, prefBimgType);
        DataBufferInt db = (DataBufferInt)bimg.getRaster().getDataBuffer();
        int data[] = db.getData();
        int offset = bimg.getRaster().getDataBuffer().getOffset();
        int scan = 0;
        SampleModel sm = bimg.getRaster().getSampleModel();
        if (sm instanceof SinglePixelPackedSampleModel) {
            scan = ((SinglePixelPackedSampleModel)sm).getScanlineStride();
        }

        WritablePixelFormat<IntBuffer> pf = getAssociatedPixelFormat(bimg);
        pr.getPixels(0, 0, iw, ih, pf, data, offset, scan);
        return bimg;
    }

    private static int getBestBufferedImageType(PixelFormat<?> fxFormat, boolean isOpaque) {
        switch (fxFormat.getType()) {
        default:
        case BYTE_BGRA_PRE:
        case INT_ARGB_PRE:
            return BufferedImage.TYPE_INT_ARGB_PRE;
        case BYTE_BGRA:
        case INT_ARGB:
            return BufferedImage.TYPE_INT_ARGB;
        case BYTE_RGB:
            return BufferedImage.TYPE_INT_RGB;
        case BYTE_INDEXED:
            return (fxFormat.isPremultiplied()
                ? BufferedImage.TYPE_INT_ARGB_PRE
                : BufferedImage.TYPE_INT_ARGB);
        }
    }

    private static boolean checkFXImageOpaque(PixelReader pr, int iw, int ih) {
        for (int x = 0; x < iw; x++) {
            for (int y = 0; y < ih; y++) {
                Color color = pr.getColor(x, y);
                if (color.getOpacity() != 1.0) {
                    return false;
                }
            }
        }
        return true;
    }

    private static WritablePixelFormat<IntBuffer> getAssociatedPixelFormat(BufferedImage bim) {
        switch (bim.getType()) {
        case BufferedImage.TYPE_INT_RGB:
        case BufferedImage.TYPE_INT_ARGB_PRE:
            return PixelFormat.getIntArgbPreInstance();
        case BufferedImage.TYPE_INT_ARGB:
            return PixelFormat.getIntArgbInstance();
        default:
            throw new Error("Failed to validate BufferedImage type");
        }
    }
}
