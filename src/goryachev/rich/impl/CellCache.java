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
package goryachev.rich.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import goryachev.rich.TextCell;

/**
 * A simple cache implementation.
 * This object must be accessed from the FX application thread, although it does not check.
 * 
 * Requirements: - cheap full invalidation (clear) - cheap random eviction
 */
public class CellCache {
    private int size;
    private final TextCell[] linear;
    private final HashMap<Integer, TextCell> data;
    private final static Random random = new Random();

    public CellCache(int capacity) {
        linear = new TextCell[capacity];
        data = new HashMap<>(capacity);
    }

    public TextCell get(int row) {
        return data.get(row);
    }

    /**
     * Adds a new cell to the cache. When the cache is full, this method evicts a
     * random cell from the cache first. NOTE: this method does not check whether
     * another cell for the given row is present, so this call must be preceded by a
     * {@link #get(int)}.
     */
    public void add(TextCell cell) {
        int row = cell.getLineIndex();

        int ix;
        if (size >= capacity()) {
            ix = evict();
        } else {
            ix = size++;
        }

        data.put(row, cell);
        linear[ix] = cell;
    }

    /** returns an index in the linear array of the cell that has been evicted */
    protected int evict() {
        int ix = random.nextInt(size);
        // does not clear the slot because it will get overwritten by the caller
        TextCell c = linear[ix];
        int row = c.getLineIndex();
        data.remove(row);
        return ix;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return linear.length;
    }

    public void clear() {
        size = 0;
        Arrays.fill(linear, null);
        data.clear();
    }
}
