/*
 * Copyright (c) 2017, Oracle and/or its affiliates. All rights reserved.
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
//package test.javafx.stage;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * https://bugs.openjdk.org/browse/JDK-8176813
 */
public class Stage_RestorePosition_8176813 {
    static CountDownLatch startupLatch;
    static Stage stage;

    // used to be main()
    public static void launch() throws Exception {
        initFX();
        try {
            Stage_RestorePosition_8176813 test = new Stage_RestorePosition_8176813();
            t(() -> test.testUnfullscreenPosition());
            t(() -> test.testDemaximizedPosition());
            t(() -> {
                test.testUnfullscreenPosition();
                test.testDemaximizedPosition();
            });
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            teardown();
        }
    }
    
    interface Ru {
        public void run() throws Exception;
    }

    static void t(Ru r) throws Exception {
        for (int i = 0; i < 10; i++) {
            r.run();
        }
    }

    static void fail(String msg) {
        throw new AssertionError(msg);
    }

    static void assertTrue(boolean cond) {
        if (!cond) {
            fail("Assertion failed: expected true, was false");
        }
    }

    static void assertFalse(boolean cond) {
        if (cond) {
            fail("Assertion failed: expected false, was true");
        }
    }
    static void assertEquals(String msg, double expected, double actual, double delta) {
        if (Math.abs(expected - actual) > delta) {
            fail("Assertion failed: expected " + expected
                + ", was: " + actual);
        }
    }

    public static class TestApp extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception {
            primaryStage.setScene(new Scene(new VBox()));
            stage = primaryStage;
            stage.setX(300);
            stage.setY(400);
            stage.addEventHandler(WindowEvent.WINDOW_SHOWN, e ->
                                    Platform.runLater(startupLatch::countDown));
            stage.show();
        }
    }

    public static void initFX() {
        startupLatch = new CountDownLatch(1);
        new Thread(() -> Application.launch(TestApp.class, (String[])null)).start();
        try {
            if (!startupLatch.await(15, TimeUnit.SECONDS)) {
                fail("Timeout waiting for FX runtime to start");
            }
        } catch (InterruptedException ex) {
            fail("Unexpected exception: " + ex);
        }
    }


    public void testUnfullscreenPosition() throws Exception {
        Thread.sleep(200);
        assertTrue(stage.isShowing());
        assertFalse(stage.isFullScreen());

        double x = stage.getX();
        double y = stage.getY();

        Platform.runLater(() -> stage.setFullScreen(true));
        Thread.sleep(700);
        assertTrue(stage.isFullScreen());
        CountDownLatch latch = new CountDownLatch(2);

        ChangeListener<Number> listenerX = (observable, oldValue, newValue) -> {
            if (Math.abs((Double) newValue - x) < 0.1) {
                latch.countDown();
            };
        };
        ChangeListener<Number> listenerY = (observable, oldValue, newValue) -> {
            if (Math.abs((Double) newValue - y) < 0.1) {
                latch.countDown();
            };
        };
        stage.xProperty().addListener(listenerX);
        stage.yProperty().addListener(listenerY);
        Platform.runLater(() -> stage.setFullScreen(false));
        latch.await(5, TimeUnit.SECONDS);
        stage.xProperty().removeListener(listenerX);
        stage.xProperty().removeListener(listenerY);

        assertEquals("Window was moved", x, stage.getX(), 0.1);
        assertEquals("Window was moved", y, stage.getY(), 0.1);
    }

    public void testDemaximizedPosition() throws Exception {
        Thread.sleep(200);
        assertTrue(stage.isShowing());
        assertFalse(stage.isMaximized());

        double x = stage.getX();
        double y = stage.getY();

        Platform.runLater(() -> stage.setMaximized(true));
        Thread.sleep(500);
        assertTrue(stage.isMaximized());
        CountDownLatch latch = new CountDownLatch(2);

        ChangeListener<Number> listenerX = (observable, oldValue, newValue) -> {
            if (Math.abs((Double) newValue - x) < 0.1) {
                latch.countDown();
            };
        };
        ChangeListener<Number> listenerY = (observable, oldValue, newValue) -> {
            if (Math.abs((Double) newValue - y) < 0.1) {
                latch.countDown();
            };
        };
        stage.xProperty().addListener(listenerX);
        stage.yProperty().addListener(listenerY);
        Platform.runLater(() -> stage.setMaximized(false));
        latch.await(5, TimeUnit.SECONDS);
        stage.xProperty().removeListener(listenerX);
        stage.xProperty().removeListener(listenerY);

        assertEquals("Window was moved", x, stage.getX(), 0.1);
        assertEquals("Window was moved", y, stage.getY(), 0.1);
    }

    public static void teardown() {
        Platform.runLater(stage::hide);
        Platform.exit();
    }
}