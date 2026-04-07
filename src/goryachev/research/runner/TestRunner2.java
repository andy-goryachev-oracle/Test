package goryachev.research.runner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public class TestRunner2 {
    
    public static void main(String args[]) throws Exception {
        
        String javaExecutablePath = ProcessHandle.current()
            .info()
            .command()
            .orElseThrow();
        IO.println(javaExecutablePath);
        
        File path = new File("bin/");
        String[] cmd = {
            javaExecutablePath,
            //"-version",
            "-Djava.library.path=/Users/angorya/Projects/jfx3/jfx/rt/build/sdk/lib",
            "-ea",
            "-Djavafx.enablePreview=true",
            "--enable-native-access=javafx.graphics",
            "--enable-native-access=javafx.web",
            "-Dfile.encoding=UTF-8",
            "-Dstdout.encoding=UTF-8",
            "-Dstderr.encoding=UTF-8",
            "-p", "/Users/angorya/Projects/jfx3/jfx/rt/build/sdk/lib",
            "--add-reads", "javafx.base=java.management",
            "--add-reads", "javafx.base=jdk.management",
            "--add-exports", "javafx.base/com.sun.javafx.property=javafx.graphics",
            "--add-exports", "java.base/sun.security.util=javafx.graphics",
            "--add-reads", "javafx.base=java.management",
            "--add-reads", "javafx.base=jdk.management",
            "--add-reads", "javafx.base=java.management",
            "--add-reads", "javafx.base=jdk.management",
            "--add-exports", "javafx.base/com.sun.javafx.property=javafx.graphics",
            "--add-exports", "java.base/sun.security.util=javafx.graphics",
            "--add-reads", "javafx.base=java.management",
            "--add-reads", "javafx.base=jdk.management",
            "--add-exports", "javafx.base/com.sun.javafx=javafx.web",
            "--add-reads", "javafx.web=java.management",
            "--add-reads", "javafx.base=java.management",
            "--add-reads", "javafx.base=jdk.management",
            "--add-modules=javafx.base,javafx.graphics,javafx.controls,javafx.fxml,javafx.web",
            "-cp", ".",
            "goryachev.research.runner.examples.EmojiTest_Easy"
        };
        String[] env = {
        };
        File dir = path;
        Process p = Runtime.getRuntime().exec(cmd, env, dir);
        new Monitor(p.getErrorStream(), System.err).start();
        new Monitor(p.getInputStream(), System.out).start();
    }
    
    private static class Monitor extends Thread {
        private final InputStream in;
        private final PrintStream out;
        
        public Monitor(InputStream in, PrintStream out) {
            this.in = in;
            this.out = out;
        }
        
        @Override
        public void run() {
            try {
                for (;;) {
                    int c = in.read();
                    if (c < 0) {
                        return;
                    }
                    out.append((char)c);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
