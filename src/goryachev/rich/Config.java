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
package goryachev.rich;

/**
 * Configuration parameters for RichTextArea.
 */
public class Config {
    /** autoscroll while selecting animation period, milliseconds (default 100 ms). */
    public final int autoScrollPeriod;

    /** "fast" autoscroll step, in pixels (default 200). */
    public final double autoScrollStepFast;
    
    /** "slow" autoscroll step, in pixels (default 20). */
    public final double autoStopStepSlow;

    /** caret blink period in milliseconds (default 500 ms). */
    public final int caretBlinkPeriod;

    /** cell cache size (default 512). */
    public final int cellCacheSize;
    
    /** autoscroll switches to fast mode when mouse is moved further out of the view, pixels (default 200). */
    public final double fastAutoScrollThreshold;
    
    /** small space between the end of last character and the right edge when typing, in pixels (default 20). */
    public final double horizontalGuard;
    
    /** maximum tab size (default 256). */
    public final int maxTabSize;

    /** scroll bars unit increment, fraction of view width/height (between 0.0 and 1.0, default 0.1). */
    public final double scrollBarsUnitIncrement;

    /** horizontal mouse wheel scroll block size as a fraction of window width (default 0.1). */
    public final double scrollWheelBlockSizeHorizontal;
    
    /** vertical mouse wheel scroll block size as a fraction of window height (default 0.1). */
    public final double scrollWheelBlockSizeVertical;

    /**
     * VFlow TextLayout sliding window margin before and after the visible area (default 3.0f).
     * Must be > 1.0f for relative navigation to work.
     */
    public final float slidingWindowMargin;
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static Config defaultConfig() {
        return Config.builder().create();
    }
    
    private Config(Builder b) {
        autoScrollPeriod = b.autoScrollPeriod;
        autoScrollStepFast = b.autoScrollStepFast;
        autoStopStepSlow = b.autoScrollStepSlow;
        caretBlinkPeriod = b.caretBlinkPeriod;
        cellCacheSize = b.cellCacheSize;
        fastAutoScrollThreshold = b.fastAutoScrollThreshold;
        horizontalGuard = b.horizontalGuard;
        maxTabSize = b.maxTabSize;
        scrollBarsUnitIncrement = b.scrollBarsUnitIncrement;
        scrollWheelBlockSizeHorizontal = b.scrollWheelBlockSizeHorizontal;
        slidingWindowMargin = b.slidingWindowMargin;
        scrollWheelBlockSizeVertical = b.scrollWheelBlockSizeVertical;
    }

    /** Config builder is necessary to make Config immutable */
    private static class Builder {
        private int autoScrollPeriod = 100;
        private double autoScrollStepFast  = 200;
        private double autoScrollStepSlow  = 20;
        private int caretBlinkPeriod = 500;
        private int cellCacheSize = 512;
        private double fastAutoScrollThreshold  = 100;
        private double horizontalGuard = 20;
        private int maxTabSize = 256;
        private double scrollBarsUnitIncrement = 0.1;
        private double scrollWheelBlockSizeHorizontal = 0.1;
        private double scrollWheelBlockSizeVertical = 0.1;
        private float slidingWindowMargin = 3.0f;
        
        public Config create() {
            return new Config(this);
        }
        
        /** Sets autoscroll animation period, in milliseconds. */
        public void setAutoScrollPeriod(int x) {
            autoScrollPeriod = x;
        }
        
        /** Sets "fast" autoscroll step, in pixels. */
        public void setAutoScrollStepFast(double x) {
            autoScrollStepFast = x;
        }
        
        /** Sets "slow" autoscroll step, in pixels. */
        public void setAutoScrollStepSlow(double x) {
            autoScrollStepSlow = x;
        }
    }
}
