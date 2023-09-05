package goryachev.bugs;

import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;

public class JFXPanel_NPE_8255248 {
    private static WebView webView;
    private static JFXPanel contentPane;
    private static AtomicBoolean failure;

    public static void main(String[] args) throws Throwable {
        failure = new AtomicBoolean(false);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();
                failure.set(true);
            }
        });
        SwingUtilities.invokeAndWait(JFXPanel_NPE_8255248::createUI);
        for (int i = 0; i < 3000; i++) {
            SwingUtilities.invokeLater(contentPane::repaint);
            Platform.runLater(() -> contentPane.setScene(null));
            Thread.sleep(1);
            Platform.runLater(() -> contentPane.setScene(webView.getScene()));
            Thread.sleep(1);
        }
        System.out.println("failure = " + failure.get());
    }

    protected static void createUI() {
        final var jFrame = new JFrame();
        contentPane = new JFXPanel();
        Platform.runLater(() -> fx(contentPane));
        jFrame.setContentPane(contentPane);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setSize(400, 400);
        jFrame.setVisible(true);

    }

    private static void fx(final JFXPanel contentPane) {
        webView = new WebView();
        final var engine = webView.getEngine();
        engine.loadContent("hello!");
        contentPane.setScene(new Scene(webView));
    }
}