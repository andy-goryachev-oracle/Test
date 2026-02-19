package goryachev.research.runner;

import java.net.URI;
import javax.tools.SimpleJavaFileObject;

/**
 * in memory java source file object
 */
public class StringJavaSource extends SimpleJavaFileObject {
    
    private final String code;

    public StringJavaSource(String name, String code) {
        super(URI.create(InMemoryJavaFileManager.createUrl(name, true)), Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}