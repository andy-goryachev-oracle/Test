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
package goryachev.research;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 */
public class NextTabComputation {
    public static void main(String[] args) {
        float tabAdvance = 57.6f;
        float lineWidth = 172.79999f;
        float tabStop = ((int)(lineWidth / tabAdvance) +1) * tabAdvance;
        //System.out.println("pos=" + lineWidth + " tabStop=" + tabStop);
        
        tabStop = ((int)(Math.floor(lineWidth / tabAdvance)) + 1) * tabAdvance;
        //System.out.println("Math.floor pos=" + lineWidth + " tabStop=" + tabStop);
        
        tabStop = nextPosition(lineWidth, tabAdvance);
        //System.out.println("andy pos=" + lineWidth + " tabStop=" + tabStop);
        
        testMultiThreaded();
    }

    private static float nextPosition(float position, float tabAdvance) {
        float n = (position / tabAdvance);
        return ((int)(n + Math.ulp(n)) + 1) * tabAdvance;
    }
    
    private static void test() {
        float tabAdvance = 1f;
        while(tabAdvance < 300) {
            System.out.println("tabAdvance=" + tabAdvance);
            
            float pos = 0.0f;
            while(pos < 10_000f) {
                float tabStop = nextPosition(pos, tabAdvance);
                if(tabStop <= pos) {
                    System.out.println("FAIL pos=" + pos + " tabStop=" + tabStop + " tabAdvance=" + tabAdvance);
                }
                
                pos += Math.ulp(pos);
            }
            
            tabAdvance += Math.ulp(tabAdvance);
        }
    }
    
    private static void testMultiThreaded() {
        int n = Runtime.getRuntime().availableProcessors();
        ExecutorService exe = Executors.newFixedThreadPool(n);
        float ta = 1f;
        while(ta < 300) {
            //System.out.println("tabAdvance=" + tabAdvance);
            float tabAdvance = ta;
            exe.execute(() -> {
                float pos = 0.0f;
                while(pos < 10_000f) {
                    float tabStop = nextPosition(pos, tabAdvance);
                    if(tabStop <= pos) {
                        System.out.println("FAIL pos=" + pos + " tabStop=" + tabStop + " tabAdvance=" + tabAdvance);
                    }
                    
                    pos += Math.ulp(pos);
                }
            });
            
            ta += Math.ulp(tabAdvance);
        }
    }
}
