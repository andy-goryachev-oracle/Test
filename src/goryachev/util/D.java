package goryachev.util;

/** debugging aid */
public class D {
    public static void p(Object x) {
        System.out.println(x);
    }
    
    public static void p(Object ... a) {
        StringBuilder sb = new StringBuilder();
        for(Object x: a)
        {
            if(sb.length() > 0)
            {
                sb.append(' ');
            }
            sb.append(x);
        }
        System.out.println(sb);
    }
    
    public static void trace() {
       new Error("Stack Trace:").printStackTrace();
    }
}
