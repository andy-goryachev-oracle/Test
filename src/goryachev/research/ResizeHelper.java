/*
 * Copyright (c) 2022, Oracle and/or its affiliates. All rights reserved.
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

import java.util.List;
import javafx.scene.control.ResizeFeaturesBase;
import javafx.scene.control.TableColumnBase;

/**
 * Helps resize Tree/TableView columns.
 */
public class ResizeHelper {
    private final ResizeFeaturesBase rf;
    private final double target;
    private final List<? extends TableColumnBase<?,?>> columns;
    private final double[] size;
    private final double[] min;
    private final double[] pref;
    private final double[] max;
    private final double sumMin;
    private final double sumPref;
    private final double sumMax;
    
    public ResizeHelper(ResizeFeaturesBase rf,
                        double target,
                        List<? extends TableColumnBase<?,?>> columns) {
        this.rf = rf;
        this.target = target;
        this.columns = columns;
        int sz = columns.size();
        size = new double[sz];
        min = new double[sz];
        pref = new double[sz];
        max = new double[sz];
        
        double smin = 0.0;
        double spref = 0.0;
        double smax = 0.0;
        
        for(int i=0; i<sz; i++) {
            TableColumnBase<?,?> c = columns.get(i);
            size[i] = c.getWidth();
            smin += (min[i] = c.getMinWidth());
            spref += (pref[i] = c.getPrefWidth());
            smax += (max[i] = c.getMaxWidth());
        }
        
        this.sumMin = smin;
        this.sumPref = spref;
        this.sumMax = smax;
    }
    
    public int count() {
        return size.length;
    }
    
    public double sumWidths() {
        return sum(size);
    }
    
    protected double sum(double[] values) {
        double rv = 0.0;
        for(double w: values) {
            rv += w;
        }
        return rv;
    }

    // TODO fixed size columns, skip non-resizeable columns
    public void resizeColumnsFromPref(double delta) {
        // hit the min rail on all the columns
//        if(target < sumMin) {
//            
//        }
        // compute shrinking/expanding ratio
        double f = (target - sumMin) / (sumMax - sumMin);
        if(f < 0.0) {
            f = 0.0;
        } else if(f > 1.0) {
            f = 1.0;
        }
        
        // TODO account for rounding error
        for(int i=0; i<count(); i++) {
            double w = Math.round(f * (pref[i] - min[i]));
            size[i] = w;
        }
        
        // apply sizes
        for(int i=0; i<count(); i++) {
            rf.setColumnWidth(columns.get(i), size[i]);
        }
    }
}
