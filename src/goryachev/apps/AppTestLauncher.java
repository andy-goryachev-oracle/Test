/*
 * Copyright (c) 2023, 2024, Oracle and/or its affiliates. All rights reserved.
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
import goryachev.bugs.*;
import goryachev.research.*;
import goryachev.tests.*;

/**
 * Use this class to launch various test snippets, so one does not have to 
 * create a new launch configuration each time.
 */
public class AppTestLauncher {
    public static void main(String[] args) throws Throwable {
        // enableLogging();
        Application.launch(Accelerator_UnexpectedBehavior_8342094.class, args);
    }

    private static void enableLogging() {
        System.setProperty("prism.order", "sw");
        System.setProperty("javafx.pulseLogger", "true");
        System.setProperty("javafx.pulseLogger.threshold", "-1");
        System.setProperty("prism.showdirty", "true");
    }
}
