/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
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
package goryachev.tests;

/**
 *
 */
public class FloatingPointErrors {
    static int ct = 1;

    public static void main(String[] args) {
        for(int v=0; v<256; v++) {
            for(int d=-2; d<=2; d++) {
                test(v, v + d);
            }
        }
    }

    private static void test(int a, int b) {
        if(a < 0) {
            return;
        } else if(b < 0) {
            return;
        } else if(a > 255) {
            return;
        } else if(b > 255) {
            return;
        }
        
        double tol = 2.0 / 255.0;
        double aa = a / 255.0;
        double bb = b / 255.0;
        double d = Math.abs(aa - bb);
        if(d >= tol) {
            System.out.println(ct + " a=" + a + " b=" + b + " delta=" + d + " tol=" + tol);
            ct++;
        }
    }
}
