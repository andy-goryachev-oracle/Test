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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.concurrent.CountDownLatch;
import javax.imageio.ImageIO;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;
import goryachev.util.ImgUtil;

// https://bugs.openjdk.org/browse/JDK-8388450
public class ImageWriteTest extends Application {
    
    Label view;
    
    @FunctionalInterface
    interface TestMethod {
        public void run() throws Throwable;
    }
    
    public static void main(String[] args) throws Exception {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        view = new Label();
        Scene sc = new Scene(view, 1000, 500);
        stage.setScene(sc);
        stage.show();
        
        // change to match the test image path
        test("/Users/angorya/Work/ImageImport/1/eclipse-key-mappings.png");
        
        // bulk test
        new Thread(this::execute).start();
    }

    private void test(String filename) throws Exception {
        File f = new File(filename);
        byte[] b = Files.readAllBytes(f.toPath());
        Image im = new Image(new ByteArrayInputStream(b));
        check(im, f, "test");
    }
    
    private void execute() {
        String path = "/Users/angorya/Work/ImageImport/all";
        for (File f : new File(path).listFiles()) {
            if(!isImage(f)) {
                continue;
            }
            IO.println(f);
            
            exec(() -> direct(f, false));
            exec(() -> direct(f, true));
            exec(() -> copyToClipboard(f, false));
            exec(() -> fromClipboard(f, false));
            exec(() -> copyToClipboard(f, true));
            exec(() -> fromClipboard(f, true));
            
            exec(() -> IO.println());
        }
        exec(() -> System.exit(0));
    }

    private static boolean isImage(File f) {
        if(f.isFile()) {
            String s = f.getName().toLowerCase();
            return
                s.endsWith(".jpg") ||
                s.endsWith(".jpeg") ||
                s.endsWith(".png") ||
                s.endsWith(".gif");
        }
        return false;
    }

    private void exec(TestMethod m) {
        CountDownLatch latch = new CountDownLatch(1);
        
        Platform.runLater(() -> {
            try {
                m.run();
            } catch(Throwable e) {
                e.printStackTrace();
            }
            latch.countDown();
        });
        
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void direct(File f, boolean useBytes) throws Exception {
        Image im = loadImage(f, useBytes);
        
        String src = useBytes ? "bytes" : "url";
        check(im, f, src);
    }
    
    private void check(Image im, File f, String src) throws Exception {
        String err = write(im);
        if (err == null) {
            print(" OK {0} {1}", f.getName(), src);
        } else {
            print("ERR {0} {1} {2}", f.getName(), src, err);
        }
    }

    private static void print(String fmt, Object ... args) {
        IO.println(MessageFormat.format(fmt, args));
    }

    private void copyToClipboard(File f, boolean useBytes) throws Exception {
        Image im = loadImage(f, useBytes);
        ClipboardContent cc = new ClipboardContent();
        cc.putImage(im);
        Clipboard cb = Clipboard.getSystemClipboard();
        cb.setContent(cc);
    }

    private void fromClipboard(File f, boolean usedBytes) throws Exception {
        Clipboard cb = Clipboard.getSystemClipboard();
        Image im = cb.getImage();

        view.setGraphic(new ImageView(im));
        
        byte[] b = writeJPG(im);
        String src = "clipboard " + (usedBytes ? "bytes" : "url");
        check(im, f, src);
    }

    private Image loadImage(File f, boolean useBytes) throws Exception {
        if(useBytes) {
            byte[] b = Files.readAllBytes(f.toPath());
            return new Image(new ByteArrayInputStream(b));
        } else {
            String url = f.toURI().toURL().toString();
            return new Image(url);
        }
    }

    private String write(Image im) throws Exception {
        byte[] b = writeJPG(im);
        if(b == null) {
            return "null";
        } else if(b.length == 0) {
            return "length=0";
        }
        return null;
    }

    public static byte[] writeJPG(Image im) throws IOException {
        return writeImage(im, "JPG");
    }

    private static byte[] writeImage(Image im, String format) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(65536);
        try {
            // using disk cache slows things down
            boolean old = ImageIO.getUseCache();
            ImageIO.setUseCache(false);
            try {
                ImageIO.write(ImgUtil.fromFXImage(im, null), format, out);
            } finally {
                ImageIO.setUseCache(old);
            }
        } finally {
            out.close();
        }
        return out.toByteArray();
    }
}
