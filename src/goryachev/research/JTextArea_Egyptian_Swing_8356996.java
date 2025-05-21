package goryachev.research;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Pure Swing test case.
 * https://bugs.openjdk.org/browse/JDK-8356996
 */
public class JTextArea_Egyptian_Swing_8356996 {

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            new JTextArea_Egyptian_Swing_8356996().startEDT();
        });
    }

    private void startEDT() {
        // TODO𓅂𓁹𓁋𓁨𓅂𓁹𓁋𓁨𓅂𓁹𓁋𓁨𓅂𓁹𓁋𓁨𓅂𓁹𓁋𓁨𓅂𓁹𓁋𓁨
        String text = "\ud80c\udd42\ud80c\udc79\ud80c\udc4b\ud80c\udc68";
        JTextArea ta = new JTextArea(text);
        JTextField tf = new JTextField(text);
        
        JPanel p = new JPanel(new BorderLayout());
        p.add(tf, BorderLayout.NORTH);
        p.add(ta, BorderLayout.CENTER);

        JFrame f = new JFrame();
        f.setTitle("Ancient Egyptian (Swing)𓅂𓁹𓁋𓁨 JDK-8356996 " + text);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setSize(400, 300);
        f.add(p);
        f.setVisible(true);
    }
}
