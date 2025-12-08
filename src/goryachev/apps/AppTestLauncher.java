package goryachev.apps;
import javafx.application.Application;
import goryachev.bugs.*;
import goryachev.research.*;
import goryachev.tests.*;

/**
 * Use this class to launch various test snippets, so one does not have to 
 * create a new launch configuration each time.
 */
public class AppTestLauncher {
    public static void main(String[] args) throws Throwable {
        // enableLogging();
        Application.launch(Clipboard_WrongType_8269630.class, args);
    }

    private static void enableLogging() {
        System.setProperty("prism.order", "sw");
        System.setProperty("javafx.pulseLogger", "true");
        System.setProperty("javafx.pulseLogger.threshold", "-1");
        System.setProperty("prism.showdirty", "true");
    }
}
