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
package goryachev.research;

/**
 * Exploring matching rules for functional interfaces
 */
public class TestFunctionInterfaceMatchingRules {
    public void match(F1 f) { }
    
    public void match(F2 f) { }
    
    public void matchVoid(F1 f) { }
    
    @FunctionalInterface
    static interface F1 {
        public void returnsVoid();
    }
    
    @FunctionalInterface
    static interface F2 {
        public boolean returnsBoolean();
    }

    static class Test extends TestFunctionInterfaceMatchingRules {
        void test() {
            match(() -> {
                // void
            });
            match(this::returnsVoid);
            
            match(() -> {
                return true;
            });
            match(this::returnsBoolean);
            
            // fails to compile
            //matchVoid(() -> {
            //    return true;
            //});

            // compiles ok ??
            matchVoid(this::returnsBoolean);
            matchVoid(this::returnsInt);
        }
        
        void returnsVoid() {
        }
        
        boolean returnsBoolean() {
            return true;
        }
        
        int returnsInt() {
            return 0;
        }
    }
}
