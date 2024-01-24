package goryachev.tests;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * https://bugs.openjdk.org/browse/JDK-8322784
 */
public class FXPanelThreadBug {

    private static void initAndShowGUI() {
        final JFXPanel panel = new JFXPanel();
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.setSize(100, 100);
        frame.setLocationRelativeTo(null);
        
        Platform.runLater(() -> {
            GridPane root = new GridPane();
            root.addRow(0, new TextField());
            Scene scene = new Scene(root);
            panel.setScene(scene);

            SwingUtilities.invokeLater(() -> {
                frame.setVisible(true);
            });
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            initAndShowGUI();
        });
    }
}