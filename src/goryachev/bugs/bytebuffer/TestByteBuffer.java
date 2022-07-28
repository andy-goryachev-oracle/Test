package goryachev.bugs.bytebuffer;
import java.nio.ByteBuffer;

public class TestByteBuffer {
    private final ByteBuffer buffer;
    
    public TestByteBuffer() {
        buffer = ByteBuffer.allocateDirect(32);
    }
    
    public void position(int p) {
        Object rv = buffer.position(0);
        System.out.println(rv);
    }
    
    public static void main(String[] args) {
        TestByteBuffer a = new TestByteBuffer();
        a.position(0);
    }
}
