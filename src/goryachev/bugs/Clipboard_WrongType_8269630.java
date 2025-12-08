package goryachev.bugs;
import java.util.Arrays;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * 
 */
public class Clipboard_WrongType_8269630 extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // still throws, even with showing the stage
//        DelayedCallback not implemented yet: JDK-8091740
//        java.lang.Exception: Stack trace
//            at java.base/java.lang.Thread.dumpStack(Thread.java:1991)
//            at javafx.graphics/com.sun.glass.ui.mac.MacSystemClipboard.pushToSystem(MacSystemClipboard.java:157)
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                ClipboardContent content = new ClipboardContent();
                content.put(DataFormat.PLAIN_TEXT, Arrays.asList("A","B"));
                Clipboard.getSystemClipboard().setContent(content);
            }
        });
        
//        stage.setScene(new Scene(new BorderPane()));
//        stage.show();
    }
}