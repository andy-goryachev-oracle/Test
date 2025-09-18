package goryachev.research;

/**
 * javap -c MoreEfficient.class
 */
public class MoreEfficient {
    /*
      public void withFilter(java.lang.String);
        Code:
             0: aload_1
             1: invokevirtual #16                 // Method java/lang/String.lines:()Ljava/util/stream/Stream;
             4: invokedynamic #22,  0             // InvokeDynamic #0:apply:()Ljava/util/function/Function;
             9: invokeinterface #26,  2           // InterfaceMethod java/util/stream/Stream.map:(Ljava/util/function/Function;)Ljava/util/stream/Stream;
            14: invokedynamic #32,  0             // InvokeDynamic #1:test:()Ljava/util/function/Predicate;
            19: invokeinterface #36,  2           // InterfaceMethod java/util/stream/Stream.filter:(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;
            24: invokedynamic #40,  0             // InvokeDynamic #2:accept:()Ljava/util/function/Consumer;
            29: invokeinterface #44,  2           // InterfaceMethod java/util/stream/Stream.forEach:(Ljava/util/function/Consumer;)V
            34: return
      */
    public void withFilter(String text) {
        text.lines().
            map((s) -> findException(s)).
            filter((c) -> c != null).
            forEach((c) -> {
                System.out.println(c);
            });
    }

    /*
      public void withExplicitCheck(java.lang.String);
        Code:
             0: aload_1
             1: invokevirtual #16                 // Method java/lang/String.lines:()Ljava/util/stream/Stream;
             4: invokedynamic #51,  0             // InvokeDynamic #3:apply:()Ljava/util/function/Function;
             9: invokeinterface #26,  2           // InterfaceMethod java/util/stream/Stream.map:(Ljava/util/function/Function;)Ljava/util/stream/Stream;
            14: invokedynamic #52,  0             // InvokeDynamic #4:accept:()Ljava/util/function/Consumer;
            19: invokeinterface #44,  2           // InterfaceMethod java/util/stream/Stream.forEach:(Ljava/util/function/Consumer;)V
            24: return
     */
    public void withExplicitCheck(String text) {
        text.lines().
            map((s) -> findException(s)).
            forEach((c) -> {
                if(c != null) {
                    System.out.println(c);
                }
            });
    }
    
    private static String findException(String text) {
        return text.startsWith("a") ? text : null;
    }
}
