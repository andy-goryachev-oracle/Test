package goryachev.tests;

import java.awt.ComponentOrientation;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * macOS seem to have a bug? idiosyncrasy? in rendering mixed text in RTL orientation.
 * 
 * Swing:
 * https://bugs.openjdk.org/browse/JDK-8335273
 * 
 * JavaFX:
 * https://bugs.openjdk.org/browse/JDK-8330559
 */
public class JTextArea_MixedTextWrapping {

    private static void initAndShowGUI() {
        JTextArea t = new JTextArea("Arabic:: " + // also has problem rendering the ':' when a line break occurs here 
            "العربية" +
            "\n" +
            "Hebrew: " +
            "עברית");
        t.setFont(t.getFont().deriveFont(24.0f));
        t.setLineWrap(true);
        t.setWrapStyleWord(false);
        t.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        JFrame frame = new JFrame();
        frame.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(t);
        frame.setSize(100, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            initAndShowGUI();
        });
    }
}