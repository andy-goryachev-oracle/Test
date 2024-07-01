package goryachev.bugs;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.Button;

/**
 * https://bugs.openjdk.org/browse/JDK-8334593
 */
public class JFXPanel_AddingRemoving_8334593 {
    private static JFrame frame;

    private static void initAndShowGUI() {
        frame = new JFrame("FX");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        final JFXPanel fxPanel = new JFXPanel();
        // fxPanel added to frame for the first time
        frame.add(fxPanel);
        frame.setSize(400, 300);
        frame.setVisible(true);

        Platform.runLater(() -> {
            initFX(fxPanel);
            // uncomment to show the issue in JDK-8334593

//            SwingUtilities.invokeLater(() -> {
//                // fxPanel removed from frame
//                frame.remove(fxPanel);
//                // fxPanel added to frame again
//                frame.add(fxPanel); // <-- leads to NullPointerException
//                frame.validate();
//                frame.repaint();
//            });
        });
    }

    private static void initFX(JFXPanel fxPanel) {
        Button b = new Button("Testbutton");
        b.setOnAction((ev) -> {
            SwingUtilities.invokeLater(() -> {
                // fxPanel removed from frame
                frame.remove(fxPanel);
                // fxPanel added to frame again
                frame.add(fxPanel); // <-- leads to NullPointerException
                frame.validate();
                frame.repaint();
            });
        });
        Scene scene = new Scene(b);
        fxPanel.setScene(scene);
    }

    public static void main() {
        SwingUtilities.invokeLater(() -> initAndShowGUI());
    }
}