# Why Do I Get This Error?

I am getting **both method register(Runnable) in TEST and method register(Func) in TEST match** error
when no such error should ever be generated.

The reproducer:

```java
public class TEST {
    @FunctionalInterface
    public static interface Func {
        public boolean accept();
    }
    
    protected void register(Runnable r) { }
    
    protected void register(Func f) { }
    
    void test() {
        register(this::run); // "This method is ambiguous for the type TEST"
    }
    
    // commenting out this line resolves the "ambiguous" error
    // why does java think this method is appropriate for the lambda above??
    public boolean run(String s) { return false; }

    // commenting out this line adds another error
    public void run() { }
}
```

The Error (using javac 23.0.1):

```
$ javac TEST.java 
TEST.java:14: error: reference to register is ambiguous
        register(this::run); // "This method is ambiguous for the type TEST"
        ^
  both method register(Runnable) in TEST and method register(Func) in TEST match
1 error
```

I don't understand how `public boolean run(String s)` method can match any of the `register()` methods.

