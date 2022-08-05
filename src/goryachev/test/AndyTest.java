package goryachev.test;

public class AndyTest {
    public static void main(String[] args) {
        test();
    }
    
    public static void test() {
        for (int i=0; i<4; i++) {
            Object rv = t(i);
            System.out.println("i=" + i + " rv=" + rv);
        }
    }

    private static Object t(int x) {
        switch(x) {
        case 1: {
            
        }
        case 2: {
            return "2";
        }
        case 3: {
            
        }
        default:
            return "default";
        }
    }
}
