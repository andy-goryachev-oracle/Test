package goryachev.bugs;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * JTextField/JTextArea inside SwingNode
 * https://bugs.openjdk.org/browse/JDK-8356996
 */
public class JTextArea_FX_Egyptian_SwingNode_8356996 extends Application {
    // 𓅂𓁹𓁋𓁨
    private static final String text = "\ud80c\udd42\ud80c\udc79\ud80c\udc4b\ud80c\udc68";

    @Override
    public void start(Stage stage) throws Exception {
        SwingNode swingNode = new SwingNode();
        SwingUtilities.invokeLater(() -> {
            initEDT(swingNode);
        });

        BorderPane bp = new BorderPane(swingNode);

        Scene scene = new Scene(bp, 500, 300);

        stage.setScene(scene);
        stage.setTitle("Ancient Egyptian (SwingNode) JDK-8356996 " + text);
        stage.show();
    }

    private void initEDT(SwingNode swingNode) {
        JTextArea ta = new JTextArea(text);
        JTextField tf = new JTextField(text);

        JPanel p = new JPanel(new BorderLayout());
        p.add(tf, BorderLayout.NORTH);
        p.add(ta, BorderLayout.CENTER);
        swingNode.setContent(p);
    }
}