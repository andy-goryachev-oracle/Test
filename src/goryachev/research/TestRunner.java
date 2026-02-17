package goryachev.research;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class TestRunner {
    
    public static void main(String args[]) throws Exception {
        
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

        JavaFileObject file = new JavaSourceFromString(
            "CompilerTest",
            """
            public class CompilerTest {
                static {
                    p("static");
                }
                
                public static void main(String args[]) {
                    p("instance");
                }
            }
            """
        );

        JavaFileManager fm = new InMemoryJavaFileManager(compiler.getStandardFileManager(null, null, null));
        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(file);
        CompilationTask task = compiler.getTask(null, fm, diagnostics, null, null, compilationUnits);

        boolean success = task.call();
        for (Diagnostic diagnostic: diagnostics.getDiagnostics()) {
            System.out.println("code=" + diagnostic.getCode());
            System.out.println("kind=" + diagnostic.getKind());
            System.out.println("pos=" + diagnostic.getPosition());
            System.out.println("start=" + diagnostic.getStartPosition());
            System.out.println("end=" + diagnostic.getEndPosition());
            System.out.println("source=" + diagnostic.getSource());
            System.out.println("message=" + diagnostic.getMessage(null));
        }

        if (success) {
            try {
                URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { new File("").toURI().toURL() });
                Class.forName("CompilerTest", true, classLoader).
                    getDeclaredMethod("main", new Class[] { String[].class }).
                    invoke(null, new Object[] { null });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // in memory file manager
    public static class InMemoryJavaFileManager implements JavaFileManager {
        private final StandardJavaFileManager fm;
        
        public InMemoryJavaFileManager(StandardJavaFileManager fm) {
            this.fm = fm;
        }
        
        private void p(String s) {
            if (false) {
                System.out.println(s);
            }
        }

        @Override
        public int isSupportedOption(String option) {
            var v = fm.isSupportedOption(option);
            p("isSupportedOption " + option + " " + v);
            return v;
        }

        @Override
        public ClassLoader getClassLoader(Location location) {
            var v = fm.getClassLoader(location);
            p("getClassLoader " + location + " " + v);
            return new URLClassLoader(new URL[0], v) {
                @Override
                public InputStream getResourceAsStream(String name) {
                    p("CL: getResourceAsStream " + name);
                    var v = super.getResourceAsStream(name);
                    return v;
                }
            };
//            return v;
        }

        @Override
        public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse) throws IOException {
            var v = fm.list(location, packageName, kinds, recurse);
            p("list " + location + " " + v);
            return v;
        }

        @Override
        public String inferBinaryName(Location location, JavaFileObject file) {
            var v = fm.inferBinaryName(location, file);
            p("inferBinaryName " + location + " file=" + file + " " + v);
            return v;
        }

        @Override
        public boolean isSameFile(FileObject a, FileObject b) {
            var v = fm.isSameFile(a, b);
            p("isSameFile " + a + " " + b + " " + v);
            return v;
        }

        @Override
        public boolean handleOption(String current, Iterator<String> remaining) {
            var v = fm.handleOption(current, remaining);
            p("handleOption " + current + " " + v);
            return v;
        }

        @Override
        public boolean hasLocation(Location location) {
            var v = fm.hasLocation(location);
            p("hasLocation " + location + " " + v);
            return v;
        }

        @Override
        public JavaFileObject getJavaFileForInput(Location location, String className, Kind kind) throws IOException {
            var v = fm.getJavaFileForInput(location, className, kind);
            p("getJavaFileForInput " + location + " " + v);
            return v;
        }

        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) throws IOException {
            System.out.println("getJavaFileForOutput " + location + " className=" + className + " kind=" + kind + " sibling=" + sibling);
            if (location.isOutputLocation()) {
                // TODO location/class name/sibling + store in a hashtable?
                return new InMemoryJavaFileObject(className, kind);
            }
            var v = fm.getJavaFileForOutput(location, className, kind, sibling);
            // TODO
            // getJavaFileForOutput CLASS_OUTPUT className=CompilerTest kind=CLASS sibling=goryachev.research.TestRunner$JavaSourceFromString[java-input:///CompilerTest.java] SimpleFileObject[/Users/angorya/Projects/Test3/Test/CompilerTest.class]
            p("getJavaFileForOutput " + location + " className=" + className + " kind=" + kind + " sibling=" + sibling + " " + v);
            return v;
        }

        @Override
        public FileObject getFileForInput(Location location, String packageName, String relativeName) throws IOException {
            var v = fm.getFileForInput(location, packageName, relativeName);
            p("getFileForInput " + location + " " + v);
            return v;
        }

        @Override
        public FileObject getFileForOutput(Location location, String packageName, String relativeName, FileObject sibling) throws IOException {
            var v = fm.getFileForOutput(location, packageName, relativeName, sibling);
            p("getFileForOutput " + location + " packageName=" + packageName + " relativeName=" + relativeName + " sibling=" + sibling + " " + v);
            return v;
        }

        @Override
        public void flush() throws IOException {
            fm.flush();
        }

        @Override
        public void close() throws IOException {
            fm.close();
        }
        
        @Override
        public Iterable<Set<Location>> listLocationsForModules(Location location) throws IOException {
            var v = fm.listLocationsForModules(location);
            p("listLocationsForModules " + location + " " + v);
            return v;
        }
        
        @Override
        public String inferModuleName(Location location) throws IOException {
            var v = fm.inferModuleName(location);
            p("inferModuleName " + location + " " + v);
            return v;
        }
    }
    
    // in memory source
    public static class JavaSourceFromString extends SimpleJavaFileObject {
        private final String code;

        public JavaSourceFromString(String name, String code) {
            super(URI.create("java-input:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }
    
    // in memory
    public static class InMemoryJavaFileObject extends SimpleJavaFileObject {

        private ByteArrayOutputStream out;
        
        public InMemoryJavaFileObject(String name, Kind kind) {
            super(URI.create("java-output:///" + name.replace('.', '/') + kind.extension), kind);
        }

        @Override
        public InputStream openInputStream() throws IOException {
            p("openInputStream");
            // TODO
            return null;
        }

        @Override
        public OutputStream openOutputStream() throws IOException {
            p("openOutputStream");
            out = new ByteArrayOutputStream();
            return out;
        }

        @Override
        public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
            p("openReader");
            // TODO
            return null;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            p("getCharContent");
            // TODO
            return null;
        }

        @Override
        public Writer openWriter() throws IOException {
            p("openWriter");
            // TODO
            return null;
        }

//        @Override
//        public long getLastModified() {
//            return 0;
//        }
//
//        @Override
//        public boolean delete() {
//            return false;
//        }

//        @Override
//        public NestingKind getNestingKind() {
//            // TODO
//            return null;
//        }

//        @Override
//        public Modifier getAccessLevel() {
//            // TODO
//            return null;
//        }
        
        private void p(String s) {
            if (true) {
                System.out.println(s);
            }
        }
    }
}
