package goryachev.research.runner;

import java.lang.reflect.Method;
import java.util.Arrays;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import javafx.application.Application;

public class TestRunner {
    
    public static void main(String args[]) throws Exception {
        
        JavaFileObject file = new StringJavaSource(
            "CompilerTest",
            """
            public class CompilerTest {
                static {
                    IO.println("static");
                }
                
                public static void main(String args[]) {
                    IO.println("instance");
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
                Class tc = Class.forName(name, true, ldr);
                Method main = getMethod(tc, "main", String.class);
                if (main != null) {
                    main.invoke(null);
                } else {
                    if (ManualTestWindow.class.isAssignableFrom(tc)) {
                        // TODO module path, lauch jdk, command line options
                        Application.launch(tc);
                    } else {
                        System.err.println("Don't know how to launch " + tc);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static Method getMethod(Class<?> c, String name, Class<?> ... args) {
        try {
            return c.getDeclaredMethod(name, args);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
