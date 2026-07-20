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

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;

// https://bugs.openjdk.org/browse/JDK-8388511
// copying a JPG image which has no alpha channel to the system clipboard should not add an alpha channel
// see also https://bugs.openjdk.org/browse/JDK-8098140
public class Clipboard_Jpg_8388511 extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        String url = Clipboard_Jpg_8388511.class.getResource("small-jpeg.jpg").toString();

        Clipboard.getSystemClipboard().clear();

        ClipboardContent cc = new ClipboardContent();
        cc.putImage(new Image(url));
        Clipboard.getSystemClipboard().setContent(cc);

        Platform.runLater(() -> {
            step2();
        });
    }

    private void step2() {
        Image im = Clipboard.getSystemClipboard().getImage();
        
        // assertNonNull(im);
        if (im == null) {
            IO.println("null image");
        }
        PixelFormat.Type t = im.getPixelReader().getPixelFormat().getType();
        boolean hasAlpha = hasAlpha(t);
        
        // assertFalse(hasAlpha);
        if (hasAlpha) {
            IO.println("JPG image should not have alpha channel: " + t);
        }
        
        System.exit(0);
    }

    private static boolean hasAlpha(PixelFormat.Type t) {
        return switch (t) {
        case BYTE_BGRA -> true;
        case BYTE_BGRA_PRE -> true;
        case BYTE_INDEXED -> false; // for the purposes of this test
        case BYTE_RGB -> false;
        case INT_ARGB -> true;
        case INT_ARGB_PRE -> true;
        };
    }
}
