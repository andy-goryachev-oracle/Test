package goryachev.bugs.bytebuffer;
import java.lang.reflect.Field;

public class TestNullFinal {
    private
    // uncomment to cause a 'blank final field may not have been initialized' error in Eclipse
    //final 
    A isNull;
    
    public TestNullFinal() {
        isNull = new A("constructor");
        System.out.println("test=" + test);
    }
    
    public static void main(String[] args) {
        new TestNullFinal();
    }
    
    private Runnable runnable = () -> {
        // ERROR: blank final field may not have been initialized
        isNull.hashCode();
    };
    
    private Object test = isNullValue();
    
    protected Object isNullValue() {
        return (isNull);
    }
    
    protected static class A {
        public A(String text) {
            System.out.println(text);
        }
    }
}
