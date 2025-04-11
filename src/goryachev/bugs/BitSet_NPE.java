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

package goryachev.bugs;

import java.util.Set;
import javafx.application.Application;
import javafx.css.PseudoClass;
import javafx.css.Selector;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class BitSet_NPE extends Application {
    @Override
    public void start(Stage stage) {
        Label t = new Label("");
        Scene scene = new Scene(t, 300, 250);
        stage.setScene(scene);
        stage.show();
        
        Selector s = Selector.createSelector(".text#three > .one:two");
        Set<PseudoClass> badBitSet = s.createMatch().getPseudoClasses();
        System.out.println("size=" + badBitSet.size() + " class=" + badBitSet.getClass() + " set=" + badBitSet);
        badBitSet.equals(null);
    }
}
