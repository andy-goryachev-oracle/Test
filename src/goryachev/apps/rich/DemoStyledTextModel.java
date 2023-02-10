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
package goryachev.apps.rich;

import java.util.AbstractList;
import java.util.Collection;
import java.util.RandomAccess;
import javafx.scene.text.TextFlow;
import goryachev.rich.StyledParagraph;
import goryachev.rich.StyledTextModel;
import goryachev.rich.TextCell;
import goryachev.rich.util.NewAPI;

/**
 * Demo StyledTextModel.
 * Does not support editing events - populate the model first, then pass it to the control.
 */
public class DemoStyledTextModel extends StyledTextModel {
    private final SList paragraphs;
    
    public DemoStyledTextModel(int size, boolean monospaced) {
        this.paragraphs = new SList(size, monospaced);
    }

    @Override
    public int getParagraphCount() {
        return paragraphs.size();
    }

    @Override
    public StyledParagraph getParagraph(int index) {
        return paragraphs.get(index);
    }

    /** */
    public static class SList extends AbstractList<StyledParagraph> implements RandomAccess {
        private final int size;
        private final boolean monospaced;
        
        public SList(int size, boolean monospaced) {
            this.size = size;
            this.monospaced = monospaced;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public boolean addAll(Collection<? extends StyledParagraph> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public StyledParagraph get(int index) {
            return new SParagraph(index);
         }

         /** */
         public class SParagraph implements StyledParagraph {
             private final int index;
    
             public SParagraph(int index) {
                 this.index = index;
             }
    
             @Override
             public String getPlainText() {
                 TextCell c = createTextCell();
                 TextFlow f = ((TextFlow)c.getContent());
                 return NewAPI.getText(f);
             }
    
             @Override
             public int getIndex() {
                 return index;
             }
    
             public int hashCode() {
                 return System.identityHashCode(SList.this) ^ index;
             }
             
             private SList list() {
                 return SList.this;
             }
    
             public boolean equals(Object x) {
                 if (x == this) {
                     return true;
                 } else if (x instanceof SParagraph p) {
                     return
                         (p.list() == list()) &&
                         (p.index == index);
                 } else {
                     return false;
                 }
             }
    
             @Override
             public TextCell createTextCell() {
                 TextCell c = new TextCell(index);
                 c.addSegment(String.valueOf(index), monospaced ? "-fx-font-family:Monospaced;" : "-fx-fill:darkgreen;", null);
                 c.addSegment(" / ", monospaced ? "-fx-font-family:Monospaced;" : null, null);
                 c.addSegment(String.valueOf(SList.this.size()), monospaced ? "-fx-font-family:Monospaced;" : "-fx-fill:black;", null);
                 if (monospaced) {
                     c.addSegment(" (monospaced)", monospaced ? "-fx-font-family:Monospaced;" : null, null);
                 }
                 return c;
             }
         }
    }
 }
