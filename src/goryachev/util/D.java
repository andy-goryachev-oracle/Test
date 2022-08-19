package goryachev.util;

/** debugging aid */
public class D {
    public static void p(Object x) {
        System.out.println(x);
    }
    
    public static void trace() {
       new Error("Stack Trace:").printStackTrace();
    }
}
