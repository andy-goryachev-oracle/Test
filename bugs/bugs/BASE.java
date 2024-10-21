package bugs;

public class BASE {
    protected void register(Runnable r) { }
    
    // protected method results in an "ambiguous" error in the subclass
    protected void base() { }

    public void basePublic() { }
}
