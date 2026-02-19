package goryachev.research.runner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import javax.tools.SimpleJavaFileObject;

/**
 * in memory java file output object
 */
public class InMemoryJavaFileOutput extends SimpleJavaFileObject {

    private ByteArrayOutputStream out;

    public InMemoryJavaFileOutput(URI uri, Kind kind) {
        super(uri, kind);
    }
    
    private void p(String s) {
        if (true) {
            System.out.println(s);
        }
    }
    
    public byte[] getBytes() {
        return out.toByteArray();
    }

    @Override
    public InputStream openInputStream() throws IOException {
        p("O.openInputStream");
        throw new Error();
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        p("O.openOutputStream");
        out = new ByteArrayOutputStream();
        return out;
    }

    @Override
    public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
        p("O.openReader");
        throw new Error();
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        p("O.getCharContent");
        throw new Error();
    }

    @Override
    public Writer openWriter() throws IOException {
        p("O.openWriter");
        throw new Error();
    }
}
