package goryachev.research.runner;

import java.util.Arrays;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

public class TestRunner {
    
    public static void main(String args[]) throws Exception {
        
        JavaFileObject file = new StringJavaSource(
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
        InMemoryJavaFileManager fm = InMemoryJavaFileManager.init(compiler);
        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(file);
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        CompilationTask task = compiler.getTask(null, fm, diagnostics, null, null, compilationUnits);
        
        boolean success = task.call();

        for (Diagnostic d: diagnostics.getDiagnostics()) {
            System.out.println("code=" + d.getCode());
            System.out.println("kind=" + d.getKind());
            System.out.println("pos=" + d.getPosition());
            System.out.println("start=" + d.getStartPosition());
            System.out.println("end=" + d.getEndPosition());
            System.out.println("source=" + d.getSource());
            System.out.println("message=" + d.getMessage(null));
        }

        if (success) {
            try {
                ClassLoader ldr = fm.getInMemClassLoader();
                Class.forName(name, true, ldr).
                    getDeclaredMethod("main", new Class[] { String[].class }).
                    invoke(null, new Object[] { null });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
