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
import java.util.BitSet;
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
    private final BitSet skip;

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
        skip = new BitSet(sz);

        for (int i = 0; i < sz; i++) {
            TableColumnBase<?,?> c = columns.get(i);
            double w = c.getWidth();
            size[i] = w;

            // TODO possibly check for min<pref<max
            if (c.isResizable()) {
                min[i] = c.getMinWidth();
                pref[i] = clip(c.getPrefWidth(), c.getMinWidth(), c.getMaxWidth());
                max[i] = c.getMaxWidth();
            } else {
                skip.set(i, true);
            }
        }

        System.out.println(this); // FIX
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

    /** returns true if one or more constraints have been hit and another pass is needed */
    public boolean resizeColumnsFromPref(double delta) {
        double remainingTarget = target;
        double sumPref = 0.0;
        double sumMin = 0.0;

        // remove fixed and skipped columns from consideration
        for (int i = 0; i < count(); i++) {
            if (skip.get(i)) {
                remainingTarget -= size[i];
            } else {
                sumMin += min[i];
                sumPref += pref[i];
            }
        }

        boolean needsAnotherPass = false;

        for (int i = 0; i < count(); i++) {
            if (skip.get(i)) {
                continue;
            }

            // compute shrinking/expanding ratio
            double f = (remainingTarget - sumMin) / (sumPref - sumMin);
            if (f < 0.0) {
                f = 0.0;
            }

            double w = Math.round(min[i] + f * (pref[i] - min[i]));
            if (w < min[i]) {
                w = min[i];
                skip.set(i, true);
                // TODO does not if went pass the visible area
                needsAnotherPass = true;
            } else if (w > max[i]) {
                w = max[i];
                skip.set(i, true);
                // TODO does not if went pass the visible area
                needsAnotherPass = true;
            }

            size[i] = w;
            remainingTarget -= w;
            sumPref -= w;
            sumMin -= w;
        }

        return needsAnotherPass;
    }

    public void applySizes() {
        for (int i = 0; i < count(); i++) {
            TableColumnBase<?,?> c = columns.get(i);
            if (c.isResizable()) {
                rf.setColumnWidth(c, size[i]);
            }
        }
    }

    protected static double clip(double v, double min, double max) {
        if (v < min) {
            return min;
        } else if (v > max) {
            return max;
        }
        return v;
    }

    @Override
    public String toString() {
        return
        //            "sumMin=" + p(sumMin) +
        //            " sumPref=" + p(sumPref) +
        //            " sumMax=" + p(sumMax) +
        " target=" + p(target);
    }

    protected static String p(double x) { // FIX remove
        return new DecimalFormat("0.#").format(x);
    }
}
