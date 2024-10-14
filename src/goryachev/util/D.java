package goryachev.util;

import java.text.DecimalFormat;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.PathElement;

/** 'D'ebugging aid */
public class D {
    private static final DecimalFormat DOUBLE_FORMAT = new DecimalFormat("0.###");

    /** dumps the path element array to a compact human-readable string */
    public static String dump(PathElement[] elements) {
        StringBuilder sb = new StringBuilder();
        if (elements == null) {
            sb.append("null");
        } else {
            for (PathElement em : elements) {
                if (em instanceof MoveTo p) {
                    sb.append('M');
                    sb.append(f(p.getX()));
                    sb.append(',');
                    sb.append(f(p.getY()));
                    sb.append(' ');
                } else if (em instanceof LineTo p) {
                    sb.append('L');
                    sb.append(f(p.getX()));
                    sb.append(',');
                    sb.append(f(p.getY()));
                    sb.append(' ');
                } else {
                    sb.append(em);
                    sb.append(' ');
                }
            }
        }
        return sb.toString();
    }

    public static String f(double v) {
        return DOUBLE_FORMAT.format(v);
    }

    public static void p(Object... a) {
        StringBuilder sb = new StringBuilder();
        for (Object x : a) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(x);
        }
        withCaller(2, sb.toString());
    }

    private static void withCaller(int level, String msg) {
        StackTraceElement t = new Throwable().getStackTrace()[level];
        String className = t.getClassName();
        int ix = className.lastIndexOf('.');
        if (ix >= 0) {
            className = className.substring(ix + 1);
        }
        System.err.println(className + "." + t.getMethodName() + ":" + t.getLineNumber() + " " + msg);
    }

    public static void trace() {
        new Error("Stack Trace:").printStackTrace();
    }

    public static SW sw() {
        return new SW();
    }

    /** stop watch */
    public static class SW {
        private final long start = System.nanoTime();

        public SW() {
        }

        @Override
        public String toString() {
            double ms = (System.nanoTime() - start) / 1_000_000_000.0;
            return f(ms);
        }
    }
}
