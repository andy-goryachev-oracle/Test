package bugs;

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
    //public boolean run(String s) { return false; }

    // commenting out this line adds another error
    public void run() { }
}
