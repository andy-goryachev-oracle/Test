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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.stage.Stage;

/**
 *
 */
public class PieChart_AddSeries extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        PieChart chart = new PieChart();
        chart.getData().setAll(createPieSeries());
        Scene sc = new Scene(chart, 300, 200);
        stage.setScene(sc);
        stage.setTitle("PieChart: Add Series");
        stage.show();

        // periodically "jiggle" the visible node in the fx thread
        new Thread(() -> {
            Random r = new Random();
            for (;;) {
                sleep(1 + r.nextInt(20));
                runAndWait(() -> {
                    chart.setAnimated(nextBoolean());
                    chart.getData().setAll(createPieSeries());
                });
            }
        }, "jiggler").start();
    }

    private static List<PieChart.Data> createPieSeries() {
        Random rnd = new Random();
        int sz = 1 + rnd.nextInt(20);
        ArrayList<Data> a = new ArrayList<>(sz);
        for (int i = 0; i < sz; i++) {
            a.add(new PieChart.Data("N" + i, rnd.nextDouble()));
        }
        return a;
    }

    private static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
        }
    }

    public static void runAndWait(Runnable r) {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                r.run();
            } finally {
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean nextBoolean() {
        return new Random().nextBoolean();
    }
}
