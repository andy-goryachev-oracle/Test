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
package goryachev.research;

import org.junit.Test;

/**
 *
 */
public class TestScaleCeil {
    // from Region
    private static final double EPSILON = 1e-14;
    
    //@Test
    public void test() {
        for(double x = 100.0; x<200.0; ) {
            t(x, 1.75);
            x = Math.nextUp(x);
        }
    }
    
    @Test
    public void testFloat() {
        int ct = 0;
        float max = Integer.MAX_VALUE;
        for(float x = 0.0f; x<max; ) {
            t(x, 1.75);
            x = Math.nextUp(x);
            ct++;
        }
        System.out.println("count=" + ct);
    }
    
    private static void t(double x, double scale)
    {
        double x1 = scaledCeil2(x, scale);
        double x2 = scaledCeil2(x1, scale);
        //System.out.println("x=" + x + " x1=" + x1 + " x2=" + x2 + " ==" + (x1 == x2));
        if(x1 != x2) {
            throw new Error("x=" + x + " x1=" + x1 + " x2=" + x2 + " ==" + (x1 == x2));
        }
    }
    
    /**
     * The value is ceiled with a given scale using Math.ceil.
     * This method guarantees that:
     *
     * scaledCeil(scaledCeil(value, scale), scale) == scaledCeil(value, scale)
     *
     * @param value The value that needs to be ceiled
     * @param scale The scale that will be used
     * @return value ceiled with scale
     */
    private static double scaledCeil(double value, double scale) {
        return Math.ceil(value * scale - EPSILON) / scale;
    }
    
    private static double scaledCeil2(double value, double scale) {
        double d = value * scale;
        return Math.ceil(d - Math.ulp(d)) / scale;
    }
}
