/*
 * Copyright (c) 2023, Oracle and/or its affiliates. All rights reserved.
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
package goryachev.apps;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

// xn+1 = λxn(1-xn)
public class ScatterChartApp extends Application {
    static double min = 2.4;
    static double max = 4.0;

    public static void main(String[] args) {
        Application.launch(ScatterChartApp.class, args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Bifurcation Diagram");

        Pane p = new Pane();
        p.widthProperty().addListener((x) -> update(p));
        p.heightProperty().addListener((x) -> update(p));
        update(p);
        
        BorderPane bp = new BorderPane(p);

        Scene scene = new Scene(bp, 500, 400);
        stage.setScene(scene);
        stage.show();
    }

    protected void update(Pane p) {
        double w = p.getWidth();
        double h = p.getHeight();
        Canvas c = new Canvas(w, h);
        GraphicsContext g = c.getGraphicsContext2D();

        g.setFill(Color.gray(0.9));
        g.fillRect(0, 0, w, h);

        int count = 1000;
        int start = 500;
        double r = 0.3;
        g.setFill(Color.rgb(0, 0, 0, 0.2));

        for (double λ = min; λ < max; λ += 0.001) {
            double x = 0.5;
            for (int i = 0; i < count; i++) {
                x = λ * x * (1.0 - x);
                if (i > start) {
                    double px = w * (λ - min) / (max - min);
                    double py = h * x;

                    g.fillOval(px - r, py - r, r + r, r + r);
                }
            }
        }
        
        p.getChildren().setAll(c);
    }
}