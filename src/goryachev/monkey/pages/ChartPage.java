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
package goryachev.monkey.pages;

import java.util.List;
import java.util.Random;
import goryachev.monkey.util.OptionPane;
import goryachev.monkey.util.ToolPane;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;

/**
 * Tests various XYCharts
 */
public class ChartPage extends ToolPane {
    public enum Mode {
        AREA,
        BAR,
        BUBBLE,
        LINE,
        SCATTER,
        STACKED_AREA,
        STACKED_BAR,
    }
    
    private ComboBox<Mode> modeSelector;
    private ChartGen base;
    protected static Random rnd = new Random();
    
    public ChartPage() {
        modeSelector = new ComboBox<>();
        modeSelector.getItems().addAll(Mode.values());
        modeSelector.setEditable(false);
        modeSelector.getSelectionModel().selectedItemProperty().addListener((s,p,c) -> {
            updateChart();
        });
        
        Button addButton = new Button("Add Series");
        addButton.setOnAction((ev) -> addSeries());
        
        Button removeButton = new Button("Remove Series");
        removeButton.setOnAction((ev) -> removeSeries());
        
        Button addRemoveButton = new Button("Add/Remove Series");
        addRemoveButton.setOnAction((ev) -> addRemoveSeries());
        
        OptionPane p = new OptionPane();
        p.label("Chart Type:");
        p.option(modeSelector);
        p.option(addButton);
        p.option(removeButton);
        p.option(addRemoveButton);
        setOptions(p);

        modeSelector.getSelectionModel().selectFirst();
    }
    
    protected void updateChart() {
        Mode m = modeSelector.getSelectionModel().getSelectedItem();
        base = createBase(m);

        BorderPane bp = new BorderPane();
        if (base != null) {
            bp.setCenter(base.chart());
        }
        setContent(bp);
    }

    protected void addSeries() {
        if (base != null) {
            base.add();
        }
    }

    protected void removeSeries() {
        if (base != null) {
            base.remove();
        }
    }
    
    protected void addRemoveSeries() {
        if (base != null) {
            base.addRemove();
        }
    }

    private ChartGen createBase(Mode m) {
        switch (m) {
        case AREA:
            break;
        case BUBBLE:
            return new BubbleGen();
        case LINE:
            return new LineGen();
        }

        return null;
    }
    
    protected static void removeFirst(List<?> list) {
        if (list.size() > 0) {
            list.remove(0);
        }
    }
    
    protected static <T> void addRemoveFirst(List<T> list) {
        if (list.size() > 0) {
            T first = list.remove(0);
            list.add(first);
        }
    }
    
    public Series<Number, Number> create() {
        String name = Long.toString(System.currentTimeMillis(), 16);
        
        XYChart.Series s = new XYChart.Series();
        s.setName(name);
        for(int i=0; i<12; i++) {
            int v = rnd.nextInt(50);
            s.getData().add(new XYChart.Data(i, v));
        }
        return s;
    }
    
    /** Chart Generator */
    protected abstract class ChartGen {
        public abstract Chart chart();
        public abstract void add();
        public abstract void remove();
        public abstract void addRemove();
    }
    
    /** Bubble Chart */
    protected class BubbleGen extends ChartGen {
        BubbleChart<Number, Number> chart;
        
        @Override
        public Chart chart() {
            NumberAxis xAxis = new NumberAxis(1, 53, 4);
            xAxis.setLabel("Week");

            NumberAxis yAxis = new NumberAxis(0, 80, 10);
            yAxis.setLabel("Product Budget");
            
            chart = new BubbleChart<Number, Number>(xAxis, yAxis);
            chart.setTitle("Budget Monitoring");
            chart.getData().addAll(create());
            return chart;
        }

        @Override
        public void add() {
            chart.getData().add(create());
        }

        @Override
        public void remove() {
            removeFirst(chart.getData());
        }

        @Override
        public void addRemove() {
            addRemoveFirst(chart.getData());
        }
    }

    /** Line Chart */
    public class LineGen extends ChartGen {
        private LineChart<Number, Number> chart;
        
        @Override
        public Chart chart() {
            NumberAxis xAxis = new NumberAxis();
            xAxis.setLabel("Month Number");

            NumberAxis yAxis = new NumberAxis();
          
            chart = new LineChart<>(xAxis, yAxis);
            chart.setTitle("Stock Monitoring, 2010");
            chart.getData().add(create());
            
            return chart;
        }
        
        @Override
        public void add() {
            Series<Number, Number> s = create();
            chart.getData().add(s);
        }
        
        @Override
        public void remove() {
            removeFirst(chart.getData());
        }

        @Override
        public void addRemove() {
            addRemoveFirst(chart.getData());
        }
    }
}
