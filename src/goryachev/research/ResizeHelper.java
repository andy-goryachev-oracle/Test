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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import javafx.scene.control.ResizeFeaturesBase;
import javafx.scene.control.TableColumnBase;

/**
 * Helps resize Tree/TableView columns.
 */
public class ResizeHelper {
    protected static final double EPSILON = 0.0000001;

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
            double w = c.getWidth();
            size[i] = w;
            
            if(c.isResizable()) {
                smin += (min[i] = c.getMinWidth());
                spref += (pref[i] = clip(c.getPrefWidth(), c.getMinWidth(), c.getMaxWidth()));
                smax += (max[i] = c.getMaxWidth());
            } else {
                smin += w;
                spref += w;
                smax += w;
            }
        }
        
        this.sumMin = smin;
        this.sumPref = spref;
        this.sumMax = smax;
        
        // FIX
        System.out.println(this);
    }
    
    public int count() {
        return columns.size();
    }
    
    public double sumWidths() {
        // TODO sumWidths?
        return sum(size);
    }
    
    protected static double sum(double[] values) {
        double rv = 0.0;
        for(double w: values) {
            rv += w;
        }
        return rv;
    }
    
    protected static boolean eq(double a, double b) {
        return Math.abs(a - b) > EPSILON;
    }

    public void resizeColumnsFromPref(double delta) {
        // compute shrinking/expanding ratio
        double f = (target - sumMin) / (sumPref - sumMin);
        if(f < 0.0) {
            f = 0.0;
        }

        ArrayList<CCol> constrained = null;
        for (int i = 0; i < count(); i++) {
            if(!columns.get(i).isResizable()) {
                continue;
            }

            double adj;
            double w = Math.round(min[i] + f * (pref[i] - min[i]));
            if(w < min[i]) {
                adj = Math.abs(w - min[i]);
                w = min[i];
            } else if(w > max[i]) {
                adj = Math.abs(w - max[i]);
                w = max[i];
            } else {
                adj = 0.0;
            }
            
            if(adj != 0.0) {
                if(constrained == null) {
                    constrained = new ArrayList(count());
                }
                constrained.add(new CCol(i, adj));
            }
            size[i] = w;
        }

        // check if hit any constraints
        if (constrained != null) {
            // sort constrained columns by closeness
            Collections.sort(constrained);
            
            // identify N columns with the largest adjustment, such that the sum of their adjustments
            // exceeds the totalAdjustment - these columns will remain at their constrained size
            double totalAdjustment = sum(size) - target;
            double adj = 0.0;
            BitSet skip = new BitSet(count());
            for (int i=0; i < constrained.size(); i++) {
                CCol c = constrained.get(i);
                skip.set(c.index);
                adj += constrained.get(i).adj;
                if(adj > Math.abs(totalAdjustment)) {
                    break;
                }
            }
            
            // distribute extra space between the original columns (except constrained columns removed in step above)
            
            double sumPref = 0.0;
            for (int i = 0; i < count(); i++) {
                if(skip.get(i) || (!columns.get(i).isResizable())) {
                    continue;
                }
                sumPref += pref[i];
            }

            // TODO avoid accumulating rounding errors
            for (int i = 0; i < count(); i++) {
                if(skip.get(i) || (!columns.get(i).isResizable())) {
                    continue;
                }
                
                double dw = Math.round(totalAdjustment * pref[i] / sumPref);
                size[i] += dw;
            }
        }

        // apply sizes
        for (int i = 0; i < count(); i++) {
            TableColumnBase<?,?> c = columns.get(i);
            if (c.isResizable()) {
                rf.setColumnWidth(c, size[i]);
            }
        }
    }
    
    protected static double clip(double v, double min, double max) {
        if(v < min) {
            return min;
        } else if(v > max) {
            return max;
        }
        return v;
    }
    
    @Override
    public String toString() {
        return
            "sumMin=" + p(sumMin) +
            " sumPref=" + p(sumPref) +
            " sumMax=" + p(sumMax) +
            " target=" + p(target);
    }
    
    protected static String p(double x) {
        return new DecimalFormat("0.#").format(x);
    }

    /** Constrained column */
    protected static class CCol implements Comparable<CCol> {
        public final int index;
        public final double adj;
        
        public CCol(int index, double adj) {
            this.index = index;
            this.adj = adj;
        }

        @Override
        public int compareTo(CCol x) {
            return (int)Math.signum(x.adj - adj);
        }
    }
}
