package goryachev.research;

import java.text.MessageFormat;

/**
 *
 */
public class TestWhitespace {
    public static void main(String[] args) {
        System.out.println("hex| trim| isWhitespace");
        for(int i=0; i<= 0x20; i++) {
            String n = String.format("%02x", i);
            System.out.println(MessageFormat.format("0x{0} {1} {2}", n, w(ws(i)), w(Character.isWhitespace(i))));
        }
    }
    
    private static String w(boolean on) {
        return on ? "W" : "-";
    }
    
    private static boolean ws(int c) {
        char[] cs = { (char)c };
        return new String(cs).trim().length() == 0;
    }
}
