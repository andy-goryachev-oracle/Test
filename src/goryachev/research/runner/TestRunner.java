package goryachev.research.runner;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

public class TestRunner {
    
    public static void main(String args[]) throws Exception {
        
        JavaFileObject file = new JavaSourceFromString(
            "CompilerTest",
            """
            public class CompilerTest {
                static {
                    System.out.println("static");
                }
                
                public static void main(String args[]) {
                    System.out.println("instance");
                }
            }
            """
        );

        String name = "CompilerTest";
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        JavaFileManager fm = new InMemoryJavaFileManager(compiler.getStandardFileManager(null, null, null), name);
        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(file);
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
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
                // FIX url
                URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { new File("").toURI().toURL() });
                Class.forName("CompilerTest", true, classLoader).
                    getDeclaredMethod("main", new Class[] { String[].class }).
                    invoke(null, new Object[] { null });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
